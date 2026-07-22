package shop.controller.client;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

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

        // Get ACTIVE vouchers
        model.addAttribute(
                "activeVouchers",
                voucherService.getActiveVouchers());

        if (refreshResult.hasWarnings() && !model.containsAttribute("warningMessage")) {
            model.addAttribute("warningMessage", String.join(" ", refreshResult.getWarnings()));
        }
        return "cart/index";
    }

    @PostMapping("/add")
    public Object addToCart(
            Authentication authentication,
            @RequestParam Long bookId,
            @RequestParam(required = false) String quantity,
            @RequestParam(required = false) String redirect,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        Integer parsedQuantity = cartService.parsePositiveIntegerQuantity(
                quantity != null ? quantity : "1");
        if (parsedQuantity == null) {
            return handleAddResponse(request, redirectAttributes, false, CartService.MSG_QUANTITY_INVALID, user,
                    redirectAfterAdd(bookId, redirect));
        }

        try {
            cartService.addBook(user.getId(), bookId, parsedQuantity);
            return handleAddResponse(request, redirectAttributes, true, "Book added to your cart.", user,
                    redirectAfterAdd(bookId, redirect));
        } catch (IllegalArgumentException ex) {
            return handleAddResponse(request, redirectAttributes, false, ex.getMessage(), user,
                    redirectAfterAdd(bookId, redirect));
        }
    }

    @PostMapping("/add-vpp")
    public Object addVppToCart(
            Authentication authentication,
            @RequestParam Long vppItemId,
            @RequestParam(required = false) String quantity,
            @RequestParam(required = false) String redirect,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        Integer parsedQuantity = cartService.parsePositiveIntegerQuantity(
                quantity != null ? quantity : "1");

        if (parsedQuantity == null) {
            return handleAddResponse(request, redirectAttributes, false, CartService.MSG_QUANTITY_INVALID, user,
                    redirectAfterAddVpp(redirect));
        }

        try {
            cartService.addVppItem(user.getId(), vppItemId, parsedQuantity);
            return handleAddResponse(request, redirectAttributes, true, "Stationery item added to your cart.", user,
                    redirectAfterAddVpp(redirect));
        } catch (IllegalArgumentException ex) {
            return handleAddResponse(request, redirectAttributes, false, ex.getMessage(), user,
                    redirectAfterAddVpp(redirect));
        }
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
            redirectAttributes.addFlashAttribute("successMessage", "Quantity updated.");
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
            redirectAttributes.addFlashAttribute("successMessage", "Item removed from your cart.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = getCurrentUser(authentication);
        cartService.clearCart(user.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Your cart has been cleared.");
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
    private String redirectAfterAddVpp(String redirect) {
    if (redirect != null && !redirect.isBlank()) {
        return "redirect:" + redirect;
    }

    return "redirect:/customer/vpp";
}

    private Object handleAddResponse(
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            boolean success,
            String message,
            User user,
            String redirectTarget) {

        if (isAjaxRequest(request)) {
            return cartAddJsonResponse(success, message, user.getId());
        }

        if (success) {
            redirectAttributes.addFlashAttribute("cartSuccessMessage", message);
        } else {
            redirectAttributes.addFlashAttribute("cartErrorMessage", message);
        }
        return redirectTarget;
    }

    private ResponseEntity<Map<String, Object>> cartAddJsonResponse(boolean success, String message, long userId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", success);
        body.put("message", message);
        body.put("cartItemCount", cartService.getCartItemCount(userId));
        return ResponseEntity.status(success ? 200 : 400).body(body);
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("You need to log in to use the cart.");
        }
        User user = userService.getUserByEmail(authentication.getName());
        if (user == null) {
            throw new IllegalStateException("User not found.");
        }
        return user;
    }
}
