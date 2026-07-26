package shop.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.Author;
import shop.service.AuthorService;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // Author list
    @GetMapping
    public String list(Model model) {

        model.addAttribute(
                "authors",
                authorService.getActiveAuthors());
        return "author/list";
    }

    // Author detail — bad/deleted/hidden id redirects back to the author
    // list with a friendly message instead of a raw 500 page.
    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Author author = authorService.getAuthorById(id);
            if (!author.isStatus()) {
                throw new IllegalArgumentException("Author not found: " + id);
            }
            model.addAttribute("author", author);
            return "author/detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This author doesn't exist or is no longer available.");
            return "redirect:/authors";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong while loading this author. Please try again.");
            return "redirect:/authors";
        }
    }

}