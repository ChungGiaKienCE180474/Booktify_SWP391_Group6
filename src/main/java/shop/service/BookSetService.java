package shop.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import shop.domain.Book;
import shop.domain.BookSet;
import shop.domain.BookSetItem;
import shop.domain.dto.BookSetForm;
import shop.domain.dto.BookSetItemForm;
import shop.repository.BookRepository;
import shop.repository.BookSetRepository;
import shop.repository.CartItemRepository;

@Service
public class BookSetService {

    private final BookSetRepository bookSetRepository;
    private final BookRepository bookRepository;
    private final StockService stockService;
    private final CartItemRepository cartItemRepository;

    public BookSetService(
            BookSetRepository bookSetRepository,
            BookRepository bookRepository,
            StockService stockService,
            CartItemRepository cartItemRepository) {
        this.bookSetRepository = bookSetRepository;
        this.bookRepository = bookRepository;
        this.stockService = stockService;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional(readOnly = true)
    public List<BookSet> findAllForAdmin() {
        return bookSetRepository.findAllWithItems();
    }

    /**
     * Tìm kiếm + lọc trạng thái cho trang admin (giống filterBooks bên Book).
     * status: "active" | "inactive" | khác/null = tất cả.
     */
    @Transactional(readOnly = true)
    public List<BookSet> filterForAdmin(String keyword, String status) {
        String kw = blankToNull(keyword);
        String lowerKw = kw == null ? null : kw.toLowerCase(Locale.ROOT);

        return bookSetRepository.findAllWithItems().stream()
                .filter(s -> matchesKeyword(s, lowerKw))
                .filter(s -> matchesStatus(s, status))
                .toList();
    }

    private boolean matchesKeyword(BookSet set, String lowerKw) {
        if (lowerKw == null) {
            return true;
        }
        String name = set.getName() == null ? "" : set.getName().toLowerCase(Locale.ROOT);
        String tag = set.getGradeLevel() == null ? "" : set.getGradeLevel().toLowerCase(Locale.ROOT);
        String desc = set.getDescription() == null ? "" : set.getDescription().toLowerCase(Locale.ROOT);
        return name.contains(lowerKw) || tag.contains(lowerKw) || desc.contains(lowerKw);
    }

    private boolean matchesStatus(BookSet set, String status) {
        if ("active".equalsIgnoreCase(status)) {
            return set.isActive();
        }
        if ("inactive".equalsIgnoreCase(status)) {
            return !set.isActive();
        }
        return true;
    }

    /**
     * Soft-delete: ẩn book set (active=false). Chặn nếu đang nằm trong giỏ khách.
     */
    @Transactional
    public void removeBookSet(long id) {
        BookSet bookSet = bookSetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book set not found."));
        // Kiểm tra xem book set có đang nằm trong giỏ hàng của khách hàng không !!!
        if (cartItemRepository.existsByBookSet_Id(id)) {
            throw new IllegalStateException(
                    "Cannot delete this book set: it is currently in a customer's cart.");
        }

        bookSet.setActive(false);
        bookSetRepository.save(bookSet);
    }

    /** Khôi phục book set đã ẩn (active=true). */
    @Transactional
    public void restoreBookSet(long id) {
        BookSet bookSet = bookSetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book set not found."));
        bookSet.setActive(true);
        bookSetRepository.save(bookSet);
    }

    // Tìm kiếm các book set đang hoạt động
    @Transactional(readOnly = true)
    public List<BookSet> searchActive(String keyword, String tag) {
        return bookSetRepository.searchActive(
                blankToNull(keyword),
                blankToNull(tag));
    }

    // Tìm kiếm các book set đang hoạt động theo tag
    @Transactional(readOnly = true)
    public List<String> listActiveTags() {
        return bookSetRepository.findDistinctActiveTags();
    }

    // Tìm kiếm tất cả các tag của book set (bao gồm cả inactive)
    @Transactional(readOnly = true)
    public List<String> listAllTags() {
        return bookSetRepository.findDistinctTags();
    }

    // Tìm kiếm book set theo id, bao gồm các item bên trong
    @Transactional(readOnly = true)
    public BookSet getByIdWithItems(long id) {
        return bookSetRepository.findByIdWithItems(id)
                .orElseThrow(() -> new IllegalArgumentException("Book set not found."));
    }

    // Tính số lượng set có thể bán dựa trên số lượng sách trong kho
    @Transactional(readOnly = true)
    public int getAvailableSetQuantity(BookSet bookSet) {
        if (bookSet == null || bookSet.getItems() == null || bookSet.getItems().isEmpty()) {
            return 0;
        }
        // Khởi tạo số lượng có sẵn là vô cực, sau đó tìm min dựa trên từng item
        int available = Integer.MAX_VALUE;
        for (BookSetItem item : bookSet.getItems()) {
            if (item.getBook() == null || !item.getBook().isActive() || item.getQuantity() <= 0) {
                return 0;
            }
            // Lấy số lượng sách trong kho và tính số set có thể bán dựa trên số lượng sách
            // và số lượng yêu cầu trong set
            int stock = stockService.getBookQuantity(item.getBook().getId());
            int setsFromBook = stock / item.getQuantity();
            available = Math.min(available, setsFromBook);
        }
        // Nếu available vẫn là Integer.MAX_VALUE, nghĩa là không có item nào hợp lệ,
        // trả về 0
        return available == Integer.MAX_VALUE ? 0 : available;
    }

    // Tính tổng giá bán lẻ của book set dựa trên giá sách và số lượng trong set
    @Transactional(readOnly = true)
    public BigDecimal getRetailTotal(BookSet bookSet) {
        BigDecimal total = BigDecimal.ZERO;
        if (bookSet == null || bookSet.getItems() == null) {
            return total;
        }
        //  Tính tổng giá bán lẻ dựa trên giá sách và số lượng trong set
        for (BookSetItem item : bookSet.getItems()) {
            if (item.getBook() == null || item.getBook().getPrice() == null) {
                continue;
            }
            total = total.add(
                    item.getBook().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }

    /**
     * Phân bổ setPrice vào từng bookId (giá 1 cuốn trong 1 bộ).
     * <p>
     * Đơn giá là SỐ NGUYÊN đồng (VND không có phần lẻ) để hiển thị nhất quán:
     * đơn giá × số lượng = thành tiền, không còn cảnh "0đ × 4 = 2đ".
     * Sách giá 0 nhận trọng số 0 (không bị phân bổ phần lẻ khống), trừ khi cả bộ
     * đều miễn phí thì chia đều theo số lượng.
     */
    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> allocateUnitPrices(BookSet bookSet) {
        Map<Long, BigDecimal> result = new LinkedHashMap<>();
        if (bookSet == null || bookSet.getItems() == null || bookSet.getItems().isEmpty()) {
            return result;
        }

        BigDecimal setPrice = bookSet.getSetPrice() == null
                ? BigDecimal.ZERO
                : bookSet.getSetPrice();

        List<BookSetItem> items = bookSet.getItems();

        // Trọng số = giá × số lượng. Sách giá 0 → trọng số 0 (không nhận phân bổ).
        BigDecimal totalWeight = BigDecimal.ZERO;
        List<BigDecimal> weights = new ArrayList<>();
        for (BookSetItem item : items) {
            BigDecimal price = item.getBook() != null && item.getBook().getPrice() != null
                    ? item.getBook().getPrice()
                    : BigDecimal.ZERO;
            BigDecimal weight = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            if (weight.signum() < 0) {
                weight = BigDecimal.ZERO;
            }
            weights.add(weight);
            totalWeight = totalWeight.add(weight);
        }

        // Cả bộ miễn phí → chia đều theo số lượng để tránh chia cho 0.
        if (totalWeight.signum() == 0) {
            totalWeight = BigDecimal.ZERO;
            for (int i = 0; i < items.size(); i++) {
                BigDecimal weight = BigDecimal.valueOf(Math.max(1, items.get(i).getQuantity()));
                weights.set(i, weight);
                totalWeight = totalWeight.add(weight);
            }
        }
        // Phân bổ giá setPrice vào từng item theo trọng số, làm tròn số nguyên đồng.
        BigDecimal allocatedSum = BigDecimal.ZERO;
        for (int i = 0; i < items.size(); i++) {
            BookSetItem item = items.get(i);
            int qty = Math.max(1, item.getQuantity());
            //  Phân bổ giá setPrice theo trọng số, làm tròn số nguyên đồng. Dòng cuối nhận phần dư để tổng khớp setPrice.
            BigDecimal componentTotal;
            if (i == items.size() - 1) {
                // Dòng cuối nhận phần dư để tổng khớp setPrice.
                componentTotal = setPrice.subtract(allocatedSum);
            } else {
                componentTotal = setPrice
                        .multiply(weights.get(i))
                        .divide(totalWeight, 0, RoundingMode.HALF_UP);
                allocatedSum = allocatedSum.add(componentTotal);
            }

            // Đơn giá làm tròn số nguyên đồng.
            BigDecimal unitPrice = componentTotal.divide(
                    BigDecimal.valueOf(qty),
                    0,
                    RoundingMode.HALF_UP);
            if (unitPrice.signum() < 0) {
                unitPrice = BigDecimal.ZERO;
            }
            result.put(item.getBook().getId(), unitPrice);
        }

        return result;
    }

    @Transactional
    public BookSet saveFromForm(BookSetForm form) {
        validateForm(form);

        BookSet bookSet;
        if (form.getId() != null) {
            bookSet = getByIdWithItems(form.getId());
            bookSet.clearItems();
            // Xóa các item cũ và FLUSH trước khi thêm lại. Nếu không, khi giữ nguyên
            // cùng cuốn sách, Hibernate insert dòng (book_set_id, book_id) mới trước khi
            // delete dòng cũ -> vi phạm unique uk_book_set_book -> lỗi khi cập nhật.
            bookSetRepository.saveAndFlush(bookSet);
        } else {
            bookSet = new BookSet();
        }

        bookSet.setName(form.getName().trim());
        bookSet.setDescription(blankToNull(form.getDescription()));
        bookSet.setImageUrl(blankToNull(form.getImageUrl()));
        bookSet.setSetPrice(form.getSetPrice());
        bookSet.setGradeLevel(blankToNull(form.getGradeLevel()));
        bookSet.setActive(form.isActive());

        for (BookSetItemForm itemForm : form.getItems()) {
            Book book = bookRepository.findById(itemForm.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Book not found: " + itemForm.getBookId()));
            if (!book.isActive()) {
                throw new IllegalArgumentException(
                        "\"" + book.getTitle() + "\" is inactive and cannot be added to a set.");
            }
            BookSetItem item = new BookSetItem();
            item.setBook(book);
            item.setQuantity(itemForm.getQuantity());
            bookSet.addItem(item);
        }

        return bookSetRepository.save(bookSet);
    }
    // Chuyển trạng thái active/inactive của book set
    @Transactional
    public void setActive(long id, boolean active) {
        BookSet bookSet = getByIdWithItems(id);
        bookSet.setActive(active);
        bookSetRepository.save(bookSet);
    }

    public BookSetForm toForm(BookSet bookSet) {
        BookSetForm form = new BookSetForm();
        form.setId(bookSet.getId());
        form.setName(bookSet.getName());
        form.setDescription(bookSet.getDescription());
        form.setImageUrl(bookSet.getImageUrl());
        form.setSetPrice(bookSet.getSetPrice());
        form.setGradeLevel(bookSet.getGradeLevel());
        form.setActive(bookSet.isActive());

        List<BookSetItemForm> items = new ArrayList<>();
        for (BookSetItem item : bookSet.getItems()) {
            if (item.getBook() != null) {
                items.add(new BookSetItemForm(item.getBook().getId(), item.getQuantity()));
            }
        }
        form.setItems(items);
        return form;
    }

    public String formatMoney(BigDecimal amount) {
        if (amount == null) {
            return "0";
        }
        return NumberFormat.getIntegerInstance(Locale.GERMANY).format(amount.longValue());
    }
    // Chuyển đổi gradeLevel hoặc tag thành nhãn hiển thị cho người dùng
    public String gradeLabel(String gradeLevel) {
        return tagLabel(gradeLevel);
    }
    // Chuyển đổi tag thành nhãn hiển thị cho người dùng
    public String tagLabel(String tag) {
        if (!StringUtils.hasText(tag)) {
            return "General";
        }
        String value = tag.trim();
        if (value.startsWith("GRADE_")) {
            return "Grade " + value.substring("GRADE_".length());
        }
        return value;
    }
    // Validate form data before saving to database
    private void validateForm(BookSetForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Book set information is required.");
        }
        if (form.getItems() == null) {
            form.setItems(new ArrayList<>());
        }
        // Validate that each book in the set has a quantity >= 1 and that there are at least 2 different books in the set
        Map<Long, Integer> qtyByBook = new LinkedHashMap<>();
        for (BookSetItemForm item : form.getItems()) {
            if (item == null || item.getBookId() == null) {
                continue;
            }
            int qty = item.getQuantity() == null ? 0 : item.getQuantity();
            if (qty < 1) {
                throw new IllegalArgumentException("Each book in the set must have quantity >= 1.");
            }
            qtyByBook.merge(item.getBookId(), qty, Integer::sum);
        }
        // ít nhất 2 cuốn sách khác nhau trong set
        if (qtyByBook.size() < 2) {
            throw new IllegalArgumentException(
                    "A book set must contain at least 2 different books (comics, textbooks, or any titles).");
        }
        // Normalize the items in the form to ensure that each book appears only once with the total quantity
        List<BookSetItemForm> normalized = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : qtyByBook.entrySet()) {
            normalized.add(new BookSetItemForm(entry.getKey(), entry.getValue()));
        }
        form.setItems(normalized);
    }
    // Convert blank strings to null for optional fields
    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
