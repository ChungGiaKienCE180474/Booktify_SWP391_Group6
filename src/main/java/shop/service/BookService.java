package shop.service;

import java.util.ArrayList;
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
    private final StockService stockService;

    public BookService(
            BookRepository bookRepository,
            CartItemRepository cartItemRepository,
            StockService stockService
    ) {
        this.bookRepository = bookRepository;
        this.cartItemRepository = cartItemRepository;
        this.stockService = stockService;
    }

    /*
     * ============================================================
     * GET BOOKS
     * ============================================================
     */

    /*
     * Includes inactive books.
     * Chỉ dùng cho admin.
     */
    public List<Book> getAllBooks() {
        return bookRepository
                .findAllByOrderByIdAsc();
    }

    /*
     * Chỉ lấy Book đang hoạt động.
     */
    public List<Book> getActiveBooks() {
        return bookRepository
                .findAllByActiveTrueOrderByIdAsc();
    }

    /*
     * Lấy Book đang hoạt động theo Category.
     */
    public List<Book> getBooksByCategory(
            Long categoryId
    ) {
        return bookRepository
                .findAllByCategoryIdAndActiveTrueOrderByIdAsc(
                        categoryId
                );
    }

    /*
     * ============================================================
     * SEARCH BOOK
     * ============================================================
     */

    public List<Book> searchBooks(
            String q,
            String status
    ) {
        List<Book> result;

        if (!StringUtils.hasText(q)) {
            result = getAllBooks();

        } else {
            result = bookRepository.search(q);
        }

        if ("active".equalsIgnoreCase(status)) {

            result = result
                    .stream()
                    .filter(Book::isActive)
                    .toList();

        } else if ("inactive".equalsIgnoreCase(status)) {

            result = result
                    .stream()
                    .filter(book ->
                            !book.isActive()
                    )
                    .toList();
        }

        return result;
    }

    /*
     * Genre filter sử dụng OR:
     *
     * Book xuất hiện nếu có ít nhất một Genre được chọn.
     *
     * Category và keyword tiếp tục được áp dụng cùng lúc.
     */
    public List<Book> filterBooks(
            String q,
            Long categoryId,
            List<Long> genreIds,
            String status
    ) {
        String keyword =
                StringUtils.hasText(q)
                        ? q.trim()
                        : null;

        boolean hasGenreFilter =
                genreIds != null
                        && !genreIds.isEmpty();

        /*
         * Hibernate không xử lý tốt null trong IN clause.
         *
         * Khi không lọc Genre, truyền List rỗng và dùng
         * hasGenreFilter để bỏ qua điều kiện.
         */
        List<Long> safeGenreIds =
                hasGenreFilter
                        ? genreIds
                        : List.of();

        List<Book> result =
                bookRepository.filterBooks(
                        keyword,
                        categoryId,
                        safeGenreIds,
                        hasGenreFilter
                );

        if ("active".equalsIgnoreCase(status)) {

            result = result
                    .stream()
                    .filter(Book::isActive)
                    .toList();

        } else if ("inactive".equalsIgnoreCase(status)) {

            result = result
                    .stream()
                    .filter(book ->
                            !book.isActive()
                    )
                    .toList();
        }

        return result;
    }

    /*
     * ============================================================
     * FIND BOOK
     * ============================================================
     */

    public Optional<Book> getBookById(
            Long id
    ) {
        return bookRepository.findById(id);
    }

    /*
     * ============================================================
     * SAVE BOOK
     * ============================================================
     *
     * Đây là đường lưu Book duy nhất.
     *
     * Sau khi Book được lưu:
     *
     * - Nếu Book chưa có dòng trong bảng stock:
     *   tạo một dòng stock mới.
     *
     * - Nếu Book đã có dòng stock:
     *   StockService sẽ không tạo trùng.
     *
     * Vì vậy phương thức này dùng được cho cả:
     *
     * - Create Book
     * - Edit Book
     */

    @Transactional
    public Book saveBook(
            Book book
    ) {
        if (book == null) {
            throw new IllegalArgumentException(
                    "Book is required."
            );
        }

        /*
         * Đảm bảo số lượng ban đầu không âm.
         *
         * Trên Create Book, giá trị này được dùng để tạo:
         *
         * stock.quantity
         */
        if (book.getStockQuantity() < 0) {
            throw new IllegalArgumentException(
                    "Stock quantity must be greater than or equal to 0."
            );
        }

        Book savedBook =
                bookRepository.save(book);

        /*
         * Tạo dòng stock nếu chưa tồn tại.
         *
         * Khi Edit Book, dòng stock đã tồn tại nên hàm này
         * không tạo thêm dòng mới và không thay đổi quantity.
         */
        stockService.recordInitialBookStock(
                savedBook
        );

        return savedBook;
    }

    /*
     * ============================================================
     * REMOVE BOOK
     * ============================================================
     *
     * Đây là soft delete.
     *
     * Book không bị xóa khỏi database mà chỉ chuyển active=false.
     */

    @Transactional
    public void removeBook(
            Long id
    ) {
        Book book =
                bookRepository
                        .findById(id)
                        .orElse(null);

        if (book == null) {
            return;
        }

        /*
         * Không cho ẩn Book nếu Book vẫn đang nằm trong giỏ hàng
         * của khách hàng.
         */
        if (cartItemRepository.existsByBook_Id(id)) {
            throw new IllegalStateException(
                    "Cannot delete this book: "
                            + "it is currently in a customer's cart."
            );
        }

        book.setActive(false);

        bookRepository.save(book);
    }

    /*
     * ============================================================
     * RESTORE BOOK
     * ============================================================
     */

    @Transactional
    public void restoreBook(
            Long id
    ) {
        bookRepository
                .findById(id)
                .ifPresent(book -> {

                    book.setActive(true);

                    bookRepository.save(book);

                    /*
                     * Nếu dữ liệu Book cũ chưa có dòng stock,
                     * hệ thống tự tạo khi restore.
                     */
                    stockService.recordInitialBookStock(
                            book
                    );
                });
    }

    /*
     * ============================================================
     * COUNTERS
     * ============================================================
     */

    public long countBooks() {
        return bookRepository.count();
    }

    public long countActiveBooks() {
        return bookRepository
                .countByActiveTrue();
    }

    /*
     * ============================================================
     * ISBN VALIDATION
     * ============================================================
     *
     * excludeId dùng khi Edit Book để không so sánh ISBN
     * với chính Book đang sửa.
     */

    public boolean isIsbnTaken(
            String isbn,
            Long excludeId
    ) {
        if (!StringUtils.hasText(isbn)) {
            return false;
        }

        return bookRepository
                .findByIsbnIgnoreCase(
                        isbn.trim()
                )
                .map(existing ->
                        excludeId == null
                                || !existing
                                .getId()
                                .equals(excludeId)
                )
                .orElse(false);
    }

    /*
     * ============================================================
     * SUGGESTED BOOKS
     * ============================================================
     *
     * Ưu tiên Book cùng Category.
     *
     * Nếu chưa đủ 6 Book thì lấy thêm Book đang hoạt động
     * từ Category khác.
     */

    public List<Book> getSuggestedBooks(
            Long categoryId,
            Long excludeBookId
    ) {
        if (categoryId == null) {

            return bookRepository
                    .findAllByActiveTrueOrderByIdAsc()
                    .stream()
                    .filter(book ->
                            excludeBookId == null
                                    || !book
                                    .getId()
                                    .equals(excludeBookId)
                    )
                    .limit(6)
                    .toList();
        }

        List<Book> suggested =
                bookRepository
                        .findAllByCategoryIdAndActiveTrueAndIdNotOrderByIdAsc(
                                categoryId,
                                excludeBookId
                        );

        if (suggested.size() < 6) {

            List<Book> extra =
                    bookRepository
                            .findAllByActiveTrueOrderByIdAsc()
                            .stream()
                            .filter(book ->
                                    excludeBookId == null
                                            || !book
                                            .getId()
                                            .equals(
                                                    excludeBookId
                                            )
                            )
                            .filter(book ->
                                    book.getCategory() == null
                                            || !book
                                            .getCategory()
                                            .getId()
                                            .equals(categoryId)
                            )
                            .limit(
                                    6 - suggested.size()
                            )
                            .toList();

            suggested =
                    new ArrayList<>(suggested);

            suggested.addAll(extra);
        }

        return suggested;
    }
}