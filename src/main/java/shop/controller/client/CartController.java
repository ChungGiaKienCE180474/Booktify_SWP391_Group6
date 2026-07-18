package shop.controller.client;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.User;
import shop.domain.dto.CartRefreshResult;
import shop.service.CartService;
import shop.service.UserService;
import shop.service.VoucherService;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    // Voucher
    private final VoucherService voucherService;

    public CartController(CartService cartService, UserService userService, VoucherService voucherService) {
        this.cartService = cartService;
        this.userService = userService;
        this.voucherService = voucherService;
    }

    @GetMapping
    public String viewCart(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        CartRefreshResult refreshResult = cartService.refreshCartResult(user.getId());
        model.addAttribute("cart", cartService.toCartDTO(refreshResult.getCart()));

        //Lấy ACTIVE vouchers
        model.addAttribute(
                "activeVouchers",
                voucherService.getActiveVouchers());

        if (refreshResult.hasWarnings() && !model.containsAttribute("warningMessage")) {
            model.addAttribute("warningMessage", String.join(" ", refreshResult.getWarnings()));
        }
        return "cart/index";
    }

    @PostMapping("/add")
    public String addToCart(
            Authentication authentication,
            @RequestParam Long bookId,
            @RequestParam(required = false) String quantity,
            @RequestParam(required = false) String redirect,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        Integer parsedQuantity = cartService.parsePositiveIntegerQuantity(
                quantity != null ? quantity : "1");
        if (parsedQuantity == null) {
            redirectAttributes.addFlashAttribute("errorMessage", CartService.MSG_QUANTITY_INVALID);
            return redirectAfterAdd(bookId, redirect);
        }

        try {
            cartService.addBook(user.getId(), bookId, parsedQuantity);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sách vào giỏ hàng.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return redirectAfterAdd(bookId, redirect);
    }

    @PostMapping("/update")
    public String updateQuantity(
            Authentication authentication,
            @RequestParam Long itemId,
            @RequestParam(required = false) String quantity,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        Integer parsedQuantity = cartService.parsePositiveIntegerQuantity(quantity);
        if (parsedQuantity == null) {
            redirectAttributes.addFlashAttribute("errorMessage", CartService.MSG_QUANTITY_INVALID);
            return "redirect:/cart";
        }

        try {
            cartService.updateQuantity(user.getId(), itemId, parsedQuantity);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật số lượng.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(
            Authentication authentication,
            @RequestParam Long itemId,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        try {
            cartService.removeItem(user.getId(), itemId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(authentication);
        cartService.clearCart(user.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa toàn bộ giỏ hàng.");
        return "redirect:/cart";
    }

    @PostMapping("/validate")
    public String validateCheckout(Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(authentication);
        cartService.refreshCart(user.getId());
        List<String> errors = cartService.validateForCheckout(user.getId());

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", String.join(" ", errors));
            return "redirect:/cart";
        }

        return "redirect:/orders/checkout";
    }

    private String redirectAfterAdd(Long bookId, String redirect) {
        if (redirect != null && !redirect.isBlank()) {
            return "redirect:" + redirect;
        }
        return "redirect:/books/" + bookId;
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Bạn cần đăng nhập để sử dụng giỏ hàng.");
        }
        User user = userService.getUserByEmail(authentication.getName());
        if (user == null) {
            throw new IllegalStateException("Không tìm thấy người dùng.");
        }
        return user;
    }
}
