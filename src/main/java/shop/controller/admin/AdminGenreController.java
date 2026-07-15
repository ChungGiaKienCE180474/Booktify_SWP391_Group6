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

// Also exposes two JSON endpoints used by the admin UI: /by-category
// (genre suggestions) and /{id}/detail (the "View" modal on the list page).
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

    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) String status,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        java.util.List<Genre> all = genreService.searchGenres(q, categoryId, status);
        int totalItems = all.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / PAGE_SIZE));
        page = Math.max(0, Math.min(page, totalPages - 1));
        int from = page * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, totalItems);
        model.addAttribute("genres", all.subList(from, to));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("q", q);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("fromItem", totalItems == 0 ? 0 : from + 1);
        model.addAttribute("toItem", to);
        return "admin/genre/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("genre", new Genre());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formMode", "create");
        return "admin/genre/form";
    }

    // categoryId is optional here — it's only used to group genres in the
    // admin UI, not a hard requirement like it is for Book.
    @PostMapping
    public String create(@ModelAttribute("genre") @Valid Genre genre,
                         BindingResult bindingResult,
                         @RequestParam(name = "categoryId", required = false) Long categoryId,
                         Model model, RedirectAttributes redirectAttributes) {

        if (StringUtils.hasText(genre.getName()) && genreService.existsByName(genre.getName())) {
            bindingResult.rejectValue("name", "genre.exists", "Genre already exists.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("formMode", "create");
            return "admin/genre/form";
        }

        if (categoryId != null) {
            categoryService.getCategoryById(categoryId).ifPresent(genre::setCategory);
        }
        genreService.saveGenre(genre);
        redirectAttributes.addFlashAttribute("successMessage", "Genre created successfully.");
        return "redirect:/admin/genres";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Genre genre = genreService.getGenreById(id)
                .orElseThrow(() -> new IllegalArgumentException("Genre not found: " + id));
        model.addAttribute("genre", genre);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formMode", "edit");
        return "admin/genre/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("genre") @Valid Genre genre,
                         BindingResult bindingResult,
                         @RequestParam(name = "categoryId", required = false) Long categoryId,
                         Model model, RedirectAttributes redirectAttributes) {

        Genre existing = genreService.getGenreById(id)
                .orElseThrow(() -> new IllegalArgumentException("Genre not found: " + id));

        if (StringUtils.hasText(genre.getName())
                && genreService.existsByNameExcludeId(genre.getName(), id)) {
            bindingResult.rejectValue("name", "genre.exists", "Genre already exists.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("formMode", "edit");
            return "admin/genre/form";
        }

        existing.setName(genre.getName());
        existing.setDescription(genre.getDescription());
        // active is intentionally left untouched — toggling it goes through
        // the Remove/Restore actions on the list page, not this form.
        existing.setCategory(categoryId != null ? categoryService.getCategoryById(categoryId).orElse(null) : null);
        genreService.saveGenre(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Genre updated successfully.");
        return "redirect:/admin/genres";
    }

    @PostMapping("/{id}/delete")
    public String remove(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            genreService.removeGenre(id);
            redirectAttributes.addFlashAttribute("successMessage", "Genre removed successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Genre not found.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred while removing the genre.");
        }
        return "redirect:/admin/genres";
    }

    @PostMapping("/{id}/restore")
    public String restore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            genreService.restoreGenre(id);
            redirectAttributes.addFlashAttribute("successMessage", "Genre restored successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred while restoring the genre.");
        }
        return "redirect:/admin/genres";
    }

    // Kept for future use — the book form no longer calls this since genres
    // stopped being scoped to a category.
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

    // Returns up to 10 book titles for this genre plus a separate total count,
    // since the modal only needs a preview, not the full list.
    @GetMapping("/{id}/detail")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> detail(@PathVariable Long id) {
        Genre genre = genreService.getGenreById(id).orElse(null);
        if (genre == null || genre.isDeleted()) {
            return ResponseEntity.notFound().build();
        }

        List<Book> allBooks = bookService.getAllBooks();
        List<Book> books = allBooks.stream()
                .filter(b -> b.getGenres().stream().anyMatch(g -> g.getId().equals(id)))
                .limit(10)
                .collect(Collectors.toList());

        long totalBooks = allBooks.stream()
                .filter(b -> b.getGenres().stream().anyMatch(g -> g.getId().equals(id)))
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
