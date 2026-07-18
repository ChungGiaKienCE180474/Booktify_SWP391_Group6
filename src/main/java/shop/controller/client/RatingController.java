package shop.controller.client;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import shop.domain.User;
import shop.repository.UserRepository;
import shop.service.RatingService;

@Controller
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final UserRepository userRepository;

    public RatingController(
            RatingService ratingService,
            UserRepository userRepository) {

        this.ratingService = ratingService;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    public String createRating(
            @RequestParam Long bookId,
            @RequestParam Integer ratingValue,
            @RequestParam String reviewText,
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        ratingService.createRating(
                bookId,
                user.getId(),
                ratingValue,
                reviewText);

        return "redirect:/books/" + bookId;
    }

}