package shop.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    // Author detail
    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model) {
        Author author = authorService.getAuthorById(id);
        if (!author.isStatus()) {
            throw new RuntimeException("Author not found");
        }
        model.addAttribute("author", author);
        model.addAttribute(
                "books",
                authorService.getBooksByAuthor(id));
        return "author/detail";
    }

}