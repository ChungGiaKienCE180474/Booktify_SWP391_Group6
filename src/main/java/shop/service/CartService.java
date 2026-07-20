package shop.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shop.domain.Book;
import shop.domain.Cart;
import shop.domain.CartItem;
import shop.domain.Promotion;
import shop.domain.User;
import shop.domain.dto.CartDTO;
import shop.domain.dto.CartItemDTO;
import shop.domain.dto.CartRefreshResult;
import shop.repository.BookRepository;
import shop.repository.CartItemRepository;
import shop.repository.CartRepository;
import shop.repository.UserRepository;

@Service
public class CartService {

    public static final String MSG_OUT_OF_STOCK =
            "This book is out of stock.";

    public static final String MSG_INACTIVE =
            "This book is no longer available and cannot be added to the cart.";

    public static final String MSG_QUANTITY_MIN =
            "Quantity must be at least 1.";

    public static final String MSG_QUANTITY_INVALID =
            "Quantity must be a positive integer.";

    public static final String MSG_EXCEED_STOCK =
            "The requested quantity exceeds available stock. Only %d book(s) remain.";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final PromotionService promotionService;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            BookRepository bookRepository,
            UserRepository userRepository,
            PromotionService promotionService) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.promotionService = promotionService;
    }

    @Transactional(readOnly = true)
    public Cart getCartForUser(long userId) {
        return cartRepository
                .findByUserIdWithItems(userId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public int getCartItemCount(long userId) {

        Cart cart = cartRepository
                .findByUserIdWithItems(userId)
                .orElse(null);

        if (cart == null || cart.getItems().isEmpty()) {
            return 0;
        }

        return cart.getItems()
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Transactional
    public Cart addBook(
            long userId,
            long bookId,
            int quantity) {

        validateQuantity(quantity);

        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Book not found."
                        )
                );

        validateBookForCart(book, quantity);

        Cart cart = getOrCreateCart(userId);

        CartItem existing = cartItemRepository
                .findByCartIdAndBookId(
                        cart.getId(),
                        bookId
                )
                .orElse(null);

        if (existing != null) {

            int newQuantity =
                    existing.getQuantity() + quantity;

            if (newQuantity > book.getStockQuantity()) {

                throw new IllegalArgumentException(
                        String.format(
                                MSG_EXCEED_STOCK,
                                book.getStockQuantity()
                        )
                );
            }

            existing.setQuantity(newQuantity);

        } else {

            CartItem item = new CartItem();

            item.setCart(cart);
            item.setBook(book);
            item.setQuantity(quantity);

            cart.getItems().add(item);
        }

        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateQuantity(
            long userId,
            long cartItemId,
            int quantity) {

        validateQuantity(quantity);

        CartItem item = cartItemRepository
                .findByIdAndCartUserId(
                        cartItemId,
                        userId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart item not found."
                        )
                );

        Book book = bookRepository
                .findById(item.getBook().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Book not found."
                        )
                );

        validateBookForCart(book, quantity);

        item.setQuantity(quantity);
        item.setBook(book);

        Cart cart = item.getCart();

        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItem(
            long userId,
            long cartItemId) {

        CartItem item = cartItemRepository
                .findByIdAndCartUserId(
                        cartItemId,
                        userId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart item not found."
                        )
                );

        Cart cart = item.getCart();

        cart.getItems().remove(item);

        cartItemRepository.delete(item);

        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(long userId) {

        Cart cart = cartRepository
                .findByUserIdWithItems(userId)
                .orElse(null);

        if (cart == null) {
            return;
        }

        for (CartItem item :
                new ArrayList<>(cart.getItems())) {

            cartItemRepository.delete(item);
        }

        cart.getItems().clear();

        cart.setTotalAmount(BigDecimal.ZERO);

        cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public List<String> validateForCheckout(long userId) {

        Cart cart = cartRepository
                .findByUserIdWithItems(userId)
                .orElse(null);

        List<String> errors = new ArrayList<>();

        if (cart == null || cart.getItems().isEmpty()) {

            errors.add(
                    "The cart must contain at least one item."
            );

            return errors;
        }

        for (CartItem item : cart.getItems()) {

            Book book = bookRepository
                    .findById(item.getBook().getId())
                    .orElse(null);

            if (book == null) {

                errors.add(
                        "An item in your cart no longer exists."
                );

                continue;
            }

            if (!book.isActive()) {

                errors.add(
                        "\""
                                + book.getTitle()
                                + "\" is no longer available."
                );
            }

            if (book.getStockQuantity() <= 0) {

                errors.add(
                        "\""
                                + book.getTitle()
                                + "\" — "
                                + MSG_OUT_OF_STOCK
                );

            } else if (
                    item.getQuantity()
                            > book.getStockQuantity()) {

                errors.add(
                        "\""
                                + book.getTitle()
                                + "\" only has "
                                + book.getStockQuantity()
                                + " book(s) remaining in stock."
                );
            }

            if (book.getPrice() == null
                    || book.getPrice()
                    .compareTo(BigDecimal.ZERO) < 0) {

                errors.add(
                        "\""
                                + book.getTitle()
                                + "\" has an invalid selling price."
                );
            }
        }

        return errors;
    }

    @Transactional
    public Cart refreshCart(long userId) {

        return refreshCartResult(userId)
                .getCart();
    }

    @Transactional
    public CartRefreshResult refreshCartWithWarnings(
            long userId) {

        Cart cart = cartRepository
                .findByUserIdWithItems(userId)
                .orElse(null);

        if (cart == null) {
            return CartRefreshResult.empty();
        }

        CartRefreshResult.Builder result =
                CartRefreshResult.builder(cart);

        List<CartItem> itemsToRemove =
                new ArrayList<>();

        for (CartItem item : cart.getItems()) {

            Book book = bookRepository
                    .findById(item.getBook().getId())
                    .orElse(null);

            if (book == null) {

                itemsToRemove.add(item);

                result.warning(
                        "An item no longer exists and was removed from your cart."
                );

                continue;
            }

            if (!book.isActive()) {

                itemsToRemove.add(item);

                result.warning(
                        "\""
                                + book.getTitle()
                                + "\" is no longer available and was removed from your cart."
                );

                continue;
            }

            if (book.getStockQuantity() <= 0) {

                itemsToRemove.add(item);

                result.warning(
                        "\""
                                + book.getTitle()
                                + "\" — "
                                + MSG_OUT_OF_STOCK
                );

                continue;
            }

            if (item.getQuantity()
                    > book.getStockQuantity()) {

                result.warning(
                        "\""
                                + book.getTitle()
                                + "\" — "
                                + String.format(
                                MSG_EXCEED_STOCK,
                                book.getStockQuantity()
                        )
                );
            }

            /*
             * Replace the book reference with the latest database version
             * before recalculating prices and stock information.
             */
            item.setBook(book);
        }

        for (CartItem item : itemsToRemove) {

            cart.getItems().remove(item);

            cartItemRepository.delete(item);
        }

        /*
         * Recalculate the cart whenever it is opened or used during checkout.
         * This allows newly activated, expired, or disabled promotions to be
         * reflected immediately.
         */
        recalculateTotal(cart);

        return result.build();
    }

    @Transactional
    public CartDTO refreshCartDTO(long userId) {

        return toCartDTO(
                refreshCart(userId)
        );
    }

    @Transactional
    public CartRefreshResult refreshCartResult(
            long userId) {

        CartRefreshResult result =
                refreshCartWithWarnings(userId);

        if (result.getCart() != null) {

            cartRepository.save(
                    result.getCart()
            );
        }

        return result;
    }

    public CartDTO toCartDTO(Cart cart) {

        if (cart == null
                || cart.getItems() == null
                || cart.getItems().isEmpty()) {

            return CartDTO.empty();
        }

        CartDTO dto = new CartDTO();

        dto.setId(cart.getId());

        dto.setTotalAmountFormatted(
                promotionService.formatMoney(
                        cart.getTotalAmount()
                )
        );

        dto.setItems(
                cart.getItems()
                        .stream()
                        .map(this::toCartItemDTO)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    private CartItemDTO toCartItemDTO(
            CartItem item) {

        CartItemDTO dto = new CartItemDTO();

        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());

        Book book = item.getBook();

        if (book == null) {

            dto.setBookTitle("Unavailable book");
            dto.setBookPriceFormatted("0");
            dto.setOriginalPriceFormatted("0");
            dto.setEffectivePriceFormatted("0");
            dto.setOriginalSubtotalFormatted("0");
            dto.setSubtotalFormatted("0");
            dto.setPromotionApplied(false);

            return dto;
        }

        BigDecimal originalPrice =
                book.getPrice() == null
                        ? BigDecimal.ZERO
                        : book.getPrice();

        Promotion bestPromotion =
                promotionService
                        .getBestPromotionForBook(book)
                        .orElse(null);

        BigDecimal effectivePrice =
                bestPromotion == null
                        ? originalPrice
                        : promotionService
                        .calculateDiscountedPrice(
                                originalPrice,
                                bestPromotion
                        );

        BigDecimal originalSubtotal =
                originalPrice.multiply(
                        BigDecimal.valueOf(
                                item.getQuantity()
                        )
                );

        BigDecimal effectiveSubtotal =
                effectivePrice.multiply(
                        BigDecimal.valueOf(
                                item.getQuantity()
                        )
                );

        dto.setBookId(book.getId());
        dto.setBookTitle(book.getTitle());
        dto.setBookAuthor(book.getAuthor());
        dto.setBookImageUrl(book.getImageUrl());

        /*
         * Keep bookPriceFormatted for compatibility with the current JSP.
         * It now contains the effective price.
         */
        dto.setBookPriceFormatted(
                promotionService.formatMoney(
                        effectivePrice
                )
        );

        dto.setOriginalPriceFormatted(
                promotionService.formatMoney(
                        originalPrice
                )
        );

        dto.setEffectivePriceFormatted(
                promotionService.formatMoney(
                        effectivePrice
                )
        );

        dto.setOriginalSubtotalFormatted(
                promotionService.formatMoney(
                        originalSubtotal
                )
        );

        dto.setSubtotalFormatted(
                promotionService.formatMoney(
                        effectiveSubtotal
                )
        );

        dto.setPromotionApplied(
                bestPromotion != null
        );

        dto.setPromotionLabel(
                bestPromotion == null
                        ? ""
                        : promotionService
                        .getDiscountLabel(
                                book,
                                bestPromotion
                        )
        );

        dto.setBookStockQuantity(
                book.getStockQuantity()
        );

        dto.setBookActive(
                book.isActive()
        );

        return dto;
    }

    private Cart getOrCreateCart(long userId) {

        return cartRepository
                .findByUserIdWithItems(userId)
                .orElseGet(() -> {

                    User user = userRepository
                            .findById(userId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "User not found."
                                    )
                            );

                    Cart cart = new Cart();

                    cart.setUser(user);

                    return cartRepository.save(cart);
                });
    }

    private void validateBookForCart(
            Book book,
            int quantity) {

        if (!book.isActive()) {

            throw new IllegalArgumentException(
                    MSG_INACTIVE
            );
        }

        if (book.getStockQuantity() <= 0) {

            throw new IllegalArgumentException(
                    MSG_OUT_OF_STOCK
            );
        }

        if (quantity > book.getStockQuantity()) {

            throw new IllegalArgumentException(
                    String.format(
                            MSG_EXCEED_STOCK,
                            book.getStockQuantity()
                    )
            );
        }
    }

    private void validateQuantity(int quantity) {

        if (quantity < 1) {

            throw new IllegalArgumentException(
                    MSG_QUANTITY_MIN
            );
        }
    }

    public Integer parsePositiveIntegerQuantity(
            String raw) {

        if (raw == null || raw.isBlank()) {
            return null;
        }

        String trimmed = raw.trim();

        if (!trimmed.matches("\\d+")) {
            return null;
        }

        try {

            long value =
                    Long.parseLong(trimmed);

            if (value < 1
                    || value > Integer.MAX_VALUE) {

                return null;
            }

            return (int) value;

        } catch (NumberFormatException exception) {

            return null;
        }
    }

    /**
     * Recalculates the cart total using the current effective selling price
     * of every book.
     */
    private void recalculateTotal(Cart cart) {

        if (cart == null
                || cart.getItems() == null
                || cart.getItems().isEmpty()) {

            if (cart != null) {
                cart.setTotalAmount(
                        BigDecimal.ZERO
                );
            }

            return;
        }

        BigDecimal total = cart.getItems()
                .stream()
                .filter(item ->
                        item != null
                                && item.getBook() != null
                )
                .map(item -> {

                    BigDecimal effectivePrice =
                            promotionService
                                    .getEffectivePrice(
                                            item.getBook()
                                    );

                    return effectivePrice.multiply(
                            BigDecimal.valueOf(
                                    item.getQuantity()
                            )
                    );
                })
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        cart.setTotalAmount(total);
    }
}