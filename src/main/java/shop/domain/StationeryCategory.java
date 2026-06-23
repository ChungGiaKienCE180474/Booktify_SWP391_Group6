package shop.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity ánh xạ tới bảng stationery_categories.
 *
 * Danh mục được quản lý riêng (CRUD độc lập) thay vì dùng Enum cứng,
 * giúp Admin thêm/sửa/xóa danh mục mà không cần deploy lại ứng dụng.
 * Validation đặt ở StationeryCategoryDTO, không đặt ở đây.
 */
@Entity
@Table(name = "stationery_categories")
public class StationeryCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tên danh mục phải duy nhất trong toàn hệ thống
    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // updatable = false: chỉ ghi khi tạo, không bao giờ bị ghi đè khi sửa
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Tự động gán thời gian tạo khi INSERT lần đầu. */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
