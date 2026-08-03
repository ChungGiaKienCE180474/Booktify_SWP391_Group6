package shop.controller.staff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.domain.Book;
import shop.service.RatingService;

@Controller
@RequestMapping("/staff/reviews")
@PreAuthorize("hasRole('STAFF')")
public class StaffRatingController {

    private final RatingService ratingService;

    public StaffRatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            Model model) {

        List<Book> books = ratingService.getBooksHasReview(keyword);

        Map<Long, Long> reviewCounts = new HashMap<>();
        Map<Long, Double> averageRatings = new HashMap<>();

        for (Book book : books) {
            reviewCounts.put(
                    book.getId(),
                    ratingService.getReviewCount(book.getId()));
            averageRatings.put(
                    book.getId(),
                    ratingService.getAverageRating(book.getId()));
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("books", books);
        model.addAttribute("reviewCounts", reviewCounts);
        model.addAttribute("averageRatings", averageRatings);

        return "layout/staff/review/list";
    }

    @GetMapping("/{bookId}")
    public String showReviews(
            @PathVariable Long bookId,
            Model model) {

        Book book = ratingService.getBook(bookId);

        model.addAttribute("ratings", ratingService.getRatingsByBookForAdmin(bookId));
        model.addAttribute("book", book);
        model.addAttribute("reviewCount", ratingService.getReviewCount(bookId));
        model.addAttribute("averageRating", ratingService.getAverageRating(bookId));

        return "layout/staff/review/detail";
    }

    @PostMapping("/book/{ratingId}/hide")
    public String hideReview(
            @PathVariable Integer ratingId,
            @RequestParam Long bookId) {

        ratingService.hideRating(ratingId);
        return "redirect:/staff/reviews/" + bookId;
    }

    @PostMapping("/book/{ratingId}/visible")
    public String visibleReview(
            @PathVariable Integer ratingId,
            @RequestParam Long bookId) {

        ratingService.visibleRating(ratingId);
        return "redirect:/staff/reviews/" + bookId;
    }
}
