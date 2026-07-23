package shop.controller.admin;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

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
import shop.domain.Category;
import shop.domain.Genre;
import shop.domain.Supplier;
import shop.repository.SupplierRepository;
import shop.service.AuthorService;
import shop.service.BookService;
import shop.service.CategoryService;
import shop.service.FileStorageService;
import shop.service.GenreService;

// Create and Edit share the same view (admin/book/form), switching on formMode.
@Controller
@RequestMapping("/admin/books")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookController {

    private static final String BOOK_IMAGE_FOLDER = "book-images";

    private final BookService bookService;
    private final CategoryService categoryService;
    private final GenreService genreService;
    private final FileStorageService fileStorageService;
    private final AuthorService authorService;
    private final SupplierRepository supplierRepository;

    public AdminBookController(BookService bookService,
            CategoryService categoryService,
            GenreService genreService,
            FileStorageService fileStorageService,
            AuthorService authorService,
            SupplierRepository supplierRepository) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.genreService = genreService;
        this.fileStorageService = fileStorageService;
        this.authorService = authorService;
        this.supplierRepository = supplierRepository;
    }

    private static final int PAGE_SIZE = 10;

    // Filtering happens in the service; pagination is just a subList over
    // the filtered result since the catalog is small enough for that.
    @GetMapping
    public String list(@RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) List<Long> genreIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "false") boolean all,
            Model model) {
        java.util.List<Book> allBooks = bookService.filterBooks(q, categoryId, genreIds, status);
        int totalItems = allBooks.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / PAGE_SIZE));
        int from;
        int to;
        if (all) {
            page = 0;
            from = 0;
            to = totalItems;
        } else {
            page = Math.max(0, Math.min(page, totalPages - 1));
            from = page * PAGE_SIZE;
            to = Math.min(from + PAGE_SIZE, totalItems);
        }
        List<Book> pageBooks = allBooks.subList(from, to);

        model.addAttribute("books", pageBooks);
        model.addAttribute("q", q);
        model.addAttribute("status", status);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedGenreIds", genreIds == null ? List.of() : genreIds);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("genres", genreService.getActiveGenres());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("fromItem", totalItems == 0 ? 0 : from + 1);
        model.addAttribute("toItem", to);
        model.addAttribute("viewingAll", all);
        return "admin/book/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("genres", genreService.getActiveGenres());
        model.addAttribute("suppliers", supplierRepository.findAllByOrderByActiveDescIdDesc());
        model.addAttribute("selectedGenreIds", List.of());
        model.addAttribute("formMode", "create");
        return "admin/book/form";
    }

    @PostMapping
    public String create(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "genreIds", required = false) List<Long> genreIds,
            @RequestParam(name = "supplierId", required = false) Long supplierId,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            Model model, RedirectAttributes redirectAttributes) {

        if (bookService.isIsbnTaken(book.getIsbn(), null)) {
            bindingResult.rejectValue("isbn", "book.isbn.exists", "ISBN already exists");
        }

        // Category is required and must resolve to an active row — a stale id
        // (deleted/deactivated between page load and submit) is rejected too.
        boolean categoryMissing = (categoryId == null);
        Category selectedCategory = categoryMissing ? null : categoryService.getCategoryById(categoryId).orElse(null);
        boolean categoryInvalid = !categoryMissing && (selectedCategory == null || !selectedCategory.isActive());
        if (categoryMissing) {
            model.addAttribute("categoryError", "Please select a category before saving the book.");
        } else if (categoryInvalid) {
            model.addAttribute("categoryError", "Selected category does not exist or is not active.");
        }

        // At least one genre is required; each must still be active/undeleted
        // at submit time regardless of the book's category.
        List<Long> distinctGenreIds = (genreIds == null) ? List.of() : genreIds.stream().distinct().toList();
        boolean genreMissing = distinctGenreIds.isEmpty();
        List<Genre> validGenres = genreMissing ? List.of() : genreService.getValidGenresByIds(distinctGenreIds);
        boolean genreInvalid = !genreMissing && validGenres.size() != distinctGenreIds.size();
        if (genreMissing) {
            model.addAttribute("genreError", "Please select at least 1 genre.");
        } else if (genreInvalid) {
            model.addAttribute("genreError", "One or more selected genres are invalid, inactive, or deleted.");
        }

        if (bindingResult.hasErrors() || categoryMissing || categoryInvalid || genreMissing || genreInvalid) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("genres", genreService.getActiveGenres());
            model.addAttribute("suppliers", supplierRepository.findAllByOrderByActiveDescIdDesc());
            model.addAttribute("selectedGenreIds", distinctGenreIds);
            model.addAttribute("formMode", "create");
            return "admin/book/form";
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                book.setImageUrl(fileStorageService.save(imageFile, BOOK_IMAGE_FOLDER));
            } catch (IOException e) {
                model.addAttribute("errorMessage", "Lỗi khi lưu ảnh: " + e.getMessage());
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("authors", authorService.getAllAuthors());
                model.addAttribute("genres", genreService.getActiveGenres());
                model.addAttribute("suppliers", supplierRepository.findAllByOrderByActiveDescIdDesc());
                model.addAttribute("selectedGenreIds", distinctGenreIds);
                model.addAttribute("formMode", "create");
                return "admin/book/form";
            }
        }

        book.setCategory(selectedCategory);
        book.setGenres(new LinkedHashSet<>(validGenres));
        book.setSupplier(supplierId == null ? null : supplierRepository.findById(supplierId).orElse(null));
        if (!StringUtils.hasText(book.getIsbn()))
            book.setIsbn(null);
        if (!StringUtils.hasText(book.getAuthor()))
            book.setAuthor(null);

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
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("genres", genreService.getActiveGenres());
        model.addAttribute("suppliers", supplierRepository.findAllByOrderByActiveDescIdDesc());
        model.addAttribute("selectedGenreIds",
                book.getGenres().stream().map(Genre::getId).toList());
        model.addAttribute("formMode", "edit");
        return "admin/book/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @ModelAttribute("book") @Valid Book book, BindingResult bindingResult,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "genreIds", required = false) List<Long> genreIds,
            @RequestParam(name = "supplierId", required = false) Long supplierId,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            Model model, RedirectAttributes redirectAttributes) {

        Book existing = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        if (bookService.isIsbnTaken(book.getIsbn(), id)) {
            bindingResult.rejectValue("isbn", "book.isbn.exists", "ISBN already exists");
        }

        boolean categoryMissing = (categoryId == null);
        Category selectedCategory = categoryMissing ? null : categoryService.getCategoryById(categoryId).orElse(null);
        boolean categoryInvalid = !categoryMissing && (selectedCategory == null || !selectedCategory.isActive());
        if (categoryMissing) {
            model.addAttribute("categoryError", "Please select a category before saving the book.");
        } else if (categoryInvalid) {
            model.addAttribute("categoryError", "Selected category does not exist or is not active.");
        }

        List<Long> distinctGenreIds = (genreIds == null) ? List.of() : genreIds.stream().distinct().toList();
        boolean genreMissing = distinctGenreIds.isEmpty();
        List<Genre> validGenres = genreMissing ? List.of() : genreService.getValidGenresByIds(distinctGenreIds);
        boolean genreInvalid = !genreMissing && validGenres.size() != distinctGenreIds.size();
        if (genreMissing) {
            model.addAttribute("genreError", "Please select at least 1 genre.");
        } else if (genreInvalid) {
            model.addAttribute("genreError", "One or more selected genres are invalid, inactive, or deleted.");
        }

        if (bindingResult.hasErrors() || categoryMissing || categoryInvalid || genreMissing || genreInvalid) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("genres", genreService.getActiveGenres());
            model.addAttribute("suppliers", supplierRepository.findAllByOrderByActiveDescIdDesc());
            model.addAttribute("selectedGenreIds", distinctGenreIds);
            model.addAttribute("formMode", "edit");
            return "admin/book/form";
        }

        existing.setTitle(book.getTitle());
        existing.setAuthor(StringUtils.hasText(book.getAuthor()) ? book.getAuthor() : null);
        existing.setIsbn(StringUtils.hasText(book.getIsbn()) ? book.getIsbn() : null);
        existing.setDescription(book.getDescription());
        existing.setPrice(book.getPrice());

        // active is intentionally left untouched — toggling it is only done
        // through the Remove/Restore actions on the list page.
        existing.setCategory(selectedCategory);
        existing.setSupplier(supplierId == null ? null : supplierRepository.findById(supplierId).orElse(null));
        // Replacing the whole Set lets Hibernate diff old vs new genres itself
        // and rewrite book_genres accordingly, instead of us doing it by hand.
        existing.setGenres(new LinkedHashSet<>(validGenres));

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Upload the new file before deleting the old one, so a failed
                // upload never leaves the book without a cover image.
                existing.setImageUrl(fileStorageService.replace(
                        imageFile, existing.getImageUrl(), BOOK_IMAGE_FOLDER));
            } catch (IOException e) {
                model.addAttribute("errorMessage", "Lỗi khi lưu ảnh: " + e.getMessage());
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("authors", authorService.getAllAuthors());
                model.addAttribute("genres", genreService.getActiveGenres());
                model.addAttribute("suppliers", supplierRepository.findAllByOrderByActiveDescIdDesc());
                model.addAttribute("selectedGenreIds", distinctGenreIds);
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
        } catch (IllegalStateException e) {
            // Book is in a customer's cart — surface the specific reason instead
            // of the generic error message.
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
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
