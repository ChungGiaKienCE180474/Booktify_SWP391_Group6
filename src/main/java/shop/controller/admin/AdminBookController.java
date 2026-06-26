package shop.controller.admin;

import java.io.IOException;

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
import shop.service.FileStorageService;
import shop.service.GenreService;

@Controller
@RequestMapping("/admin/books")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookController {

    private static final String BOOK_IMAGE_FOLDER = "book-images";

    private final BookService bookService;
    private final CategoryService categoryService;
    private final GenreService genreService;
    private final FileStorageService fileStorageService;

    public AdminBookController(BookService bookService,
                               CategoryService categoryService,
                               GenreService genreService,
                               FileStorageService fileStorageService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.genreService = genreService;
        this.fileStorageService = fileStorageService;
    }

    private static final int PAGE_SIZE = 10;

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String status,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        java.util.List<Book> all = bookService.searchBooks(q, status);
        int totalItems = all.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / PAGE_SIZE));
        page = Math.max(0, Math.min(page, totalPages - 1));
        int from = page * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, totalItems);
        model.addAttribute("books", all.subList(from, to));
        model.addAttribute("q", q);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("fromItem", totalItems == 0 ? 0 : from + 1);
        model.addAttribute("toItem", to);
        return "admin/book/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("formMode", "create");
        return "admin/book/form";
    }

    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "genreId", required = false) Long genreId,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            Model model, RedirectAttributes redirectAttributes) {

        if (bookService.isIsbnTaken(book.getIsbn(), null)) {
            bindingResult.rejectValue("isbn", "book.isbn.exists", "ISBN already exists");
        }
        boolean categoryMissing = (categoryId == null);
        if (categoryMissing) {
            model.addAttribute("categoryError", "Please select a category before saving the book.");
        }
        // Genre is optional, but if provided it must belong to the selected category
        boolean genreCategoryMismatch = false;
        if (!categoryMissing && genreId != null) {
            genreCategoryMismatch = genreService.getGenreById(genreId)
                    .map(g -> g.getCategory() == null || !g.getCategory().getId().equals(categoryId))
                    .orElse(false);
            if (genreCategoryMismatch) {
                model.addAttribute("genreError", "Selected genre does not belong to the selected category.");
            }
        }
        if (bindingResult.hasErrors() || categoryMissing || genreCategoryMismatch) {
            model.addAttribute("categories", categoryService.getAllCategories());
            if (categoryId != null) {
                model.addAttribute("genres", genreService.getActiveGenresByCategory(categoryId));
            }
            model.addAttribute("formMode", "create");
            return "admin/book/form";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                book.setImageUrl(fileStorageService.save(imageFile, BOOK_IMAGE_FOLDER));
            } catch (IOException e) {
                model.addAttribute("errorMessage", "Lỗi khi lưu ảnh: " + e.getMessage());
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("formMode", "create");
                return "admin/book/form";
            }
        }

        categoryService.getCategoryById(categoryId).ifPresent(book::setCategory);
        if (genreId != null) { genreService.getGenreById(genreId).ifPresent(book::setGenre); }
        if (!StringUtils.hasText(book.getIsbn())) book.setIsbn(null);

        bookService.saveBook(book);
        redirectAttributes.addFlashAttribute("successMessage", "Book created successfully.");
        return "redirect:/admin/books";
    }

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

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "genreId", required = false) Long genreId,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            Model model, RedirectAttributes redirectAttributes) {

        Book existing = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        if (bookService.isIsbnTaken(book.getIsbn(), id)) {
            bindingResult.rejectValue("isbn", "book.isbn.exists", "ISBN already exists");
        }
        boolean categoryMissing = (categoryId == null);
        if (categoryMissing) {
            model.addAttribute("categoryError", "Please select a category before saving the book.");
        }
        boolean genreCategoryMismatch = false;
        if (!categoryMissing && genreId != null) {
            genreCategoryMismatch = genreService.getGenreById(genreId)
                    .map(g -> g.getCategory() == null || !g.getCategory().getId().equals(categoryId))
                    .orElse(false);
            if (genreCategoryMismatch) {
                model.addAttribute("genreError", "Selected genre does not belong to the selected category.");
            }
        }
        if (bindingResult.hasErrors() || categoryMissing || genreCategoryMismatch) {
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
        // NOTE: active is NOT updated here — use Remove/Restore actions on the list page
        existing.setCategory(categoryService.getCategoryById(categoryId).orElse(null));
        existing.setGenre(genreId != null ? genreService.getGenreById(genreId).orElse(null) : null);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // replace() = save mới trước, xóa cũ sau 
                existing.setImageUrl(fileStorageService.replace(
                        imageFile, existing.getImageUrl(), BOOK_IMAGE_FOLDER));
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

    @PostMapping("/{id}/delete")
    public String remove(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.removeBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred while removing the book.");
        }
        return "redirect:/admin/books";
    }

    @PostMapping("/{id}/restore")
    public String restore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.restoreBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book restored successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred while restoring the book.");
        }
        return "redirect:/admin/books";
    }
}
