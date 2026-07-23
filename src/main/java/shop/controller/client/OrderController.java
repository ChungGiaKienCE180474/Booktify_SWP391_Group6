package shop.controller.client;

import java.math.BigDecimal;

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
import shop.domain.OrderStatus;
import shop.domain.PaymentMethod;
import shop.domain.User;
import shop.domain.dto.CartDTO;
import shop.domain.dto.CartRefreshResult;
import shop.domain.dto.OrderDTO;
import shop.service.CartService;
import shop.service.OrderService;
import shop.service.UserService;
import shop.service.VoucherService;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    private final VoucherService voucherService;

    public OrderController(OrderService orderService, CartService cartService, UserService userService,
            VoucherService voucherService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
        this.voucherService = voucherService;
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
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot create an order with an empty cart.");
            return "redirect:/cart";
        }

        try {
            orderService.validateCheckoutForm(checkoutForm);
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("checkout.invalid", ex.getMessage());
        }

        if (bindingResult.hasErrors()) {
            BigDecimal discount = resolveDiscount(checkoutForm, refreshedCart.getTotalAmount(), model);
            populateCheckoutModel(model, cart, refreshedCart.getTotalAmount(), discount);
            model.addAttribute("checkoutForm", checkoutForm);
            return "order/checkout";
        }

        try {
            var order = orderService.createOrderFromCart(user.getId(), checkoutForm);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Order placed successfully! Order code: " + order.getOrderCode());
            return "redirect:/orders/" + order.getId();
        } catch (IllegalArgumentException ex) {
            BigDecimal discount = resolveDiscount(checkoutForm, refreshedCart.getTotalAmount(), model);
            populateCheckoutModel(model, cart, refreshedCart.getTotalAmount(), discount);
            model.addAttribute("checkoutForm", checkoutForm);
            model.addAttribute("errorMessage", ex.getMessage());
            return "order/checkout";
        }
    }

    @PostMapping("/apply-voucher")
    public String applyVoucher(
            Authentication authentication,
            @ModelAttribute("checkoutForm") CheckoutForm checkoutForm,
            Model model) {

        User user = getCurrentUser(authentication);
        CartRefreshResult refreshResult = cartService.refreshCartResult(user.getId());
        CartDTO cart = cartService.toCartDTO(refreshResult.getCart());
        BigDecimal subtotal = refreshResult.getCart().getTotalAmount();

        try {
            BigDecimal discount = voucherService.calculateDiscount(checkoutForm.getVoucherCode(), subtotal);
            model.addAttribute("discountAmountFormatted", orderService.formatMoney(discount));
            model.addAttribute("checkoutTotalFormatted", orderService.getCheckoutTotalFormatted(subtotal, discount));
            model.addAttribute("successMessage", "Voucher applied successfully!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("discountAmountFormatted", "0");
            model.addAttribute("checkoutTotalFormatted", orderService.getCheckoutTotalFormatted(subtotal, BigDecimal.ZERO));
        }

        model.addAttribute("checkoutForm", checkoutForm);
        model.addAttribute("cart", cart);
        model.addAttribute("paymentLabel", PaymentMethod.COD.getLabel());
        return "order/checkout";
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

        BigDecimal subtotal = refreshResult.getCart().getTotalAmount();
        model.addAttribute("cart", cart);
        model.addAttribute("paymentLabel", PaymentMethod.COD.getLabel());
        model.addAttribute("discountAmountFormatted", "0");
        model.addAttribute("checkoutTotalFormatted", orderService.getCheckoutTotalFormatted(subtotal, BigDecimal.ZERO));
        return "order/checkout";
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
                    model.addAttribute("canCancelOrder", canCancelOrder(order));
                    return "order/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Order not found.");
                    return "redirect:/orders";
                });
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(
            Authentication authentication,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        try {
            orderService.cancelOrderForUser(user.getId(), id);
            redirectAttributes.addFlashAttribute("successMessage", "Order cancelled successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    private BigDecimal resolveDiscount(CheckoutForm checkoutForm, BigDecimal subtotal, Model model) {
        if (checkoutForm.getVoucherCode() == null || checkoutForm.getVoucherCode().isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return voucherService.calculateDiscount(checkoutForm.getVoucherCode(), subtotal);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private void populateCheckoutModel(
            Model model,
            CartDTO cart,
            BigDecimal subtotal,
            BigDecimal discountAmount) {

        model.addAttribute("cart", cart);
        model.addAttribute("paymentLabel", PaymentMethod.COD.getLabel());
        model.addAttribute("discountAmountFormatted", orderService.formatMoney(discountAmount));
        model.addAttribute("checkoutTotalFormatted",
                orderService.getCheckoutTotalFormatted(subtotal, discountAmount));
    }

    private boolean canCancelOrder(OrderDTO order) {
        if (order == null || order.getStatus() == null) {
            return false;
        }
        try {
            return OrderStatus.valueOf(order.getStatus()).canBeCancelled();
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("You need to log in to place an order.");
        }
        User user = userService.getUserByEmail(authentication.getName());
        if (user == null) {
            throw new IllegalStateException("User not found.");
        }
        return user;
    }
}
