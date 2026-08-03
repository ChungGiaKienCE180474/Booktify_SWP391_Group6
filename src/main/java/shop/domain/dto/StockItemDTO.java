package shop.domain.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import shop.domain.StockProductType;

public class StockItemDTO {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    /*
     * ID của dòng trong bảng stock.
     */
    private Long stockId;

    /*
     * ID sản phẩm:
     * - books.id nếu là Book
     * - vpp_items.id nếu là VPP
     */
    private Long productId;

    /*
     * BOOK hoặc VPP.
     */
    private StockProductType productType;

    /*
     * Tên sách hoặc tên văn phòng phẩm.
     */
    private String productName;

    /*
     * Mã sản phẩm.
     */
    private String code;

    /*
     * Đường dẫn hình ảnh sản phẩm.
     */
    private String imageUrl;

    /*
     * Tên danh mục.
     */
    private String categoryName;

    /*
     * Tên nhà cung cấp.
     */
    private String supplierName;

    /*
     * Số lượng lấy từ stock.quantity.
     */
    private int currentStock;

    /*
     * IN_STOCK, LOW_STOCK hoặc OUT_OF_STOCK.
     */
    private String stockStatus;

    /*
     * Nhãn hiển thị:
     * In Stock, Low Stock hoặc Out of Stock.
     */
    private String stockStatusLabel;

    /*
     * CSS class của trạng thái tồn kho.
     */
    private String stockStatusCssClass;

    /*
     * Đây không phải stock.updated_at.
     *
     * Giá trị này có thể lấy từ:
     * - Book.updatedAt
     * - VppItem.updatedAt
     *
     * Dùng cho cột Last Updated trên giao diện.
     */
    private LocalDateTime updatedAt;

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public StockProductType getProductType() {
        return productType;
    }

    public void setProductType(
            StockProductType productType
    ) {
        this.productType = productType;
    }

    public String getProductTypeLabel() {
        return productType == null
                ? ""
                : productType.getLabel();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(
            String productName
    ) {
        this.productName = productName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(
            String code
    ) {
        this.code = code;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(
            String imageUrl
    ) {
        this.imageUrl = imageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(
            String categoryName
    ) {
        this.categoryName = categoryName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(
            String supplierName
    ) {
        this.supplierName = supplierName;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(
            int currentStock
    ) {
        this.currentStock = currentStock;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(
            String stockStatus
    ) {
        this.stockStatus = stockStatus;
    }

    public String getStockStatusLabel() {
        return stockStatusLabel;
    }

    public void setStockStatusLabel(
            String stockStatusLabel
    ) {
        this.stockStatusLabel = stockStatusLabel;
    }

    public String getStockStatusCssClass() {
        return stockStatusCssClass;
    }

    public void setStockStatusCssClass(
            String stockStatusCssClass
    ) {
        this.stockStatusCssClass =
                stockStatusCssClass;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt
    ) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAtFormatted() {
        if (updatedAt == null) {
            return "—";
        }

        return updatedAt.format(
                DATE_TIME_FORMATTER
        );
    }
}