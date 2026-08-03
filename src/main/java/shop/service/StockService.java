package shop.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import shop.domain.Book;
import shop.domain.Stock;
import shop.domain.StockMovementType;
import shop.domain.StockProductType;
import shop.domain.StockTransaction;
import shop.domain.User;
import shop.domain.VppItem;
import shop.domain.dto.StockItemDTO;
import shop.domain.dto.StockSummaryDTO;
import shop.domain.dto.StockTransactionDTO;
import shop.repository.BookRepository;
import shop.repository.StockRepository;
import shop.repository.StockTransactionRepository;
import shop.repository.VppItemRepository;

@Service
@Transactional(readOnly = true)
public class StockService {

    public static final int LOW_STOCK_THRESHOLD = 10;

    private final StockRepository stockRepository;
    private final BookRepository bookRepository;
    private final VppItemRepository vppItemRepository;
    private final StockTransactionRepository stockTransactionRepository;

    public StockService(
            StockRepository stockRepository,
            BookRepository bookRepository,
            VppItemRepository vppItemRepository,
            StockTransactionRepository stockTransactionRepository
    ) {
        this.stockRepository = stockRepository;
        this.bookRepository = bookRepository;
        this.vppItemRepository = vppItemRepository;
        this.stockTransactionRepository =
                stockTransactionRepository;
    }

    /*
     * =========================================================
     * INVENTORY LIST
     * =========================================================
     */

    public Page<StockItemDTO> searchInventory(
            String keyword,
            String productType,
            String stockStatus,
            String supplier,
            String category,
            Pageable pageable
    ) {
        String normalizedKeyword =
                normalize(keyword);

        String normalizedType =
                normalizeUpper(productType);

        String normalizedStatus =
                normalizeUpper(stockStatus);

        String normalizedSupplier =
                normalize(supplier);

        String normalizedCategory =
                normalize(category);

        List<StockItemDTO> filteredItems =
                buildInventory()
                        .stream()
                        .filter(item ->
                                matchesKeyword(
                                        item,
                                        normalizedKeyword
                                )
                        )
                        .filter(item ->
                                matchesProductType(
                                        item,
                                        normalizedType
                                )
                        )
                        .filter(item ->
                                matchesStockStatus(
                                        item,
                                        normalizedStatus
                                )
                        )
                        .filter(item ->
                                matchesExactValue(
                                        item.getSupplierName(),
                                        normalizedSupplier
                                )
                        )
                        .filter(item ->
                                matchesExactValue(
                                        item.getCategoryName(),
                                        normalizedCategory
                                )
                        )
                        .sorted(
                                inventoryComparator()
                        )
                        .toList();

        int pageNumber =
                Math.max(
                        pageable.getPageNumber(),
                        0
                );

        int pageSize =
                Math.max(
                        pageable.getPageSize(),
                        1
                );

        int startIndex =
                Math.min(
                        pageNumber * pageSize,
                        filteredItems.size()
                );

        int endIndex =
                Math.min(
                        startIndex + pageSize,
                        filteredItems.size()
                );

        List<StockItemDTO> pageContent =
                new ArrayList<>(
                        filteredItems.subList(
                                startIndex,
                                endIndex
                        )
                );

        return new PageImpl<>(
                pageContent,
                PageRequest.of(
                        pageNumber,
                        pageSize,
                        pageable.getSort()
                ),
                filteredItems.size()
        );
    }

    public StockSummaryDTO getSummary() {
        List<StockItemDTO> inventory =
                buildInventory();

        long totalStockUnits =
                inventory
                        .stream()
                        .mapToLong(
                                StockItemDTO::getCurrentStock
                        )
                        .sum();

        long lowStockProducts =
                inventory
                        .stream()
                        .filter(item ->
                                "LOW_STOCK".equals(
                                        item.getStockStatus()
                                )
                        )
                        .count();

        long outOfStockProducts =
                inventory
                        .stream()
                        .filter(item ->
                                "OUT_OF_STOCK".equals(
                                        item.getStockStatus()
                                )
                        )
                        .count();

        return new StockSummaryDTO(
                inventory.size(),
                totalStockUnits,
                lowStockProducts,
                outOfStockProducts
        );
    }

    public List<StockItemDTO> getAttentionItems(
            int limit
    ) {
        return buildInventory()
                .stream()
                .filter(item ->
                        item.getCurrentStock()
                                <= LOW_STOCK_THRESHOLD
                )
                .sorted(
                        Comparator
                                .comparingInt(
                                        StockItemDTO
                                                ::getCurrentStock
                                )
                                .thenComparing(
                                        StockItemDTO
                                                ::getProductName,
                                        String.CASE_INSENSITIVE_ORDER
                                )
                )
                .limit(
                        Math.max(limit, 0)
                )
                .toList();
    }

    public Set<String> getSupplierOptions() {
        return buildInventory()
                .stream()
                .map(
                        StockItemDTO::getSupplierName
                )
                .filter(
                        StringUtils::hasText
                )
                .filter(value ->
                        !"—".equals(value)
                )
                .sorted(
                        String.CASE_INSENSITIVE_ORDER
                )
                .collect(
                        Collectors.toCollection(
                                LinkedHashSet::new
                        )
                );
    }

    public Set<String> getCategoryOptions() {
        return buildInventory()
                .stream()
                .map(
                        StockItemDTO::getCategoryName
                )
                .filter(
                        StringUtils::hasText
                )
                .filter(value ->
                        !"—".equals(value)
                )
                .sorted(
                        String.CASE_INSENSITIVE_ORDER
                )
                .collect(
                        Collectors.toCollection(
                                LinkedHashSet::new
                        )
                );
    }

    /*
     * Dùng cho các service hoặc DTO cần lấy tồn kho Book.
     */
    public int getBookQuantity(
            Long bookId
    ) {
        if (bookId == null) {
            return 0;
        }

        return stockRepository
                .findByBook_Id(bookId)
                .map(
                        Stock::getQuantity
                )
                .orElse(0);
    }

    /*
     * Dùng cho các service hoặc DTO cần lấy tồn kho VPP.
     */
    public int getVppQuantity(
            Long vppItemId
    ) {
        if (vppItemId == null) {
            return 0;
        }

        return stockRepository
                .findByVppItem_Id(vppItemId)
                .map(
                        Stock::getQuantity
                )
                .orElse(0);
    }

    /*
     * =========================================================
     * TRANSACTION HISTORY
     * =========================================================
     */

    public Page<StockTransactionDTO> searchTransactions(
            String productType,
            Long productId,
            String movementType,
            String keyword,
            Pageable pageable
    ) {
        StockProductType parsedProductType =
                parseOptionalProductType(
                        productType
                );

        StockMovementType parsedMovementType =
                parseOptionalMovementType(
                        movementType
                );

        if (productId != null
                && parsedProductType == null) {

            throw new IllegalArgumentException(
                    "Product type is required when filtering by product."
            );
        }

        return stockTransactionRepository
                .search(
                        parsedProductType,
                        productId,
                        parsedMovementType,
                        normalize(keyword),
                        pageable
                )
                .map(
                        this::toTransactionDTO
                );
    }

    public List<StockTransactionDTO>
    getRecentTransactions() {

        return stockTransactionRepository
                .findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(
                        this::toTransactionDTO
                )
                .toList();
    }

    public long getTodayMovementCount() {
        LocalDate today =
                LocalDate.now();

        LocalDateTime start =
                today.atStartOfDay();

        LocalDateTime end =
                today
                        .plusDays(1)
                        .atStartOfDay();

        return stockTransactionRepository
                .countByCreatedAtBetween(
                        start,
                        end
                );
    }

    public long getAdjustmentCount() {
        return stockTransactionRepository
                .countByMovementType(
                        StockMovementType.ADJUSTMENT
                );
    }

    /*
     * =========================================================
     * INITIAL STOCK
     * =========================================================
     */

    @Transactional
    public void recordInitialBookStock(
            Book book
    ) {
        if (book == null
                || book.getId() == null) {

            throw new IllegalArgumentException(
                    "Saved book is required for initial stock."
            );
        }

        /*
         * Không tạo hai dòng stock cho cùng một Book.
         */
        if (stockRepository.existsByBook_Id(
                book.getId()
        )) {
            return;
        }

        int initialStock =
                Math.max(
                        book.getStockQuantity(),
                        0
                );

        Stock stock =
                Stock.forBook(
                        book,
                        initialStock
                );

        stockRepository.save(stock);

        /*
         * Đồng bộ tạm cột cũ.
         */
        syncLegacyQuantity(stock);

        saveTransaction(
                StockProductType.BOOK,
                book,
                null,
                StockMovementType.INITIAL_STOCK,
                initialStock,
                0,
                initialStock,
                "PRODUCT_CREATE",
                book.getId(),
                "BOOK-" + book.getId(),
                "Initial stock",
                null,
                null
        );
    }

    @Transactional
    public void recordInitialVppStock(
            VppItem item
    ) {
        if (item == null
                || item.getId() == null) {

            throw new IllegalArgumentException(
                    "Saved stationery product is required for initial stock."
            );
        }

        /*
         * Không tạo hai dòng stock cho cùng một VPP.
         */
        if (stockRepository.existsByVppItem_Id(
                item.getId()
        )) {
            return;
        }

        int initialStock =
                getSafeVppStock(item);

        Stock stock =
                Stock.forVppItem(
                        item,
                        initialStock
                );

        stockRepository.save(stock);

        /*
         * Đồng bộ tạm cột cũ.
         */
        syncLegacyQuantity(stock);

        saveTransaction(
                StockProductType.VPP,
                null,
                item,
                StockMovementType.INITIAL_STOCK,
                initialStock,
                0,
                initialStock,
                "PRODUCT_CREATE",
                item.getId(),
                "VPP-" + item.getId(),
                "Initial stock",
                null,
                null
        );
    }

    /*
     * Tạo dòng stock còn thiếu cho các Book và VPP đã tồn tại trước
     * khi bảng stock được thêm vào project.
     */
    @Transactional
    public int initializeMissingStockRows() {
        int createdRows = 0;

        for (Book book :
                bookRepository
                        .findAllByOrderByIdAsc()) {

            if (!stockRepository.existsByBook_Id(
                    book.getId()
            )) {
                Stock stock =
                        Stock.forBook(
                                book,
                                Math.max(
                                        book.getStockQuantity(),
                                        0
                                )
                        );

                stockRepository.save(stock);

                createdRows++;
            }
        }

        for (VppItem item :
                vppItemRepository.findAll()) {

            if (!stockRepository.existsByVppItem_Id(
                    item.getId()
            )) {
                Stock stock =
                        Stock.forVppItem(
                                item,
                                getSafeVppStock(item)
                        );

                stockRepository.save(stock);

                createdRows++;
            }
        }

        return createdRows;
    }

    /*
     * =========================================================
     * ADMIN STOCK IN
     * =========================================================
     */

    @Transactional
    public void stockIn(
            String productType,
            Long productId,
            int quantity,
            String referenceCode,
            String note,
            User performedBy
    ) {
        requirePositiveQuantity(quantity);

        StockProductType type =
                StockProductType.fromValue(
                        productType
                );

        Stock stock =
                lockStock(
                        type,
                        productId
                );

        int stockBefore =
                stock.getQuantity();

        stock.increase(quantity);

        stockRepository.save(stock);

        /*
         * Giữ cho các trang cũ chưa chuyển đổi vẫn đọc đúng tồn kho.
         */
        syncLegacyQuantity(stock);

        saveTransaction(
                type,
                stock.getBook(),
                stock.getVppItem(),
                StockMovementType.STOCK_IN,
                quantity,
                stockBefore,
                stock.getQuantity(),
                "GOODS_RECEIPT",
                null,
                referenceCode,
                "Stock received",
                note,
                performedBy
        );
    }

    /*
     * =========================================================
     * ADMIN STOCK OUT
     * =========================================================
     */

    @Transactional
    public void stockOut(
            String productType,
            Long productId,
            int quantity,
            String reason,
            String note,
            User performedBy
    ) {
        requirePositiveQuantity(quantity);

        requireReason(reason);

        StockProductType type =
                StockProductType.fromValue(
                        productType
                );

        Stock stock =
                lockStock(
                        type,
                        productId
                );

        int stockBefore =
                stock.getQuantity();

        ensureEnoughStock(
                stockBefore,
                quantity,
                stock.getProductName()
        );

        stock.decrease(quantity);

        stockRepository.save(stock);

        syncLegacyQuantity(stock);

        saveTransaction(
                type,
                stock.getBook(),
                stock.getVppItem(),
                StockMovementType.STOCK_OUT,
                -quantity,
                stockBefore,
                stock.getQuantity(),
                "MANUAL_STOCK_OUT",
                null,
                null,
                reason,
                note,
                performedBy
        );
    }

    /*
     * =========================================================
     * ADMIN STOCK ADJUSTMENT
     * =========================================================
     */

    @Transactional
    public void adjustStock(
            String productType,
            Long productId,
            int actualStock,
            String reason,
            String note,
            User performedBy
    ) {
        if (actualStock < 0) {
            throw new IllegalArgumentException(
                    "Actual stock must be greater than or equal to 0."
            );
        }

        requireReason(reason);

        StockProductType type =
                StockProductType.fromValue(
                        productType
                );

        Stock stock =
                lockStock(
                        type,
                        productId
                );

        int stockBefore =
                stock.getQuantity();

        int quantityChange =
                actualStock - stockBefore;

        ensureStockChanged(
                quantityChange
        );

        stock.adjust(actualStock);

        stockRepository.save(stock);

        syncLegacyQuantity(stock);

        saveTransaction(
                type,
                stock.getBook(),
                stock.getVppItem(),
                StockMovementType.ADJUSTMENT,
                quantityChange,
                stockBefore,
                stock.getQuantity(),
                "STOCKTAKE",
                null,
                null,
                reason,
                note,
                performedBy
        );
    }

    /*
     * =========================================================
     * ORDER SALE
     * =========================================================
     */

    @Transactional
    public void decreaseBookForSale(
            Long bookId,
            int quantity,
            Long orderId,
            String orderCode
    ) {
        requirePositiveQuantity(quantity);

        Stock stock =
                lockStock(
                        StockProductType.BOOK,
                        bookId
                );

        int stockBefore =
                stock.getQuantity();

        ensureEnoughStock(
                stockBefore,
                quantity,
                stock.getProductName()
        );

        stock.decrease(quantity);

        stockRepository.save(stock);

        syncLegacyQuantity(stock);

        saveTransaction(
                StockProductType.BOOK,
                stock.getBook(),
                null,
                StockMovementType.SALE,
                -quantity,
                stockBefore,
                stock.getQuantity(),
                "ORDER",
                orderId,
                orderCode,
                "Customer order",
                null,
                null
        );
    }

    @Transactional
    public void decreaseVppForSale(
            Long itemId,
            int quantity,
            Long orderId,
            String orderCode
    ) {
        requirePositiveQuantity(quantity);

        Stock stock =
                lockStock(
                        StockProductType.VPP,
                        itemId
                );

        int stockBefore =
                stock.getQuantity();

        ensureEnoughStock(
                stockBefore,
                quantity,
                stock.getProductName()
        );

        stock.decrease(quantity);

        stockRepository.save(stock);

        syncLegacyQuantity(stock);

        saveTransaction(
                StockProductType.VPP,
                null,
                stock.getVppItem(),
                StockMovementType.SALE,
                -quantity,
                stockBefore,
                stock.getQuantity(),
                "ORDER",
                orderId,
                orderCode,
                "Customer order",
                null,
                null
        );
    }

    /*
     * =========================================================
     * CANCELLED ORDER STOCK RESTORE
     * =========================================================
     */

    @Transactional
    public void restoreBookForCancelledOrder(
            Long bookId,
            int quantity,
            Long orderId,
            String orderCode
    ) {
        requirePositiveQuantity(quantity);

        Stock stock =
                lockStock(
                        StockProductType.BOOK,
                        bookId
                );

        int stockBefore =
                stock.getQuantity();

        stock.increase(quantity);

        stockRepository.save(stock);

        syncLegacyQuantity(stock);

        saveTransaction(
                StockProductType.BOOK,
                stock.getBook(),
                null,
                StockMovementType.ORDER_CANCELLED,
                quantity,
                stockBefore,
                stock.getQuantity(),
                "ORDER",
                orderId,
                orderCode,
                "Order cancelled",
                null,
                null
        );
    }

    @Transactional
    public void restoreVppForCancelledOrder(
            Long itemId,
            int quantity,
            Long orderId,
            String orderCode
    ) {
        requirePositiveQuantity(quantity);

        Stock stock =
                lockStock(
                        StockProductType.VPP,
                        itemId
                );

        int stockBefore =
                stock.getQuantity();

        stock.increase(quantity);

        stockRepository.save(stock);

        syncLegacyQuantity(stock);

        saveTransaction(
                StockProductType.VPP,
                null,
                stock.getVppItem(),
                StockMovementType.ORDER_CANCELLED,
                quantity,
                stockBefore,
                stock.getQuantity(),
                "ORDER",
                orderId,
                orderCode,
                "Order cancelled",
                null,
                null
        );
    }

    /*
     * =========================================================
     * INVENTORY BUILDING
     * =========================================================
     */

    private List<StockItemDTO> buildInventory() {
        List<StockItemDTO> inventory =
                new ArrayList<>();

        List<Stock> stocks =
                stockRepository
                        .findAllWithProductsOrderByIdAsc();

        for (Stock stock : stocks) {

            if (stock.isBookStock()) {
                inventory.add(
                        toStockItemDTO(stock)
                );

                continue;
            }

            if (stock.isVppStock()
                    && !stock
                    .getVppItem()
                    .isDeleted()) {

                inventory.add(
                        toStockItemDTO(stock)
                );
            }
        }

        return inventory;
    }

    private StockItemDTO toStockItemDTO(
            Stock stock
    ) {
        if (stock.isBookStock()) {
            return toBookStockItemDTO(
                    stock
            );
        }

        if (stock.isVppStock()) {
            return toVppStockItemDTO(
                    stock
            );
        }

        throw new IllegalStateException(
                "Stock record is not linked to a product."
        );
    }

    /*
     * =========================================================
     * BOOK STOCK DTO
     * =========================================================
     */

    private StockItemDTO toBookStockItemDTO(
            Stock stock
    ) {
        Book book =
                stock.getBook();

        StockItemDTO dto =
                new StockItemDTO();

        dto.setStockId(
                stock.getId()
        );

        dto.setProductId(
                book.getId()
        );

        dto.setProductType(
                StockProductType.BOOK
        );

        dto.setProductName(
                book.getTitle()
        );

        if (StringUtils.hasText(
                book.getIsbn()
        )) {
            dto.setCode(
                    "ISBN " + book.getIsbn()
            );

        } else {
            dto.setCode(
                    "BOOK-" + book.getId()
            );
        }

        if (book.getCategory() == null) {
            dto.setCategoryName("—");

        } else {
            dto.setCategoryName(
                    book
                            .getCategory()
                            .getName()
            );
        }

        if (book.getSupplier() == null) {
            dto.setSupplierName("—");

        } else {
            dto.setSupplierName(
                    book
                            .getSupplier()
                            .getSupplierName()
            );
        }

        dto.setImageUrl(
                book.getImageUrl()
        );

        /*
         * Lấy số lượng từ bảng stock.
         */
        dto.setCurrentStock(
                stock.getQuantity()
        );

        dto.setUpdatedAt(
                book.getUpdatedAt()
        );

        applyStockStatus(dto);

        return dto;
    }

    /*
     * =========================================================
     * VPP STOCK DTO
     * =========================================================
     */

    private StockItemDTO toVppStockItemDTO(
            Stock stock
    ) {
        VppItem item =
                stock.getVppItem();

        StockItemDTO dto =
                new StockItemDTO();

        dto.setStockId(
                stock.getId()
        );

        dto.setProductId(
                item.getId()
        );

        dto.setProductType(
                StockProductType.VPP
        );

        dto.setProductName(
                item.getName()
        );

        dto.setCode(
                "VPP-" + item.getId()
        );

        if (item.getCategory() == null) {
            dto.setCategoryName("—");

        } else {
            dto.setCategoryName(
                    item
                            .getCategory()
                            .getName()
            );
        }

        if (StringUtils.hasText(
                item.getSupplier()
        )) {
            dto.setSupplierName(
                    item.getSupplier()
            );

        } else {
            dto.setSupplierName("—");
        }

        /*
         * Đường dẫn đúng của ảnh VPP.
         */
        if (item.hasImageData()) {
            dto.setImageUrl(
                    "/uploads/vpp/"
                            + item.getId()
                            + "/image"
            );

        } else {
            dto.setImageUrl(
                    item.getImagePath()
            );
        }

        /*
         * Lấy số lượng từ bảng stock.
         */
        dto.setCurrentStock(
                stock.getQuantity()
        );

        dto.setUpdatedAt(
                item.getUpdatedAt()
        );

        applyStockStatus(dto);

        return dto;
    }

    /*
     * =========================================================
     * STOCK STATUS
     * =========================================================
     */

    private void applyStockStatus(
            StockItemDTO dto
    ) {
        int stock =
                dto.getCurrentStock();

        if (stock <= 0) {
            dto.setStockStatus(
                    "OUT_OF_STOCK"
            );

            dto.setStockStatusLabel(
                    "Out of Stock"
            );

            dto.setStockStatusCssClass(
                    "out"
            );

            return;
        }

        if (stock <= LOW_STOCK_THRESHOLD) {
            dto.setStockStatus(
                    "LOW_STOCK"
            );

            dto.setStockStatusLabel(
                    "Low Stock"
            );

            dto.setStockStatusCssClass(
                    "low"
            );

            return;
        }

        dto.setStockStatus(
                "IN_STOCK"
        );

        dto.setStockStatusLabel(
                "In Stock"
        );

        dto.setStockStatusCssClass(
                "in"
        );
    }

    /*
     * =========================================================
     * TRANSACTION DTO MAPPING
     * =========================================================
     */

    private StockTransactionDTO toTransactionDTO(
            StockTransaction transaction
    ) {
        StockTransactionDTO dto =
                new StockTransactionDTO();

        dto.setId(
                transaction.getId()
        );

        dto.setProductType(
                transaction.getProductType()
        );

        dto.setMovementType(
                transaction.getMovementType()
        );

        dto.setQuantityChange(
                transaction.getQuantityChange()
        );

        dto.setStockBefore(
                transaction.getStockBefore()
        );

        dto.setStockAfter(
                transaction.getStockAfter()
        );

        dto.setReferenceCode(
                transaction.getReferenceCode()
        );

        dto.setReason(
                transaction.getReason()
        );

        dto.setNote(
                transaction.getNote()
        );

        dto.setPerformedByName(
                transaction.getPerformedByName()
        );

        dto.setCreatedAt(
                transaction.getCreatedAt()
        );

        if (transaction.getProductType()
                == StockProductType.BOOK
                && transaction.getBook() != null) {

            Book book =
                    transaction.getBook();

            dto.setProductId(
                    book.getId()
            );

            dto.setProductName(
                    book.getTitle()
            );

            dto.setImageUrl(
                    book.getImageUrl()
            );

            return dto;
        }

        if (transaction.getVppItem() != null) {
            VppItem item =
                    transaction.getVppItem();

            dto.setProductId(
                    item.getId()
            );

            dto.setProductName(
                    item.getName()
            );

            if (item.hasImageData()) {
                dto.setImageUrl(
                        "/uploads/vpp/"
                                + item.getId()
                                + "/image"
                );

            } else {
                dto.setImageUrl(
                        item.getImagePath()
                );
            }
        }

        return dto;
    }

    /*
     * =========================================================
     * SAVE STOCK TRANSACTION
     * =========================================================
     */

    private void saveTransaction(
            StockProductType productType,
            Book book,
            VppItem vppItem,
            StockMovementType movementType,
            int quantityChange,
            int stockBefore,
            int stockAfter,
            String referenceType,
            Long referenceId,
            String referenceCode,
            String reason,
            String note,
            User performedBy
    ) {
        StockTransaction transaction =
                new StockTransaction();

        transaction.setProductType(
                productType
        );

        transaction.setBook(
                book
        );

        transaction.setVppItem(
                vppItem
        );

        transaction.setMovementType(
                movementType
        );

        transaction.setQuantityChange(
                quantityChange
        );

        transaction.setStockBefore(
                stockBefore
        );

        transaction.setStockAfter(
                stockAfter
        );

        transaction.setReferenceType(
                clean(referenceType)
        );

        transaction.setReferenceId(
                referenceId
        );

        transaction.setReferenceCode(
                clean(referenceCode)
        );

        transaction.setReason(
                clean(reason)
        );

        transaction.setNote(
                clean(note)
        );

        transaction.setPerformedBy(
                performedBy
        );

        if (performedBy == null
                || !StringUtils.hasText(
                performedBy.getFullName()
        )) {
            transaction.setPerformedByName(
                    "System"
            );

        } else {
            transaction.setPerformedByName(
                    performedBy.getFullName()
            );
        }

        stockTransactionRepository.save(
                transaction
        );
    }

    /*
     * =========================================================
     * LOCK STOCK RECORD
     * =========================================================
     */

    private Stock lockStock(
            StockProductType type,
            Long productId
    ) {
        if (productId == null) {
            throw new IllegalArgumentException(
                    "Product ID is required."
            );
        }

        if (type == StockProductType.BOOK) {
            return stockRepository
                    .findByBookIdForUpdate(
                            productId
                    )
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Stock record for this book was not found."
                            )
                    );
        }

        return stockRepository
                .findByVppItemIdForUpdate(
                        productId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Stock record for this stationery product was not found."
                        )
                );
    }

    /*
     * =========================================================
     * TEMPORARY LEGACY SYNCHRONIZATION
     * =========================================================
     *
     * Bảng stock là nguồn chính.
     *
     * Nhưng Cart, customer pages và các form cũ có thể vẫn còn đọc:
     *
     * books.stock_quantity
     * vpp_items.stock_quantity
     *
     * Vì vậy tạm đồng bộ hai cột cũ để project chưa bị lỗi.
     *
     * Khi toàn bộ module đã đổi sang stock.quantity thì mới:
     *
     * 1. Xóa hàm này.
     * 2. Xóa các lệnh gọi syncLegacyQuantity().
     * 3. Xóa hai cột stock_quantity cũ trong database.
     */

    private void syncLegacyQuantity(
            Stock stock
    ) {
        if (stock.isBookStock()) {
            Book book =
                    stock.getBook();

            book.setStockQuantity(
                    stock.getQuantity()
            );

            bookRepository.save(book);

            return;
        }

        if (stock.isVppStock()) {
            VppItem item =
                    stock.getVppItem();

            item.setStockQuantity(
                    stock.getQuantity()
            );

            vppItemRepository.save(item);
        }
    }

    /*
     * =========================================================
     * FILTER HELPERS
     * =========================================================
     */

    private Comparator<StockItemDTO>
    inventoryComparator() {

        return Comparator
                .comparingInt(
                        (StockItemDTO item) ->
                                stockPriority(
                                        item.getStockStatus()
                                )
                )
                .thenComparingInt(
                        StockItemDTO::getCurrentStock
                )
                .thenComparing(
                        StockItemDTO::getProductName,
                        String.CASE_INSENSITIVE_ORDER
                );
    }

    private int stockPriority(
            String status
    ) {
        if ("OUT_OF_STOCK".equals(status)) {
            return 0;
        }

        if ("LOW_STOCK".equals(status)) {
            return 1;
        }

        return 2;
    }

    private boolean matchesKeyword(
            StockItemDTO item,
            String keyword
    ) {
        if (!StringUtils.hasText(keyword)) {
            return true;
        }

        String lowercaseKeyword =
                keyword.toLowerCase(
                        Locale.ROOT
                );

        return containsIgnoreCase(
                item.getProductName(),
                lowercaseKeyword
        )
                || containsIgnoreCase(
                item.getCode(),
                lowercaseKeyword
        )
                || containsIgnoreCase(
                item.getCategoryName(),
                lowercaseKeyword
        )
                || containsIgnoreCase(
                item.getSupplierName(),
                lowercaseKeyword
        );
    }

    private boolean matchesProductType(
            StockItemDTO item,
            String productType
    ) {
        if (!StringUtils.hasText(productType)
                || "ALL".equalsIgnoreCase(
                productType
        )) {
            return true;
        }

        return item
                .getProductType()
                .name()
                .equalsIgnoreCase(
                        productType
                );
    }

    private boolean matchesStockStatus(
            StockItemDTO item,
            String stockStatus
    ) {
        if (!StringUtils.hasText(stockStatus)
                || "ALL".equalsIgnoreCase(
                stockStatus
        )) {
            return true;
        }

        return item
                .getStockStatus()
                .equalsIgnoreCase(
                        stockStatus
                );
    }

    private boolean matchesExactValue(
            String actualValue,
            String selectedValue
    ) {
        if (!StringUtils.hasText(selectedValue)
                || "ALL".equalsIgnoreCase(
                selectedValue
        )) {
            return true;
        }

        return actualValue != null
                && actualValue.equalsIgnoreCase(
                        selectedValue
                );
    }

    private boolean containsIgnoreCase(
            String actualValue,
            String lowercaseKeyword
    ) {
        return actualValue != null
                && actualValue
                .toLowerCase(
                        Locale.ROOT
                )
                .contains(
                        lowercaseKeyword
                );
    }

    /*
     * =========================================================
     * VALIDATION AND NORMALIZATION
     * =========================================================
     */

    private int getSafeVppStock(
            VppItem item
    ) {
        if (item.getStockQuantity() == null) {
            return 0;
        }

        return Math.max(
                item.getStockQuantity(),
                0
        );
    }

    private void requirePositiveQuantity(
            int quantity
    ) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than 0."
            );
        }
    }

    private void requireReason(
            String reason
    ) {
        if (!StringUtils.hasText(reason)) {
            throw new IllegalArgumentException(
                    "Reason is required."
            );
        }
    }

    private void ensureEnoughStock(
            int availableStock,
            int requestedQuantity,
            String productName
    ) {
        if (availableStock < requestedQuantity) {
            throw new IllegalArgumentException(
                    "Insufficient stock for "
                            + productName
                            + ". Available quantity: "
                            + availableStock
                            + "."
            );
        }
    }

    private void ensureStockChanged(
            int quantityChange
    ) {
        if (quantityChange == 0) {
            throw new IllegalArgumentException(
                    "Actual stock is unchanged."
            );
        }
    }

    private StockProductType
    parseOptionalProductType(
            String value
    ) {
        if (!StringUtils.hasText(value)
                || "ALL".equalsIgnoreCase(value)) {

            return null;
        }

        return StockProductType.fromValue(
                value
        );
    }

    private StockMovementType
    parseOptionalMovementType(
            String value
    ) {
        if (!StringUtils.hasText(value)
                || "ALL".equalsIgnoreCase(value)) {

            return null;
        }

        return StockMovementType.fromValue(
                value
        );
    }

    private String normalize(
            String value
    ) {
        return StringUtils.hasText(value)
                ? value.trim()
                : null;
    }

    private String normalizeUpper(
            String value
    ) {
        return StringUtils.hasText(value)
                ? value
                .trim()
                .toUpperCase(Locale.ROOT)
                : null;
    }

    private String clean(
            String value
    ) {
        return StringUtils.hasText(value)
                ? value.trim()
                : null;
    }
}