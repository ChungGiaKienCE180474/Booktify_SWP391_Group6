package shop.controller.client;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.domain.Book;
import shop.service.BookService;
import shop.service.CategoryService;

@Controller
@RequestMapping("/books")
public class ClientBookController {

    private final BookService bookService;
    private final CategoryService categoryService;

    public ClientBookController(BookService bookService, CategoryService categoryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    /**
     * View List Of Books — optional filter by category, keyword search, sort.
     * Params: categoryId, q (keyword), sort (price_asc|price_desc|newest)
     */
    @GetMapping
    public String list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "default") String sort,
            Model model) {

        List<Book> books;

        if (StringUtils.hasText(q)) {
            // Keyword search across all active books (uses existing BookService.searchBooks
            // but filtered to active only)
            books = bookService.searchBooks(q).stream()
                    .filter(Book::isActive)
                    .collect(java.util.stream.Collectors.toList());
        } else if (categoryId != null) {
            books = bookService.getBooksByCategory(categoryId);
            model.addAttribute("selectedCategory",
                    categoryService.getCategoryById(categoryId).orElse(null));
        } else {
            books = bookService.getActiveBooks();
        }

        // Apply sort
        switch (sort) {
            case "price_asc"  -> books.sort(Comparator.comparing(Book::getPrice));
            case "price_desc" -> books.sort(Comparator.comparing(Book::getPrice).reversed());
            // "newest" and "default" keep the id-asc order from repository
        }

        model.addAttribute("books", books);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        return "book/list";
    }

    /** View Book Details + Suggested Books */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id)
                .filter(Book::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        Long catId = book.getCategory() != null ? book.getCategory().getId() : null;
        model.addAttribute("book", book);
        model.addAttribute("suggestedBooks", bookService.getSuggestedBooks(catId, id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/detail";
    }

    /** View List Of Products — alias */
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("books", bookService.getActiveBooks());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/list";
    }
}
