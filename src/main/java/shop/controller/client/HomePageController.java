package shop.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import shop.service.BookService;
import shop.service.CategoryService;

@Controller
public class HomePageController {

    private final CategoryService categoryService;
    private final BookService bookService;

    public HomePageController(CategoryService categoryService, BookService bookService) {
        this.categoryService = categoryService;
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String getHomePage(Model model, HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("fullName", session.getAttribute("fullName"));
            model.addAttribute("role", session.getAttribute("role"));
        }
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("featuredBooks", bookService.getActiveBooks());
        return "homepage/index";
    }
}
