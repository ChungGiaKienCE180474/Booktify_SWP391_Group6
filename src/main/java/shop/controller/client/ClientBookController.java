package shop.controller.client;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.domain.Book;
import shop.service.AuthorService;
import shop.service.BookService;
import shop.service.CategoryService;
import shop.service.GenreService;

import shop.domain.Rating;
import shop.service.RatingService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.User;
import shop.repository.UserRepository;

@Controller
@RequestMapping("/books")
public class ClientBookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final RatingService ratingService;
    private final UserRepository userRepository;
    private final GenreService genreService;
    private final AuthorService authorService;

    public ClientBookController(BookService bookService,
                                 CategoryService categoryService,
                                 RatingService ratingService,
                                 UserRepository userRepository,
                                 GenreService genreService,
                                 AuthorService authorService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.ratingService = ratingService;
        this.userRepository = userRepository;
        this.genreService = genreService;
        this.authorService = authorService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) List<Long> genreIds,
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "default") String sort,
            Model model) {

        // Status is hardcoded to "active" — customers should never see hidden books.
        List<Book> books = bookService.filterBooks(q, categoryId, genreIds, "active");

        if (categoryId != null) {
            model.addAttribute("selectedCategory",
                    categoryService.getCategoryById(categoryId).orElse(null));
        }

        switch (sort) {
            case "price_asc" -> books.sort(Comparator.comparing(Book::getPrice));
            case "price_desc" -> books.sort(Comparator.comparing(Book::getPrice).reversed());
            // "newest" and "default" both keep the id-asc order from the repository.
        }

        model.addAttribute("books", books);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("genres", genreService.getActiveGenres());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedGenreIds", genreIds == null ? List.of() : genreIds);
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        return "book/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication authentication) {
        Book book = bookService.getBookById(id)
                .filter(Book::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        Long catId = book.getCategory() != null ? book.getCategory().getId() : null;
        model.addAttribute("book", book);
        model.addAttribute("ratings", ratingService.getRatingsByBook(id));
        model.addAttribute("canReview", false);

        if (authentication != null) {

            User user = userRepository.findByEmail(authentication.getName());

            if (user != null) {

                model.addAttribute("currentUser", user);

                model.addAttribute(
                        "myRating",
                        ratingService.getCustomerRating(
                                id,
                                user.getId()).orElse(null));

                model.addAttribute(
                        "canReview",
                        ratingService.canCustomerReview(
                                id,
                                user.getId()));
            }
        }
        model.addAttribute("suggestedBooks", bookService.getSuggestedBooks(catId, id));
        model.addAttribute("categories", categoryService.getAllCategories());
        // Only used to link the author name to their profile when one matches.
        model.addAttribute("authorProfile", authorService.findActiveByName(book.getAuthor()).orElse(null));
        return "book/detail";
    }

    /* Rating Book */
    @PostMapping("/{id}/rating")
    public String createRating(
            @PathVariable Long id,
            @RequestParam Integer ratingValue,
            @RequestParam String reviewText,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Vui lòng đăng nhập để đánh giá.");
            return "redirect:/login";
        }
        User customer = userRepository
                .findByEmail(authentication.getName());

        if (customer == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Không tìm thấy tài khoản.");
            return "redirect:/books/" + id;
        }
        try {
            ratingService.createRating(
                    id,
                    customer.getId(),
                    ratingValue,
                    reviewText);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Đánh giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());
        }
        return "redirect:/books/" + id;
    }

    /** View List Of Products — alias */
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("books", bookService.getActiveBooks());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "book/list";
    }
}