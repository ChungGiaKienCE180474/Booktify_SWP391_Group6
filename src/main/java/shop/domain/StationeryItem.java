package shop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity ánh xạ tới bảng stationery_items trong PostgreSQL.
 *
 * Lưu ý thiết kế:
 * - Validation (NotBlank, DecimalMin...) được đặt ở StationeryItemDTO, KHÔNG đặt ở đây.
 *   Entity chỉ chứa ràng buộc cơ sở dữ liệu (@Column nullable/unique).
 * - Xóa dùng cơ chế "soft delete": trường deleted=true thay vì xóa hàng khỏi DB,
 *   giúp bảo toàn lịch sử khi sản phẩm đã được đặt hàng trước đó.
 */
@Entity
@Table(name = "stationery_items")
public class StationeryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tên mặt hàng, duy nhất trong DB (unique = true)
    @Column(unique = true, nullable = false, length = 200)
    private String name;

    // Tên danh mục lưu dạng String (khóa ngoại mềm tới bảng stationery_categories)
    @Column(nullable = false, length = 100)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Kiểu BigDecimal để tránh lỗi làm tròn số thập phân khi tính tiền
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(length = 200)
    private String supplier;

    // Trạng thái hoạt động (liên quan Order Management), mặc định ACTIVE
    @Column(length = 20)
    private String status = "ACTIVE";

    // Đường dẫn web tới ảnh sản phẩm, ví dụ: /images/stationery/uuid_ten.jpg
    @Column(length = 500)
    private String imagePath;

    /**
     * Cờ soft delete: true = đã xóa (ẩn khỏi giao diện), false = đang hoạt động.
     * columnDefinition đảm bảo khi ALTER TABLE thêm cột vào DB cũ, các hàng sẵn có
     * nhận giá trị FALSE thay vì NULL (tránh lỗi NOT NULL constraint).
     */
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

    // updatable = false: createdAt chỉ ghi một lần khi INSERT, không bao giờ UPDATE
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ── Lifecycle hooks ──────────────────────────────────────────────────────

    /** Tự động gán thời gian khi entity được lưu lần đầu. */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt  = LocalDateTime.now();
    }

    /** Tự động cập nhật updatedAt mỗi khi entity bị chỉnh sửa. */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
