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
import shop.domain.User;
import shop.domain.dto.CartDTO;
import shop.domain.dto.CartItemDTO;
import shop.repository.BookRepository;
import shop.repository.CartItemRepository;
import shop.repository.CartRepository;
import shop.repository.UserRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            BookRepository bookRepository,
            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Cart getCartForUser(long userId) {
        return cartRepository.findByUserIdWithItems(userId).orElse(null);
    }

    @Transactional(readOnly = true)
    public int getCartItemCount(long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId).orElse(null);
        if (cart == null || cart.getItems().isEmpty()) {
            return 0;
        }
        return cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
    }

    @Transactional
    public Cart addBook(long userId, long bookId, int quantity) {
        validateQuantity(quantity);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách."));

        validateBookForCart(book, quantity);

        Cart cart = getOrCreateCart(userId);
        CartItem existing = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId).orElse(null);

        if (existing != null) {
            int newQuantity = existing.getQuantity() + quantity;
            if (newQuantity > book.getStockQuantity()) {
                throw new IllegalArgumentException(
                        "Số lượng vượt quá tồn kho (còn " + book.getStockQuantity() + " cuốn).");
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
    public Cart updateQuantity(long userId, long cartItemId, int quantity) {
        validateQuantity(quantity);

        CartItem item = cartItemRepository.findByIdAndCartUserId(cartItemId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm trong giỏ hàng."));

        Book book = bookRepository.findById(item.getBook().getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách."));

        validateBookForCart(book, quantity);

        if (quantity > book.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Số lượng vượt quá tồn kho (còn " + book.getStockQuantity() + " cuốn).");
        }

        item.setQuantity(quantity);
        Cart cart = item.getCart();
        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItem(long userId, long cartItemId) {
        CartItem item = cartItemRepository.findByIdAndCartUserId(cartItemId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm trong giỏ hàng."));

        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId).orElse(null);
        if (cart == null) {
            return;
        }
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Transactional(readOnly = true)
    public List<String> validateForCheckout(long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId).orElse(null);
        List<String> errors = new ArrayList<>();

        if (cart == null || cart.getItems().isEmpty()) {
            errors.add("Giỏ hàng phải có ít nhất một sản phẩm.");
            return errors;
        }

        for (CartItem item : cart.getItems()) {
            Book book = bookRepository.findById(item.getBook().getId()).orElse(null);
            if (book == null) {
                errors.add("Một sản phẩm trong giỏ không còn tồn tại.");
                continue;
            }
            if (!book.isActive()) {
                errors.add("\"" + book.getTitle() + "\" đã ngừng kinh doanh.");
            }
            if (book.getStockQuantity() <= 0) {
                errors.add("\"" + book.getTitle() + "\" đã hết hàng.");
            } else if (item.getQuantity() > book.getStockQuantity()) {
                errors.add("\"" + book.getTitle() + "\" chỉ còn " + book.getStockQuantity() + " cuốn trong kho.");
            }
            if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                errors.add("\"" + book.getTitle() + "\" có giá bán không hợp lệ.");
            }
        }

        return errors;
    }

    @Transactional
    public Cart refreshCart(long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId).orElse(null);
        if (cart == null) {
            return null;
        }

        List<CartItem> toRemove = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            Book book = bookRepository.findById(item.getBook().getId()).orElse(null);
            if (book == null || !book.isActive() || book.getStockQuantity() <= 0) {
                toRemove.add(item);
                continue;
            }
            if (item.getQuantity() > book.getStockQuantity()) {
                item.setQuantity(book.getStockQuantity());
            }
            item.setBook(book);
        }

        for (CartItem item : toRemove) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        }

        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    @Transactional
    public CartDTO refreshCartDTO(long userId) {
        return toCartDTO(refreshCart(userId));
    }

    public CartDTO toCartDTO(Cart cart) {
        if (cart == null || cart.getItems().isEmpty()) {
            return CartDTO.empty();
        }
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setTotalAmountFormatted(cart.getTotalAmountFormatted());
        dto.setItems(cart.getItems().stream()
                .map(this::toCartItemDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private CartItemDTO toCartItemDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotalFormatted(item.getSubtotalFormatted());

        Book book = item.getBook();
        if (book != null) {
            dto.setBookId(book.getId());
            dto.setBookTitle(book.getTitle());
            dto.setBookAuthor(book.getAuthor());
            dto.setBookImageUrl(book.getImageUrl());
            dto.setBookPriceFormatted(book.getPriceFormatted());
            dto.setBookStockQuantity(book.getStockQuantity());
            dto.setBookActive(book.isActive());
        }
        return dto;
    }

    private Cart getOrCreateCart(long userId) {
        return cartRepository.findByUserIdWithItems(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng."));
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    private void validateBookForCart(Book book, int quantity) {
        if (!book.isActive()) {
            throw new IllegalArgumentException("Sản phẩm đã ngừng kinh doanh, không thể thêm vào giỏ hàng.");
        }
        if (book.getStockQuantity() <= 0) {
            throw new IllegalArgumentException("Sản phẩm đã hết hàng, không thể thêm vào giỏ hàng.");
        }
        if (quantity > book.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Số lượng vượt quá tồn kho (còn " + book.getStockQuantity() + " cuốn).");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn hoặc bằng 1.");
        }
    }

    private void recalculateTotal(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(total);
    }
}
