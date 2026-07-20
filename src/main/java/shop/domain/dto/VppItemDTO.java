package shop.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VppItemDTO {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Long id;

    @NotBlank(message = "Product name is required.")
    @Size(max = 200, message = "Product name must be less than 200 characters.")
    private String name;

    private Long categoryId;

    @NotBlank(message = "Category name is required.")
    @Size(max = 120, message = "Category name must be less than 120 characters.")
    private String categoryName;

    private String description;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0.")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required.")
    @Min(value = 0, message = "Stock quantity must be greater than or equal to 0.")
    private Integer stockQuantity;

    private String supplier;

    private String status = "ACTIVE";

    private String imagePath;

    private boolean hasImage;

    private boolean deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }

    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    public String getCreatedAtFormatted() {
        return createdAt == null ? "—" : createdAt.format(DATE_TIME_FORMATTER);
    }

    public String getUpdatedAtFormatted() {
        return updatedAt == null ? "—" : updatedAt.format(DATE_TIME_FORMATTER);
    }

    public Long getId() {
        return id;
    }

    public VppItemDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public VppItemDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public VppItemDTO setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public VppItemDTO setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VppItemDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public VppItemDTO setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public VppItemDTO setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
        return this;
    }

    public String getSupplier() {
        return supplier;
    }

    public VppItemDTO setSupplier(String supplier) {
        this.supplier = supplier;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public VppItemDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public VppItemDTO setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public VppItemDTO setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
        return this;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public VppItemDTO setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public VppItemDTO setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public VppItemDTO setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}