package shop.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO cho danh mục văn phòng phẩm.
 *
 * Dùng trong form tạo mới / chỉnh sửa danh mục (create.jsp, edit.jsp).
 * Sau khi validate, Service chuyển DTO → StationeryCategory entity để lưu DB.
 * Khi hiển thị danh sách (category/list.jsp), Service chuyển entity → DTO để trả về View.
 */
public class StationeryCategoryDTO {

    // Cần thiết cho form action trong edit.jsp: /stationery/categories/${stationeryCategory.id}/edit
    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    private String description;

    // Chỉ hiển thị trong category/list.jsp, không có trong form
    private LocalDateTime createdAt;

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
