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

import shop.domain.Cart;
import shop.domain.User;
import shop.service.CartService;
import shop.service.UserService;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public String viewCart(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        Cart cart = cartService.refreshCart(user.getId());
        model.addAttribute("cart", cart);
        return "cart/index";
    }

    @PostMapping("/add")
    public String addToCart(
            Authentication authentication,
            @RequestParam Long bookId,
            @RequestParam(defaultValue = "1") int quantity,
            @RequestParam(required = false) String redirect,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        try {
            cartService.addBook(user.getId(), bookId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sách vào giỏ hàng.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        if (redirect != null && !redirect.isBlank()) {
            return "redirect:" + redirect;
        }
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/update")
    public String updateQuantity(
            Authentication authentication,
            @RequestParam Long itemId,
            @RequestParam int quantity,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        try {
            cartService.updateQuantity(user.getId(), itemId, quantity);
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

    @PostMapping("/validate")
    public String validateCheckout(Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(authentication);
        cartService.refreshCart(user.getId());
        List<String> errors = cartService.validateForCheckout(user.getId());

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", String.join(" ", errors));
            return "redirect:/cart";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Giỏ hàng hợp lệ. Chức năng thanh toán sẽ được triển khai tiếp theo.");
        return "redirect:/cart";
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
