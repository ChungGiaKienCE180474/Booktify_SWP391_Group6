package shop.controller.admin;

import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import shop.domain.Book;
import shop.service.RatingService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reviews")
public class AdminRatingController {

    private final RatingService ratingService;

    public AdminRatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public String list(Model model) {

        List<Book> books = ratingService.getBooksHasReview();

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

        model.addAttribute("books", books);
        model.addAttribute("reviewCounts", reviewCounts);
        model.addAttribute("averageRatings", averageRatings);

        return "admin/review/list";
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

        return "admin/review/detail";
    }

    @PostMapping("/{ratingId}/hide")
    public String hideReview(
            @PathVariable Integer ratingId,
            @RequestParam Long bookId) {

        ratingService.hideRating(ratingId);

        return "redirect:/admin/reviews/" + bookId;
    }

    @PostMapping("/{ratingId}/visible")
    public String visibleReview(
            @PathVariable Integer ratingId,
            @RequestParam Long bookId) {

        ratingService.visibleRating(ratingId);

        return "redirect:/admin/reviews/" + bookId;
    }
}