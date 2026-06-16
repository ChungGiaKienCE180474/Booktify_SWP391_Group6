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

    public List<Book> searchBooks(String q) {
        if (!StringUtils.hasText(q)) {
            return getAllBooks();
        }
        return bookRepository.search(q);
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void softDeleteBook(Long id) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setActive(false);
            bookRepository.save(book);
        });
    }

    public long countBooks() {
        return bookRepository.count();
    }

    public long countActiveBooks() {
        return bookRepository.findAll().stream().filter(Book::isActive).count();
    }

    public boolean existsByIsbnIgnoreCase(String isbn) {
        return bookRepository.existsByIsbnIgnoreCase(isbn);
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
