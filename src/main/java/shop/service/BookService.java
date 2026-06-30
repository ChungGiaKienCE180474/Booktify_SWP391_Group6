package shop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import shop.domain.Book;
import shop.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAllByOrderByIdAsc();
    }

    public List<Book> getActiveBooks() {
        return bookRepository.findAllByActiveTrueOrderByIdAsc();
    }

    public List<Book> getBooksByCategory(Long categoryId) {
        return bookRepository.findAllByCategoryIdAndActiveTrueOrderByIdAsc(categoryId);
    }

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

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void removeBook(Long id) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setActive(false);
            bookRepository.save(book);
        });
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

    /** Đếm sách active bằng COUNT query — không load data vào RAM */
    public long countActiveBooks() {
        return bookRepository.countByActiveTrue();
    }

    /**
     * Kiểm tra ISBN đã bị dùng bởi sách khác chưa.
     * @param isbn      ISBN cần kiểm tra
     * @param excludeId id sách hiện tại (khi edit) — null khi create
     * @return true nếu ISBN đã tồn tại ở sách khác
     */
    public boolean isIsbnTaken(String isbn, Long excludeId) {
        if (!org.springframework.util.StringUtils.hasText(isbn)) return false;
        return bookRepository.findByIsbnIgnoreCase(isbn)
                .map(existing -> excludeId == null || !existing.getId().equals(excludeId))
                .orElse(false);
    }

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
