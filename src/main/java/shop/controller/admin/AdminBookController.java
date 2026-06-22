package shop.controller.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import shop.domain.Book;
import shop.service.BookService;
import shop.service.CategoryService;
import shop.service.GenreService;

@Controller
@RequestMapping("/admin/books")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final GenreService genreService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public AdminBookController(BookService bookService,
                               CategoryService categoryService,
                               GenreService genreService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.genreService = genreService;
    }

    // ── Lưu ảnh upload lên disk ──────────────────────────────────────────────
    private String saveImage(MultipartFile file) throws IOException {
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + (ext != null ? "." + ext : "");
        Path dir = Paths.get(uploadDir, "book-images");
        Files.createDirectories(dir);
        Files.copy(file.getInputStream(), dir.resolve(filename));
        return "/uploads/book-images/" + filename;
    }

    // ── LIST ─────────────────────────────────────────────────────────────────
    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("books", bookService.searchBooks(q));
        model.addAttribute("q", q);
        return "admin/book/list";
    }

    // ── CREATE FORM ──────────────────────────────────────────────────────────
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formMode", "create");
        return "admin/book/form";
    }

    // ── CREATE SUBMIT ────────────────────────────────────────────────────────
    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "genreId", required = false) Long genreId,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            Model model, RedirectAttributes redirectAttributes) {

        if (StringUtils.hasText(book.getIsbn()) && bookService.existsByIsbnIgnoreCase(book.getIsbn())) {
            bindingResult.rejectValue("isbn", "book.isbn.exists", "ISBN already exists");
        }
        boolean categoryMissing = (categoryId == null);
        boolean genreMissing = (genreId == null);
        if (categoryMissing) {
            model.addAttribute("categoryError", "Please select a category before saving the book.");
        }
        if (genreMissing) {
            model.addAttribute("genreError", "Please select a genre before saving the book.");
        }
        if (bindingResult.hasErrors() || categoryMissing || genreMissing) {
            model.addAttribute("categories", categoryService.getAllCategories());
            if (categoryId != null) {
                model.addAttribute("genres", genreService.getActiveGenresByCategory(categoryId));
            }
            model.addAttribute("formMode", "create");
            return "admin/book/form";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                book.setImageUrl(saveImage(imageFile));
            } catch (IOException e) {
                model.addAttribute("errorMessage", "Lỗi khi lưu ảnh: " + e.getMessage());
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("formMode", "create");
                return "admin/book/form";
            }
        }

        categoryService.getCategoryById(categoryId).ifPresent(book::setCategory);
        genreService.getGenreById(genreId).ifPresent(book::setGenre);
        if (!StringUtils.hasText(book.getIsbn())) book.setIsbn(null);

        bookService.saveBook(book);
        redirectAttributes.addFlashAttribute("successMessage", "Book created successfully.");
        return "redirect:/admin/books";
    }

    // ── EDIT FORM ────────────────────────────────────────────────────────────
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
        model.addAttribute("book", book);
        model.addAttribute("categories", categoryService.getAllCategories());
        if (book.getCategory() != null) {
            model.addAttribute("genres",
                    genreService.getActiveGenresByCategory(book.getCategory().getId()));
        }
        model.addAttribute("formMode", "edit");
        return "admin/book/form";
    }

    // ── UPDATE SUBMIT ────────────────────────────────────────────────────────
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "genreId", required = false) Long genreId,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            Model model, RedirectAttributes redirectAttributes) {

        Book existing = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        boolean isbnChanged = StringUtils.hasText(book.getIsbn())
                && !book.getIsbn().equalsIgnoreCase(existing.getIsbn());
        if (isbnChanged && bookService.existsByIsbnIgnoreCase(book.getIsbn())) {
            bindingResult.rejectValue("isbn", "book.isbn.exists", "ISBN already exists");
        }
        boolean categoryMissing = (categoryId == null);
        boolean genreMissing = (genreId == null);
        if (categoryMissing) {
            model.addAttribute("categoryError", "Please select a category before saving the book.");
        }
        if (genreMissing) {
            model.addAttribute("genreError", "Please select a genre before saving the book.");
        }
        if (bindingResult.hasErrors() || categoryMissing || genreMissing) {
            model.addAttribute("categories", categoryService.getAllCategories());
            if (categoryId != null) {
                model.addAttribute("genres", genreService.getActiveGenresByCategory(categoryId));
            }
            model.addAttribute("formMode", "edit");
            return "admin/book/form";
        }

        existing.setTitle(book.getTitle());
        existing.setAuthor(book.getAuthor());
        existing.setIsbn(StringUtils.hasText(book.getIsbn()) ? book.getIsbn() : null);
        existing.setDescription(book.getDescription());
        existing.setPrice(book.getPrice());
        existing.setStockQuantity(book.getStockQuantity());
        existing.setActive(book.isActive());
        existing.setCategory(categoryService.getCategoryById(categoryId).orElse(null));
        existing.setGenre(genreService.getGenreById(genreId).orElse(null));

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                existing.setImageUrl(saveImage(imageFile));
            } catch (IOException e) {
                model.addAttribute("errorMessage", "Lỗi khi lưu ảnh: " + e.getMessage());
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("formMode", "edit");
                return "admin/book/form";
            }
        }

        bookService.saveBook(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully.");
        return "redirect:/admin/books";
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.softDeleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred while deleting the book.");
        }
        return "redirect:/admin/books";
    }
}
