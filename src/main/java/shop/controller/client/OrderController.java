package shop.controller.client;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import shop.domain.CheckoutForm;
import shop.domain.PaymentMethod;
import shop.domain.User;
import shop.domain.dto.CartDTO;
import shop.domain.dto.CartRefreshResult;
import shop.service.CartService;
import shop.service.OrderService;
import shop.service.UserService;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    public OrderController(OrderService orderService, CartService cartService, UserService userService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/checkout")
    public String checkoutForm(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        CartRefreshResult refreshResult = cartService.refreshCartResult(user.getId());
        CartDTO cart = cartService.toCartDTO(refreshResult.getCart());

        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        if (!model.containsAttribute("checkoutForm")) {
            model.addAttribute("checkoutForm", orderService.buildCheckoutFormFromUser(user));
        }
        populateCheckoutModel(model, cart, refreshResult.getCart().getTotalAmount());
        if (refreshResult.hasWarnings()) {
            model.addAttribute("warningMessage", String.join(" ", refreshResult.getWarnings()));
        }
        return "order/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(
            Authentication authentication,
            @Valid @ModelAttribute("checkoutForm") CheckoutForm checkoutForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        var refreshedCart = cartService.refreshCart(user.getId());
        CartDTO cart = cartService.toCartDTO(refreshedCart);

        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể tạo đơn hàng khi giỏ hàng trống.");
            return "redirect:/cart";
        }

        try {
            orderService.validateCheckoutForm(checkoutForm);
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("checkout.invalid", ex.getMessage());
        }

        if (bindingResult.hasErrors()) {
            populateCheckoutModel(model, cart, refreshedCart.getTotalAmount());
            return "order/checkout";
        }

        try {
            var order = orderService.createOrderFromCart(user.getId(), checkoutForm);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đặt hàng thành công! Mã đơn: " + order.getOrderCode());
            return "redirect:/orders/" + order.getId();
        } catch (IllegalArgumentException ex) {
            populateCheckoutModel(model, cart, refreshedCart.getTotalAmount());
            model.addAttribute("errorMessage", ex.getMessage());
            return "order/checkout";
        }
    }

    @GetMapping
    public String orderHistory(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        model.addAttribute("orders", orderService.getOrdersForUser(user.getId()));
        return "order/list";
    }

    @GetMapping("/{id}")
    public String orderDetail(
            Authentication authentication,
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        return orderService.getOrderForUser(user.getId(), id)
                .map(order -> {
                    model.addAttribute("order", order);
                    return "order/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng.");
                    return "redirect:/orders";
                });
    }

    private void populateCheckoutModel(Model model, CartDTO cart, java.math.BigDecimal subtotal) {
        model.addAttribute("cart", cart);
        model.addAttribute("paymentLabel", PaymentMethod.COD.getLabel());
        model.addAttribute("shippingFeeFormatted", orderService.getCodShippingFeeFormatted());
        model.addAttribute("checkoutTotalFormatted", orderService.getCheckoutTotalFormatted(subtotal));
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Bạn cần đăng nhập để đặt hàng.");
        }
        User user = userService.getUserByEmail(authentication.getName());
        if (user == null) {
            throw new IllegalStateException("Không tìm thấy người dùng.");
        }
        return user;
    }
}
