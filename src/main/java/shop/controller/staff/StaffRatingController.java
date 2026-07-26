package shop.controller.staff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import shop.domain.Book;
import shop.domain.VppItem;
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

        List<VppItem> vppItems = ratingService.getVppItemsHasReview(keyword);

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

        Map<Long, Long> vppReviewCounts = new HashMap<>();
        Map<Long, Double> vppAverageRatings = new HashMap<>();

        for (VppItem item : vppItems) {

            vppReviewCounts.put(
                    item.getId(),
                    ratingService.getReviewCountVpp(item.getId()));

            vppAverageRatings.put(
                    item.getId(),
                    ratingService.getAverageRatingVpp(item.getId()));
        }

        model.addAttribute("keyword", keyword);

        model.addAttribute("books", books);
        model.addAttribute("reviewCounts", reviewCounts);
        model.addAttribute("averageRatings", averageRatings);

        model.addAttribute("vppItems", vppItems);
        model.addAttribute("vppReviewCounts", vppReviewCounts);
        model.addAttribute("vppAverageRatings", vppAverageRatings);

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

    @GetMapping("/vpp/{vppItemId}")
    public String showVppReviews(@PathVariable Long vppItemId, Model model) {

        VppItem item = ratingService.getVppItem(vppItemId);

        model.addAttribute("ratings", ratingService.getVppRatings(vppItemId));
        model.addAttribute("item", item);
        model.addAttribute("reviewCount", ratingService.getReviewCountVpp(vppItemId));
        model.addAttribute("averageRating", ratingService.getAverageRatingVpp(vppItemId));

        return "layout/staff/review/vpp-detail";
    }

    @PostMapping("/book/{ratingId}/hide")
    public String hideReview(
            @PathVariable Integer ratingId,
            @RequestParam Long bookId) {

        ratingService.hideRating(ratingId);

        return "redirect:/staff/reviews/" + bookId;
    }

    @PostMapping("/vpp/{ratingId}/hide")
    public String hideVppReview(
            @PathVariable Integer ratingId,
            @RequestParam Long vppItemId) {

        ratingService.hideRating(ratingId);

        return "redirect:/staff/reviews/vpp/" + vppItemId;
    }

    @PostMapping("/book/{ratingId}/visible")
    public String visibleReview(
            @PathVariable Integer ratingId,
            @RequestParam Long bookId) {

        ratingService.visibleRating(ratingId);

        return "redirect:/staff/reviews/" + bookId;
    }

    @PostMapping("/vpp/{ratingId}/visible")
    public String visibleVppReview(
            @PathVariable Integer ratingId,
            @RequestParam Long vppItemId) {

        ratingService.visibleRating(ratingId);

        return "redirect:/staff/reviews/vpp/" + vppItemId;
    }
}
