package shop.controller.client;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.domain.Author;
import shop.domain.Book;
import shop.domain.Promotion;
import shop.service.AuthorService;
import shop.service.BookService;
import shop.service.PromotionService;

/**
 * Site-wide search from the header search bar — books and authors.
 */
@Controller
public class SearchController {

    private final BookService bookService;
    private final PromotionService promotionService;
    private final AuthorService authorService;

    public SearchController(
            BookService bookService,
            PromotionService promotionService,
            AuthorService authorService) {

        this.bookService = bookService;
        this.promotionService = promotionService;
        this.authorService = authorService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        String keyword = q == null ? "" : q.trim();

        List<Book> books = keyword.isEmpty()
                ? List.of()
                : bookService.filterBooks(keyword, null, null, "active");

        Map<Long, Promotion> bestPromotionMap = promotionService.getBestPromotionMap(books);

        List<Author> authors = keyword.isEmpty()
                ? List.of()
                : authorService.searchAuthors(keyword, "active");

        model.addAttribute("q", keyword);
        model.addAttribute("books", books);
        model.addAttribute("bestPromotionMap", bestPromotionMap);
        model.addAttribute("discountedPriceFormattedMap",
                promotionService.getDiscountedPriceFormattedMap(books, bestPromotionMap));
        model.addAttribute("discountLabelMap",
                promotionService.getDiscountLabelMap(books, bestPromotionMap));
        model.addAttribute("authors", authors);
        model.addAttribute("totalResults", books.size() + authors.size());

        return "search/results";
    }
}
