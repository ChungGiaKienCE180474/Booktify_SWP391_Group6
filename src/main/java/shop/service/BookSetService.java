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

@Service
public class BookSetService {

    private final BookSetRepository bookSetRepository;
    private final BookRepository bookRepository;
    private final StockService stockService;

    public BookSetService(
            BookSetRepository bookSetRepository,
            BookRepository bookRepository,
            StockService stockService) {
        this.bookSetRepository = bookSetRepository;
        this.bookRepository = bookRepository;
        this.stockService = stockService;
    }

    @Transactional(readOnly = true)
    public List<BookSet> findAllForAdmin() {
        return bookSetRepository.findAllWithItems();
    }

    @Transactional(readOnly = true)
    public List<BookSet> searchActive(String keyword, String tag) {
        return bookSetRepository.searchActive(
                blankToNull(keyword),
                blankToNull(tag)
        );
    }

    @Transactional(readOnly = true)
    public List<String> listActiveTags() {
        return bookSetRepository.findDistinctActiveTags();
    }

    @Transactional(readOnly = true)
    public List<String> listAllTags() {
        return bookSetRepository.findDistinctTags();
    }

    @Transactional(readOnly = true)
    public BookSet getByIdWithItems(long id) {
        return bookSetRepository.findByIdWithItems(id)
                .orElseThrow(() -> new IllegalArgumentException("Book set not found."));
    }

    @Transactional(readOnly = true)
    public int getAvailableSetQuantity(BookSet bookSet) {
        if (bookSet == null || bookSet.getItems() == null || bookSet.getItems().isEmpty()) {
            return 0;
        }

        int available = Integer.MAX_VALUE;
        for (BookSetItem item : bookSet.getItems()) {
            if (item.getBook() == null || !item.getBook().isActive() || item.getQuantity() <= 0) {
                return 0;
            }
            int stock = stockService.getBookQuantity(item.getBook().getId());
            int setsFromBook = stock / item.getQuantity();
            available = Math.min(available, setsFromBook);
        }

        return available == Integer.MAX_VALUE ? 0 : available;
    }

    @Transactional(readOnly = true)
    public BigDecimal getRetailTotal(BookSet bookSet) {
        BigDecimal total = BigDecimal.ZERO;
        if (bookSet == null || bookSet.getItems() == null) {
            return total;
        }
        for (BookSetItem item : bookSet.getItems()) {
            if (item.getBook() == null || item.getBook().getPrice() == null) {
                continue;
            }
            total = total.add(
                    item.getBook().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }
        return total;
    }

    /**
     * Phân bổ setPrice vào từng bookId (giá 1 cuốn trong 1 bộ).
     * Dòng cuối được điều chỉnh để tổng khớp setPrice.
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
        BigDecimal totalWeight = BigDecimal.ZERO;
        List<BigDecimal> weights = new ArrayList<>();

        for (BookSetItem item : items) {
            BigDecimal price = item.getBook() != null && item.getBook().getPrice() != null
                    ? item.getBook().getPrice()
                    : BigDecimal.ZERO;
            BigDecimal weight = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            if (weight.compareTo(BigDecimal.ZERO) <= 0) {
                weight = BigDecimal.ONE;
            }
            weights.add(weight);
            totalWeight = totalWeight.add(weight);
        }

        BigDecimal allocatedSum = BigDecimal.ZERO;
        for (int i = 0; i < items.size(); i++) {
            BookSetItem item = items.get(i);
            BigDecimal componentTotal;
            if (i == items.size() - 1) {
                componentTotal = setPrice.subtract(allocatedSum);
            } else {
                componentTotal = setPrice
                        .multiply(weights.get(i))
                        .divide(totalWeight, 2, RoundingMode.HALF_UP);
                allocatedSum = allocatedSum.add(componentTotal);
            }

            BigDecimal unitPrice = componentTotal.divide(
                    BigDecimal.valueOf(item.getQuantity()),
                    2,
                    RoundingMode.HALF_UP
            );
            if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
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
                            "Book not found: " + itemForm.getBookId()
                    ));
            if (!book.isActive()) {
                throw new IllegalArgumentException(
                        "\"" + book.getTitle() + "\" is inactive and cannot be added to a set."
                );
            }
            BookSetItem item = new BookSetItem();
            item.setBook(book);
            item.setQuantity(itemForm.getQuantity());
            bookSet.addItem(item);
        }

        return bookSetRepository.save(bookSet);
    }

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

    public String gradeLabel(String gradeLevel) {
        return tagLabel(gradeLevel);
    }

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

    private void validateForm(BookSetForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Book set information is required.");
        }
        if (form.getItems() == null) {
            form.setItems(new ArrayList<>());
        }

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

        if (qtyByBook.size() < 2) {
            throw new IllegalArgumentException(
                    "A book set must contain at least 2 different books (comics, textbooks, or any titles)."
            );
        }

        List<BookSetItemForm> normalized = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : qtyByBook.entrySet()) {
            normalized.add(new BookSetItemForm(entry.getKey(), entry.getValue()));
        }
        form.setItems(normalized);
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
