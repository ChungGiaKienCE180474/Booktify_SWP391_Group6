package shop.controller.client;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.domain.Author;
import shop.domain.Book;
import shop.domain.Promotion;
import shop.domain.dto.VppItemDTO;
import shop.service.AuthorService;
import shop.service.BookService;
import shop.service.PromotionService;
import shop.service.VppItemService;

/**
 * Site-wide search reachable from the header search bar. Unlike the
 * per-section pages (/books, /customer/vpp, /authors) which only search
 * their own domain, this endpoint queries all three at once so a single
 * keyword (e.g. an author's name) surfaces matching books, stationery
 * items, and author profiles together.
 */
@Controller
public class SearchController {

    private static final int VPP_RESULT_LIMIT = 12;

    private final BookService bookService;
    private final PromotionService promotionService;
    private final VppItemService vppItemService;
    private final AuthorService authorService;

    public SearchController(
            BookService bookService,
            PromotionService promotionService,
            VppItemService vppItemService,
            AuthorService authorService) {

        this.bookService = bookService;
        this.promotionService = promotionService;
        this.vppItemService = vppItemService;
        this.authorService = authorService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        String keyword = q == null ? "" : q.trim();

        List<Book> books = keyword.isEmpty()
                ? List.of()
                : bookService.filterBooks(keyword, null, null, "active");

        Map<Long, Promotion> bestPromotionMap = promotionService.getBestPromotionMap(books);

        List<VppItemDTO> vppItems = keyword.isEmpty()
                ? List.of()
                : vppItemService.searchForCustomer(
                        keyword, null, false, "newest", PageRequest.of(0, VPP_RESULT_LIMIT))
                        .getContent();

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
        model.addAttribute("vppItems", vppItems);
        model.addAttribute("authors", authors);
        model.addAttribute("totalResults", books.size() + vppItems.size() + authors.size());

        return "search/results";
    }
}
