package shop.controller.client;

import java.math.BigDecimal;
import java.util.HashMap;
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

@Controller
@RequestMapping("/cart")
public class CartController {

    public static final String PROMOTION_SESSION_KEY =
            "cartPromotionSelections";

    private final CartService cartService;
    private final UserService userService;
    private final VoucherService voucherService;
    private final PromotionService promotionService;
    private final OrderService orderService;

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

    @GetMapping
    public String viewCart(
            Authentication authentication,
            Model model,
            HttpSession session) {

        User user = getCurrentUser(authentication);

        CartRefreshResult refreshResult =
                cartService.refreshCartResult(user.getId());

        Cart cart = refreshResult.getCart();

        model.addAttribute(
                "cart",
                cartService.toCartDTO(cart)
        );

        Map<Long, Promotion> percentagePromotionMap =
                new HashMap<>();

        Map<Long, Promotion> fixedPromotionMap =
                new HashMap<>();

        if (cart != null && cart.getItems() != null) {
            cart.getItems().forEach(cartItem -> {

                Book book = cartItem.getBook();

                if (book == null || book.getId() == null) {
                    return;
                }

                promotionService
                        .getActivePromotionForBookByType(
                                book,
                                true
                        )
                        .ifPresent(promotion ->
                                percentagePromotionMap.put(
                                        book.getId(),
                                        promotion
                                )
                        );

                promotionService
                        .getActivePromotionForBookByType(
                                book,
                                false
                        )
                        .ifPresent(promotion ->
                                fixedPromotionMap.put(
                                        book.getId(),
                                        promotion
                                )
                        );
            });
        }

        Map<Long, String> selectedPromotionMap =
                getPromotionSelections(session);

        removeSelectionsNotInCart(
                cart,
                selectedPromotionMap
        );

        Map<Long, String> selectedUnitPriceMap =
                new HashMap<>();

        Map<Long, String> selectedSubtotalMap =
                new HashMap<>();

        if (cart != null && cart.getItems() != null) {
            cart.getItems().forEach(cartItem -> {

                Book book = cartItem.getBook();

                if (book == null || book.getId() == null) {
                    return;
                }

                PromotionSelection selection =
                        PromotionSelection.fromValue(
                                selectedPromotionMap.get(
                                        book.getId()
                                )
                        );

                BigDecimal effectivePrice =
                        promotionService.getPriceForSelection(
                                book,
                                selection
                        );

                BigDecimal lineTotal =
                        effectivePrice.multiply(
                                BigDecimal.valueOf(
                                        cartItem.getQuantity()
                                )
                        );

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

        BigDecimal originalSubtotal =
                safeAmount(
                        cart == null
                                ? null
                                : cart.getTotalAmount()
                );

        BigDecimal promotionSubtotal =
                promotionService
                        .calculateCartTotalBySelections(
                                cart,
                                selectedPromotionMap
                        );

        BigDecimal promotionDiscount =
                originalSubtotal
                        .subtract(promotionSubtotal)
                        .max(BigDecimal.ZERO);

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

        model.addAttribute(
                "activeVouchers",
                voucherService.getActiveVouchers()
        );

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

        return "cart/index";
    }

    @PostMapping("/add")
    public Object addToCart(
            Authentication authentication,
            @RequestParam Long bookId,
            @RequestParam(required = false)
            String quantity,
            @RequestParam(required = false)
            String redirect,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        User user =
                getCurrentUser(authentication);

        Integer parsedQuantity =
                cartService.parsePositiveIntegerQuantity(
                        quantity != null
                                ? quantity
                                : "1"
                );

        if (parsedQuantity == null) {
            return handleAddResponse(
                    request,
                    redirectAttributes,
                    false,
                    CartService.MSG_QUANTITY_INVALID,
                    user,
                    redirectAfterAdd(
                            bookId,
                            redirect
                    )
            );
        }

        try {
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

    @PostMapping("/add-vpp")
    public Object addVppToCart(
            Authentication authentication,
            @RequestParam Long vppItemId,
            @RequestParam(required = false)
            String quantity,
            @RequestParam(required = false)
            String redirect,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        User user =
                getCurrentUser(authentication);

        Integer parsedQuantity =
                cartService.parsePositiveIntegerQuantity(
                        quantity != null
                                ? quantity
                                : "1"
                );

        if (parsedQuantity == null) {
            return handleAddResponse(
                    request,
                    redirectAttributes,
                    false,
                    CartService.MSG_QUANTITY_INVALID,
                    user,
                    redirectAfterAddVpp(
                            redirect
                    )
            );
        }

        try {
            cartService.addVppItem(
                    user.getId(),
                    vppItemId,
                    parsedQuantity
            );

            return handleAddResponse(
                    request,
                    redirectAttributes,
                    true,
                    "Stationery item added to your cart.",
                    user,
                    redirectAfterAddVpp(
                            redirect
                    )
            );

        } catch (IllegalArgumentException exception) {
            return handleAddResponse(
                    request,
                    redirectAttributes,
                    false,
                    exception.getMessage(),
                    user,
                    redirectAfterAddVpp(
                            redirect
                    )
            );
        }
    }

    @PostMapping("/update")
    public String updateQuantity(
            Authentication authentication,
            @RequestParam Long itemId,
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

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(
            Authentication authentication,
            @RequestParam Long itemId,
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

        session.removeAttribute(
                PROMOTION_SESSION_KEY
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Your cart has been cleared."
        );

        return "redirect:/cart";
    }

    @PostMapping("/promotion")
    public String updatePromotion(
            Authentication authentication,
            @RequestParam Long bookId,
            @RequestParam String selection,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User user =
                getCurrentUser(authentication);

        Cart cart =
                cartService
                        .refreshCartResult(
                                user.getId()
                        )
                        .getCart();

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

        PromotionSelection selected =
                PromotionSelection.fromValue(
                        selection
                );

        Map<Long, String> selections =
                getPromotionSelections(
                        session
                );

        selections.put(
                bookId,
                selected.name()
        );

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

    @PostMapping("/validate")
    public String validateCheckout(
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user =
                getCurrentUser(authentication);

        cartService.refreshCart(
                user.getId()
        );

        List<String> errors =
                cartService.validateForCheckout(
                        user.getId()
                );

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    String.join(
                            " ",
                            errors
                    )
            );

            return "redirect:/cart";
        }

        return "redirect:/orders/checkout";
    }

    private String redirectAfterAdd(
            Long bookId,
            String redirect) {

        if (redirect != null
                && !redirect.isBlank()) {

            return "redirect:" + redirect;
        }

        return "redirect:/books/" + bookId;
    }

    private String redirectAfterAddVpp(
            String redirect) {

        if (redirect != null
                && !redirect.isBlank()) {

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
            return cartAddJsonResponse(
                    success,
                    message,
                    user.getId()
            );
        }

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

    private ResponseEntity<Map<String, Object>>
    cartAddJsonResponse(
            boolean success,
            String message,
            long userId) {

        Map<String, Object> body =
                new LinkedHashMap<>();

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
                        userId
                )
        );

        return ResponseEntity
                .status(
                        success
                                ? 200
                                : 400
                )
                .body(body);
    }

    private boolean isAjaxRequest(
            HttpServletRequest request) {

        return "XMLHttpRequest".equals(
                request.getHeader(
                        "X-Requested-With"
                )
        );
    }

    @SuppressWarnings("unchecked")
    private Map<Long, String>
    getPromotionSelections(
            HttpSession session) {

        Object value =
                session.getAttribute(
                        PROMOTION_SESSION_KEY
                );

        if (value instanceof Map<?, ?>) {
            return (Map<Long, String>) value;
        }

        Map<Long, String> selections =
                new HashMap<>();

        session.setAttribute(
                PROMOTION_SESSION_KEY,
                selections
        );

        return selections;
    }

    private void removeSelectionsNotInCart(
            Cart cart,
            Map<Long, String> selections) {

        if (selections == null
                || selections.isEmpty()) {

            return;
        }

        if (cart == null
                || cart.getItems() == null) {

            selections.clear();
            return;
        }

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

    private BigDecimal safeAmount(
            BigDecimal amount) {

        return amount == null
                ? BigDecimal.ZERO
                : amount;
    }

    private User getCurrentUser(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "You need to log in to use the cart."
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