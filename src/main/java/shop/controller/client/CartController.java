// Controller giỏ hàng — quản lý thêm/sửa/xóa item, chọn KM, validate trước checkout
package shop.controller.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// ResponseEntity: trả JSON cho request AJAX (thêm giỏ không reload trang)
import org.springframework.http.ResponseEntity;
// Authentication: thông tin user đã đăng nhập từ Spring Security
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import shop.domain.Book;
import shop.domain.Cart;
import shop.domain.Promotion;
import shop.domain.PromotionSelection;
import shop.domain.User;
import shop.domain.dto.CartRefreshResult;
import shop.service.CartService;
import shop.service.OrderService;
import shop.service.PromotionService;
import shop.service.UserService;
import shop.service.VoucherService;

// @Controller + prefix /cart — yêu cầu đăng nhập (SecurityConfiguration)
// Luồng order: viewCart → chọn KM → POST /cart/validate → redirect /orders/checkout
@Controller
@RequestMapping("/cart")
public class CartController {

    /**
     * Key HttpSession lưu lựa chọn khuyến mãi từng sách (Map bookId → PERCENTAGE|FIXED|NONE).
     * OrderController đọc key này khi checkout qua applyCartPromotionSelections().
     */
    public static final String PROMOTION_SESSION_KEY =
            "cartPromotionSelections";

    // Service thao tác giỏ: add, update, remove, refresh, validate...
    private final CartService cartService;
    // Service lấy User theo email
    private final UserService userService;
    // Service voucher — hiển thị danh sách voucher active trên trang giỏ
    private final VoucherService voucherService;
    // Service khuyến mãi — tính giá theo lựa chọn PERCENTAGE/FIXED/NONE
    private final PromotionService promotionService;
    // Dùng formatMoney() và validate liên quan order/checkout
    private final OrderService orderService;

    // Constructor injection — Spring tự inject 5 service
    public CartController(
            CartService cartService,
            UserService userService,
            VoucherService voucherService,
            PromotionService promotionService,
            OrderService orderService) {

        this.cartService = cartService;
        this.userService = userService;
        this.voucherService = voucherService;
        this.promotionService = promotionService;
        this.orderService = orderService;
    }

    // =========================================================
    // XEM GIỎ HÀNG
    // =========================================================

    // GET /cart — hiển thị trang giỏ hàng với giá, KM, tổng tiền
    @GetMapping
    public String viewCart(
            Authentication authentication, // User đăng nhập
            Model model,                   // Truyền data sang cart/index.jsp
            HttpSession session) {         // Đọc lựa chọn KM đã lưu

        User user = getCurrentUser(authentication); // Email → User entity

        // Refresh giỏ: cập nhật giá, xóa sản phẩm inactive/hết hàng, thu thập warnings
        CartRefreshResult refreshResult =
                cartService.refreshCartResult(user.getId());

        Cart cart = refreshResult.getCart(); // Entity Cart sau refresh

        // CartDTO để JSP hiển thị (format tiền, tên sách, ảnh...)
        model.addAttribute(
                "cart",
                cartService.toCartDTO(cart)
        );

        // Map bookId → promotion giảm theo % (để hiển thị option trên UI)
        Map<Long, Promotion> percentagePromotionMap =
                new HashMap<>();

        // Map bookId → promotion giảm số tiền cố định
        Map<Long, Promotion> fixedPromotionMap =
                new HashMap<>();

        if (cart != null && cart.getItems() != null) {
            cart.getItems().forEach(cartItem -> { // Duyệt từng dòng giỏ

                Book book = cartItem.getBook(); // Chỉ sách mới có KM per-item

                if (book == null || book.getId() == null) {
                    return; // VPP hoặc dòng lỗi — bỏ qua
                }

                // Tìm KM % đang active cho sách này
                promotionService
                        .getActivePromotionForBookByType(
                                book,
                                true // true = percentage promotion
                        )
                        .ifPresent(promotion ->
                                percentagePromotionMap.put(
                                        book.getId(),
                                        promotion
                                )
                        );

                // Tìm KM fixed amount đang active
                promotionService
                        .getActivePromotionForBookByType(
                                book,
                                false // false = fixed amount promotion
                        )
                        .ifPresent(promotion ->
                                fixedPromotionMap.put(
                                        book.getId(),
                                        promotion
                                )
                        );
            });
        }

        // Map bookId → "PERCENTAGE"|"FIXED"|"NONE" — đọc từ session
        Map<Long, String> selectedPromotionMap =
                getPromotionSelections(session);

        // Xóa selection của sách đã bị remove khỏi giỏ
        removeSelectionsNotInCart(
                cart,
                selectedPromotionMap
        );

        // Map cartItemId → đơn giá đã format theo KM user chọn
        Map<Long, String> selectedUnitPriceMap =
                new HashMap<>();

        // Map cartItemId → thành tiền dòng (đơn giá × số lượng) đã format
        Map<Long, String> selectedSubtotalMap =
                new HashMap<>();

        if (cart != null && cart.getItems() != null) {
            cart.getItems().forEach(cartItem -> {

                Book book = cartItem.getBook();

                if (book == null || book.getId() == null) {
                    return;
                }

                // Parse lựa chọn KM từ session — mặc định NONE nếu chưa chọn
                PromotionSelection selection =
                        PromotionSelection.fromValue(
                                selectedPromotionMap.get(
                                        book.getId()
                                )
                        );

                // Giá bán sau khi áp KM (PERCENTAGE/FIXED/NONE)
                BigDecimal effectivePrice =
                        promotionService.getPriceForSelection(
                                book,
                                selection
                        );

                // Thành tiền dòng = giá × số lượng
                BigDecimal lineTotal =
                        effectivePrice.multiply(
                                BigDecimal.valueOf(
                                        cartItem.getQuantity()
                                )
                        );

                // Key = cartItem.getId() (ID dòng giỏ, không phải bookId)
                selectedUnitPriceMap.put(
                        cartItem.getId(),
                        orderService.formatMoney(
                                effectivePrice
                        )
                );

                selectedSubtotalMap.put(
                        cartItem.getId(),
                        orderService.formatMoney(
                                lineTotal
                        )
                );
            });
        }

        // Tổng gốc trước khuyến mãi (sum giá gốc × quantity)
        BigDecimal originalSubtotal =
                safeAmount(
                        cart == null
                                ? null
                                : cart.getTotalAmount()
                );

        // Tổng sau khi áp KM theo lựa chọn từng sách
        BigDecimal promotionSubtotal =
                promotionService
                        .calculateCartTotalBySelections(
                                cart,
                                selectedPromotionMap
                        );

        // Số tiền giảm từ KM = gốc - sau KM (không âm)
        BigDecimal promotionDiscount =
                originalSubtotal
                        .subtract(promotionSubtotal)
                        .max(BigDecimal.ZERO);

        // Đưa các map và tổng tiền đã format vào model cho JSP
        model.addAttribute(
                "percentagePromotionMap",
                percentagePromotionMap
        );

        model.addAttribute(
                "fixedPromotionMap",
                fixedPromotionMap
        );

        model.addAttribute(
                "selectedPromotionMap",
                selectedPromotionMap
        );

        model.addAttribute(
                "selectedUnitPriceMap",
                selectedUnitPriceMap
        );

        model.addAttribute(
                "selectedSubtotalMap",
                selectedSubtotalMap
        );

        model.addAttribute(
                "originalSubtotalFormatted",
                orderService.formatMoney(
                        originalSubtotal
                )
        );

        model.addAttribute(
                "promotionDiscountFormatted",
                orderService.formatMoney(
                        promotionDiscount
                )
        );

        model.addAttribute(
                "promotionSubtotalFormatted",
                orderService.formatMoney(
                        promotionSubtotal
                )
        );

        // Danh sách voucher đang active — hiển thị gợi ý trên trang giỏ
        model.addAttribute(
                "activeVouchers",
                voucherService.getActiveVouchers()
        );

        // Hiển thị cảnh báo refresh (VD: sách hết hàng đã bị xóa khỏi giỏ)
        if (refreshResult.hasWarnings()
                && !model.containsAttribute(
                "warningMessage"
        )) {

            model.addAttribute(
                    "warningMessage",
                    String.join(
                            " ",
                            refreshResult.getWarnings()
                    )
            );
        }

        return "cart/index"; // WEB-INF/view/cart/index.jsp
    }

    // =========================================================
    // THÊM SÁCH VÀO GIỎ
    // =========================================================

    // POST /cart/add — thêm sách vào giỏ (form hoặc AJAX)
    @PostMapping("/add")
    public Object addToCart(
            Authentication authentication,
            @RequestParam Long bookId,              // ID sách cần thêm
            @RequestParam(required = false)
            String quantity,                        // Số lượng — mặc định "1" nếu null
            @RequestParam(required = false)
            String redirect,                        // URL redirect tùy chọn sau khi thêm
            HttpServletRequest request,             // Kiểm tra AJAX qua header
            RedirectAttributes redirectAttributes) {  // Flash message khi redirect

        User user =
                getCurrentUser(authentication);

        // Parse quantity — phải là số nguyên dương
        Integer parsedQuantity =
                cartService.parsePositiveIntegerQuantity(
                        quantity != null
                                ? quantity
                                : "1" // Mặc định thêm 1 cuốn
                );

        if (parsedQuantity == null) {
            // Số lượng không hợp lệ (âm, 0, không phải số)
            return handleAddResponse(
                    request,
                    redirectAttributes,
                    false, // success = false
                    CartService.MSG_QUANTITY_INVALID,
                    user,
                    redirectAfterAdd(
                            bookId,
                            redirect
                    )
            );
        }

        try {
            // Gọi service: kiểm tra tồn kho, merge nếu sách đã có trong giỏ
            cartService.addBook(
                    user.getId(),
                    bookId,
                    parsedQuantity
            );

            return handleAddResponse(
                    request,
                    redirectAttributes,
                    true,
                    "Book added to your cart.",
                    user,
                    redirectAfterAdd(
                            bookId,
                            redirect
                    )
            );

        } catch (IllegalArgumentException exception) {
            // VD: hết hàng, sách inactive, vượt stock
            return handleAddResponse(
                    request,
                    redirectAttributes,
                    false,
                    exception.getMessage(),
                    user,
                    redirectAfterAdd(
                            bookId,
                            redirect
                    )
            );
        }
    }

    // =========================================================
    // CẬP NHẬT / XÓA / XÓA HẾT GIỎ
    // =========================================================

    // POST /cart/update — đổi số lượng 1 dòng giỏ
    @PostMapping("/update")
    public String updateQuantity(
            Authentication authentication,
            @RequestParam Long itemId,              // ID dòng cart_items (không phải bookId)
            @RequestParam(required = false)
            String quantity,
            RedirectAttributes redirectAttributes) {

        User user =
                getCurrentUser(authentication);

        Integer parsedQuantity =
                cartService.parsePositiveIntegerQuantity(
                        quantity
                );

        if (parsedQuantity == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    CartService.MSG_QUANTITY_INVALID
            );

            return "redirect:/cart";
        }

        try {
            cartService.updateQuantity(
                    user.getId(),
                    itemId,
                    parsedQuantity
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Quantity updated."
            );

        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/cart"; // PRG — luôn redirect sau POST
    }

    // POST /cart/remove — xóa 1 dòng khỏi giỏ
    @PostMapping("/remove")
    public String removeItem(
            Authentication authentication,
            @RequestParam Long itemId,              // ID dòng cart_items
            RedirectAttributes redirectAttributes) {

        User user =
                getCurrentUser(authentication);

        try {
            cartService.removeItem(
                    user.getId(),
                    itemId
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Item removed from your cart."
            );

        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/cart";
    }

    // POST /cart/clear — xóa toàn bộ giỏ + xóa KM trong session
    @PostMapping("/clear")
    public String clearCart(
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        User user =
                getCurrentUser(authentication);

        cartService.clearCart(
                user.getId()
        );

        // Xóa lựa chọn KM — tránh dùng selection cũ khi thêm sách mới
        session.removeAttribute(
                PROMOTION_SESSION_KEY
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your cart has been cleared."
        );

        return "redirect:/cart";
    }

    // =========================================================
    // KHUYẾN MÃI
    // =========================================================

    /**
     * POST /cart/promotion — user chọn KM (% hoặc fixed) cho 1 sách trong giỏ.
     * Lưu vào session để OrderController mang sang checkout.
     */
    @PostMapping("/promotion")
    public String updatePromotion(
            Authentication authentication,
            @RequestParam Long bookId,      // Sách cần áp/bỏ KM
            @RequestParam String selection, // "PERCENTAGE" | "FIXED" | "NONE"
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user =
                getCurrentUser(authentication);

        // Refresh giỏ trước khi kiểm tra sách còn trong giỏ không
        Cart cart =
                cartService
                        .refreshCartResult(
                                user.getId()
                        )
                        .getCart();

        // Chỉ cho áp KM nếu bookId thật sự có trong giỏ
        boolean bookExistsInCart =
                cart != null
                        && cart.getItems() != null
                        && cart.getItems()
                        .stream()
                        .anyMatch(item ->
                                item.getBook() != null
                                        && bookId.equals(
                                        item.getBook()
                                                .getId()
                                )
                        );

        if (!bookExistsInCart) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "This book is no longer in your cart."
            );

            return "redirect:/cart";
        }

        // Parse "PERCENTAGE" / "FIXED" / "NONE" từ form
        PromotionSelection selected =
                PromotionSelection.fromValue(
                        selection
                );

        // Lấy map hiện tại từ session (hoặc tạo map rỗng mới)
        Map<Long, String> selections =
                getPromotionSelections(
                        session
                );

        // Ghi lựa chọn KM cho bookId
        selections.put(
                bookId,
                selected.name()
        );

        // Lưu lại session — OrderController đọc khi POST /orders/checkout
        session.setAttribute(
                PROMOTION_SESSION_KEY,
                selections
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                selected == PromotionSelection.NONE
                        ? "Promotion removed from this product."
                        : "Promotion applied successfully."
        );

        return "redirect:/cart";
    }

    // =========================================================
    // VALIDATE TRƯỚC CHECKOUT
    // =========================================================

    /**
     * POST /cart/validate — kiểm tra giỏ hợp lệ rồi chuyển sang trang checkout.
     * Bước bắt buộc trước GET /orders/checkout.
     */
    @PostMapping("/validate")
    public String validateCheckout(
            Authentication authentication, // User đã login từ Spring Security
            RedirectAttributes redirectAttributes) { // Flash message sau redirect

        User user =
                getCurrentUser(authentication); // Lấy User entity từ email trong Authentication

        // Đồng bộ giỏ với DB: cập nhật giá, xóa sản phẩm inactive/hết hàng
        cartService.refreshCart(
                user.getId() // PK user → tìm cart của user đó
        );

        // Validate nghiệp vụ: stock, active, giá... — trả List lỗi (rỗng nếu OK)
        List<String> errors =
                cartService.validateForCheckout(
                        user.getId()
                );

        if (!errors.isEmpty()) { // Còn lỗi → không cho sang checkout
            redirectAttributes.addFlashAttribute(
                    "errorMessage", // Key hiển thị trên trang cart
                    String.join(
                            " ",  // Nối nhiều lỗi thành 1 chuỗi
                            errors
                    )
            );

            return "redirect:/cart"; // Ở lại trang giỏ hàng
        }

        // Giỏ hợp lệ → chuyển sang OrderController.checkoutForm (GET /orders/checkout)
        return "redirect:/orders/checkout";
    }

    // =========================================================
    // HELPER — REDIRECT SAU KHI THÊM GIỎ
    // =========================================================

    // Xác định URL redirect sau khi thêm sách — ưu tiên param redirect nếu có
    private String redirectAfterAdd(
            Long bookId,    // Fallback: quay về trang chi tiết sách
            String redirect) {

        if (redirect != null
                && !redirect.isBlank()) {

            return "redirect:" + redirect; // VD redirect=/cart
        }

        return "redirect:/books/" + bookId; // Mặc định: trang chi tiết sách vừa thêm
    }

    // =========================================================
    // HELPER — RESPONSE THÊM GIỎ (FORM vs AJAX)
    // =========================================================

    // Trả JSON nếu AJAX, hoặc redirect + flash message nếu form submit thường
    private Object handleAddResponse(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            boolean success,        // true = thêm thành công
            String message,         // Nội dung thông báo
            User user,              // Cần userId để đếm số item trong giỏ (JSON)
            String redirectTarget) { // Chuỗi "redirect:..." khi không phải AJAX

        if (isAjaxRequest(request)) {
            // Frontend gửi X-Requested-With: XMLHttpRequest → trả JSON
            return cartAddJsonResponse(
                    success,
                    message,
                    user.getId()
            );
        }

        // Form submit thường → flash message + redirect
        if (success) {
            redirectAttributes.addFlashAttribute(
                    "cartSuccessMessage",
                    message
            );

        } else {
            redirectAttributes.addFlashAttribute(
                    "cartErrorMessage",
                    message
            );
        }

        return redirectTarget;
    }

    // Build JSON body cho AJAX add-to-cart — cập nhật badge số item trên header
    private ResponseEntity<Map<String, Object>>
    cartAddJsonResponse(
            boolean success,
            String message,
            long userId) {

        Map<String, Object> body =
                new LinkedHashMap<>(); // Giữ thứ tự key khi serialize JSON

        body.put(
                "success",
                success
        );

        body.put(
                "message",
                message
        );

        body.put(
                "cartItemCount",
                cartService.getCartItemCount(
                        userId // Tổng số dòng trong giỏ — hiển thị icon giỏ
                )
        );

        return ResponseEntity
                .status(
                        success
                                ? 200  // OK
                                : 400  // Bad Request — lỗi nghiệp vụ
                )
                .body(body);
    }

    // Kiểm tra request có phải AJAX không — convention jQuery/fetch
    private boolean isAjaxRequest(
            HttpServletRequest request) {

        return "XMLHttpRequest".equals(
                request.getHeader(
                        "X-Requested-With"
                )
        );
    }

    // =========================================================
    // HELPER — SESSION KHUYẾN MÃI
    // =========================================================

    // Đọc map lựa chọn KM từ session — tạo map mới nếu chưa có
    @SuppressWarnings("unchecked") // Cast Map từ session — kiểm tra instanceof trước
    private Map<Long, String>
    getPromotionSelections(
            HttpSession session) {

        Object value =
                session.getAttribute(
                        PROMOTION_SESSION_KEY
                );

        if (value instanceof Map<?, ?>) {
            return (Map<Long, String>) value; // Trả map đã lưu — sửa trực tiếp cũng cập nhật session
        }

        // Lần đầu: tạo map rỗng và gắn vào session
        Map<Long, String> selections =
                new HashMap<>();

        session.setAttribute(
                PROMOTION_SESSION_KEY,
                selections
        );

        return selections;
    }

    // Dọn selection của sách không còn trong giỏ — tránh map session phình ra
    private void removeSelectionsNotInCart(
            Cart cart,
            Map<Long, String> selections) {

        if (selections == null
                || selections.isEmpty()) {

            return; // Không có gì để dọn
        }

        if (cart == null
                || cart.getItems() == null) {

            selections.clear(); // Giỏ rỗng → xóa hết selection
            return;
        }

        // Xóa key bookId nếu không còn dòng sách tương ứng trong giỏ
        selections.keySet()
                .removeIf(bookId ->
                        cart.getItems()
                                .stream()
                                .noneMatch(item ->
                                        item.getBook() != null
                                                && bookId.equals(
                                                item.getBook()
                                                        .getId()
                                        )
                                )
                );
    }

    // =========================================================
    // HELPER — TIỆN ÍCH
    // =========================================================

    // Coalesce null BigDecimal → ZERO — tránh NPE khi tính tổng
    private BigDecimal safeAmount(
            BigDecimal amount) {

        return amount == null
                ? BigDecimal.ZERO
                : amount;
    }

    // Lấy User entity từ Authentication — dùng chung mọi endpoint /cart
    private User getCurrentUser(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {
            // Chưa login — SecurityConfiguration thường chặn trước, đây là lớp phòng thủ
            throw new IllegalStateException(
                    "You need to log in to use the cart."
            );
        }

        User user =
                userService.getUserByEmail(
                        authentication.getName() // getName() = email (CustomUserDetailsService)
                );

        if (user == null) {
            // Email trong session không còn trong DB
            throw new IllegalStateException(
                    "User not found."
            );
        }

        return user; // Entity User đầy đủ từ bảng users
    }
}
