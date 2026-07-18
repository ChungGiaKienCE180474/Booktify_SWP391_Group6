package shop.controller.client;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

import shop.domain.Book;
import shop.domain.Promotion;
import shop.service.BookService;
import shop.service.CategoryService;
import shop.service.PromotionService;

@Controller
public class HomePageController {

    private final CategoryService categoryService;
    private final BookService bookService;
    private final PromotionService promotionService;

    public HomePageController(
            CategoryService categoryService,
            BookService bookService,
            PromotionService promotionService) {

        this.categoryService = categoryService;
        this.bookService = bookService;
        this.promotionService = promotionService;
    }

    @GetMapping("/")
    public String getHomePage(
            Model model,
            HttpServletRequest request) {

        var session = request.getSession(false);

        if (session != null) {
            model.addAttribute(
                    "username",
                    session.getAttribute("username")
            );

            model.addAttribute(
                    "fullName",
                    session.getAttribute("fullName")
            );

            model.addAttribute(
                    "role",
                    session.getAttribute("role")
            );
        }

        List<Book> featuredBooks =
                bookService.getActiveBooks();

        Map<Long, Promotion> bestPromotionMap =
                promotionService.getBestPromotionMap(
                        featuredBooks
                );

        model.addAttribute(
                "categories",
                categoryService.getAllCategories()
        );

        model.addAttribute(
                "featuredBooks",
                featuredBooks
        );

        model.addAttribute(
                "bestPromotionMap",
                bestPromotionMap
        );

        model.addAttribute(
                "discountedPriceFormattedMap",
                promotionService
                        .getDiscountedPriceFormattedMap(
                                featuredBooks,
                                bestPromotionMap
                        )
        );

        model.addAttribute(
                "discountLabelMap",
                promotionService.getDiscountLabelMap(
                        featuredBooks,
                        bestPromotionMap
                )
        );

        return "homepage/index";
    }
}