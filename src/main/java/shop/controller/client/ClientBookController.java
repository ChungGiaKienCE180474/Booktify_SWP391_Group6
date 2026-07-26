package shop.controller.client;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.Author;
import shop.domain.Book;
import shop.domain.Promotion;
import shop.domain.User;
import shop.repository.UserRepository;
import shop.service.BookService;
import shop.service.CategoryService;
import shop.service.GenreService;
import shop.service.PromotionService;
import shop.service.RatingService;

@Controller
@RequestMapping("/books")
public class ClientBookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final RatingService ratingService;
    private final UserRepository userRepository;
    private final GenreService genreService;
    private final PromotionService promotionService;

    public ClientBookController(
            BookService bookService,
            CategoryService categoryService,
            RatingService ratingService,
            UserRepository userRepository,
            GenreService genreService,
            PromotionService promotionService) {

        this.bookService = bookService;
        this.categoryService = categoryService;
        this.ratingService = ratingService;
        this.userRepository = userRepository;
        this.genreService = genreService;
        this.promotionService = promotionService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) List<Long> genreIds,
            @RequestParam(required = false) String q,
            Model model) {

        List<Book> books = bookService.filterBooks(q, categoryId, genreIds, "active");

        if (categoryId != null) {
            model.addAttribute("selectedCategory",
                    categoryService.getCategoryById(categoryId).orElse(null));
        }

        Map<Long, Promotion> bestPromotionMap = promotionService.getBestPromotionMap(books);

        model.addAttribute("books", books);
        model.addAttribute("bestPromotionMap", bestPromotionMap);
        model.addAttribute("discountedPriceFormattedMap",
                promotionService.getDiscountedPriceFormattedMap(books, bestPromotionMap));
        model.addAttribute("discountLabelMap",
                promotionService.getDiscountLabelMap(books, bestPromotionMap));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("genres", genreService.getActiveGenres());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedGenreIds", genreIds == null ? List.of() : genreIds);
        model.addAttribute("q", q);

        return "book/list";
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Any bad/deleted/inactive id, or a failure while building the
        // suggestion/promotion data below, now redirects back to the shop
        // with a friendly message instead of surfacing a raw 500 page.
        try {
            Book book = bookService.getBookById(id)
                    .filter(Book::isActive)
                    .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

            Long categoryId = book.getCategory() == null ? null : book.getCategory().getId();
            Promotion bestPromotion = promotionService.getBestPromotionForBook(book).orElse(null);

            model.addAttribute("book", book);
            model.addAttribute("bestPromotion", bestPromotion);
            model.addAttribute("discountedPriceFormatted",
                    promotionService.getDiscountedPriceFormatted(book, bestPromotion));
            model.addAttribute("discountLabel",
                    promotionService.getDiscountLabel(book, bestPromotion));
            model.addAttribute("ratings", ratingService.getRatingsByBook(id));
            model.addAttribute("canReview", false);

            if (authentication != null && authentication.isAuthenticated()) {
                User user = userRepository.findByEmail(authentication.getName());

                if (user != null) {
                    model.addAttribute("currentUser", user);
                    model.addAttribute("myRating",
                            ratingService.getCustomerRating(id, user.getId()).orElse(null));
                    model.addAttribute("canReview",
                            ratingService.canCustomerReview(id, user.getId()));
                }
            }

            List<Book> suggestedBooks = bookService.getSuggestedBooks(categoryId, id);
            Map<Long, Promotion> suggestedPromotionMap = promotionService.getBestPromotionMap(suggestedBooks);

            model.addAttribute("suggestedBooks", suggestedBooks);
            model.addAttribute("suggestedPromotionMap", suggestedPromotionMap);
            model.addAttribute("suggestedDiscountedPriceFormattedMap",
                    promotionService.getDiscountedPriceFormattedMap(suggestedBooks, suggestedPromotionMap));
            model.addAttribute("suggestedDiscountLabelMap",
                    promotionService.getDiscountLabelMap(suggestedBooks, suggestedPromotionMap));
            model.addAttribute("categories", categoryService.getAllCategories());
            // Book.author is a real FK now, so this always points at the right
            // Author record even after the author gets renamed — only a hidden
            // (status=false) author profile is suppressed from the link.
            Author bookAuthor = book.getAuthor();
            model.addAttribute("authorProfile",
                    (bookAuthor != null && bookAuthor.isStatus()) ? bookAuthor : null);

            return "book/detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This book doesn't exist or is no longer available.");
            return "redirect:/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong while loading this book. Please try again.");
            return "redirect:/books";
        }
    }

    /** View List Of Products — alias */
    @GetMapping("/products")
    public String products(Model model) {
        List<Book> books = bookService.getActiveBooks();
        Map<Long, Promotion> bestPromotionMap = promotionService.getBestPromotionMap(books);

        model.addAttribute("books", books);
        model.addAttribute("bestPromotionMap", bestPromotionMap);
        model.addAttribute("discountedPriceFormattedMap",
                promotionService.getDiscountedPriceFormattedMap(books, bestPromotionMap));
        model.addAttribute("discountLabelMap",
                promotionService.getDiscountLabelMap(books, bestPromotionMap));
        model.addAttribute("categories", categoryService.getAllCategories());

        return "book/list";
    }
}
