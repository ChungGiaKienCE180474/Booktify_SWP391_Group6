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
import shop.domain.PromotionSelection;
import shop.domain.User;
import shop.domain.VppItem;
import shop.domain.dto.OrderDTO;
import shop.domain.dto.OrderItemDTO;
import shop.repository.BookRepository;
import shop.repository.OrderRepository;
import shop.repository.UserRepository;
import shop.repository.VppItemRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final VoucherService voucherService;
    private final PromotionService promotionService;
    private final VppItemRepository vppItemRepository;
    private final StockService stockService;

    public OrderService(
            OrderRepository orderRepository,
            BookRepository bookRepository,
            CartService cartService,
            UserRepository userRepository,
            VoucherService voucherService,
            PromotionService promotionService,
            VppItemRepository vppItemRepository,
            StockService stockService
    ) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.voucherService = voucherService;
        this.promotionService = promotionService;
        this.vppItemRepository = vppItemRepository;
        this.stockService = stockService;
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersForUser(
            long userId
    ) {
        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<OrderDTO> getOrderForUser(
            long userId,
            long orderId
    ) {
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
            String sort
    ) {
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
            String keyword
    ) {
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
            String keyword
    ) {
        return value != null
                && value.toLowerCase(Locale.ROOT)
                .contains(keyword);
    }

    private Comparator<Order> resolveSortComparator(
            String sort
    ) {
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
            Order order
    ) {
        return order.getUser() != null
                ? order.getUser().getFullName()
                : null;
    }

    @Transactional(readOnly = true)
    public Optional<OrderDTO> getOrderById(
            long orderId
    ) {
        return orderRepository
                .findByIdWithUserAndItems(orderId)
                .map(this::toDetailDTO);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersForCustomer(
            long customerUserId
    ) {
        return getOrdersForUser(
                customerUserId
        );
    }

    @Transactional
    public OrderDTO createOrderFromCart(
            long userId,
            CheckoutForm form
    ) {
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
                BigDecimal.ZERO;

        Order order =
                new Order();

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

        order.setShippingFee(
                shippingFee
        );

        BigDecimal subtotal =
                BigDecimal.ZERO;

        for (CartItem cartItem :
                cart.getItems()) {

            /*
             * ====================================================
             * VPP ITEM
             * ====================================================
             */

            if (cartItem.getBook() == null
                    && cartItem.getVppItem() != null) {

                VppItem vppItem =
                        vppItemRepository
                                .findById(
                                        cartItem
                                                .getVppItem()
                                                .getId()
                                )
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "A stationery item in your cart no longer exists."
                                        )
                                );

                validateVppLine(
                        vppItem,
                        cartItem.getQuantity()
                );

                BigDecimal unitPrice =
                        vppItem.getPrice();

                BigDecimal lineTotal =
                        unitPrice.multiply(
                                BigDecimal.valueOf(
                                        cartItem.getQuantity()
                                )
                        );

                subtotal =
                        subtotal.add(lineTotal);

                OrderItem orderItem =
                        new OrderItem();

                orderItem.setOrder(order);
                orderItem.setBook(null);
                orderItem.setVppItem(vppItem);

                orderItem.setBookTitle(
                        vppItem.getName()
                );

                orderItem.setUnitPrice(
                        unitPrice
                );

                orderItem.setQuantity(
                        cartItem.getQuantity()
                );

                orderItem.setLineTotal(
                        lineTotal
                );

                order.getItems().add(
                        orderItem
                );

                continue;
            }

            /*
             * ====================================================
             * BOOK
             * ====================================================
             */

            if (cartItem.getBook() == null) {

                throw new IllegalArgumentException(
                        "An item in your cart is invalid."
                );
            }

            Book book =
                    bookRepository
                            .findById(
                                    cartItem
                                            .getBook()
                                            .getId()
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

            PromotionSelection selectedPromotion =
                    form.resolvePromotionSelection(
                            book.getId()
                    );

            BigDecimal effectiveUnitPrice =
                    promotionService
                            .getPriceForSelection(
                                    book,
                                    selectedPromotion
                            );

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
            orderItem.setVppItem(null);

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

            order.getItems().add(
                    orderItem
            );
        }

        order.setSubtotal(
                subtotal
        );

        BigDecimal discountAmount =
                resolveDiscount(
                        form.getVoucherCode(),
                        subtotal
                );

        order.setDiscountAmount(
                discountAmount
        );

        order.setShippingFee(
                shippingFee
        );

        BigDecimal total =
                subtotal.subtract(
                        discountAmount
                );

        if (total.compareTo(
                BigDecimal.ZERO
        ) < 0) {
            total =
                    BigDecimal.ZERO;
        }

        order.setTotalAmount(
                total
        );

        Order savedOrder =
                orderRepository.save(order);

        /*
         * Bảng stock là nguồn số lượng chính.
         *
         * StockService sẽ:
         * - khóa dòng stock;
         * - kiểm tra số lượng;
         * - giảm stock.quantity;
         * - đồng bộ cột stock_quantity cũ tạm thời;
         * - ghi lịch sử SALE.
         *
         * Nếu giảm kho thất bại thì toàn bộ transaction,
         * bao gồm đơn hàng vừa tạo, sẽ rollback.
         */
        decreaseOrderStock(
                savedOrder
        );

        if (form.getVoucherCode() != null
                && !form.getVoucherCode().isBlank()) {

            voucherService
                    .decreaseVoucherQuantity(
                            form.getVoucherCode()
                    );
        }

        cartService.clearCart(
                userId
        );

        return toDetailDTO(
                savedOrder
        );
    }

    @Transactional
    public void updateOrderStatus(
            long orderId,
            String newStatus
    ) {
        Order order =
                orderRepository
                        .findByIdWithUserAndItems(orderId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Order not found."
                                )
                        );

        OrderStatus targetStatus =
                OrderStatus.fromValue(
                        newStatus
                );

        OrderStatus currentStatus =
                OrderStatus.fromValue(
                        order.getStatus()
                );

        if (targetStatus == currentStatus) {
            return;
        }

        if (!currentStatus.canTransitionTo(
                targetStatus
        )) {
            throw new IllegalArgumentException(
                    "Cannot change order status from "
                            + currentStatus.getLabel()
                            + " to "
                            + targetStatus.getLabel()
                            + "."
            );
        }

        /*
         * Khi đơn bị hủy, hoàn lại hàng vào bảng stock.
         */
        if (targetStatus
                == OrderStatus.CANCELLED) {

            restoreOrderStock(
                    order
            );
        }

        order.setStatus(
                targetStatus.name()
        );

        orderRepository.save(
                order
        );
    }

    @Transactional
    public void cancelOrderForUser(
            long userId,
            long orderId
    ) {
        Order order =
                orderRepository
                        .findByIdAndUserIdWithItems(
                                orderId,
                                userId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Order not found."
                                )
                        );

        OrderStatus currentStatus =
                OrderStatus.fromValue(
                        order.getStatus()
                );

        if (!currentStatus.canBeCancelled()) {

            throw new IllegalArgumentException(
                    "Only pending orders can be cancelled."
            );
        }

        updateOrderStatus(
                orderId,
                OrderStatus.CANCELLED.name()
        );
    }

    /*
     * ============================================================
     * DECREASE STOCK WHEN ORDER IS CREATED
     * ============================================================
     */

    private void decreaseOrderStock(
            Order order
    ) {
        for (OrderItem item :
                order.getItems()) {

            if (item.getVppItem() != null) {

                stockService
                        .decreaseVppForSale(
                                item
                                        .getVppItem()
                                        .getId(),
                                item.getQuantity(),
                                order.getId(),
                                order.getOrderCode()
                        );

                continue;
            }

            if (item.getBook() != null) {

                stockService
                        .decreaseBookForSale(
                                item
                                        .getBook()
                                        .getId(),
                                item.getQuantity(),
                                order.getId(),
                                order.getOrderCode()
                        );
            }
        }
    }

    /*
     * ============================================================
     * RESTORE STOCK WHEN ORDER IS CANCELLED
     * ============================================================
     */

    private void restoreOrderStock(
            Order order
    ) {
        for (OrderItem item :
                order.getItems()) {

            if (item.getVppItem() != null) {

                stockService
                        .restoreVppForCancelledOrder(
                                item
                                        .getVppItem()
                                        .getId(),
                                item.getQuantity(),
                                order.getId(),
                                order.getOrderCode()
                        );

                continue;
            }

            if (item.getBook() != null) {

                stockService
                        .restoreBookForCancelledOrder(
                                item
                                        .getBook()
                                        .getId(),
                                item.getQuantity(),
                                order.getId(),
                                order.getOrderCode()
                        );
            }
        }
    }

    public void validateCheckoutForm(
            CheckoutForm form
    ) {
        if (form == null) {

            throw new IllegalArgumentException(
                    "Checkout information is required."
            );
        }

        String voucherCode =
                form.getVoucherCode();

        if (voucherCode == null
                || voucherCode.isBlank()) {

            return;
        }

        voucherCode =
                voucherCode.trim();

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
            Cart cart
    ) {
        List<String> errors =
                new ArrayList<>();

        if (cart == null
                || cart.getItems().isEmpty()) {

            errors.add(
                    "The cart must contain at least one item."
            );

            return errors;
        }

        int validItemCount =
                0;

        for (CartItem item :
                cart.getItems()) {

            /*
             * ====================================================
             * VALIDATE VPP
             * ====================================================
             */

            if (item.getBook() == null
                    && item.getVppItem() != null) {

                VppItem vppItem =
                        vppItemRepository
                                .findById(
                                        item
                                                .getVppItem()
                                                .getId()
                                )
                                .orElse(null);

                if (vppItem == null) {

                    errors.add(
                            "A stationery item in your cart no longer exists."
                    );

                    continue;
                }

                if (vppItem.isDeleted()
                        || !vppItem.isActive()) {

                    errors.add(
                            "\""
                                    + vppItem.getName()
                                    + "\" is no longer available."
                    );

                    continue;
                }

                int availableStock =
                        stockService
                                .getVppQuantity(
                                        vppItem.getId()
                                );

                if (availableStock <= 0) {

                    errors.add(
                            "\""
                                    + vppItem.getName()
                                    + "\" — "
                                    + CartService.MSG_OUT_OF_STOCK
                    );

                    continue;
                }

                if (item.getQuantity()
                        > availableStock) {

                    errors.add(
                            "\""
                                    + vppItem.getName()
                                    + "\" — "
                                    + String.format(
                                    CartService.MSG_EXCEED_STOCK,
                                    availableStock
                            )
                    );

                    continue;
                }

                if (vppItem.getPrice() == null
                        || vppItem.getPrice()
                        .compareTo(BigDecimal.ZERO) < 0) {

                    errors.add(
                            "\""
                                    + vppItem.getName()
                                    + "\" has an invalid selling price."
                    );

                    continue;
                }

                validItemCount++;

                continue;
            }

            /*
             * ====================================================
             * VALIDATE BOOK
             * ====================================================
             */

            if (item.getBook() == null) {

                errors.add(
                        "An item in your cart is invalid."
                );

                continue;
            }

            Book book =
                    bookRepository
                            .findById(
                                    item
                                            .getBook()
                                            .getId()
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

            int availableStock =
                    stockService
                            .getBookQuantity(
                                    book.getId()
                            );

            if (availableStock <= 0) {

                errors.add(
                        "\""
                                + book.getTitle()
                                + "\" — "
                                + CartService.MSG_OUT_OF_STOCK
                );

                continue;
            }

            if (item.getQuantity()
                    > availableStock) {

                errors.add(
                        "\""
                                + book.getTitle()
                                + "\" — "
                                + String.format(
                                CartService.MSG_EXCEED_STOCK,
                                availableStock
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
            User user
    ) {
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

    public String getCheckoutTotalFormatted(
            BigDecimal subtotal,
            BigDecimal discountAmount
    ) {
        BigDecimal base =
                subtotal == null
                        ? BigDecimal.ZERO
                        : subtotal;

        BigDecimal discount =
                discountAmount == null
                        ? BigDecimal.ZERO
                        : discountAmount;

        BigDecimal total =
                base.subtract(
                        discount
                );

        if (total.compareTo(
                BigDecimal.ZERO
        ) < 0) {
            total =
                    BigDecimal.ZERO;
        }

        return formatMoney(
                total
        );
    }

    private void validateBookLine(
            Book book,
            int quantity
    ) {
        if (!book.isActive()) {

            throw new IllegalArgumentException(
                    "\""
                            + book.getTitle()
                            + "\" is no longer available."
            );
        }

        /*
         * Đọc tồn kho từ bảng stock.
         */
        int availableStock =
                stockService
                        .getBookQuantity(
                                book.getId()
                        );

        if (availableStock <= 0) {

            throw new IllegalArgumentException(
                    "\""
                            + book.getTitle()
                            + "\" — "
                            + CartService.MSG_OUT_OF_STOCK
            );
        }

        if (quantity > availableStock) {

            throw new IllegalArgumentException(
                    "\""
                            + book.getTitle()
                            + "\" — "
                            + String.format(
                            CartService.MSG_EXCEED_STOCK,
                            availableStock
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

    private void validateVppLine(
            VppItem item,
            int quantity
    ) {
        if (item.isDeleted()
                || !item.isActive()) {

            throw new IllegalArgumentException(
                    "\""
                            + item.getName()
                            + "\" is no longer available."
            );
        }

        /*
         * Đọc tồn kho từ bảng stock.
         */
        int availableStock =
                stockService
                        .getVppQuantity(
                                item.getId()
                        );

        if (availableStock <= 0) {

            throw new IllegalArgumentException(
                    "\""
                            + item.getName()
                            + "\" — "
                            + CartService.MSG_OUT_OF_STOCK
            );
        }

        if (quantity > availableStock) {

            throw new IllegalArgumentException(
                    "\""
                            + item.getName()
                            + "\" — "
                            + String.format(
                            CartService.MSG_EXCEED_STOCK,
                            availableStock
                    )
            );
        }

        if (item.getPrice() == null
                || item.getPrice()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "\""
                            + item.getName()
                            + "\" has an invalid selling price."
            );
        }
    }

    private BigDecimal resolveDiscount(
            String voucherCode,
            BigDecimal subtotal
    ) {
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
                                DateTimeFormatter
                                        .ofPattern(
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
                    .existsByOrderCode(
                            orderCode
                    )) {

                return orderCode;
            }
        }

        throw new IllegalStateException(
                "A unique order code could not be generated. Please try again."
        );
    }

    private OrderDTO toSummaryDTO(
            Order order
    ) {
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
            Order order
    ) {
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
                    order
                            .getUser()
                            .getEmail()
            );

            dto.setCustomerName(
                    order
                            .getUser()
                            .getFullName()
            );
        }

        dto.setItems(
                order.getItems()
                        .stream()
                        .map(
                                this::toOrderItemDTO
                        )
                        .collect(
                                Collectors.toList()
                        )
        );

        return dto;
    }

    private OrderItemDTO toOrderItemDTO(
            OrderItem item
    ) {
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
                    item
                            .getBook()
                            .getId()
            );

            dto.setBookImageUrl(
                    item
                            .getBook()
                            .getImageUrl()
            );
        }

        if (item.getVppItem() != null) {

            dto.setBookId(
                    item
                            .getVppItem()
                            .getId()
            );

            dto.setBookImageUrl(
                    "/uploads/vpp/"
                            + item
                            .getVppItem()
                            .getId()
                            + "/image"
            );
        }

        return dto;
    }

    private String blankToNull(
            String value
    ) {
        if (value == null
                || value.isBlank()) {

            return null;
        }

        return value.trim();
    }

    public String formatMoney(
            BigDecimal amount
    ) {
        if (amount == null) {
            return "0";
        }

        return NumberFormat
                .getIntegerInstance(
                        Locale.GERMANY
                )
                .format(
                        amount.longValue()
                );
    }

    public long countAllOrders() {
        return orderRepository.count();
    }

    public long countOrdersByUser(
            long userId
    ) {
        return orderRepository
                .countByUserId(userId);
    }
}
