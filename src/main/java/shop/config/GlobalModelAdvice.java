package shop.config;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import shop.domain.Category;
import shop.service.CartService;
import shop.service.CategoryService;
import shop.service.UserService;

@ControllerAdvice
public class GlobalModelAdvice {

    private final CategoryService categoryService;
    private final CartService cartService;
    private final UserService userService;

    public GlobalModelAdvice(CategoryService categoryService, CartService cartService, UserService userService) {
        this.categoryService = categoryService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @ModelAttribute("categories")
    public List<Category> categories() {
        return categoryService.getAllCategories();
    }

    @ModelAttribute("cartItemCount")
    public int cartItemCount(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return 0;
        }
        var user = userService.getUserByEmail(authentication.getName());
        if (user == null) {
            return 0;
        }
        return cartService.getCartItemCount(user.getId());
    }
}
