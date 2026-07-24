package shop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import shop.domain.Book;
import shop.domain.Category;
import shop.domain.Cart;
import shop.domain.Promotion;
import shop.domain.PromotionSelection;
import shop.repository.PromotionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Transactional(readOnly = true)
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAllWithCategories();
    }

    @Transactional(readOnly = true)
    public List<Promotion> getActivePromotions() {
        return promotionRepository.findActivePromotions();
    }

    @Transactional(readOnly = true)
    public Optional<Promotion> getPromotionById(Long id) {
        return promotionRepository.findByIdWithCategories(id);
    }

    public boolean existsByNameIgnoreCase(String name) {
        return promotionRepository.existsByNameIgnoreCase(name);
    }

    public boolean hasCategoryConflict(
            Long categoryId,
            boolean percentage,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long excludePromotionId) {

        if (categoryId == null || startDate == null || endDate == null) {
            return false;
        }

        return promotionRepository.existsConflictingPromotion(
                categoryId, percentage, startDate, endDate, excludePromotionId);
    }

    @Transactional
    public Promotion savePromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Transactional
    public void softDeletePromotion(Long id) {
        promotionRepository.findById(id)
                .ifPresent(promotion -> {
                    promotion.setActive(false);
                    promotionRepository.save(promotion);
                });
    }


    @Transactional(readOnly = true)
    public Optional<Promotion> getActivePromotionForBookByType(Book book, boolean percentage) {
        if (book == null || book.getPrice() == null || book.getCategory() == null
                || book.getCategory().getId() == null) {
            return Optional.empty();
        }

        return promotionRepository
                .findActivePromotionsByCategoryIdAndType(book.getCategory().getId(), percentage)
                .stream()
                .findFirst();
    }

    @Transactional(readOnly = true)
    public Optional<Promotion> getPromotionForSelection(Book book, PromotionSelection selection) {
        if (selection == null || selection == PromotionSelection.NONE) {
            return Optional.empty();
        }
        return getActivePromotionForBookByType(book, selection == PromotionSelection.PERCENTAGE);
    }

    @Transactional(readOnly = true)
    public BigDecimal getPriceForSelection(Book book, PromotionSelection selection) {
        if (book == null || book.getPrice() == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return getPromotionForSelection(book, selection)
                .map(promotion -> calculateDiscountedPrice(book.getPrice(), promotion))
                .orElse(book.getPrice().setScale(2, RoundingMode.HALF_UP));
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateCartTotal(Cart cart, PromotionSelection selection) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return cart.getItems().stream()
                .filter(item -> item != null)
                .map(item -> {
                    BigDecimal unitPrice = BigDecimal.ZERO;
                    if (item.getBook() != null) {
                        unitPrice = getPriceForSelection(item.getBook(), selection);
                    } else if (item.getVppItem() != null && item.getVppItem().getPrice() != null) {
                        unitPrice = item.getVppItem().getPrice();
                    }
                    return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Returns the best currently active promotion for a book.
     *
     * When more than one promotion is available, the promotion that produces
     * the lowest final selling price is selected.
     */
    @Transactional(readOnly = true)
    public Optional<Promotion> getBestPromotionForBook(Book book) {

        if (book == null
                || book.getPrice() == null
                || book.getCategory() == null
                || book.getCategory().getId() == null) {
            return Optional.empty();
        }

        return promotionRepository
                .findActivePromotionsByCategoryId(
                        book.getCategory().getId()
                )
                .stream()
                .min(
                        Comparator.comparing(
                                promotion ->
                                        calculateDiscountedPrice(
                                                book.getPrice(),
                                                promotion
                                        )
                        )
                );
    }

    /**
     * Returns the effective selling price of a book at the current time.
     *
     * CartService and OrderService must use this method so the cart,
     * checkout, and order history always use consistent prices.
     */
    @Transactional(readOnly = true)
    public BigDecimal getEffectivePrice(Book book) {

        if (book == null || book.getPrice() == null) {
            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        return getBestPromotionForBook(book)
                .map(promotion ->
                        calculateDiscountedPrice(
                                book.getPrice(),
                                promotion
                        )
                )
                .orElse(
                        book.getPrice().setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
                );
    }

    /**
     * Creates a map containing the best promotion for every promoted book.
     *
     * The map key is the book ID.
     */
    @Transactional(readOnly = true)
    public Map<Long, Promotion> getBestPromotionMap(
            List<Book> books) {

        Map<Long, Promotion> result = new HashMap<>();

        if (books == null || books.isEmpty()) {
            return result;
        }

        for (Book book : books) {

            if (book == null || book.getId() == null) {
                continue;
            }

            getBestPromotionForBook(book)
                    .ifPresent(promotion ->
                            result.put(
                                    book.getId(),
                                    promotion
                            )
                    );
        }

        return result;
    }

    /**
     * Creates a map containing the final discounted price for every promoted
     * book.
     *
     * The map key is the book ID.
     */
    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> getDiscountedPriceMap(
            List<Book> books) {

        Map<Long, BigDecimal> result = new HashMap<>();

        if (books == null || books.isEmpty()) {
            return result;
        }

        for (Book book : books) {

            if (book == null
                    || book.getId() == null
                    || book.getPrice() == null) {
                continue;
            }

            getBestPromotionForBook(book)
                    .ifPresent(promotion ->
                            result.put(
                                    book.getId(),
                                    calculateDiscountedPrice(
                                            book.getPrice(),
                                            promotion
                                    )
                            )
                    );
        }

        return result;
    }

    /**
     * Creates a map containing the actual discount amount for every promoted
     * book.
     *
     * The map key is the book ID.
     */
    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> getDiscountAmountMap(
            List<Book> books) {

        Map<Long, BigDecimal> result = new HashMap<>();

        if (books == null || books.isEmpty()) {
            return result;
        }

        for (Book book : books) {

            if (book == null
                    || book.getId() == null
                    || book.getPrice() == null) {
                continue;
            }

            getBestPromotionForBook(book)
                    .ifPresent(promotion ->
                            result.put(
                                    book.getId(),
                                    calculateDiscountAmount(
                                            book.getPrice(),
                                            promotion
                                    )
                            )
                    );
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getDiscountedPriceFormattedMap(
            List<Book> books) {

        Map<Long, Promotion> bestPromotionMap =
                getBestPromotionMap(books);

        return getDiscountedPriceFormattedMap(
                books,
                bestPromotionMap
        );
    }

    public Map<Long, String> getDiscountedPriceFormattedMap(
            List<Book> books,
            Map<Long, Promotion> bestPromotionMap) {

        Map<Long, String> result = new HashMap<>();

        if (books == null
                || books.isEmpty()
                || bestPromotionMap == null
                || bestPromotionMap.isEmpty()) {
            return result;
        }

        for (Book book : books) {

            if (book == null || book.getId() == null) {
                continue;
            }

            Promotion promotion =
                    bestPromotionMap.get(book.getId());

            if (promotion == null) {
                continue;
            }

            result.put(
                    book.getId(),
                    getDiscountedPriceFormatted(
                            book,
                            promotion
                    )
            );
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getDiscountLabelMap(
            List<Book> books) {

        Map<Long, Promotion> bestPromotionMap =
                getBestPromotionMap(books);

        return getDiscountLabelMap(
                books,
                bestPromotionMap
        );
    }

    public Map<Long, String> getDiscountLabelMap(
            List<Book> books,
            Map<Long, Promotion> bestPromotionMap) {

        Map<Long, String> result = new HashMap<>();

        if (books == null
                || books.isEmpty()
                || bestPromotionMap == null
                || bestPromotionMap.isEmpty()) {
            return result;
        }

        for (Book book : books) {

            if (book == null || book.getId() == null) {
                continue;
            }

            Promotion promotion =
                    bestPromotionMap.get(book.getId());

            if (promotion == null) {
                continue;
            }

            result.put(
                    book.getId(),
                    getDiscountLabel(
                            book,
                            promotion
                    )
            );
        }

        return result;
    }

    @Transactional(readOnly = true)
    public String getDiscountedPriceFormatted(Book book) {

        if (book == null) {
            return "0";
        }

        Promotion promotion =
                getBestPromotionForBook(book)
                        .orElse(null);

        return getDiscountedPriceFormatted(
                book,
                promotion
        );
    }

    public String getDiscountedPriceFormatted(
            Book book,
            Promotion promotion) {

        if (book == null || book.getPrice() == null) {
            return "0";
        }

        BigDecimal effectivePrice;

        if (promotion == null) {
            effectivePrice = book.getPrice();
        } else {
            effectivePrice = calculateDiscountedPrice(
                    book.getPrice(),
                    promotion
            );
        }

        return formatMoney(effectivePrice);
    }

    @Transactional(readOnly = true)
    public String getDiscountLabel(Book book) {

        if (book == null) {
            return "";
        }

        Promotion promotion =
                getBestPromotionForBook(book)
                        .orElse(null);

        return getDiscountLabel(
                book,
                promotion
        );
    }

    public String getDiscountLabel(
            Book book,
            Promotion promotion) {

        if (book == null || promotion == null) {
            return "";
        }

        return buildDiscountLabel(
                book.getPrice(),
                promotion
        );
    }

    public String buildDiscountLabel(
            BigDecimal originalPrice,
            Promotion promotion) {

        if (originalPrice == null
                || promotion == null
                || promotion.getDiscountValue() == null) {
            return "";
        }

        if (promotion.isPercentage()) {
            return "-"
                    + promotion.getDiscountValue()
                    .stripTrailingZeros()
                    .toPlainString()
                    + "%";
        }

        BigDecimal actualDiscount =
                calculateDiscountAmount(
                        originalPrice,
                        promotion
                );

        return "-"
                + formatMoney(actualDiscount)
                + " VND";
    }

    public String formatMoney(BigDecimal amount) {

        if (amount == null) {
            return "0";
        }

        return NumberFormat
                .getIntegerInstance(Locale.GERMANY)
                .format(amount.longValue());
    }

    public BigDecimal calculateDiscountedPrice(
            BigDecimal originalPrice,
            Promotion promotion) {

        if (originalPrice == null) {
            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        if (promotion == null
                || promotion.getDiscountValue() == null) {
            return originalPrice.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        BigDecimal discountValue =
                promotion.getDiscountValue();

        if (discountValue.compareTo(BigDecimal.ZERO) < 0) {
            return originalPrice.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        BigDecimal result;

        if (promotion.isPercentage()) {

            BigDecimal percentage =
                    discountValue.min(
                            BigDecimal.valueOf(100)
                    );

            BigDecimal discountAmount =
                    originalPrice
                            .multiply(percentage)
                            .divide(
                                    BigDecimal.valueOf(100),
                                    2,
                                    RoundingMode.HALF_UP
                            );

            result =
                    originalPrice.subtract(discountAmount);

        } else {

            result =
                    originalPrice.subtract(discountValue);
        }

        if (result.compareTo(BigDecimal.ZERO) < 0) {
            result = BigDecimal.ZERO;
        }

        return result.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    public BigDecimal calculateDiscountAmount(
            BigDecimal originalPrice,
            Promotion promotion) {

        if (originalPrice == null || promotion == null) {
            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        return originalPrice
                .subtract(
                        calculateDiscountedPrice(
                                originalPrice,
                                promotion
                        )
                )
                .max(BigDecimal.ZERO)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }

    public boolean appliesToCategory(
            Promotion promotion,
            Category category) {

        if (promotion == null
                || category == null
                || category.getId() == null
                || promotion.getApplicableCategories() == null) {
            return false;
        }

        return promotion
                .getApplicableCategories()
                .stream()
                .anyMatch(item ->
                        item != null
                                && item.getId() != null
                                && item.getId()
                                .equals(category.getId())
                );
    }
    @Transactional(readOnly = true)
    public boolean hasApplicablePromotion(
            Cart cart,
            PromotionSelection selection) {

        if (cart == null
                || cart.getItems() == null
                || cart.getItems().isEmpty()
                || selection == null
                || selection == PromotionSelection.NONE) {
            return false;
        }

        return cart.getItems()
                .stream()
                .filter(item -> item != null)
                .filter(item -> item.getBook() != null)
                .anyMatch(item ->
                        getPromotionForSelection(
                                item.getBook(),
                                selection
                        ).isPresent()
                );
    }
    @Transactional(readOnly = true)
    public BigDecimal calculateCartTotalBySelections(
            Cart cart,
            Map<Long, String> bookPromotionSelections) {

        if (cart == null
                || cart.getItems() == null
                || cart.getItems().isEmpty()) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        return cart.getItems()
                .stream()
                .filter(item -> item != null)
                .map(item -> {
                    BigDecimal unitPrice = BigDecimal.ZERO;

                    if (item.getBook() != null) {
                        Book book = item.getBook();

                        String selectedValue =
                                bookPromotionSelections == null
                                        ? null
                                        : bookPromotionSelections.get(
                                        book.getId()
                                );

                        PromotionSelection selection =
                                PromotionSelection.fromValue(
                                        selectedValue
                                );

                        /*
                         * getPriceForSelection() tự kiểm tra:
                         * - promotion còn hoạt động;
                         * - đúng thể loại;
                         * - đúng loại % hoặc VND.
                         *
                         * Nếu lựa chọn không hợp lệ thì trả giá gốc.
                         */
                        unitPrice = getPriceForSelection(
                                book,
                                selection
                        );
                    } else if (item.getVppItem() != null
                            && item.getVppItem().getPrice() != null) {

                        unitPrice =
                                item.getVppItem().getPrice();
                    }

                    return unitPrice.multiply(
                            BigDecimal.valueOf(
                                    item.getQuantity()
                            )
                    );
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
