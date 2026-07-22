package shop.controller.client;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // CREATE
    @PostMapping("/create")
    public String createRating(
            @RequestParam Long bookId,
            @RequestParam Integer ratingValue,
            @RequestParam String reviewText,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null) {
            return "redirect:/login";
        }

        if (ratingValue == null) {

            redirectAttributes.addFlashAttribute(
                    "ratingError",
                    "Please select the number of stars.");

            return "redirect:/books/" + bookId;
        }

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

    // UPDATE
    @PostMapping("/update")
    public String updateRating(
            @RequestParam Long bookId,
            @RequestParam Integer ratingValue,
            @RequestParam String reviewText,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName());

        ratingService.updateRating(
                bookId,
                user.getId(),
                ratingValue,
                reviewText);

        return "redirect:/books/" + bookId;
    }

    // DELETE
    @PostMapping("/delete")
    public String deleteRating(
            @RequestParam Long bookId,
            Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(authentication.getName());

        if (user == null) {
            return "redirect:/login";
        }

        ratingService.deleteRating(
                bookId,
                user.getId());

        return "redirect:/books/" + bookId;
    }

    // ==============VPP=================

    @PostMapping("/vpp/create")
    public String createVppRating(
            @RequestParam("vppItemId") Long vppItemId,
            @RequestParam("ratingValue") Integer ratingValue,
            @RequestParam("reviewText") String reviewText,
            Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userRepository
                .findByEmail(authentication.getName());

        if (user == null) {
            return "redirect:/login";
        }
        ratingService.createVppRating(
                vppItemId,
                user.getId(),
                ratingValue,
                reviewText);

        return "redirect:/customer/vpp/" + vppItemId;
    }

    // UPDATE
    @PostMapping("/vpp/update")
    public String updateVppRating(
            @RequestParam("vppItemId") Long vppItemId,
            @RequestParam("ratingValue") Integer ratingValue,
            @RequestParam("reviewText") String reviewText,
            Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userRepository
                .findByEmail(authentication.getName());

        if (user == null) {
            return "redirect:/login";
        }
        ratingService.updateVppRating(
                vppItemId,
                user.getId(),
                ratingValue,
                reviewText);

        return "redirect:/customer/vpp/" + vppItemId;
    }

    // DELETE
    @PostMapping("/vpp/delete")
    public String deleteVppRating(
            @RequestParam("vppItemId") Long vppItemId,
            Authentication authentication) {

        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userRepository
                .findByEmail(authentication.getName());

        if (user == null) {
            return "redirect:/login";
        }
        ratingService.deleteVppRating(
                vppItemId,
                user.getId());

        return "redirect:/customer/vpp/" + vppItemId;
    }
}