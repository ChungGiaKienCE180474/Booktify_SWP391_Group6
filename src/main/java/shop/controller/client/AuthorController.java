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

    // Danh sách Author
    @GetMapping
    public String list(Model model) {

        model.addAttribute("authors", authorService.getAllAuthors());

        return "author/list";
    }

    // Chi tiết Author
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         Model model) {

        Author author = authorService.getAuthorById(id);

        model.addAttribute("author", author);

        return "author/detail";
    }

}