package shop.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shop.domain.Book;
import shop.domain.Cart;
import shop.domain.CartItem;
import shop.domain.CheckoutForm;
import shop.domain.Order;
import shop.domain.OrderItem;
import shop.domain.OrderStatus;
import shop.domain.PaymentMethod;
import shop.domain.User;
import shop.domain.dto.CartDTO;
import shop.domain.dto.OrderDTO;
import shop.domain.dto.OrderItemDTO;
import shop.repository.BookRepository;
import shop.repository.OrderRepository;
import shop.repository.UserRepository;

@Service
public class OrderService {

    public static final BigDecimal COD_SHIPPING_FEE = new BigDecimal("30000");

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final CartService cartService;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        BookRepository bookRepository,
                        CartService cartService,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersForUser(long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<OrderDTO> getOrderForUser(long userId, long orderId) {
        return orderRepository.findByIdAndUserIdWithItems(orderId, userId)
                .map(this::toDetailDTO);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAllWithUserAndItemsOrderByCreatedAtDesc().stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<OrderDTO> getOrderById(long orderId) {
        return orderRepository.findByIdWithUserAndItems(orderId)
                .map(this::toDetailDTO);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersForCustomer(long customerUserId) {
        return getOrdersForUser(customerUserId);
    }

    @Transactional
    public OrderDTO createOrderFromCart(long userId, CheckoutForm form) {
        validateCheckoutForm(form);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng."));

        cartService.refreshCart(userId);
        Cart cart = cartService.getCartForUser(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Không thể tạo đơn hàng khi giỏ hàng trống.");
        }

        List<String> validationErrors = validateCartItemsForOrder(cart);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" ", validationErrors));
        }

        BigDecimal discountAmount = resolveDiscount(form.getVoucherCode());
        BigDecimal shippingFee = COD_SHIPPING_FEE;

        Order order = new Order();
        order.setOrderCode(generateUniqueOrderCode());
        order.setUser(user);
        order.setRecipientName(form.getRecipientName().trim());
        order.setRecipientPhone(form.getRecipientPhone().trim());
        order.setShippingAddress(form.getShippingAddress().trim());
        order.setPaymentMethod(PaymentMethod.COD.name());
        order.setShippingMethod("STANDARD");
        order.setStatus(OrderStatus.PENDING.name());
        order.setVoucherCode(blankToNull(form.getVoucherCode()));
        order.setNote(blankToNull(form.getNote()));
        order.setDiscountAmount(discountAmount);
        order.setShippingFee(shippingFee);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            Book book = bookRepository.findById(cartItem.getBook().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm trong giỏ không còn tồn tại."));

            validateBookLine(book, cartItem.getQuantity());

            BigDecimal lineTotal = book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setBookTitle(book.getTitle());
            orderItem.setUnitPrice(book.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setLineTotal(lineTotal);
            order.getItems().add(orderItem);

            book.setStockQuantity(book.getStockQuantity() - cartItem.getQuantity());
            bookRepository.save(book);
        }

        order.setSubtotal(subtotal);
        BigDecimal total = subtotal.subtract(discountAmount).add(shippingFee);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        cartService.clearCart(userId);
        return toDetailDTO(saved);
    }

    @Transactional
    public void updateOrderStatus(long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng."));

        try {
            OrderStatus.valueOf(newStatus);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Trạng thái đơn hàng không hợp lệ.");
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    public void validateCheckoutForm(CheckoutForm form) {
        if (form.getVoucherCode() != null && !form.getVoucherCode().isBlank()) {
            resolveDiscount(form.getVoucherCode());
        }
    }

    public List<String> validateCartItemsForOrder(Cart cart) {
        List<String> errors = new ArrayList<>();
        if (cart == null || cart.getItems().isEmpty()) {
            errors.add("Giỏ hàng phải có ít nhất một sản phẩm.");
            return errors;
        }

        int validCount = 0;
        for (CartItem item : cart.getItems()) {
            Book book = bookRepository.findById(item.getBook().getId()).orElse(null);
            if (book == null) {
                errors.add("Một sản phẩm trong giỏ không còn tồn tại.");
                continue;
            }
            if (!book.isActive()) {
                errors.add("\"" + book.getTitle() + "\" đã ngừng kinh doanh.");
                continue;
            }
            if (book.getStockQuantity() <= 0) {
                errors.add("\"" + book.getTitle() + "\" — " + CartService.MSG_OUT_OF_STOCK);
                continue;
            }
            if (item.getQuantity() > book.getStockQuantity()) {
                errors.add("\"" + book.getTitle() + "\" — "
                        + String.format(CartService.MSG_EXCEED_STOCK, book.getStockQuantity()));
                continue;
            }
            if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                errors.add("\"" + book.getTitle() + "\" có giá bán không hợp lệ.");
                continue;
            }
            validCount++;
        }

        if (validCount == 0) {
            errors.add("Đơn hàng phải có ít nhất một sản phẩm hợp lệ.");
        }
        return errors;
    }

    public CheckoutForm buildCheckoutFormFromUser(User user) {
        CheckoutForm form = new CheckoutForm();
        if (user != null) {
            form.setRecipientName(user.getFullName());
            form.setRecipientPhone(user.getPhone());
            form.setShippingAddress(user.getAddress());
        }
        return form;
    }

    public String getCodShippingFeeFormatted() {
        return formatMoney(COD_SHIPPING_FEE);
    }

    public String getCheckoutTotalFormatted(BigDecimal subtotal) {
        BigDecimal base = subtotal == null ? BigDecimal.ZERO : subtotal;
        return formatMoney(base.add(COD_SHIPPING_FEE));
    }

    private void validateBookLine(Book book, int quantity) {
        if (!book.isActive()) {
            throw new IllegalArgumentException("\"" + book.getTitle() + "\" đã ngừng kinh doanh.");
        }
        if (book.getStockQuantity() <= 0) {
            throw new IllegalArgumentException("\"" + book.getTitle() + "\" — " + CartService.MSG_OUT_OF_STOCK);
        }
        if (quantity > book.getStockQuantity()) {
            throw new IllegalArgumentException("\"" + book.getTitle() + "\" — "
                    + String.format(CartService.MSG_EXCEED_STOCK, book.getStockQuantity()));
        }
        if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("\"" + book.getTitle() + "\" có giá bán không hợp lệ.");
        }
    }

    private BigDecimal resolveDiscount(String voucherCode) {
        if (voucherCode == null || voucherCode.isBlank()) {
            return BigDecimal.ZERO;
        }
        throw new IllegalArgumentException("Mã voucher/khuyến mãi không hợp lệ hoặc chưa được hỗ trợ.");
    }

    private String generateUniqueOrderCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        for (int i = 0; i < 20; i++) {
            int suffix = ThreadLocalRandom.current().nextInt(100000, 999999);
            String code = "ORD-" + datePart + "-" + suffix;
            if (!orderRepository.existsByOrderCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Không thể tạo mã đơn hàng duy nhất. Vui lòng thử lại.");
    }

    private OrderDTO toSummaryDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderCode(order.getOrderCode());
        dto.setStatus(order.getStatus());
        dto.setStatusLabel(order.getStatusLabel());
        dto.setTotalAmountFormatted(order.getTotalAmountFormatted());
        dto.setCreatedAtFormatted(order.getCreatedAtFormatted());
        dto.setRecipientName(order.getRecipientName());
        return dto;
    }

    private OrderDTO toDetailDTO(Order order) {
        OrderDTO dto = toSummaryDTO(order);
        dto.setRecipientPhone(order.getRecipientPhone());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPaymentMethodLabel(order.getPaymentMethodLabel());
        dto.setVoucherCode(order.getVoucherCode());
        dto.setSubtotalFormatted(order.getSubtotalFormatted());
        dto.setDiscountAmountFormatted(order.getDiscountAmountFormatted());
        dto.setShippingFeeFormatted(order.getShippingFeeFormatted());
        dto.setNote(order.getNote());
        if (order.getUser() != null) {
            dto.setCustomerEmail(order.getUser().getEmail());
            dto.setCustomerName(order.getUser().getFullName());
        }
        dto.setItems(order.getItems().stream().map(this::toOrderItemDTO).collect(Collectors.toList()));
        return dto;
    }

    private OrderItemDTO toOrderItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setQuantity(item.getQuantity());
        dto.setBookTitle(item.getBookTitle());
        dto.setUnitPriceFormatted(item.getUnitPriceFormatted());
        dto.setLineTotalFormatted(item.getLineTotalFormatted());
        if (item.getBook() != null) {
            dto.setBookId(item.getBook().getId());
            dto.setBookImageUrl(item.getBook().getImageUrl());
        }
        return dto;
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return NumberFormat.getIntegerInstance(Locale.GERMANY).format(amount.longValue());
    }

    public long countAllOrders() {
        return orderRepository.count();
    }

    public long countOrdersByUser(long userId) {
        return orderRepository.countByUserId(userId);
    }
}
