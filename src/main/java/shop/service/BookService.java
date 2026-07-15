package shop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import shop.domain.Book;
import shop.repository.BookRepository;
import shop.repository.CartItemRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;

    public BookService(BookRepository bookRepository, CartItemRepository cartItemRepository) {
        this.bookRepository = bookRepository;
        this.cartItemRepository = cartItemRepository;
    }

    // Includes inactive books — admin only, never expose this list to customers.
    public List<Book> getAllBooks() {
        return bookRepository.findAllByOrderByIdAsc();
    }

    public List<Book> getActiveBooks() {
        return bookRepository.findAllByActiveTrueOrderByIdAsc();
    }

    public List<Book> getBooksByCategory(Long categoryId) {
        return bookRepository.findAllByCategoryIdAndActiveTrueOrderByIdAsc(categoryId);
    }

    // Status filtering happens in memory after the query since it's a simple
    // three-way toggle (all/active/inactive) on an already small result set.
    public List<Book> searchBooks(String q, String status) {
        List<Book> result;
        if (!StringUtils.hasText(q)) {
            result = getAllBooks();
        } else {
            result = bookRepository.search(q);
        }
        if ("active".equals(status)) {
            result = result.stream().filter(Book::isActive).toList();
        } else if ("inactive".equals(status)) {
            result = result.stream().filter(b -> !b.isActive()).toList();
        }
        return result;
    }

    // Genre matching is OR-based: a book shows up if it has at least one of
    // the selected genres. Category and keyword are ANDed on top of that.
    public List<Book> filterBooks(String q, Long categoryId, List<Long> genreIds, String status) {
        String keyword = StringUtils.hasText(q) ? q : null;
        boolean hasGenreFilter = genreIds != null && !genreIds.isEmpty();
        // Hibernate chokes on binding null into an IN clause, so we pass an
        // empty list instead and let hasGenreFilter=false skip the condition.
        List<Long> safeGenreIds = hasGenreFilter ? genreIds : List.of();
        List<Book> result = bookRepository.filterBooks(keyword, categoryId, safeGenreIds, hasGenreFilter);
        if ("active".equals(status)) {
            result = result.stream().filter(Book::isActive).toList();
        } else if ("inactive".equals(status)) {
            result = result.stream().filter(b -> !b.isActive()).toList();
        }
        return result;
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    // Single write path for Book, including the book_genres join table.
    // Wrapped in a transaction so a failure partway through (e.g. an invalid
    // genre reference) rolls back the whole save instead of leaving orphaned rows.
    @Transactional
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // Blocks soft-deleting a book that's currently sitting in any customer's
    // cart — otherwise the cart would keep referencing a book the storefront
    // can no longer sell.
    public void removeBook(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) return;
        if (cartItemRepository.existsByBook_Id(id)) {
            throw new IllegalStateException(
                    "Cannot delete this book: it is currently in a customer's cart.");
        }
        book.setActive(false);
        bookRepository.save(book);
    }

    public void restoreBook(Long id) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setActive(true);
            bookRepository.save(book);
        });
    }

    public long countBooks() {
        return bookRepository.count();
    }

    public long countActiveBooks() {
        return bookRepository.countByActiveTrue();
    }

    // excludeId lets edit forms ignore the book's own ISBN when checking for
    // duplicates.
    public boolean isIsbnTaken(String isbn, Long excludeId) {
        if (!org.springframework.util.StringUtils.hasText(isbn))
            return false;
        return bookRepository.findByIsbnIgnoreCase(isbn)
                .map(existing -> excludeId == null || !existing.getId().equals(excludeId))
                .orElse(false);
    }

    // Prefers books from the same category; tops up with other active books
    // if there aren't enough to fill the 6-item suggestion list.
    public List<Book> getSuggestedBooks(Long categoryId, Long excludeBookId) {
        if (categoryId == null) {
            return bookRepository.findAllByActiveTrueOrderByIdAsc()
                    .stream()
                    .filter(b -> !b.getId().equals(excludeBookId))
                    .limit(6)
                    .toList();
        }
        List<Book> suggested = bookRepository
                .findAllByCategoryIdAndActiveTrueAndIdNotOrderByIdAsc(categoryId, excludeBookId);
        if (suggested.size() < 6) {
            List<Book> extra = bookRepository.findAllByActiveTrueOrderByIdAsc()
                    .stream()
                    .filter(b -> !b.getId().equals(excludeBookId)
                            && (b.getCategory() == null || !b.getCategory().getId().equals(categoryId)))
                    .limit(6 - suggested.size())
                    .toList();
            suggested = new java.util.ArrayList<>(suggested);
            suggested.addAll(extra);
        }
        return suggested;
    }
}
