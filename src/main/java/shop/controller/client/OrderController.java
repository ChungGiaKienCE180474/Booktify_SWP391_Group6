// Package controller client — xử lý đặt hàng, checkout, lịch sử đơn hàng
package shop.controller.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

// ResponseEntity: trả JSON với HTTP status (dùng cho API preview promotion)
import org.springframework.http.ResponseEntity;
// Authentication: thông tin user đã đăng nhập từ Spring Security
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// @ResponseBody: trả JSON thay vì view HTML
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import shop.domain.Book;
import shop.domain.Cart;
import shop.domain.CheckoutForm;
import shop.domain.OrderStatus;
import shop.domain.PaymentMethod;
import shop.domain.Promotion;
import shop.domain.PromotionSelection;
import shop.domain.User;
import shop.domain.dto.CartDTO;
import shop.domain.dto.CartRefreshResult;
import shop.domain.dto.OrderDTO;
import shop.domain.dto.PromotionPreviewDTO;
import shop.service.CartService;
import shop.service.OrderService;
import shop.service.PromotionService;
import shop.service.UserService;
import shop.service.VnPayService;
import shop.service.VoucherService;

// @Controller + prefix URL /orders — yêu cầu đăng nhập (SecurityConfiguration)
// Luồng: checkout -> placeOrder -> lịch sử/chi tiết/hủy đơn
@Controller
@RequestMapping("/orders")
public class OrderController {

        // Service tạo đơn, validate checkout, format tiền...
        private final OrderService orderService;
        // Service giỏ hàng: refresh giá/tồn kho, chuyển Cart → CartDTO
        private final CartService cartService;
        // Service lấy thông tin user theo email
        private final UserService userService;
        // Service tính giảm giá voucher
        private final VoucherService voucherService;
        // Service khuyến mãi theo sách (% hoặc cố định)
        private final PromotionService promotionService;
        // Service tạo URL thanh toán VNPay
        private final VnPayService vnPayService;

        // Constructor injection: Spring tự inject các service qua tham số
        public OrderController(
                        OrderService orderService,
                        CartService cartService,
                        UserService userService,
                        VoucherService voucherService,
                        PromotionService promotionService,
                        VnPayService vnPayService) {

                this.orderService = orderService; // Gán field final
                this.cartService = cartService;
                this.userService = userService;
                this.voucherService = voucherService;
                this.promotionService = promotionService;
                this.vnPayService = vnPayService;
        }

        // =========================================================
        // ĐẶT HÀNG (POST checkout)
        // =========================================================

        // POST /orders/checkout — submit form checkout và tạo đơn hàng
        @PostMapping("/checkout") // Form checkout.jsp POST về đây
        public String placeOrder(
                        Authentication authentication, // User đăng nhập
                        @Valid // Bean Validation trên CheckoutForm
                        @ModelAttribute("checkoutForm") // Bind form fields vào object
                        CheckoutForm checkoutForm,
                        BindingResult bindingResult, // Chứa lỗi @Valid + reject thủ công
                        Model model, // Data cho view khi render lại checkout
                        RedirectAttributes redirectAttributes, // Flash message sau redirect
                        HttpSession session, // Lấy/xóa promotion selections
                        HttpServletRequest request) { // IP cho VNPay

                // Lấy user đang đăng nhập từ Spring Security
                User user = getCurrentUser(authentication); // Email → User entity

                // Áp dụng lựa chọn khuyến mãi từ session (nếu user chọn ở trang giỏ hàng)
                applyCartPromotionSelections(checkoutForm, session);

                // Refresh giỏ hàng: cập nhật giá, xóa sách hết hàng, đồng bộ DB
                CartRefreshResult refreshResult = cartService.refreshCartResult(
                                user.getId());

                // Entity Cart sau khi refresh
                Cart refreshedCart = refreshResult.getCart();

                // CartDTO để hiển thị trên view (đã format, tính tổng...)
                CartDTO cart = cartService.toCartDTO(
                                refreshedCart);

                // Giỏ rỗng → không cho đặt hàng, quay về trang giỏ
                if (cart.isEmpty()) {
                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        "Cannot create an order with an empty cart.");

                        return "redirect:/cart";
                }

                // Validate nghiệp vụ checkout (địa chỉ, SĐT, voucher, payment...)
                try {
                        orderService.validateCheckoutForm(
                                        checkoutForm);
                } catch (IllegalArgumentException exception) {
                        // Gắn lỗi vào BindingResult để hiển thị trên form
                        bindingResult.reject(
                                        "checkout.invalid",
                                        exception.getMessage());
                }

                // Có lỗi validate → render lại trang checkout với thông báo
                if (bindingResult.hasErrors()) {
                        populateCheckoutModel(
                                        model,
                                        cart,
                                        refreshedCart,
                                        checkoutForm);

                        model.addAttribute(
                                        "checkoutForm",
                                        checkoutForm);

                        return "order/checkout";
                }

                PaymentMethod paymentMethod = checkoutForm.resolvePaymentMethod();

                if (paymentMethod == PaymentMethod.VNPAY
                                && !vnPayService.isConfigured()) {
                        bindingResult.reject(
                                        "checkout.invalid",
                                        "VNPay is not configured. Please choose COD or contact support.");
                }

                if (bindingResult.hasErrors()) {
                        populateCheckoutModel(
                                        model,
                                        cart,
                                        refreshedCart,
                                        checkoutForm);

                        model.addAttribute(
                                        "checkoutForm",
                                        checkoutForm);

                        return "order/checkout";
                }

                // Validate OK → tạo đơn hàng từ giỏ hàng
                try {
                        OrderDTO order = orderService.createOrderFromCart(
                                        user.getId(),
                                        checkoutForm);

                        if (paymentMethod == PaymentMethod.VNPAY) {
                                shop.domain.Order paymentOrder = orderService.getOrderEntityForPayment(
                                                order.getId());

                                String paymentUrl = vnPayService.createPaymentUrl(
                                                paymentOrder,
                                                request.getRemoteAddr());

                                session.removeAttribute(
                                                CartController.PROMOTION_SESSION_KEY);

                                redirectAttributes.addFlashAttribute(
                                                "successMessage",
                                                "Order created. Please complete VNPay payment to confirm your order.");

                                return "redirect:" + paymentUrl;
                        }

                        // Flash message báo đặt hàng thành công kèm mã đơn
                        redirectAttributes.addFlashAttribute(
                                        "successMessage",
                                        "Order placed successfully! Order code: "
                                                        + order.getOrderCode());

                        // Xóa lựa chọn promotion trong session sau khi đặt hàng xong
                        session.removeAttribute(
                                        CartController.PROMOTION_SESSION_KEY);

                        // Chuyển đến trang chi tiết đơn vừa tạo
                        return "redirect:/orders/"
                                        + order.getId();

                } catch (IllegalArgumentException | IllegalStateException exception) {
                        // Lỗi nghiệp vụ (hết hàng, voucher invalid, VNPay...) → hiển thị lại checkout
                        populateCheckoutModel(
                                        model,
                                        cart,
                                        refreshedCart,
                                        checkoutForm);

                        model.addAttribute(
                                        "checkoutForm",
                                        checkoutForm);

                        model.addAttribute(
                                        "errorMessage",
                                        exception.getMessage());

                        return "order/checkout";
                }
        }

        // =========================================================
        // ÁP DỤNG VOUCHER
        // =========================================================

        // POST /orders/apply-voucher — áp mã giảm giá và render lại trang checkout
        @PostMapping("/apply-voucher")
        public String applyVoucher(
                        Authentication authentication,
                        @ModelAttribute("checkoutForm") CheckoutForm checkoutForm,
                        Model model,
                        HttpSession session) {

                User user = getCurrentUser(authentication);

                applyCartPromotionSelections(checkoutForm, session);

                CartRefreshResult refreshResult = cartService.refreshCartResult(user.getId());
                CartDTO cart = cartService.toCartDTO(refreshResult.getCart());

                String voucherCode = checkoutForm.getVoucherCode();

                if (voucherCode == null || voucherCode.trim().isEmpty()) {
                        populateCheckoutModel(model, cart, refreshResult.getCart(), checkoutForm);
                        model.addAttribute("errorMessage", "Please enter a voucher code.");
                        model.addAttribute("checkoutForm", checkoutForm);
                        return "order/checkout";
                }

                try {
                        // populateCheckoutModel sẽ validate và tính voucher discount
                        populateCheckoutModel(model, cart, refreshResult.getCart(), checkoutForm);
                        model.addAttribute("successMessage", "Voucher applied successfully!");
                } catch (IllegalArgumentException exception) {
                        // Voucher không hợp lệ → xóa mã voucher khỏi form
                        checkoutForm.setVoucherCode(null);
                        populateCheckoutModel(model, cart, refreshResult.getCart(), checkoutForm);
                        model.addAttribute("errorMessage", exception.getMessage());
                }

                model.addAttribute("checkoutForm", checkoutForm);

                return "order/checkout";
        }

        // =========================================================
        // PREVIEW KHUYẾN MÃI (AJAX)
        // =========================================================

        // POST /orders/preview-promotion — API trả JSON preview giá sau KM + voucher
        @PostMapping("/preview-promotion")
        @ResponseBody
        public ResponseEntity<PromotionPreviewDTO> previewPromotion(
                        Authentication authentication,
                        @ModelAttribute CheckoutForm checkoutForm) {

                User user = getCurrentUser(authentication);

                CartRefreshResult refreshResult = cartService.refreshCartResult(
                                user.getId());

                Cart cart = refreshResult.getCart();

                // Tổng tiền gốc trước khuyến mãi
                BigDecimal originalSubtotal = safeAmount(
                                cart.getTotalAmount());

                // Tổng sau khi áp khuyến mãi theo lựa chọn từng sách
                BigDecimal promotionSubtotal = promotionService
                                .calculateCartTotalBySelections(
                                                cart,
                                                checkoutForm
                                                                .getBookPromotionSelections());

                // Số tiền giảm từ khuyến mãi = gốc - sau KM (không âm)
                BigDecimal promotionDiscount = originalSubtotal
                                .subtract(promotionSubtotal)
                                .max(BigDecimal.ZERO);

                // Giảm giá voucher, mặc định 0
                BigDecimal voucherDiscount = BigDecimal.ZERO;

                String message = "Promotion prices updated.";

                try {
                        String voucherCode = checkoutForm.getVoucherCode();

                        // Nếu có mã voucher → tính giảm giá trên promotionSubtotal
                        if (voucherCode != null
                                        && !voucherCode.isBlank()) {

                                voucherDiscount = voucherService.calculateDiscount(
                                                voucherCode,
                                                promotionSubtotal);

                                message = "Promotion and voucher prices updated.";
                        }

                } catch (IllegalArgumentException exception) {
                        // Voucher lỗi → trả HTTP 400 với preview lỗi
                        PromotionPreviewDTO errorResponse = buildPreviewResponse(
                                        originalSubtotal,
                                        promotionDiscount,
                                        BigDecimal.ZERO,
                                        promotionSubtotal,
                                        exception.getMessage());

                        return ResponseEntity
                                        .badRequest()
                                        .body(errorResponse);
                }

                // Tổng cuối = sau KM - voucher (không âm)
                BigDecimal finalTotal = promotionSubtotal
                                .subtract(voucherDiscount)
                                .max(BigDecimal.ZERO);

                PromotionPreviewDTO response = buildPreviewResponse(
                                originalSubtotal,
                                promotionDiscount,
                                voucherDiscount,
                                finalTotal,
                                message);

                return ResponseEntity.ok(response);
        }

        // =========================================================
        // HIỂN THỊ TRANG CHECKOUT
        // =========================================================

        // GET /orders/checkout — hiển thị form thanh toán
        @GetMapping("/checkout")
        public String checkoutForm(
                        Authentication authentication,
                        Model model,
                        HttpSession session) {

                User user = getCurrentUser(authentication);

                CartRefreshResult refreshResult = cartService.refreshCartResult(
                                user.getId());

                CartDTO cart = cartService.toCartDTO(
                                refreshResult.getCart());

                // Giỏ rỗng → redirect về /cart
                if (cart.isEmpty()) {
                        return "redirect:/cart";
                }

                CheckoutForm checkoutForm;

                // Lần đầu vào checkout: tạo form từ thông tin user (địa chỉ, SĐT...)
                if (!model.containsAttribute(
                                "checkoutForm")) {

                        checkoutForm = orderService
                                        .buildCheckoutFormFromUser(
                                                        user);

                        model.addAttribute(
                                        "checkoutForm",
                                        checkoutForm);

                } else {
                        // Sau redirect/validate lỗi: giữ form cũ trong model
                        checkoutForm = (CheckoutForm) model.getAttribute(
                                        "checkoutForm");
                }

                // Gắn promotion selections từ session giỏ hàng
                applyCartPromotionSelections(
                                checkoutForm,
                                session);

                // Khởi tạo mặc định NONE cho sách chưa có lựa chọn KM
                initializePromotionSelections(
                                refreshResult.getCart(),
                                checkoutForm);

                // Tính tổng tiền, map promotion, format tiền → đưa vào model
                populateCheckoutModel(
                                model,
                                cart,
                                refreshResult.getCart(),
                                checkoutForm);

                return "order/checkout";
        }

        // =========================================================
        // LỊCH SỬ ĐƠN HÀNG
        // =========================================================

        // GET /orders — danh sách đơn hàng của user
        @GetMapping // Map GET /orders (prefix class đã là /orders)
        public String orderHistory(
                        Authentication authentication, // User đã đăng nhập
                        Model model) {

                User user = getCurrentUser(authentication); // Lấy User từ email trong Authentication

                model.addAttribute(
                                "orders", // Key JSP dùng ${orders}
                                orderService.getOrdersForUser(
                                                user.getId() // Chỉ lấy đơn của user hiện tại
                                ));

                return "order/list"; // WEB-INF/view/order/list.jsp
        }

        // =========================================================
        // CHI TIẾT ĐƠN HÀNG
        // =========================================================

        // GET /orders/{id} — chi tiết một đơn (id phải là số)
        @GetMapping("/{id:\\d+}") // Chỉ match id dạng số, vd /orders/42
        public String orderDetail(
                        Authentication authentication, // User đăng nhập
                        @PathVariable Long id, // ID đơn từ URL
                        Model model, // Truyền data sang JSP
                        RedirectAttributes redirectAttributes) { // Flash message khi lỗi

                User user = getCurrentUser(authentication); // Lấy User entity từ session

                // getOrderForUser trả Optional: có đơn và thuộc user thì present
                return orderService
                                .getOrderForUser(
                                                user.getId(), // Chỉ tìm đơn của user này
                                                id // ID đơn cần xem
                                )
                                .map(order -> { // Optional.map — có đơn → render detail
                                        model.addAttribute(
                                                        "order", // OrderDTO chi tiết cho JSP
                                                        order);

                                        // Flag cho nút hủy đơn trên view — chỉ PENDING mới hủy được
                                        model.addAttribute(
                                                        "canCancelOrder",
                                                        canCancelOrder(order) // true/false
                                        );

                                        return "order/detail"; // View chi tiết đơn
                                })
                                .orElseGet(() -> { // Optional rỗng — không tìm thấy hoặc không thuộc user
                                        redirectAttributes
                                                        .addFlashAttribute(
                                                                        "errorMessage",
                                                                        "Order not found." // Hiện trên trang /orders
                                                                                           // sau redirect
                                        );

                                        return "redirect:/orders"; // Quay về danh sách đơn
                                });
        }

        // =========================================================
        // HỦY ĐƠN HÀNG
        // =========================================================

        // POST /orders/{id}/cancel — hủy đơn nếu trạng thái cho phép
        @PostMapping("/{id:\\d+}/cancel") // Regex: id phải là số — tránh conflict route khác
        public String cancelOrder(
                        Authentication authentication,
                        @PathVariable Long id, // ID đơn cần hủy
                        RedirectAttributes redirectAttributes) {

                User user = getCurrentUser(authentication); // User đang đăng nhập

                try {
                        orderService.cancelOrderForUser(
                                        user.getId(), // Chỉ hủy đơn thuộc user này
                                        id // ID đơn từ URL
                        );

                        redirectAttributes.addFlashAttribute(
                                        "successMessage",
                                        "Order cancelled successfully." // Hiện trên trang detail sau redirect
                        );

                } catch (IllegalArgumentException exception) {
                        // VD: đơn không phải PENDING, không thuộc user, không tồn tại
                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        exception.getMessage());
                }

                return "redirect:/orders/" + id; // PRG — quay lại trang chi tiết cùng đơn
        }

        // POST /orders/{id}/confirm-payment — khách xác nhận đã thanh toán (COD)
        @PostMapping("/{id:\\d+}/confirm-payment")
        public String confirmPayment(
                        Authentication authentication,
                        @PathVariable Long id,
                        RedirectAttributes redirectAttributes) {

                User user = getCurrentUser(authentication);

                try {
                        orderService.customerConfirmPayment(
                                        user.getId(),
                                        id);

                        redirectAttributes.addFlashAttribute(
                                        "successMessage",
                                        "Payment confirmation sent. Please wait for the shop to complete your order.");

                } catch (IllegalArgumentException exception) {
                        redirectAttributes.addFlashAttribute(
                                        "errorMessage",
                                        exception.getMessage());
                }

                return "redirect:/orders/" + id;
        }

        // =========================================================
        // CHUẨN BỊ DỮ LIỆU CHO VIEW CHECKOUT
        // =========================================================

        // Đưa cart, promotion maps, các mức giá đã format vào model
        private void populateCheckoutModel(
                        Model model,
                        CartDTO cart,
                        Cart rawCart,
                        CheckoutForm checkoutForm) {

                if (checkoutForm == null) {
                        checkoutForm = new CheckoutForm();
                }

                // Map bookId → promotion giảm theo phần trăm
                Map<Long, Promotion> percentagePromotionMap = new HashMap<>();

                // Map bookId → promotion giảm số tiền cố định
                Map<Long, Promotion> fixedPromotionMap = new HashMap<>();

                // Duyệt từng item trong giỏ để lấy KM đang active
                if (rawCart != null
                                && rawCart.getItems() != null) {

                        rawCart.getItems().forEach(
                                        cartItem -> {

                                                Book book = cartItem.getBook();

                                                if (book == null
                                                                || book.getId() == null) {
                                                        return;
                                                }

                                                // true = promotion theo %
                                                promotionService
                                                                .getActivePromotionForBookByType(
                                                                                book,
                                                                                true)
                                                                .ifPresent(
                                                                                promotion -> percentagePromotionMap
                                                                                                .put(
                                                                                                                book.getId(),
                                                                                                                promotion));

                                                // false = promotion giảm cố định
                                                promotionService
                                                                .getActivePromotionForBookByType(
                                                                                book,
                                                                                false)
                                                                .ifPresent(
                                                                                promotion -> fixedPromotionMap
                                                                                                .put(
                                                                                                                book.getId(),
                                                                                                                promotion));
                                        });
                }

                BigDecimal originalSubtotal = safeAmount(
                                rawCart.getTotalAmount());

                BigDecimal promotionSubtotal = promotionService
                                .calculateCartTotalBySelections(
                                                rawCart,
                                                checkoutForm
                                                                .getBookPromotionSelections());

                BigDecimal promotionDiscount = originalSubtotal
                                .subtract(promotionSubtotal)
                                .max(BigDecimal.ZERO);

                BigDecimal voucherDiscount = BigDecimal.ZERO;

                String voucherCode = checkoutForm.getVoucherCode();

                // Có voucher → tính discount (ném exception nếu voucher invalid)
                if (voucherCode != null
                                && !voucherCode.isBlank()) {

                        voucherDiscount = voucherService.calculateDiscount(
                                        voucherCode,
                                        promotionSubtotal);
                }

                BigDecimal finalTotal = promotionSubtotal
                                .subtract(voucherDiscount)
                                .max(BigDecimal.ZERO);

                model.addAttribute(
                                "cart",
                                cart);

                model.addAttribute(
                                "paymentMethods",
                                PaymentMethod.values());

                model.addAttribute(
                                "paymentLabel",
                                PaymentMethod.COD.getLabel());

                model.addAttribute(
                                "percentagePromotionMap",
                                percentagePromotionMap);

                model.addAttribute(
                                "fixedPromotionMap",
                                fixedPromotionMap);

                model.addAttribute(
                                "originalSubtotalFormatted",
                                orderService.formatMoney(
                                                originalSubtotal));

                model.addAttribute(
                                "promotionDiscountFormatted",
                                orderService.formatMoney(
                                                promotionDiscount));

                model.addAttribute(
                                "discountAmountFormatted",
                                orderService.formatMoney(
                                                voucherDiscount));

                model.addAttribute(
                                "checkoutTotalFormatted",
                                orderService.formatMoney(
                                                finalTotal));
        }

        // =========================================================
        // KHỞI TẠO LỰA CHỌN KHUYẾN MÃI MẶC ĐỊNH
        // =========================================================

        // Gán PromotionSelection.NONE cho sách chưa có entry trong map
        private void initializePromotionSelections(
                        Cart rawCart, // Entity giỏ — có Book trong từng CartItem
                        CheckoutForm checkoutForm) { // Form chứa Map bookId → loại KM

                if (rawCart == null
                                || rawCart.getItems() == null
                                || checkoutForm == null) {
                        return; // Thiếu data → bỏ qua
                }

                Map<Long, String> selections = checkoutForm
                                .getBookPromotionSelections(); // Map mutable — sửa trực tiếp

                rawCart.getItems().forEach(
                                cartItem -> { // Lambda duyệt từng dòng giỏ
                                        Book book = cartItem.getBook(); // Chỉ sách mới có KM per-item

                                        if (book == null
                                                        || book.getId() == null) {
                                                return; // VPP hoặc sách lỗi — skip
                                        }

                                        // putIfAbsent: chỉ set NONE nếu key chưa tồn tại (giữ lựa chọn user)
                                        selections.putIfAbsent(
                                                        book.getId(), // Key = ID sách
                                                        PromotionSelection.NONE.name() // Value = "NONE" — không dùng KM
                                        );
                                });
        }

        // Đọc map promotion selections từ session (key
        // CartController.PROMOTION_SESSION_KEY)
        @SuppressWarnings("unchecked") // Cast Map từ session — kiểm tra instanceof trước
        private void applyCartPromotionSelections(
                        CheckoutForm checkoutForm, // Form checkout cần gắn selections
                        HttpSession session) { // Session lưu lựa chọn từ trang /cart

                if (checkoutForm == null || session == null) {
                        return;
                }

                Object value = session.getAttribute(
                                CartController.PROMOTION_SESSION_KEY // "cartBookPromotionSelections"
                );

                // Session lưu Map<Long, String> → copy vào checkoutForm (tránh sửa session trực
                // tiếp)
                if (value instanceof Map<?, ?>) {
                        checkoutForm.setBookPromotionSelections(
                                        new HashMap<>((Map<Long, String>) value) // Clone map
                        );
                }
        }

        // =========================================================
        // TẠO RESPONSE PREVIEW PROMOTION
        // =========================================================

        // Build DTO JSON với các mức giá đã format VND — trả về AJAX preview-promotion
        private PromotionPreviewDTO buildPreviewResponse(
                        BigDecimal originalSubtotal, // Tổng gốc trước KM
                        BigDecimal promotionDiscount, // Số tiền giảm từ KM
                        BigDecimal voucherDiscount, // Số tiền giảm từ voucher
                        BigDecimal finalTotal, // Tổng cuối cùng
                        String message) { // Message hiển thị trên UI

                return new PromotionPreviewDTO(
                                "PER_ITEM", // Loại KM: giảm theo từng sách
                                orderService.formatMoney(
                                                originalSubtotal // VD "500,000 đ"
                                ),
                                orderService.formatMoney(
                                                promotionDiscount),
                                orderService.formatMoney(
                                                voucherDiscount),
                                orderService.formatMoney(
                                                finalTotal),
                                message // VD "Promotion and voucher prices updated."
                );
        }

        // Tránh NullPointerException khi totalAmount null — dùng trong previewPromotion
        private BigDecimal safeAmount(
                        BigDecimal amount) { // Có thể null từ cart.getTotalAmount()

                return amount == null
                                ? BigDecimal.ZERO // Coalesce null → 0
                                : amount;
        }

        // Kiểm tra đơn có thể hủy theo OrderStatus.canBeCancelled() — hiện chỉ PENDING
        private boolean canCancelOrder(
                        OrderDTO order) { // DTO từ getOrderForUser

                if (order == null
                                || order.getStatus() == null) {
                        return false; // Không có status → không hiện nút hủy
                }

                try {
                        return OrderStatus
                                        .valueOf(
                                                        order.getStatus() // String "PENDING" → enum
                                        )
                                        .canBeCancelled(); // PENDING=true, SHIPPED/DELIVERED=false...

                } catch (IllegalArgumentException exception) {
                        return false; // Status lạ trong DB → an toàn không cho hủy
                }
        }

        // Lấy User entity từ Authentication — dùng chung mọi endpoint /orders
        private User getCurrentUser(
                        Authentication authentication) {

                if (authentication == null
                                || !authentication.isAuthenticated()) {
                        // Chưa login — SecurityConfiguration thường chặn trước, đây là lớp phòng thủ
                        throw new IllegalStateException(
                                        "You need to log in to place an order.");
                }

                User user = userService.getUserByEmail(
                                authentication.getName() // getName() = email (CustomUserDetailsService)
                );

                if (user == null) {
                        // Email trong token/session không còn trong DB
                        throw new IllegalStateException(
                                        "User not found.");
                }

                return user; // Entity User đầy đủ từ bảng users
        }
}
