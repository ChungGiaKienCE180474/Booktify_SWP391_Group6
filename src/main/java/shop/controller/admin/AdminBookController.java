package shop.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import shop.domain.Book;
import shop.service.BookService;
import shop.service.CategoryService;

@Controller
@RequestMapping("/admin/books")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookController {

    private final BookService bookService;
    private final CategoryService categoryService;

    public AdminBookController(BookService bookService, CategoryService categoryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("books", bookService.searchBooks(q));
        model.addAttribute("q", q);
        return "admin/book/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formMode", "create");
        return "admin/book/form";
    }

    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            Model model, RedirectAttributes redirectAttributes) {

        if (StringUtils.hasText(book.getIsbn()) && bookService.existsByIsbnIgnoreCase(book.getIsbn())) {
            bindingResult.rejectValue("isbn", "book.isbn.exists", "ISBN already exists");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("formMode", "create");
            return "admin/book/form";
        }

        if (categoryId != null) {
            categoryService.getCategoryById(categoryId).ifPresent(book::setCategory);
        }
        if (!StringUtils.hasText(book.getIsbn())) {
            book.setIsbn(null);
        }

        bookService.saveBook(book);
        redirectAttributes.addFlashAttribute("successMessage", "Book created successfully.");
        return "redirect:/admin/books";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formMode", "edit");
        return "admin/book/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            Model model, RedirectAttributes redirectAttributes) {

        Book existing = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        boolean isbnChanged = StringUtils.hasText(book.getIsbn())
                && !book.getIsbn().equalsIgnoreCase(existing.getIsbn());
        if (isbnChanged && bookService.existsByIsbnIgnoreCase(book.getIsbn())) {
            bindingResult.rejectValue("isbn", "book.isbn.exists", "ISBN already exists");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("formMode", "edit");
            return "admin/book/form";
        }

        existing.setTitle(book.getTitle());
        existing.setAuthor(book.getAuthor());
        existing.setIsbn(StringUtils.hasText(book.getIsbn()) ? book.getIsbn() : null);
        existing.setDescription(book.getDescription());
        existing.setPrice(book.getPrice());
        existing.setStockQuantity(book.getStockQuantity());
        existing.setImageUrl(book.getImageUrl());
        existing.setActive(book.isActive());
        existing.setCategory(categoryId != null
                ? categoryService.getCategoryById(categoryId).orElse(null)
                : null);

        bookService.saveBook(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully.");
        return "redirect:/admin/books";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.softDeleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred while deleting the book.");
        }
        return "redirect:/admin/books";
    }
}
