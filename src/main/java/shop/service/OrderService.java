// Package service — logic nghiệp vụ đơn hàng (checkout, hủy đơn, quản lý stock)
package shop.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import shop.domain.Book;
import shop.domain.BookSet;
import shop.domain.BookSetItem;
import shop.domain.Cart;
import shop.domain.CartItem;
import shop.domain.CheckoutForm;
import shop.domain.Order;
import shop.domain.OrderItem;
import shop.domain.OrderStatus;
import shop.domain.PaymentMethod;
import shop.domain.PaymentStatus;
import shop.domain.PromotionSelection;
import shop.domain.User;
import shop.domain.dto.OrderDTO;
import shop.domain.dto.OrderItemDTO;
import shop.repository.BookRepository;
import shop.repository.OrderRepository;
import shop.repository.UserRepository;

/**
 * Service xử lý toàn bộ nghiệp vụ đơn hàng.
 * <p>
 * Chức năng chính:
 * <ul>
 *   <li>{@link #createOrderFromCart} — tạo đơn từ giỏ, trừ stock, xóa cart (@Transactional)</li>
 *   <li>{@link #cancelOrderForUser} — customer hủy đơn PENDING</li>
 *   <li>{@link #updateOrderStatus} — admin/staff chuyển trạng thái (state machine)</li>
 *   <li>{@link #searchOrders} — tìm kiếm/lọc cho admin/staff</li>
 * </ul>
 * Phụ thuộc: CartService, PromotionService, VoucherService, StockService.
 */
@Service
public class OrderService {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    /** Giỏ hàng: refresh, clear sau đặt hàng */
    private final CartService cartService;
    private final UserRepository userRepository;
    /** Tính và validate voucher, decreaseVoucherQuantity */
    private final VoucherService voucherService;
    /** Giá sau KM từng sách (PERCENTAGE/FIXED/NONE) */
    private final PromotionService promotionService;
    /** Giảm/hoàn tồn kho khi tạo/hủy đơn */
    private final StockService stockService;
    private final BookSetService bookSetService;

    // Inject các repository và service phụ thuộc
    public OrderService(
            OrderRepository orderRepository,
            BookRepository bookRepository,
            CartService cartService,
            UserRepository userRepository,
            VoucherService voucherService,
            PromotionService promotionService,
            StockService stockService,
            BookSetService bookSetService
    ) {
        this.orderRepository = orderRepository;       // CRUD bảng orders
        this.bookRepository = bookRepository;         // Load sách khi tạo OrderItem
        this.cartService = cartService;               // Refresh/clear giỏ hàng
        this.userRepository = userRepository;         // Load user đặt đơn
        this.voucherService = voucherService;         // Validate + tính voucher
        this.promotionService = promotionService;     // Giá sau KM từng sách
        this.stockService = stockService;             // Trừ/hoàn tồn kho
        this.bookSetService = bookSetService;
    }

    // Lấy danh sách đơn hàng của user — OrderController GET /orders
    @Transactional(readOnly = true) // Chỉ đọc DB, không ghi — tối ưu performance
    public List<OrderDTO> getOrdersForUser(
            long userId // ID customer đang đăng nhập
    ) {
        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(userId) // Query: đơn của user, mới nhất trước
                .stream() // Chuyển List<Order> thành Stream để map
                .map(this::toSummaryDTO) // Entity -> DTO tóm tắt (không có items)
                .collect(Collectors.toList()); // Thu thập lại thành List<OrderDTO>
    }

    // Lấy chi tiết 1 đơn thuộc user — OrderController GET /orders/{id}
    @Transactional(readOnly = true)
    public Optional<OrderDTO> getOrderForUser(
            long userId,   // Chỉ lấy đơn của user này — bảo mật
            long orderId   // ID đơn từ URL
    ) {
        return orderRepository
                .findByIdAndUserIdWithItems(orderId, userId) // JOIN FETCH items, book, vpp
                .map(this::toDetailDTO); // Có đơn -> map full detail; không -> Optional.empty()
    }

    // Lấy tất cả đơn — có thể dùng nội bộ admin (searchOrders dùng query tương tự)
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository
                .findAllWithUserAndItemsOrderByCreatedAtDesc() // All orders + user + items
                .stream()
                .map(this::toDetailDTO) // Full detail cho mỗi đơn
                .collect(Collectors.toList());
    }

    // Tìm kiếm đơn hàng theo keyword, status, sort — AdminOrderController / StaffOrderController
    @Transactional(readOnly = true)
    public List<OrderDTO> searchOrders(
            String keyword, // Mã đơn, tên KH, email, người nhận, SĐT (nullable)
            String status,  // PENDING, CONFIRMED... hoặc "all"
            String sort     // default, oldest, total_asc...
    ) {
        // Bước 1: Load toàn bộ đơn từ DB (kèm user + items)
        List<Order> orders =
                orderRepository
                        .findAllWithUserAndItemsOrderByCreatedAtDesc();

        // Bước 2: Lọc theo keyword nếu user nhập tìm kiếm
        if (StringUtils.hasText(keyword)) {

            String normalizedKeyword =
                    keyword.trim()                    // Bỏ khoảng trắng đầu/cuối
                            .toLowerCase(Locale.ROOT); // Lowercase để so không phân biệt hoa thường

            orders = orders.stream()
                    .filter(order ->
                            matchesKeyword(           // Giữ đơn khớp keyword
                                    order,
                                    normalizedKeyword
                            )
                    )
                    .collect(Collectors.toList());    // List mới đã lọc
        }

        // Bước 3: Lọc theo trạng thái nếu không phải "all"
        if (StringUtils.hasText(status)
                && !"all".equalsIgnoreCase(status)) {

            String normalizedStatus =
                    status.trim()
                            .toUpperCase(Locale.ROOT); // PENDING, CONFIRMED...

            orders = orders.stream()
                    .filter(order ->
                            normalizedStatus.equals(  // So khớp chính xác status
                                    order.getStatus()
                            )
                    )
                    .collect(Collectors.toList());
        }

        // Bước 4: Sắp xếp theo tham số sort (mặc định mới nhất trước)
        orders.sort(
                resolveSortComparator(sort)
        );

        // Bước 5: Map entity -> DTO và trả về
        return orders.stream()
                .map(this::toDetailDTO)
                .collect(Collectors.toList());
    }

    // Kiểm tra một Order có khớp từ khóa tìm kiếm không
    private boolean matchesKeyword(
            Order order,
            String keyword // Đã normalize lowercase
    ) {
        return containsIgnoreCase(
                order.getOrderCode(),  // Tìm theo mã đơn ORD-...
                keyword
        )
                || (
                order.getUser() != null  // Có thông tin customer
                        && (
                        containsIgnoreCase(
                                order.getUser().getFullName(), // Tên customer
                                keyword
                        )
                                || containsIgnoreCase(
                                order.getUser().getEmail(),    // Email customer
                                keyword
                        )
                )
        )
                || containsIgnoreCase(
                order.getRecipientName(),  // Tên người nhận trên đơn
                keyword
        )
                || containsIgnoreCase(
                order.getRecipientPhone(), // SĐT người nhận
                keyword
        );
    }

    // Helper: value có chứa keyword không (case insensitive)
    private boolean containsIgnoreCase(
            String value,
            String keyword
    ) {
        return value != null                              // value không null
                && value.toLowerCase(Locale.ROOT)       // lowercase value
                .contains(keyword);                       // substring match
    }

    // Trả Comparator sắp xếp đơn theo sort param (admin/staff list)
    private Comparator<Order> resolveSortComparator(
            String sort // Query param ?sort=... từ URL
    ) {
        if (sort == null) {
            sort = "default"; // Không truyền sort → dùng mặc định mới nhất trước
        }

        switch (sort) { // Chọn cách sắp xếp theo giá trị sort

            case "oldest":
                // Cũ nhất trước — createdAt tăng dần
                return Comparator.comparing(
                        Order::getCreatedAt, // So sánh theo ngày tạo
                        Comparator.nullsLast( // null createdAt xếp cuối
                                Comparator.naturalOrder() // LocalDateTime tăng dần
                        )
                );

            case "id_asc":
                // ID nhỏ → lớn
                return Comparator.comparing(
                        Order::getId // So sánh theo primary key
                );

            case "id_desc":
                // ID lớn → nhỏ (đơn mới tạo thường ID lớn hơn)
                return Comparator.comparing(
                        Order::getId
                ).reversed(); // Đảo thứ tự so sánh

            case "total_asc":
                // Tổng tiền thấp → cao
                return Comparator.comparing(
                        Order::getTotalAmount, // BigDecimal totalAmount
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                );

            case "total_desc":
                // Tổng tiền cao → thấp
                return Comparator.comparing(
                        Order::getTotalAmount,
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                ).reversed();

            case "code_asc":
                // Mã đơn A→Z (không phân biệt hoa thường)
                return Comparator.comparing(
                        Order::getOrderCode, // VD ORD-20260728-123456
                        Comparator.nullsLast(
                                String::compareToIgnoreCase
                        )
                );

            case "code_desc":
                // Mã đơn Z→A
                return Comparator.comparing(
                        Order::getOrderCode,
                        Comparator.nullsLast(
                                String::compareToIgnoreCase
                        )
                ).reversed();

            case "customer_asc":
                // Tên khách A→Z
                return Comparator.comparing(
                        this::getCustomerFullName, // Method reference lấy fullName từ order.user
                        Comparator.nullsLast(
                                String.CASE_INSENSITIVE_ORDER
                        )
                );

            case "customer_desc":
                // Tên khách Z→A
                return Comparator.comparing(
                        this::getCustomerFullName,
                        Comparator.nullsLast(
                                String.CASE_INSENSITIVE_ORDER
                        )
                ).reversed();

            case "default":
            default:
                // Mặc định: mới nhất trước — createdAt giảm dần
                return Comparator.comparing(
                        Order::getCreatedAt,
                        Comparator.nullsLast(
                                Comparator.naturalOrder()
                        )
                ).reversed(); // Đảo → mới nhất đầu danh sách
        }
    }

    // Lấy tên khách từ order.user — dùng cho sort customer_asc/desc
    private String getCustomerFullName(
            Order order // Entity đơn hàng
    ) {
        return order.getUser() != null // User có thể null nếu data lỗi
                ? order.getUser().getFullName() // Tên đầy đủ từ bảng users
                : null; // null → xếp cuối khi sort
    }

    @Transactional(readOnly = true)
    public Optional<OrderDTO> getOrderById(
            long orderId
    ) {
        // Không lọc user — admin/staff xem mọi đơn
        return orderRepository
                .findByIdWithUserAndItems(orderId)
                .map(this::toDetailDTO);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersForCustomer(
            long customerUserId
    ) {
        // Alias của getOrdersForUser — dùng từ AdminCustomerController
        return getOrdersForUser(
                customerUserId
        );
    }

    // Tạo đơn hàng từ giỏ — OrderController POST /orders/checkout
    // @Transactional: toàn bộ method trong 1 transaction — lỗi giữa chừng rollback hết
    @Transactional
    public OrderDTO createOrderFromCart(
            long userId,
            CheckoutForm form
    ) {
        // Kiểm tra voucher hợp lệ (nếu có) trước khi xử lý giỏ
        validateCheckoutForm(form);

        // Load user từ DB — throw nếu userId không tồn tại
        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."
                        )
                );

        /*
         * Refresh giỏ trước khi tạo đơn để áp dụng:
         * - khuyến mãi hiện tại
         * - tồn kho mới nhất
         * - trạng thái active của sản phẩm
         * - giá bán cập nhật
         */
        cartService.refreshCart(userId);

        // Lấy entity Cart sau refresh (đã sync với DB)
        Cart cart =
                cartService.getCartForUser(userId);

        // Không cho tạo đơn từ giỏ rỗng
        if (cart == null
                || cart.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "An order cannot be created from an empty cart."
            );
        }

        // Kiểm tra từng item: tồn tại, active, đủ stock, giá hợp lệ
        List<String> validationErrors =
                validateCartItemsForOrder(cart);

        // Gom tất cả lỗi validation thành 1 message
        if (!validationErrors.isEmpty()) {

            throw new IllegalArgumentException(
                    String.join(
                            " ",
                            validationErrors
                    )
            );
        }

        // Phí ship — hiện chưa tính phí, luôn 0
        BigDecimal shippingFee =
                BigDecimal.ZERO;

        // Khởi tạo entity Order (chưa save DB)
        Order order =
                new Order();

        // Sinh mã đơn unique dạng ORD-yyyyMMdd-xxxxxx
        order.setOrderCode(
                generateUniqueOrderCode()
        );

        order.setUser(user);

        // Thông tin giao hàng từ CheckoutForm — trim khoảng trắng thừa
        order.setRecipientName(
                form.getRecipientName().trim()
        );

        order.setRecipientPhone(
                form.getRecipientPhone().trim()
        );

        order.setShippingAddress(
                form.getShippingAddress().trim()
        );

        // Lưu tên/SĐT/địa chỉ vừa nhập làm mặc định cho tài khoản (tùy chọn)
        if (form.isSaveAddress()) {
            user.setFullName(form.getRecipientName().trim());
            user.setPhone(form.getRecipientPhone().trim());
            user.setAddress(form.getShippingAddress().trim());
            userRepository.save(user);
        }

        // Phương thức thanh toán từ form — mặc định COD
        PaymentMethod paymentMethod =
                form.resolvePaymentMethod();

        order.setPaymentMethod(
                paymentMethod.name()
        );

        // Vận chuyển tiêu chuẩn — chưa có lựa chọn khác trên form
        order.setShippingMethod(
                "STANDARD"
        );

        // Đơn mới luôn ở trạng thái PENDING — chờ admin/staff xác nhận
        order.setStatus(
                OrderStatus.PENDING.name()
        );

        // Lưu mã voucher (null nếu không dùng)
        order.setVoucherCode(
                blankToNull(
                        form.getVoucherCode()
                )
        );

        // Ghi chú tùy chọn từ customer
        order.setNote(
                blankToNull(
                        form.getNote()
                )
        );

        order.setShippingFee(
                shippingFee
        );

        // Biến cộng dồn tổng tiền hàng (sau KM từng dòng)
        BigDecimal subtotal =
                BigDecimal.ZERO;

        // Validate tồn kho gộp (sách lẻ + thành phần bộ) trước khi tạo dòng đơn
        validateAggregatedBookDemand(cart);

        // Duyệt từng item trong giỏ để tạo OrderItem
        for (CartItem cartItem :
                cart.getItems()) {

            if (cartItem.getBookSet() != null) {
                BookSet bookSet = bookSetService.getByIdWithItems(
                        cartItem.getBookSet().getId()
                );
                if (!bookSet.isActive()) {
                    throw new IllegalArgumentException(
                            "\"" + bookSet.getName() + "\" is no longer available."
                    );
                }

                int availableSets = bookSetService.getAvailableSetQuantity(bookSet);
                if (cartItem.getQuantity() > availableSets) {
                    throw new IllegalArgumentException(
                            "\"" + bookSet.getName() + "\" — "
                                    + String.format(
                                    CartService.MSG_EXCEED_STOCK,
                                    availableSets
                            )
                    );
                }

                Map<Long, BigDecimal> unitPrices =
                        bookSetService.allocateUnitPrices(bookSet);

                for (BookSetItem setItem : bookSet.getItems()) {
                    Book book = setItem.getBook();
                    int lineQty = cartItem.getQuantity() * setItem.getQuantity();
                    BigDecimal unitPrice = unitPrices.getOrDefault(
                            book.getId(),
                            BigDecimal.ZERO
                    );
                    BigDecimal lineTotal = unitPrice.multiply(
                            BigDecimal.valueOf(lineQty)
                    );
                    subtotal = subtotal.add(lineTotal);

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setBook(book);
                    orderItem.setVppItem(null);
                    orderItem.setBookTitle(book.getTitle());
                    orderItem.setUnitPrice(unitPrice);
                    orderItem.setQuantity(lineQty);
                    orderItem.setLineTotal(lineTotal);
                    orderItem.setBookSetId(bookSet.getId());
                    orderItem.setBookSetName(bookSet.getName());
                    order.getItems().add(orderItem);
                }
                continue;
            }

            if (cartItem.getBook() == null) {
                throw new IllegalArgumentException(
                        "An item in your cart is invalid. Please refresh your cart."
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

        // Tổng tiền hàng sau KM (trước voucher)
        order.setSubtotal(
                subtotal
        );

        // Tính giảm giá voucher trên subtotal
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

        // Tổng thanh toán = subtotal - voucher (không âm)
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

        // INSERT orders + order_items (cascade) vào DB — trả về entity có id generated
        Order savedOrder =
                orderRepository.save(order);

        boolean isVnPayOrder =
                paymentMethod == PaymentMethod.VNPAY;

        /*
         * VNPay: chờ thanh toán thành công mới trừ kho và giảm voucher.
         * COD: hoàn tất ngay sau khi tạo đơn.
         */
        if (!isVnPayOrder) {
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
        }

        cartService.clearCart(
                userId
        );

        // Map entity đã save -> OrderDTO trả về Controller (redirect /orders/{id})
        return toDetailDTO(
                savedOrder
        );
    }

    // Cập nhật trạng thái đơn — AdminOrderController / StaffOrderController POST .../status
    @Transactional
    public void updateOrderStatus(
            long orderId,
            String newStatus
    ) {
        // Load đơn kèm user + items (cần items nếu hủy để hoàn stock)
        Order order =
                orderRepository
                        .findByIdWithUserAndItems(orderId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Order not found."
                                )
                        );

        // Parse String form -> enum OrderStatus
        OrderStatus targetStatus =
                OrderStatus.fromValue(
                        newStatus
                );

        OrderStatus currentStatus =
                OrderStatus.fromValue(
                        order.getStatus()
                );

        // Không làm gì nếu trạng thái không đổi
        if (targetStatus == currentStatus) {
            return;
        }

        // State machine: vd PENDING->SHIPPING là invalid
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

        // VNPay đã thanh toán — không cho hủy đơn
        if (targetStatus == OrderStatus.CANCELLED
                && isPaidVnPayOrder(order)) {
            throw new IllegalArgumentException(
                    "Paid VNPay orders cannot be cancelled."
            );
        }

        // Chuyển sang CANCELLED -> hoàn hàng vào kho trước khi đổi status
        if (targetStatus
                == OrderStatus.CANCELLED) {

            if (shouldRestoreStockOnCancel(order)) {
                restoreOrderStock(
                        order
                );
            }
        }

        // VNPay chưa thanh toán — không cho admin/staff xác nhận thủ công
        if (targetStatus == OrderStatus.CONFIRMED
                && currentStatus == OrderStatus.PENDING
                && PaymentMethod.VNPAY.name().equals(order.getPaymentMethod())) {

            throw new IllegalArgumentException(
                    "VNPay orders can only be confirmed after successful online payment."
            );
        }

        // Cập nhật status và lưu — @PreUpdate set updatedAt
        order.setStatus(
                targetStatus.name()
        );

        orderRepository.save(
                order
        );
    }

    // User hủy đơn của mình — OrderController POST /orders/{id}/cancel
    @Transactional
    public void cancelOrderForUser(
            long userId,
            long orderId
    ) {
        // Chỉ lấy đơn thuộc userId — tránh hủy đơn người khác
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

        // Customer chỉ hủy được PENDING — canBeCancelled() == true
        if (!currentStatus.canBeCancelled()) {

            throw new IllegalArgumentException(
                    "Only pending orders can be cancelled."
            );
        }

        // Tái sử dụng updateOrderStatus -> CANCELLED + restoreOrderStock
        updateOrderStatus(
                orderId,
                OrderStatus.CANCELLED.name()
        );
    }

    /*
     * ============================================================
     * GIẢM TỒN KHO KHI TẠO ĐƠN
     * ============================================================
     */

    private void decreaseOrderStock(
            Order order
    ) {
        // Duyệt từng dòng đơn — trừ stock tương ứng
        for (OrderItem item :
                order.getItems()) {

            if (item.getVppItem() != null) {

                // Trừ tồn kho VPP + ghi lịch sử SALE (orderId, orderCode để audit)
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

                // Trừ tồn kho sách + ghi lịch sử SALE
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
     * HOÀN TỒN KHO KHI HỦY ĐƠN
     * ============================================================
     */

    private void restoreOrderStock(
            Order order
    ) {
        // Hoàn lại số lượng đã trừ khi đơn bị CANCELLED
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

    // Validate form checkout — OrderController gọi trước khi createOrderFromCart
    public void validateCheckoutForm(
            CheckoutForm form
    ) {
        if (form == null) {

            throw new IllegalArgumentException(
                    "Checkout information is required."
            );
        }

        if (!PaymentMethod.isValid(form.getPaymentMethod())) {
            throw new IllegalArgumentException(
                    "Invalid payment method."
            );
        }

        String voucherCode =
                form.getVoucherCode();

        // Không có voucher -> bỏ qua validate voucher
        if (voucherCode == null
                || voucherCode.isBlank()) {

            return;
        }

        voucherCode =
                voucherCode.trim();

        // Chỉ cho phép 1 voucher mỗi đơn — không nhập nhiều mã cách nhau dấu phẩy/space
        if (voucherCode.contains(",")
                || voucherCode.contains(" ")) {

            throw new IllegalArgumentException(
                    "Only one voucher may be applied to each order."
            );
        }

        // Ném exception nếu voucher hết hạn, hết lượt, hoặc không tồn tại
        voucherService.findValidVoucher(
                voucherCode
        );
    }

    /**
     * Các trạng thái tiếp theo hợp lệ — dùng dropdown Admin/Staff.
     * Đơn VNPay đã thanh toán không được phép chuyển sang CANCELLED.
     */
    public Set<OrderStatus> getAllowedNextStatuses(Order order) {
        return getAllowedNextStatuses(
                order.getStatus(),
                order.getPaymentMethod()
        );
    }

    /* ===================== Xác nhận thanh toán COD ===================== */

    /**
     * Khách được bấm "Tôi đã thanh toán" khi: đơn COD, đang giao hoặc đã giao
     * (SHIPPING/DELIVERED), và trạng thái thanh toán còn UNPAID.
     */
    public boolean canCustomerConfirmPayment(Order order) {
        if (!PaymentMethod.COD.name().equals(order.getPaymentMethod())) {
            return false;
        }
        if (PaymentStatus.fromValue(order.getPaymentStatus()) != PaymentStatus.UNPAID) {
            return false;
        }
        OrderStatus status = OrderStatus.fromValue(order.getStatus());
        return status == OrderStatus.SHIPPING || status == OrderStatus.DELIVERED;
    }

    /**
     * Admin được bấm "Hoàn thành" khi: đơn COD và khách đã báo đã thanh toán
     * (AWAITING_CONFIRMATION).
     */
    public boolean canAdminCompletePayment(Order order) {
        return PaymentMethod.COD.name().equals(order.getPaymentMethod())
                && PaymentStatus.fromValue(order.getPaymentStatus())
                        == PaymentStatus.AWAITING_CONFIRMATION;
    }

    /** Khách xác nhận đã thanh toán COD — UNPAID → AWAITING_CONFIRMATION. */
    @Transactional
    public void customerConfirmPayment(long userId, long orderId) {
        Order order = orderRepository
                .findByIdAndUserIdWithItems(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));

        if (!canCustomerConfirmPayment(order)) {
            throw new IllegalArgumentException(
                    "This order cannot be confirmed as paid at the moment."
            );
        }

        order.setPaymentStatus(PaymentStatus.AWAITING_CONFIRMATION.name());
        orderRepository.save(order);
    }

    /**
     * Admin hoàn thành đơn COD — AWAITING_CONFIRMATION → PAID (+ paidAt),
     * đồng thời đẩy đơn sang DELIVERED.
     */
    @Transactional
    public void adminCompletePayment(long orderId) {
        Order order = orderRepository
                .findByIdWithUserAndItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found."));

        if (!canAdminCompletePayment(order)) {
            throw new IllegalArgumentException(
                    "This order is not awaiting payment confirmation."
            );
        }

        order.setPaymentStatus(PaymentStatus.PAID.name());
        order.setPaidAt(LocalDateTime.now());
        order.setStatus(OrderStatus.DELIVERED.name());
        orderRepository.save(order);
    }

    public Set<OrderStatus> getAllowedNextStatuses(
            String status,
            String paymentMethod
    ) {
        OrderStatus currentStatus =
                OrderStatus.fromValue(status);

        Set<OrderStatus> allowed =
                EnumSet.copyOf(currentStatus.allowedTransitions());

        if (PaymentMethod.VNPAY.name().equals(paymentMethod)
                && currentStatus != OrderStatus.PENDING) {
            allowed.remove(OrderStatus.CANCELLED);
        }

        return allowed;
    }

    private boolean isPaidVnPayOrder(Order order) {
        if (!PaymentMethod.VNPAY.name().equals(order.getPaymentMethod())) {
            return false;
        }

        OrderStatus currentStatus =
                OrderStatus.fromValue(order.getStatus());

        return currentStatus != OrderStatus.PENDING;
    }

    private boolean shouldRestoreStockOnCancel(Order order) {
        if (!PaymentMethod.VNPAY.name().equals(order.getPaymentMethod())) {
            return true;
        }

        OrderStatus currentStatus =
                OrderStatus.fromValue(order.getStatus());

        return currentStatus != OrderStatus.PENDING;
    }

    /**
     * Hủy đơn VNPay chưa thanh toán — khi khách hủy hoặc thanh toán thất bại trên cổng VNPay.
     *
     * @return true nếu đơn vừa được chuyển sang CANCELLED
     */
    @Transactional
    public boolean cancelPendingVnPayOrder(long orderId) {
        Order order =
                orderRepository
                        .findByIdWithUserAndItems(orderId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Order not found."
                                )
                        );

        if (!PaymentMethod.VNPAY.name().equals(order.getPaymentMethod())) {
            return false;
        }

        OrderStatus currentStatus =
                OrderStatus.fromValue(order.getStatus());

        if (currentStatus == OrderStatus.CANCELLED) {
            return false;
        }

        if (currentStatus != OrderStatus.PENDING) {
            return false;
        }

        updateOrderStatus(
                orderId,
                OrderStatus.CANCELLED.name()
        );

        return true;
    }

    /**
     * Xác nhận thanh toán VNPay thành công — chuyển đơn sang CONFIRMED,
     * trừ kho, giảm voucher và xóa giỏ hàng.
     */
    @Transactional
    public boolean confirmVnPayPayment(long orderId, BigDecimal paidAmount) {
        Order order =
                orderRepository
                        .findByIdWithUserAndItems(orderId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Order not found."
                                )
                        );

        if (!PaymentMethod.VNPAY.name().equals(order.getPaymentMethod())) {
            throw new IllegalArgumentException(
                    "This order is not a VNPay payment order."
            );
        }

        OrderStatus currentStatus =
                OrderStatus.fromValue(order.getStatus());

        if (currentStatus == OrderStatus.CONFIRMED
                || currentStatus == OrderStatus.SHIPPING
                || currentStatus == OrderStatus.DELIVERED) {
            return true;
        }

        if (currentStatus != OrderStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Cannot confirm payment for order in status "
                            + currentStatus.getLabel()
                            + "."
            );
        }

        if (paidAmount == null
                || paidAmount.compareTo(order.getTotalAmount()) != 0) {
            throw new IllegalArgumentException(
                    "Paid amount does not match order total."
            );
        }

        decreaseOrderStock(order);

        if (order.getVoucherCode() != null
                && !order.getVoucherCode().isBlank()) {
            // Tiền đã vào — KHÔNG được để lỗi voucher (hết lượt/hết hạn) làm rollback
            // đơn đã thanh toán. Trừ voucher trong transaction riêng (REQUIRES_NEW);
            // nếu thất bại thì bỏ qua, vẫn xác nhận đơn.
            try {
                voucherService.decreaseVoucherQuantityIsolated(
                        order.getVoucherCode()
                );
            } catch (RuntimeException ex) {
                logger.warn(
                        "VNPay order {} paid but voucher '{}' could not be consumed: {}",
                        order.getId(),
                        order.getVoucherCode(),
                        ex.getMessage()
                );
            }
        }

        order.setStatus(
                OrderStatus.CONFIRMED.name()
        );

        order.setPaymentStatus(PaymentStatus.PAID.name());
        order.setPaidAt(LocalDateTime.now());

        orderRepository.save(order);
        return true;
    }

    public Order getOrderEntityForPayment(long orderId) {
        return orderRepository
                .findByIdWithUserAndItems(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found."
                        )
                );
    }

    // Validate toàn bộ item trong giỏ trước checkout — trả list lỗi (rỗng = OK)
    public List<String> validateCartItemsForOrder(
            Cart cart // Entity giỏ hàng đã refresh
    ) {
        List<String> errors =
                new ArrayList<>(); // Gom mọi lỗi validation vào 1 list

        if (cart == null
                || cart.getItems().isEmpty()) {
            // Giỏ null hoặc không có dòng nào

            errors.add(
                    "The cart must contain at least one item."
            );

            return errors; // Trả sớm — không cần duyệt item
        }

        int validItemCount =
                0; // Đếm số dòng hợp lệ — phải >= 1 mới cho checkout

        for (CartItem item :
                cart.getItems()) { // Duyệt từng dòng trong giỏ

            if (item.getBookSet() != null) {
                try {
                    BookSet bookSet = bookSetService.getByIdWithItems(
                            item.getBookSet().getId()
                    );
                    if (!bookSet.isActive()) {
                        errors.add("\"" + bookSet.getName() + "\" is no longer available.");
                        continue;
                    }
                    if (bookSet.getItems().size() < 2) {
                        errors.add("\"" + bookSet.getName() + "\" is invalid.");
                        continue;
                    }
                    int available = bookSetService.getAvailableSetQuantity(bookSet);
                    if (available <= 0) {
                        errors.add("\"" + bookSet.getName() + "\" — "
                                + CartService.MSG_OUT_OF_STOCK);
                        continue;
                    }
                    if (item.getQuantity() > available) {
                        errors.add("\"" + bookSet.getName() + "\" — "
                                + String.format(CartService.MSG_EXCEED_STOCK, available));
                        continue;
                    }
                    validItemCount++;
                } catch (IllegalArgumentException ex) {
                    errors.add("A book set in your cart no longer exists.");
                }
                continue;
            }

            if (item.getBook() == null) {
                errors.add(
                        "An item in your cart is invalid. Please refresh your cart."
                );
                continue;
            }

            Book book =
                    bookRepository
                            .findById(
                                    item
                                            .getBook()
                                            .getId() // Load lại Book từ DB
                            )
                            .orElse(null);

            if (book == null) {
                // Sách đã bị xóa

                errors.add(
                        "An item in your cart no longer exists."
                );

                continue;
            }

            if (!book.isActive()) {
                // Admin tắt bán sách

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
                                    book.getId() // Tồn kho sách
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

            validItemCount++; // Dòng sách hợp lệ
        } // end for

        if (validItemCount == 0) {
            // Mọi dòng đều lỗi — không có item nào đặt được

            errors.add(
                    "The order must contain at least one valid item."
            );
        }

        try {
            validateAggregatedBookDemand(cart);
        } catch (IllegalArgumentException ex) {
            errors.add(ex.getMessage());
        }

        return errors; // Rỗng = pass; có phần tử = fail với message cụ thể
    }

    /**
     * Cộng dồn nhu cầu tồn kho từ sách lẻ + thành phần bộ, tránh oversell.
     */
    private void validateAggregatedBookDemand(Cart cart) {
        Map<Long, Integer> demand = new HashMap<>();
        Map<Long, String> titles = new HashMap<>();

        for (CartItem item : cart.getItems()) {
            if (item.getBook() != null) {
                Long bookId = item.getBook().getId();
                demand.merge(bookId, item.getQuantity(), Integer::sum);
                titles.put(bookId, item.getBook().getTitle());
                continue;
            }

            if (item.getBookSet() != null) {
                BookSet bookSet = bookSetService.getByIdWithItems(
                        item.getBookSet().getId()
                );
                for (BookSetItem setItem : bookSet.getItems()) {
                    Long bookId = setItem.getBook().getId();
                    int need = item.getQuantity() * setItem.getQuantity();
                    demand.merge(bookId, need, Integer::sum);
                    titles.put(bookId, setItem.getBook().getTitle());
                }
            }
        }

        for (Map.Entry<Long, Integer> entry : demand.entrySet()) {
            int available = stockService.getBookQuantity(entry.getKey());
            if (entry.getValue() > available) {
                String title = titles.getOrDefault(entry.getKey(), "A book");
                throw new IllegalArgumentException(
                        "\"" + title + "\" — combined cart demand ("
                                + entry.getValue()
                                + ") exceeds available stock ("
                                + available
                                + ")."
                );
            }
        }
    }

    // Tạo CheckoutForm pre-fill từ thông tin user — OrderController GET checkout
    public CheckoutForm buildCheckoutFormFromUser(
            User user // Entity user đang đăng nhập
    ) {
        CheckoutForm form =
                new CheckoutForm(); // Object form rỗng

        if (user != null) { // Phòng null — thường user luôn có khi đã login

            form.setRecipientName(
                    user.getFullName() // Pre-fill tên người nhận = tên user
            );

            form.setRecipientPhone(
                    user.getPhone() // Có thể null nếu user chưa cập nhật profile
            );

            form.setShippingAddress(
                    user.getAddress() // Có thể null — user sửa trên form checkout
            );
        }

        return form; // Trả về form để bind lên checkout.jsp
    }

    // Format tổng tiền checkout để hiển thị trên view — subtotal trừ discount
    public String getCheckoutTotalFormatted(
            BigDecimal subtotal,      // Tổng sau khuyến mãi
            BigDecimal discountAmount // Giảm giá voucher
    ) {
        BigDecimal base =
                subtotal == null
                        ? BigDecimal.ZERO // null → 0
                        : subtotal;

        BigDecimal discount =
                discountAmount == null
                        ? BigDecimal.ZERO // Không có voucher → 0
                        : discountAmount;

        BigDecimal total =
                base.subtract(
                        discount // Tổng cuối = subtotal - voucher
                );

        if (total.compareTo(
                BigDecimal.ZERO
        ) < 0) {
            // Voucher lớn hơn subtotal → không cho âm
            total =
                    BigDecimal.ZERO;
        }

        return formatMoney(
                total // VD "150,000 đ" — method format có sẵn trong service
        );
    }

    // Validate 1 dòng sách khi tạo đơn (active, tồn kho, giá) — throw nếu lỗi
    private void validateBookLine(
            Book book,     // Entity sách từ DB
            int quantity   // Số lượng user đặt
    ) {
        if (!book.isActive()) {
            // Sách đã ngừng bán

            throw new IllegalArgumentException(
                    "\""
                            + book.getTitle()
                            + "\" is no longer available."
            );
        }

        // Đọc tồn kho từ bảng stock (nguồn chính, không dùng book.stock cũ)
        int availableStock =
                stockService
                        .getBookQuantity(
                                book.getId()
                        );

        if (availableStock <= 0) {
            // Hết hàng tại thời điểm tạo đơn

            throw new IllegalArgumentException(
                    "\""
                            + book.getTitle()
                            + "\" — "
                            + CartService.MSG_OUT_OF_STOCK
            );
        }

        if (quantity > availableStock) {
            // Race condition hoặc refresh chưa kịp — chặn vượt tồn

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
            // Giá không hợp lệ

            throw new IllegalArgumentException(
                    "\""
                            + book.getTitle()
                            + "\" has an invalid selling price."
            );
        }
        // Pass — không return gì (void)
    }

    // Tính số tiền giảm từ voucher — dùng trong createOrderFromCart
    private BigDecimal resolveDiscount(
            String voucherCode,  // Mã voucher user nhập (có thể null/blank)
            BigDecimal subtotal  // Tổng trước voucher (sau KM)
    ) {
        if (voucherCode == null
                || voucherCode.isBlank()) {
            // Không áp voucher

            return BigDecimal.ZERO;
        }

        return voucherService
                .calculateDiscount(
                        voucherCode, // Validate + tính % hoặc fixed amount
                        subtotal     // Base amount để áp voucher
                );
    }

    // Sinh mã đơn unique: ORD-yyyyMMdd-xxxxxx, thử tối đa 20 lần
    private String generateUniqueOrderCode() {
        // Phần ngày: vd 20260728
        String datePart =
                LocalDateTime.now()
                        .format(
                                DateTimeFormatter
                                        .ofPattern(
                                                "yyyyMMdd"
                                        )
                        );

        // Thử tối đa 20 lần random suffix tránh trùng
        for (int attempt = 0;
             attempt < 20;
             attempt++) {

            // Random 6 chữ số: 100000 .. 999998
            int suffix =
                    ThreadLocalRandom
                            .current()
                            .nextInt(
                                    100000,
                                    999999
                            );

            // Ghép mã: ORD-20260728-384521
            String orderCode =
                    "ORD-"
                            + datePart
                            + "-"
                            + suffix;

            // Kiểm tra unique trên bảng orders
            if (!orderRepository
                    .existsByOrderCode(
                            orderCode
                    )) {

                return orderCode;
            }
            // Trùng -> vòng lặp thử suffix khác
        }

        // Hiếm — 20 lần đều trùng
        throw new IllegalStateException(
                "A unique order code could not be generated. Please try again."
        );
    }

    // Chuyển Order → OrderDTO tóm tắt — dùng trang danh sách GET /orders
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

        dto.setPaymentStatus(
                order.getPaymentStatus()
        );

        dto.setPaymentStatusLabel(
                order.getPaymentStatusLabel()
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

    // Mở rộng toSummaryDTO — thêm items, địa chỉ, voucher (trang chi tiết)
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

        dto.setPaymentMethod(
                order.getPaymentMethod()
        );

        dto.setPaymentMethodLabel(
                order.getPaymentMethodLabel()
        );

        dto.setCanCustomerConfirmPayment(
                canCustomerConfirmPayment(order)
        );

        dto.setCanAdminCompletePayment(
                canAdminCompletePayment(order)
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

        // Thông tin customer — hiển thị trên admin/staff detail
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

        // Map từng OrderItem entity -> OrderItemDTO
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

    // Map 1 dòng sản phẩm sang DTO cho JSP
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

        dto.setBookSetId(item.getBookSetId());
        dto.setBookSetName(item.getBookSetName());

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

            // DTO dùng chung field bookId cho VPP id
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

    /** Chuỗi rỗng -> null để DB không lưu "" */
    private String blankToNull(
            String value
    ) {
        if (value == null
                || value.isBlank()) {

            return null;
        }

        return value.trim();
    }

    // Format tiền VND kiểu Đức (1.234.567) — OrderController dùng cho preview checkout
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

    /** Tổng số đơn — AdminDashboard */
    public long countAllOrders() {
        return orderRepository.count();
    }

    /** Số đơn của 1 user — profile hoặc thống kê */
    public long countOrdersByUser(
            long userId
    ) {
        return orderRepository
                .countByUserId(userId);
    }
}
