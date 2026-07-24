package shop.controller.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
import shop.service.VoucherService;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;
    private final VoucherService voucherService;
    private final PromotionService promotionService;

    public OrderController(
            OrderService orderService,
            CartService cartService,
            UserService userService,
            VoucherService voucherService,
            PromotionService promotionService) {

        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
        this.voucherService = voucherService;
        this.promotionService = promotionService;
    }

    // =========================================================
    // PLACE ORDER
    // =========================================================

    @PostMapping("/checkout")
    public String placeOrder(
            Authentication authentication,
            @Valid
            @ModelAttribute("checkoutForm")
            CheckoutForm checkoutForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        User user =
                getCurrentUser(authentication);

        applyCartPromotionSelections(checkoutForm, session);

        CartRefreshResult refreshResult =
                cartService.refreshCartResult(
                        user.getId()
                );

        Cart refreshedCart =
                refreshResult.getCart();

        CartDTO cart =
                cartService.toCartDTO(
                        refreshedCart
                );

        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot create an order with an empty cart."
            );

            return "redirect:/cart";
        }

        try {
            orderService.validateCheckoutForm(
                    checkoutForm
            );
        } catch (IllegalArgumentException exception) {
            bindingResult.reject(
                    "checkout.invalid",
                    exception.getMessage()
            );
        }

        if (bindingResult.hasErrors()) {
            populateCheckoutModel(
                    model,
                    cart,
                    refreshedCart,
                    checkoutForm
            );

            model.addAttribute(
                    "checkoutForm",
                    checkoutForm
            );

            return "order/checkout";
        }

        try {
            OrderDTO order =
                    orderService.createOrderFromCart(
                            user.getId(),
                            checkoutForm
                    );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Order placed successfully! Order code: "
                            + order.getOrderCode()
            );

            session.removeAttribute(
                    CartController.PROMOTION_SESSION_KEY
            );

            return "redirect:/orders/"
                    + order.getId();

        } catch (IllegalArgumentException exception) {
            populateCheckoutModel(
                    model,
                    cart,
                    refreshedCart,
                    checkoutForm
            );

            model.addAttribute(
                    "checkoutForm",
                    checkoutForm
            );

            model.addAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            return "order/checkout";
        }
    }

    // =========================================================
    // APPLY VOUCHER
    // =========================================================

    @PostMapping("/apply-voucher")
    public String applyVoucher(
            Authentication authentication,
            @ModelAttribute("checkoutForm")
            CheckoutForm checkoutForm,
            Model model,
            HttpSession session) {

        User user =
                getCurrentUser(authentication);

        applyCartPromotionSelections(checkoutForm, session);

        CartRefreshResult refreshResult =
                cartService.refreshCartResult(
                        user.getId()
                );

        CartDTO cart =
                cartService.toCartDTO(
                        refreshResult.getCart()
                );

        try {
            populateCheckoutModel(
                    model,
                    cart,
                    refreshResult.getCart(),
                    checkoutForm
            );

            model.addAttribute(
                    "successMessage",
                    "Voucher applied successfully!"
            );

        } catch (IllegalArgumentException exception) {
            checkoutForm.setVoucherCode(null);

            populateCheckoutModel(
                    model,
                    cart,
                    refreshResult.getCart(),
                    checkoutForm
            );

            model.addAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        model.addAttribute(
                "checkoutForm",
                checkoutForm
        );

        return "order/checkout";
    }

    // =========================================================
    // PREVIEW PROMOTION
    // =========================================================

    @PostMapping("/preview-promotion")
    @ResponseBody
    public ResponseEntity<PromotionPreviewDTO>
    previewPromotion(
            Authentication authentication,
            @ModelAttribute
            CheckoutForm checkoutForm) {

        User user =
                getCurrentUser(authentication);

        CartRefreshResult refreshResult =
                cartService.refreshCartResult(
                        user.getId()
                );

        Cart cart =
                refreshResult.getCart();

        BigDecimal originalSubtotal =
                safeAmount(
                        cart.getTotalAmount()
                );

        BigDecimal promotionSubtotal =
                promotionService
                        .calculateCartTotalBySelections(
                                cart,
                                checkoutForm
                                        .getBookPromotionSelections()
                        );

        BigDecimal promotionDiscount =
                originalSubtotal
                        .subtract(promotionSubtotal)
                        .max(BigDecimal.ZERO);

        BigDecimal voucherDiscount =
                BigDecimal.ZERO;

        String message =
                "Promotion prices updated.";

        try {
            String voucherCode =
                    checkoutForm.getVoucherCode();

            if (voucherCode != null
                    && !voucherCode.isBlank()) {

                voucherDiscount =
                        voucherService.calculateDiscount(
                                voucherCode,
                                promotionSubtotal
                        );

                message =
                        "Promotion and voucher prices updated.";
            }

        } catch (IllegalArgumentException exception) {
            PromotionPreviewDTO errorResponse =
                    buildPreviewResponse(
                            originalSubtotal,
                            promotionDiscount,
                            BigDecimal.ZERO,
                            promotionSubtotal,
                            exception.getMessage()
                    );

            return ResponseEntity
                    .badRequest()
                    .body(errorResponse);
        }

        BigDecimal finalTotal =
                promotionSubtotal
                        .subtract(voucherDiscount)
                        .max(BigDecimal.ZERO);

        PromotionPreviewDTO response =
                buildPreviewResponse(
                        originalSubtotal,
                        promotionDiscount,
                        voucherDiscount,
                        finalTotal,
                        message
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // SHOW CHECKOUT
    // =========================================================

    @GetMapping("/checkout")
    public String checkoutForm(
            Authentication authentication,
            Model model,
            HttpSession session) {

        User user =
                getCurrentUser(authentication);

        CartRefreshResult refreshResult =
                cartService.refreshCartResult(
                        user.getId()
                );

        CartDTO cart =
                cartService.toCartDTO(
                        refreshResult.getCart()
                );

        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        CheckoutForm checkoutForm;

        if (!model.containsAttribute(
                "checkoutForm")) {

            checkoutForm =
                    orderService
                            .buildCheckoutFormFromUser(
                                    user
                            );

            model.addAttribute(
                    "checkoutForm",
                    checkoutForm
            );

        } else {
            checkoutForm =
                    (CheckoutForm)
                            model.getAttribute(
                                    "checkoutForm"
                            );
        }

        applyCartPromotionSelections(
                checkoutForm,
                session
        );

        initializePromotionSelections(
                refreshResult.getCart(),
                checkoutForm
        );

        populateCheckoutModel(
                model,
                cart,
                refreshResult.getCart(),
                checkoutForm
        );

        return "order/checkout";
    }

    // =========================================================
    // ORDER HISTORY
    // =========================================================

    @GetMapping
    public String orderHistory(
            Authentication authentication,
            Model model) {

        User user =
                getCurrentUser(authentication);

        model.addAttribute(
                "orders",
                orderService.getOrdersForUser(
                        user.getId()
                )
        );

        return "order/list";
    }

    // =========================================================
    // ORDER DETAIL
    // =========================================================

    @GetMapping("/{id:\\d+}")
    public String orderDetail(
            Authentication authentication,
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        User user =
                getCurrentUser(authentication);

        return orderService
                .getOrderForUser(
                        user.getId(),
                        id
                )
                .map(order -> {
                    model.addAttribute(
                            "order",
                            order
                    );

                    model.addAttribute(
                            "canCancelOrder",
                            canCancelOrder(order)
                    );

                    return "order/detail";
                })
                .orElseGet(() -> {
                    redirectAttributes
                            .addFlashAttribute(
                                    "errorMessage",
                                    "Order not found."
                            );

                    return "redirect:/orders";
                });
    }

    // =========================================================
    // CANCEL ORDER
    // =========================================================

    @PostMapping("/{id:\\d+}/cancel")
    public String cancelOrder(
            Authentication authentication,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        User user =
                getCurrentUser(authentication);

        try {
            orderService.cancelOrderForUser(
                    user.getId(),
                    id
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Order cancelled successfully."
            );

        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
        }

        return "redirect:/orders/" + id;
    }

    // =========================================================
    // CHECKOUT MODEL
    // =========================================================

    private void populateCheckoutModel(
            Model model,
            CartDTO cart,
            Cart rawCart,
            CheckoutForm checkoutForm) {

        if (checkoutForm == null) {
            checkoutForm =
                    new CheckoutForm();
        }

        Map<Long, Promotion>
                percentagePromotionMap =
                new HashMap<>();

        Map<Long, Promotion>
                fixedPromotionMap =
                new HashMap<>();

        if (rawCart != null
                && rawCart.getItems() != null) {

            rawCart.getItems().forEach(
                    cartItem -> {

                        Book book =
                                cartItem.getBook();

                        if (book == null
                                || book.getId() == null) {
                            return;
                        }

                        promotionService
                                .getActivePromotionForBookByType(
                                        book,
                                        true
                                )
                                .ifPresent(
                                        promotion ->
                                                percentagePromotionMap
                                                        .put(
                                                                book.getId(),
                                                                promotion
                                                        )
                                );

                        promotionService
                                .getActivePromotionForBookByType(
                                        book,
                                        false
                                )
                                .ifPresent(
                                        promotion ->
                                                fixedPromotionMap
                                                        .put(
                                                                book.getId(),
                                                                promotion
                                                        )
                                );
                    }
            );
        }

        BigDecimal originalSubtotal =
                safeAmount(
                        rawCart.getTotalAmount()
                );

        BigDecimal promotionSubtotal =
                promotionService
                        .calculateCartTotalBySelections(
                                rawCart,
                                checkoutForm
                                        .getBookPromotionSelections()
                        );

        BigDecimal promotionDiscount =
                originalSubtotal
                        .subtract(promotionSubtotal)
                        .max(BigDecimal.ZERO);

        BigDecimal voucherDiscount =
                BigDecimal.ZERO;

        String voucherCode =
                checkoutForm.getVoucherCode();

        if (voucherCode != null
                && !voucherCode.isBlank()) {

            voucherDiscount =
                    voucherService.calculateDiscount(
                            voucherCode,
                            promotionSubtotal
                    );
        }

        BigDecimal finalTotal =
                promotionSubtotal
                        .subtract(voucherDiscount)
                        .max(BigDecimal.ZERO);

        model.addAttribute(
                "cart",
                cart
        );

        model.addAttribute(
                "paymentLabel",
                PaymentMethod.COD.getLabel()
        );

        model.addAttribute(
                "percentagePromotionMap",
                percentagePromotionMap
        );

        model.addAttribute(
                "fixedPromotionMap",
                fixedPromotionMap
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
                "discountAmountFormatted",
                orderService.formatMoney(
                        voucherDiscount
                )
        );

        model.addAttribute(
                "checkoutTotalFormatted",
                orderService.formatMoney(
                        finalTotal
                )
        );
    }

    // =========================================================
    // DEFAULT PROMOTION SELECTIONS
    // =========================================================

    private void initializePromotionSelections(
            Cart rawCart,
            CheckoutForm checkoutForm) {

        if (rawCart == null
                || rawCart.getItems() == null
                || checkoutForm == null) {
            return;
        }

        Map<Long, String> selections =
                checkoutForm
                        .getBookPromotionSelections();

        rawCart.getItems().forEach(
                cartItem -> {
                    Book book =
                            cartItem.getBook();

                    if (book == null
                            || book.getId() == null) {
                        return;
                    }

                    selections.putIfAbsent(
                            book.getId(),
                            PromotionSelection.NONE.name()
                    );
                }
        );
    }

    @SuppressWarnings("unchecked")
    private void applyCartPromotionSelections(
            CheckoutForm checkoutForm,
            HttpSession session) {

        if (checkoutForm == null || session == null) {
            return;
        }

        Object value = session.getAttribute(
                CartController.PROMOTION_SESSION_KEY
        );

        if (value instanceof Map<?, ?>) {
            checkoutForm.setBookPromotionSelections(
                    new HashMap<>((Map<Long, String>) value)
            );
        }
    }

    // =========================================================
    // PREVIEW RESPONSE
    // =========================================================

    private PromotionPreviewDTO
    buildPreviewResponse(
            BigDecimal originalSubtotal,
            BigDecimal promotionDiscount,
            BigDecimal voucherDiscount,
            BigDecimal finalTotal,
            String message) {

        return new PromotionPreviewDTO(
                "PER_ITEM",
                orderService.formatMoney(
                        originalSubtotal
                ),
                orderService.formatMoney(
                        promotionDiscount
                ),
                orderService.formatMoney(
                        voucherDiscount
                ),
                orderService.formatMoney(
                        finalTotal
                ),
                message
        );
    }

    private BigDecimal safeAmount(
            BigDecimal amount) {

        return amount == null
                ? BigDecimal.ZERO
                : amount;
    }

    private boolean canCancelOrder(
            OrderDTO order) {

        if (order == null
                || order.getStatus() == null) {
            return false;
        }

        try {
            return OrderStatus
                    .valueOf(
                            order.getStatus()
                    )
                    .canBeCancelled();

        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private User getCurrentUser(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "You need to log in to place an order."
            );
        }

        User user =
                userService.getUserByEmail(
                        authentication.getName()
                );

        if (user == null) {
            throw new IllegalStateException(
                    "User not found."
            );
        }

        return user;
    }
}


