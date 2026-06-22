package shop.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import shop.domain.Book;
import shop.domain.Genre;
import shop.service.BookService;
import shop.service.CategoryService;
import shop.service.GenreService;

@Controller
@RequestMapping("/admin/genres")
@PreAuthorize("hasRole('ADMIN')")
public class AdminGenreController {

    private final GenreService genreService;
    private final CategoryService categoryService;
    private final BookService bookService;

    public AdminGenreController(GenreService genreService,
                                CategoryService categoryService,
                                BookService bookService) {
        this.genreService = genreService;
        this.categoryService = categoryService;
        this.bookService = bookService;
    }

    // ── LIST ─────────────────────────────────────────────────────────────────
    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) Long categoryId,
                       Model model) {
        model.addAttribute("genres", genreService.searchGenres(q, categoryId));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("q", q);
        model.addAttribute("selectedCategoryId", categoryId);
        return "admin/genre/list";
    }

    // ── CREATE FORM ──────────────────────────────────────────────────────────
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("genre", new Genre());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formMode", "create");
        return "admin/genre/form";
    }

    // ── CREATE SUBMIT ────────────────────────────────────────────────────────
    @PostMapping
    public String create(@ModelAttribute("genre") @Valid Genre genre,
                         BindingResult bindingResult,
                         @RequestParam(name = "categoryId", required = false) Long categoryId,
                         Model model, RedirectAttributes redirectAttributes) {

        boolean categoryMissing = (categoryId == null);
        if (categoryMissing) {
            model.addAttribute("categoryError", "Please select a category.");
        } else if (StringUtils.hasText(genre.getName())
                && genreService.existsByNameAndCategory(genre.getName(), categoryId)) {
            bindingResult.rejectValue("name", "genre.exists",
                    "Genre already exists in this category.");
        }

        if (bindingResult.hasErrors() || categoryMissing) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("formMode", "create");
            return "admin/genre/form";
        }

        categoryService.getCategoryById(categoryId).ifPresent(genre::setCategory);
        genreService.saveGenre(genre);
        redirectAttributes.addFlashAttribute("successMessage", "Genre created successfully.");
        return "redirect:/admin/genres";
    }

    // ── EDIT FORM ────────────────────────────────────────────────────────────
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Genre genre = genreService.getGenreById(id)
                .orElseThrow(() -> new IllegalArgumentException("Genre not found: " + id));
        model.addAttribute("genre", genre);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formMode", "edit");
        return "admin/genre/form";
    }

    // ── UPDATE SUBMIT ────────────────────────────────────────────────────────
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("genre") @Valid Genre genre,
                         BindingResult bindingResult,
                         @RequestParam(name = "categoryId", required = false) Long categoryId,
                         Model model, RedirectAttributes redirectAttributes) {

        Genre existing = genreService.getGenreById(id)
                .orElseThrow(() -> new IllegalArgumentException("Genre not found: " + id));

        boolean categoryMissing = (categoryId == null);
        if (categoryMissing) {
            model.addAttribute("categoryError", "Please select a category.");
        } else if (StringUtils.hasText(genre.getName())
                && genreService.existsByNameAndCategoryExcludeId(genre.getName(), categoryId, id)) {
            bindingResult.rejectValue("name", "genre.exists",
                    "Genre already exists in this category.");
        }

        if (bindingResult.hasErrors() || categoryMissing) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("formMode", "edit");
            return "admin/genre/form";
        }

        existing.setName(genre.getName());
        existing.setDescription(genre.getDescription());
        existing.setActive(genre.isActive());
        categoryService.getCategoryById(categoryId).ifPresent(existing::setCategory);
        genreService.saveGenre(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Genre updated successfully.");
        return "redirect:/admin/genres";
    }

    // ── DELETE (soft) ────────────────────────────────────────────────────────
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            genreService.deleteGenre(id);
            redirectAttributes.addFlashAttribute("successMessage", "Genre deleted successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Genre not found.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred while deleting the genre.");
        }
        return "redirect:/admin/genres";
    }

    // ── AJAX: genres by category (cho Book form) ──────────────────────────────
    @GetMapping("/by-category")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getByCategory(
            @RequestParam Long categoryId) {
        List<Map<String, Object>> result = genreService
                .getActiveGenresByCategory(categoryId)
                .stream()
                .map(g -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", g.getId());
                    m.put("name", g.getName());
                    return m;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ── AJAX: genre detail (cho View modal) ───────────────────────────────────
    @GetMapping("/{id}/detail")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> detail(@PathVariable Long id) {
        Genre genre = genreService.getGenreById(id).orElse(null);
        if (genre == null || genre.isDeleted()) {
            return ResponseEntity.notFound().build();
        }

        List<Book> books = bookService.getAllBooks().stream()
                .filter(b -> b.getGenre() != null && b.getGenre().getId().equals(id))
                .limit(10)
                .collect(Collectors.toList());

        long totalBooks = bookService.getAllBooks().stream()
                .filter(b -> b.getGenre() != null && b.getGenre().getId().equals(id))
                .count();

        Map<String, Object> data = new HashMap<>();
        data.put("name", genre.getName());
        data.put("category", genre.getCategory() != null ? genre.getCategory().getName() : "—");
        data.put("description", genre.getDescription() != null ? genre.getDescription() : "");
        data.put("active", genre.isActive());
        data.put("totalBooks", totalBooks);
        data.put("createdAt", genre.getCreatedAtString());
        data.put("updatedAt", genre.getUpdatedAtString());
        data.put("books", books.stream().map(Book::getTitle).collect(Collectors.toList()));
        return ResponseEntity.ok(data);
    }
}
