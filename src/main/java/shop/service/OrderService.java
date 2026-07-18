package shop.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import shop.domain.Book;
import shop.domain.Cart;
import shop.domain.CartItem;
import shop.domain.CheckoutForm;
import shop.domain.Order;
import shop.domain.OrderItem;
import shop.domain.OrderStatus;
import shop.domain.PaymentMethod;
import shop.domain.User;
import shop.domain.Voucher;
import shop.domain.dto.OrderDTO;
import shop.domain.dto.OrderItemDTO;
import shop.repository.BookRepository;
import shop.repository.OrderRepository;
import shop.repository.UserRepository;

@Service
public class OrderService {

    public static final BigDecimal COD_SHIPPING_FEE =
            new BigDecimal("30000");

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final VoucherService voucherService;
    private final PromotionService promotionService;

    public OrderService(
            OrderRepository orderRepository,
            BookRepository bookRepository,
            CartService cartService,
            UserRepository userRepository,
            VoucherService voucherService,
            PromotionService promotionService) {

        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.voucherService = voucherService;
        this.promotionService = promotionService;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersForUser(
            long userId) {

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<OrderDTO> getOrderForUser(
            long userId,
            long orderId) {

        return orderRepository
                .findByIdAndUserIdWithItems(
                        orderId,
                        userId
                )
                .map(this::toDetailDTO);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {

        return orderRepository
                .findAllWithUserAndItemsOrderByCreatedAtDesc()
                .stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> searchOrders(
            String keyword,
            String status,
            String sort) {

        List<Order> orders =
                orderRepository
                        .findAllWithUserAndItemsOrderByCreatedAtDesc();

        if (StringUtils.hasText(keyword)) {

            String normalizedKeyword =
                    keyword.trim()
                            .toLowerCase(Locale.ROOT);

            orders = orders.stream()
                    .filter(order ->
                            matchesKeyword(
                                    order,
                                    normalizedKeyword
                            )
                    )
                    .collect(Collectors.toList());
        }

        if (StringUtils.hasText(status)
                && !"all".equalsIgnoreCase(status)) {

            String normalizedStatus =
                    status.trim()
                            .toUpperCase(Locale.ROOT);

            orders = orders.stream()
                    .filter(order ->
                            normalizedStatus.equals(
                                    order.getStatus()
                            )
                    )
                    .collect(Collectors.toList());
        }

        orders.sort(
                resolveSortComparator(sort)
        );

        return orders.stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toList());
    }

    private boolean matchesKeyword(
            Order order,
            String keyword) {

        return containsIgnoreCase(
                order.getOrderCode(),
                keyword
        )
                || (
                order.getUser() != null
                        && (
                        containsIgnoreCase(
                                order.getUser().getFullName(),
                                keyword
                        )
                                || containsIgnoreCase(
                                order.getUser().getEmail(),
                                keyword
                        )
                )
        )
                || containsIgnoreCase(
                order.getRecipientName(),
                keyword
        )
                || containsIgnoreCase(
                order.getRecipientPhone(),
                keyword
        );
    }

    private boolean containsIgnoreCase(
            String value,
            String keyword) {

        return value != null
                && value.toLowerCase(Locale.ROOT)
                .contains(keyword);
    }

    private Comparator<Order> resolveSortComparator(
            String sort) {

        if (sort == null) {
            sort = "default";
        }

        switch (sort) {

            case "oldest":

                return Comparator.comparing(
                        Order::getCreatedAt,
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                );

            case "id_asc":

                return Comparator.comparing(
                        Order::getId
                );

            case "id_desc":

                return Comparator.comparing(
                        Order::getId
                ).reversed();

            case "total_asc":

                return Comparator.comparing(
                        Order::getTotalAmount,
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                );

            case "total_desc":

                return Comparator.comparing(
                        Order::getTotalAmount,
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                ).reversed();

            case "code_asc":

                return Comparator.comparing(
                        Order::getOrderCode,
                        Comparator.nullsLast(
                                String::compareToIgnoreCase
                        )
                );

            case "code_desc":

                return Comparator.comparing(
                        Order::getOrderCode,
                        Comparator.nullsLast(
                                String::compareToIgnoreCase
                        )
                ).reversed();

            case "customer_asc":

                return Comparator.comparing(
                        this::getCustomerFullName,
                        Comparator.nullsLast(
                                String.CASE_INSENSITIVE_ORDER
                        )
                );

            case "customer_desc":

                return Comparator.comparing(
                        this::getCustomerFullName,
                        Comparator.nullsLast(
                                String.CASE_INSENSITIVE_ORDER
                        )
                ).reversed();

            case "default":
            default:

                return Comparator.comparing(
                        Order::getCreatedAt,
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                ).reversed();
        }
    }

    private String getCustomerFullName(
            Order order) {

        return order.getUser() != null
                ? order.getUser().getFullName()
                : null;
    }

    @Transactional(readOnly = true)
    public Optional<OrderDTO> getOrderById(
            long orderId) {

        return orderRepository
                .findByIdWithUserAndItems(orderId)
                .map(this::toDetailDTO);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersForCustomer(
            long customerUserId) {

        return getOrdersForUser(
                customerUserId
        );
    }

    @Transactional
    public OrderDTO createOrderFromCart(
            long userId,
            CheckoutForm form) {

        validateCheckoutForm(form);

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."
                        )
                );

        /*
         * Refresh the cart before creating the order so current promotions,
         * stock quantities, active statuses, and prices are applied.
         */
        cartService.refreshCart(userId);

        Cart cart =
                cartService.getCartForUser(userId);

        if (cart == null
                || cart.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "An order cannot be created from an empty cart."
            );
        }

        List<String> validationErrors =
                validateCartItemsForOrder(cart);

        if (!validationErrors.isEmpty()) {

            throw new IllegalArgumentException(
                    String.join(
                            " ",
                            validationErrors
                    )
            );
        }

        BigDecimal shippingFee =
                COD_SHIPPING_FEE;

        Order order = new Order();

        order.setOrderCode(
                generateUniqueOrderCode()
        );

        order.setUser(user);

        order.setRecipientName(
                form.getRecipientName().trim()
        );

        order.setRecipientPhone(
                form.getRecipientPhone().trim()
        );

        order.setShippingAddress(
                form.getShippingAddress().trim()
        );

        order.setPaymentMethod(
                PaymentMethod.COD.name()
        );

        order.setShippingMethod(
                "STANDARD"
        );

        order.setStatus(
                OrderStatus.PENDING.name()
        );

        order.setVoucherCode(
                blankToNull(
                        form.getVoucherCode()
                )
        );

        order.setNote(
                blankToNull(
                        form.getNote()
                )
        );

        order.setShippingFee(shippingFee);

        BigDecimal subtotal =
                BigDecimal.ZERO;

        for (CartItem cartItem :
                cart.getItems()) {

            Book book = bookRepository
                    .findById(
                            cartItem.getBook().getId()
                    )
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "An item in your cart no longer exists."
                            )
                    );

            validateBookLine(
                    book,
                    cartItem.getQuantity()
            );

            /*
             * Use the promotion price that is active when the order is
             * created.
             */
            BigDecimal effectiveUnitPrice =
                    promotionService
                            .getEffectivePrice(book);

            BigDecimal lineTotal =
                    effectiveUnitPrice.multiply(
                            BigDecimal.valueOf(
                                    cartItem.getQuantity()
                            )
                    );

            subtotal =
                    subtotal.add(lineTotal);

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(order);
            orderItem.setBook(book);

            /*
             * Store the book title and effective unit price directly in the
             * order item. Order history will therefore keep the purchased
             * price even after a promotion expires or is disabled.
             */
            orderItem.setBookTitle(
                    book.getTitle()
            );

            orderItem.setUnitPrice(
                    effectiveUnitPrice
            );

            orderItem.setQuantity(
                    cartItem.getQuantity()
            );

            orderItem.setLineTotal(
                    lineTotal
            );

            order.getItems().add(orderItem);

            book.setStockQuantity(
                    book.getStockQuantity()
                            - cartItem.getQuantity()
            );

            bookRepository.save(book);
        }

        order.setSubtotal(subtotal);

        BigDecimal discountAmount =
                resolveDiscount(
                        form.getVoucherCode(),
                        subtotal
                );

        BigDecimal finalShippingFee =
                shippingFee;

        if (form.getVoucherCode() != null
                && !form.getVoucherCode().isBlank()) {

            Voucher voucher =
                    voucherService.findValidVoucher(
                            form.getVoucherCode()
                    );

            if ("FREESHIP".equals(
                    voucher.getDiscountType())) {

                finalShippingFee =
                        BigDecimal.ZERO;
            }
        }

        order.setDiscountAmount(
                discountAmount
        );

        order.setShippingFee(
                finalShippingFee
        );

        BigDecimal total =
                subtotal
                        .subtract(discountAmount)
                        .add(finalShippingFee);

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        order.setTotalAmount(total);

        Order savedOrder =
                orderRepository.save(order);

        /*
         * Reduce the available voucher quantity only after the order has been
         * created successfully.
         */
        if (form.getVoucherCode() != null
                && !form.getVoucherCode().isBlank()) {

            voucherService
                    .decreaseVoucherQuantity(
                            form.getVoucherCode()
                    );
        }

        cartService.clearCart(userId);

        return toDetailDTO(savedOrder);
    }

    @Transactional
    public void updateOrderStatus(
            long orderId,
            String newStatus) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found."
                        )
                );

        try {

            OrderStatus.valueOf(newStatus);

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "Invalid order status."
            );
        }

        order.setStatus(newStatus);

        orderRepository.save(order);
    }

    public void validateCheckoutForm(
            CheckoutForm form) {

        String voucherCode =
                form.getVoucherCode();

        if (voucherCode == null
                || voucherCode.isBlank()) {

            return;
        }

        /*
         * Remove leading and trailing spaces.
         */
        voucherCode = voucherCode.trim();

        /*
         * Only one voucher may be used for each order.
         */
        if (voucherCode.contains(",")
                || voucherCode.contains(" ")) {

            throw new IllegalArgumentException(
                    "Only one voucher may be applied to each order."
            );
        }

        voucherService.findValidVoucher(
                voucherCode
        );
    }

    public List<String> validateCartItemsForOrder(
            Cart cart) {

        List<String> errors =
                new ArrayList<>();

        if (cart == null
                || cart.getItems().isEmpty()) {

            errors.add(
                    "The cart must contain at least one item."
            );

            return errors;
        }

        int validItemCount = 0;

        for (CartItem item : cart.getItems()) {

            Book book = bookRepository
                    .findById(
                            item.getBook().getId()
                    )
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

                continue;
            }

            if (book.getStockQuantity() <= 0) {

                errors.add(
                        "\""
                                + book.getTitle()
                                + "\" — "
                                + CartService.MSG_OUT_OF_STOCK
                );

                continue;
            }

            if (item.getQuantity()
                    > book.getStockQuantity()) {

                errors.add(
                        "\""
                                + book.getTitle()
                                + "\" — "
                                + String.format(
                                CartService.MSG_EXCEED_STOCK,
                                book.getStockQuantity()
                        )
                );

                continue;
            }

            if (book.getPrice() == null
                    || book.getPrice()
                    .compareTo(BigDecimal.ZERO) < 0) {

                errors.add(
                        "\""
                                + book.getTitle()
                                + "\" has an invalid selling price."
                );

                continue;
            }

            validItemCount++;
        }

        if (validItemCount == 0) {

            errors.add(
                    "The order must contain at least one valid item."
            );
        }

        return errors;
    }

    public CheckoutForm buildCheckoutFormFromUser(
            User user) {

        CheckoutForm form =
                new CheckoutForm();

        if (user != null) {

            form.setRecipientName(
                    user.getFullName()
            );

            form.setRecipientPhone(
                    user.getPhone()
            );

            form.setShippingAddress(
                    user.getAddress()
            );
        }

        return form;
    }

    public String getCodShippingFeeFormatted() {

        return formatMoney(
                COD_SHIPPING_FEE
        );
    }

    public String getCheckoutTotalFormatted(
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal shippingFee) {

        BigDecimal base =
                subtotal == null
                        ? BigDecimal.ZERO
                        : subtotal;

        BigDecimal discount =
                discountAmount == null
                        ? BigDecimal.ZERO
                        : discountAmount;

        BigDecimal shipping =
                shippingFee == null
                        ? BigDecimal.ZERO
                        : shippingFee;

        BigDecimal total =
                base.subtract(discount)
                        .add(shipping);

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        return formatMoney(total);
    }

    private void validateBookLine(
            Book book,
            int quantity) {

        if (!book.isActive()) {

            throw new IllegalArgumentException(
                    "\""
                            + book.getTitle()
                            + "\" is no longer available."
            );
        }

        if (book.getStockQuantity() <= 0) {

            throw new IllegalArgumentException(
                    "\""
                            + book.getTitle()
                            + "\" — "
                            + CartService.MSG_OUT_OF_STOCK
            );
        }

        if (quantity > book.getStockQuantity()) {

            throw new IllegalArgumentException(
                    "\""
                            + book.getTitle()
                            + "\" — "
                            + String.format(
                            CartService.MSG_EXCEED_STOCK,
                            book.getStockQuantity()
                    )
            );
        }

        if (book.getPrice() == null
                || book.getPrice()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "\""
                            + book.getTitle()
                            + "\" has an invalid selling price."
            );
        }
    }

    private BigDecimal resolveDiscount(
            String voucherCode,
            BigDecimal subtotal) {

        if (voucherCode == null
                || voucherCode.isBlank()) {

            return BigDecimal.ZERO;
        }

        return voucherService
                .calculateDiscount(
                        voucherCode,
                        subtotal
                );
    }

    private String generateUniqueOrderCode() {

        String datePart =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "yyyyMMdd"
                                )
                        );

        for (int attempt = 0;
             attempt < 20;
             attempt++) {

            int suffix =
                    ThreadLocalRandom
                            .current()
                            .nextInt(
                                    100000,
                                    999999
                            );

            String orderCode =
                    "ORD-"
                            + datePart
                            + "-"
                            + suffix;

            if (!orderRepository
                    .existsByOrderCode(orderCode)) {

                return orderCode;
            }
        }

        throw new IllegalStateException(
                "A unique order code could not be generated. Please try again."
        );
    }

    private OrderDTO toSummaryDTO(
            Order order) {

        OrderDTO dto =
                new OrderDTO();

        dto.setId(
                order.getId()
        );

        dto.setOrderCode(
                order.getOrderCode()
        );

        dto.setStatus(
                order.getStatus()
        );

        dto.setStatusLabel(
                order.getStatusLabel()
        );

        dto.setTotalAmountFormatted(
                order.getTotalAmountFormatted()
        );

        dto.setCreatedAtFormatted(
                order.getCreatedAtFormatted()
        );

        dto.setRecipientName(
                order.getRecipientName()
        );

        return dto;
    }

    private OrderDTO toDetailDTO(
            Order order) {

        OrderDTO dto =
                toSummaryDTO(order);

        dto.setRecipientPhone(
                order.getRecipientPhone()
        );

        dto.setShippingAddress(
                order.getShippingAddress()
        );

        dto.setPaymentMethodLabel(
                order.getPaymentMethodLabel()
        );

        dto.setVoucherCode(
                order.getVoucherCode()
        );

        dto.setSubtotalFormatted(
                order.getSubtotalFormatted()
        );

        dto.setDiscountAmountFormatted(
                order.getDiscountAmountFormatted()
        );

        dto.setShippingFeeFormatted(
                order.getShippingFeeFormatted()
        );

        dto.setNote(
                order.getNote()
        );

        if (order.getUser() != null) {

            dto.setCustomerEmail(
                    order.getUser().getEmail()
            );

            dto.setCustomerName(
                    order.getUser().getFullName()
            );
        }

        dto.setItems(
                order.getItems()
                        .stream()
                        .map(this::toOrderItemDTO)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    private OrderItemDTO toOrderItemDTO(
            OrderItem item) {

        OrderItemDTO dto =
                new OrderItemDTO();

        dto.setQuantity(
                item.getQuantity()
        );

        dto.setBookTitle(
                item.getBookTitle()
        );

        dto.setUnitPriceFormatted(
                item.getUnitPriceFormatted()
        );

        dto.setLineTotalFormatted(
                item.getLineTotalFormatted()
        );

        if (item.getBook() != null) {

            dto.setBookId(
                    item.getBook().getId()
            );

            dto.setBookImageUrl(
                    item.getBook().getImageUrl()
            );
        }

        return dto;
    }

    private String blankToNull(
            String value) {

        if (value == null
                || value.isBlank()) {

            return null;
        }

        return value.trim();
    }

    public String formatMoney(
            BigDecimal amount) {

        if (amount == null) {
            return "0";
        }

        return NumberFormat
                .getIntegerInstance(Locale.GERMANY)
                .format(amount.longValue());
    }

    public long countAllOrders() {

        return orderRepository.count();
    }

    public long countOrdersByUser(
            long userId) {

        return orderRepository
                .countByUserId(userId);
    }
}