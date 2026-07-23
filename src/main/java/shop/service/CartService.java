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
import shop.domain.VppItem;
import shop.domain.dto.CartDTO;
import shop.domain.dto.CartItemDTO;
import shop.domain.dto.CartRefreshResult;
import shop.repository.BookRepository;
import shop.repository.CartItemRepository;
import shop.repository.CartRepository;
import shop.repository.UserRepository;
import shop.repository.VppItemRepository;

@Service
public class CartService {

    public static final String MSG_OUT_OF_STOCK =
            "This product is out of stock.";

    public static final String MSG_INACTIVE =
            "This product is no longer available and cannot be added to the cart.";

    public static final String MSG_QUANTITY_MIN =
            "Quantity must be at least 1.";

    public static final String MSG_QUANTITY_INVALID =
            "Quantity must be a positive integer.";

    public static final String MSG_EXCEED_STOCK =
            "The requested quantity exceeds available stock. Only %d item(s) remain.";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final VppItemRepository vppItemRepository;
    private final PromotionService promotionService;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            BookRepository bookRepository,
            UserRepository userRepository,
            VppItemRepository vppItemRepository,
            PromotionService promotionService) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.vppItemRepository = vppItemRepository;
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

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return 0;
        }

        return cart.getItems()
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Transactional
    public Cart addBook(long userId, long bookId, int quantity) {

        validateQuantity(quantity);

        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Book not found.")
                );

        validateBookForCart(book, quantity);

        Cart cart = getOrCreateCart(userId);

        CartItem existing = cartItemRepository
                .findByCartIdAndBookId(cart.getId(), bookId)
                .orElse(null);

        if (existing != null) {

            int newQuantity = existing.getQuantity() + quantity;

            if (newQuantity > book.getStockQuantity()) {
                throw new IllegalArgumentException(
                        String.format(MSG_EXCEED_STOCK, book.getStockQuantity())
                );
            }

            existing.setQuantity(newQuantity);

        } else {

            CartItem item = new CartItem();

            item.setCart(cart);
            item.setBook(book);
            item.setVppItem(null);
            item.setQuantity(quantity);

            cart.getItems().add(item);
        }

        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart addVppItem(long userId, long vppItemId, int quantity) {

        validateQuantity(quantity);

        VppItem vppItem = vppItemRepository
                .findById(vppItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Stationery item not found.")
                );

        validateVppForCart(vppItem, quantity);

        Cart cart = getOrCreateCart(userId);

        CartItem existing = cartItemRepository
                .findByCartIdAndVppItemId(cart.getId(), vppItemId)
                .orElse(null);

        if (existing != null) {

            int newQuantity = existing.getQuantity() + quantity;

            if (newQuantity > vppItem.getStockQuantity()) {
                throw new IllegalArgumentException(
                        String.format(MSG_EXCEED_STOCK, vppItem.getStockQuantity())
                );
            }

            existing.setQuantity(newQuantity);

        } else {

            CartItem item = new CartItem();

            item.setCart(cart);
            item.setBook(null);
            item.setVppItem(vppItem);
            item.setQuantity(quantity);

            cart.getItems().add(item);
        }

        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateQuantity(long userId, long cartItemId, int quantity) {

        validateQuantity(quantity);

        CartItem item = cartItemRepository
                .findByIdAndCartUserId(cartItemId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart item not found.")
                );

        if (item.getBook() != null) {

            Book book = bookRepository
                    .findById(item.getBook().getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Book not found.")
                    );

            validateBookForCart(book, quantity);
            item.setBook(book);

        } else if (item.getVppItem() != null) {

            VppItem vppItem = vppItemRepository
                    .findById(item.getVppItem().getId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Stationery item not found.")
                    );

            validateVppForCart(vppItem, quantity);
            item.setVppItem(vppItem);

        } else {
            throw new IllegalArgumentException("Invalid cart item.");
        }

        item.setQuantity(quantity);

        Cart cart = item.getCart();

        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItem(long userId, long cartItemId) {

        CartItem item = cartItemRepository
                .findByIdAndCartUserId(cartItemId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cart item not found.")
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

        for (CartItem item : new ArrayList<>(cart.getItems())) {
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

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            errors.add("The cart must contain at least one item.");
            return errors;
        }

        for (CartItem item : cart.getItems()) {

            if (item.getBook() != null) {

                Book book = bookRepository
                        .findById(item.getBook().getId())
                        .orElse(null);

                if (book == null) {
                    errors.add("An item in your cart no longer exists.");
                    continue;
                }

                if (!book.isActive()) {
                    errors.add("\"" + book.getTitle() + "\" is no longer available.");
                }

                if (book.getStockQuantity() <= 0) {
                    errors.add("\"" + book.getTitle() + "\" — " + MSG_OUT_OF_STOCK);
                } else if (item.getQuantity() > book.getStockQuantity()) {
                    errors.add("\"" + book.getTitle() + "\" only has "
                            + book.getStockQuantity()
                            + " item(s) remaining in stock.");
                }

                if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                    errors.add("\"" + book.getTitle() + "\" has an invalid selling price.");
                }

                continue;
            }

            if (item.getVppItem() != null) {

                VppItem vppItem = vppItemRepository
                        .findById(item.getVppItem().getId())
                        .orElse(null);

                if (vppItem == null) {
                    errors.add("A stationery item in your cart no longer exists.");
                    continue;
                }

                if (vppItem.isDeleted() || !vppItem.isActive()) {
                    errors.add("\"" + vppItem.getName() + "\" is no longer available.");
                }

                if (vppItem.getStockQuantity() == null || vppItem.getStockQuantity() <= 0) {
                    errors.add("\"" + vppItem.getName() + "\" — " + MSG_OUT_OF_STOCK);
                } else if (item.getQuantity() > vppItem.getStockQuantity()) {
                    errors.add("\"" + vppItem.getName() + "\" only has "
                            + vppItem.getStockQuantity()
                            + " item(s) remaining in stock.");
                }

                if (vppItem.getPrice() == null || vppItem.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                    errors.add("\"" + vppItem.getName() + "\" has an invalid selling price.");
                }

                continue;
            }

            errors.add("An item in your cart is invalid.");
        }

        return errors;
    }

    @Transactional
    public Cart refreshCart(long userId) {
        return refreshCartResult(userId).getCart();
    }

    @Transactional
    public CartRefreshResult refreshCartWithWarnings(long userId) {

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

            if (item.getBook() != null) {

                Book book = bookRepository
                        .findById(item.getBook().getId())
                        .orElse(null);

                if (book == null) {
                    itemsToRemove.add(item);
                    result.warning("An item no longer exists and was removed from your cart.");
                    continue;
                }

                if (!book.isActive()) {
                    itemsToRemove.add(item);
                    result.warning("\"" + book.getTitle() + "\" is no longer available and was removed from your cart.");
                    continue;
                }

                if (book.getStockQuantity() <= 0) {
                    itemsToRemove.add(item);
                    result.warning("\"" + book.getTitle() + "\" — " + MSG_OUT_OF_STOCK);
                    continue;
                }

                if (item.getQuantity() > book.getStockQuantity()) {
                    result.warning("\"" + book.getTitle() + "\" — "
                            + String.format(MSG_EXCEED_STOCK, book.getStockQuantity()));
                }

                item.setBook(book);
                continue;
            }

            if (item.getVppItem() != null) {

                VppItem vppItem = vppItemRepository
                        .findById(item.getVppItem().getId())
                        .orElse(null);

                if (vppItem == null) {
                    itemsToRemove.add(item);
                    result.warning("A stationery item no longer exists and was removed from your cart.");
                    continue;
                }

                if (vppItem.isDeleted() || !vppItem.isActive()) {
                    itemsToRemove.add(item);
                    result.warning("\"" + vppItem.getName() + "\" is no longer available and was removed from your cart.");
                    continue;
                }

                if (vppItem.getStockQuantity() == null || vppItem.getStockQuantity() <= 0) {
                    itemsToRemove.add(item);
                    result.warning("\"" + vppItem.getName() + "\" — " + MSG_OUT_OF_STOCK);
                    continue;
                }

                if (item.getQuantity() > vppItem.getStockQuantity()) {
                    result.warning("\"" + vppItem.getName() + "\" — "
                            + String.format(MSG_EXCEED_STOCK, vppItem.getStockQuantity()));
                }

                item.setVppItem(vppItem);
                continue;
            }

            itemsToRemove.add(item);
            result.warning("An invalid item was removed from your cart.");
        }

        for (CartItem item : itemsToRemove) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        }

        recalculateTotal(cart);

        return result.build();
    }

    @Transactional
    public CartDTO refreshCartDTO(long userId) {
        return toCartDTO(refreshCart(userId));
    }

    @Transactional
    public CartRefreshResult refreshCartResult(long userId) {

        CartRefreshResult result =
                refreshCartWithWarnings(userId);

        if (result.getCart() != null) {
            cartRepository.save(result.getCart());
        }

        return result;
    }

    public CartDTO toCartDTO(Cart cart) {

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return CartDTO.empty();
        }

        CartDTO dto = new CartDTO();

        dto.setId(cart.getId());

        dto.setTotalAmountFormatted(
                promotionService.formatMoney(cart.getTotalAmount())
        );

        dto.setItems(
                cart.getItems()
                        .stream()
                        .map(this::toCartItemDTO)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    private CartItemDTO toCartItemDTO(CartItem item) {

        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());

        Book book = item.getBook();
        if (book != null) {
            BigDecimal price = book.getPrice() == null ? BigDecimal.ZERO : book.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

            dto.setBookId(book.getId());
            dto.setBookTitle(book.getTitle());
            dto.setBookAuthor(book.getAuthor());
            dto.setBookImageUrl(book.getImageUrl());
            dto.setBookPriceFormatted(promotionService.formatMoney(price));
            dto.setOriginalPriceFormatted(promotionService.formatMoney(price));
            dto.setEffectivePriceFormatted(promotionService.formatMoney(price));
            dto.setOriginalSubtotalFormatted(promotionService.formatMoney(subtotal));
            dto.setSubtotalFormatted(promotionService.formatMoney(subtotal));
            dto.setPromotionApplied(false);
            dto.setPromotionLabel("");
            dto.setBookStockQuantity(book.getStockQuantity());
            dto.setBookActive(book.isActive());
            return dto;
        }

        VppItem vppItem = item.getVppItem();
        if (vppItem != null) {
            BigDecimal price = vppItem.getPrice() == null ? BigDecimal.ZERO : vppItem.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            dto.setBookId(vppItem.getId());
            dto.setBookTitle(vppItem.getName());
            dto.setBookAuthor("Stationery");
            dto.setBookImageUrl("/uploads/vpp/" + vppItem.getId() + "/image");
            dto.setBookPriceFormatted(promotionService.formatMoney(price));
            dto.setOriginalPriceFormatted(promotionService.formatMoney(price));
            dto.setEffectivePriceFormatted(promotionService.formatMoney(price));
            dto.setOriginalSubtotalFormatted(promotionService.formatMoney(subtotal));
            dto.setSubtotalFormatted(promotionService.formatMoney(subtotal));
            dto.setPromotionApplied(false);
            dto.setPromotionLabel("");
            dto.setBookStockQuantity(vppItem.getStockQuantity() == null ? 0 : vppItem.getStockQuantity());
            dto.setBookActive(vppItem.isActive() && !vppItem.isDeleted());
            return dto;
        }

        dto.setBookTitle("Unavailable product");
        dto.setBookPriceFormatted("0");
        dto.setOriginalPriceFormatted("0");
        dto.setEffectivePriceFormatted("0");
        dto.setOriginalSubtotalFormatted("0");
        dto.setSubtotalFormatted("0");
        dto.setPromotionApplied(false);
        dto.setPromotionLabel("");
        dto.setBookStockQuantity(0);
        dto.setBookActive(false);
        return dto;
    }

    private Cart getOrCreateCart(long userId) {

        return cartRepository
                .findByUserIdWithItems(userId)
                .orElseGet(() -> {

                    User user = userRepository
                            .findById(userId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException("User not found.")
                            );

                    Cart cart = new Cart();

                    cart.setUser(user);
                    cart.setTotalAmount(BigDecimal.ZERO);

                    return cartRepository.save(cart);
                });
    }

    private void validateBookForCart(Book book, int quantity) {

        if (!book.isActive()) {
            throw new IllegalArgumentException(MSG_INACTIVE);
        }

        if (book.getStockQuantity() <= 0) {
            throw new IllegalArgumentException(MSG_OUT_OF_STOCK);
        }

        if (quantity > book.getStockQuantity()) {
            throw new IllegalArgumentException(
                    String.format(MSG_EXCEED_STOCK, book.getStockQuantity())
            );
        }

        if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Book price is invalid.");
        }
    }

    private void validateVppForCart(VppItem item, int quantity) {

        if (item.isDeleted() || !item.isActive()) {
            throw new IllegalArgumentException(MSG_INACTIVE);
        }

        if (item.getStockQuantity() == null || item.getStockQuantity() <= 0) {
            throw new IllegalArgumentException(MSG_OUT_OF_STOCK);
        }

        if (quantity > item.getStockQuantity()) {
            throw new IllegalArgumentException(
                    String.format(MSG_EXCEED_STOCK, item.getStockQuantity())
            );
        }

        if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Stationery item price is invalid.");
        }
    }

    private void validateQuantity(int quantity) {

        if (quantity < 1) {
            throw new IllegalArgumentException(MSG_QUANTITY_MIN);
        }
    }

    public Integer parsePositiveIntegerQuantity(String raw) {

        if (raw == null || raw.isBlank()) {
            return null;
        }

        String trimmed = raw.trim();

        if (!trimmed.matches("\\d+")) {
            return null;
        }

        try {

            long value = Long.parseLong(trimmed);

            if (value < 1 || value > Integer.MAX_VALUE) {
                return null;
            }

            return (int) value;

        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void recalculateTotal(Cart cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            if (cart != null) {
                cart.setTotalAmount(BigDecimal.ZERO);
            }
            return;
        }

        BigDecimal total = cart.getItems().stream()
                .filter(item -> item != null)
                .map(item -> {
                    BigDecimal unitPrice = BigDecimal.ZERO;
                    if (item.getBook() != null && item.getBook().getPrice() != null) {
                        unitPrice = item.getBook().getPrice();
                    } else if (item.getVppItem() != null && item.getVppItem().getPrice() != null) {
                        unitPrice = item.getVppItem().getPrice();
                    }
                    return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(total);
    }
}
