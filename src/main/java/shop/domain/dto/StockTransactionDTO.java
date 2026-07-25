package shop.domain.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import shop.domain.StockMovementType;
import shop.domain.StockProductType;

public class StockTransactionDTO {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private Long id;
    private Long productId;
    private StockProductType productType;
    private String productName;
    private String imageUrl;
    private StockMovementType movementType;
    private int quantityChange;
    private int stockBefore;
    private int stockAfter;
    private String referenceCode;
    private String reason;
    private String note;
    private String performedByName;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(
            Long productId
    ) {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(
            String imageUrl
    ) {
        this.imageUrl = imageUrl;
    }

    public StockMovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(
            StockMovementType movementType
    ) {
        this.movementType = movementType;
    }

    public String getMovementLabel() {
        return movementType == null
                ? ""
                : movementType.getLabel();
    }

    public String getMovementCssClass() {
        return movementType == null
                ? ""
                : movementType.getCssClass();
    }

    public int getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(
            int quantityChange
    ) {
        this.quantityChange = quantityChange;
    }

    public String getQuantityChangeFormatted() {
        return quantityChange > 0
                ? "+" + quantityChange
                : String.valueOf(quantityChange);
    }

    public int getStockBefore() {
        return stockBefore;
    }

    public void setStockBefore(
            int stockBefore
    ) {
        this.stockBefore = stockBefore;
    }

    public int getStockAfter() {
        return stockAfter;
    }

    public void setStockAfter(
            int stockAfter
    ) {
        this.stockAfter = stockAfter;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(
            String referenceCode
    ) {
        this.referenceCode = referenceCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(
            String reason
    ) {
        this.reason = reason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(
            String note
    ) {
        this.note = note;
    }

    public String getPerformedByName() {
        return performedByName;
    }

    public void setPerformedByName(
            String performedByName
    ) {
        this.performedByName = performedByName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }

    public String getCreatedAtFormatted() {
        return createdAt == null
                ? "—"
                : createdAt.format(DATE_TIME_FORMATTER);
    }
}