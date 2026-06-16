package shop.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import shop.service.BookService;
import shop.service.CategoryService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final CategoryService categoryService;
    private final BookService bookService;

    public AdminDashboardController(CategoryService categoryService, BookService bookService) {
        this.categoryService = categoryService;
        this.bookService = bookService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalCategories", categoryService.countCategories());
        model.addAttribute("activeCategories", categoryService.countActiveCategories());
        model.addAttribute("totalBooks", bookService.countBooks());
        model.addAttribute("activeBooks", bookService.countActiveBooks());
        return "admin/dashboard/index";
    }
}
