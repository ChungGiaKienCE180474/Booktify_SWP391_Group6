package shop.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) cho mặt hàng văn phòng phẩm.
 *
 * Đóng vai trò trung gian giữa form HTML và entity StationeryItem:
 *   - Controller nhận dữ liệu form → bind vào DTO này (@ModelAttribute)
 *   - Service chuyển DTO → Entity trước khi lưu DB  (itemDTOtoItem)
 *   - Service chuyển Entity → DTO trước khi trả về View  (itemToItemDTO)
 *
 * Toàn bộ quy tắc validate đặt ở đây, KHÔNG đặt ở Entity,
 * để tách biệt ràng buộc nghiệp vụ (form) khỏi ràng buộc DB.
 */
public class StationeryItemDTO {

    // Dùng khi hiển thị (GET edit/detail), không có trong form tạo mới
    private Long id;

    @NotBlank(message = "Item name is required")
    @Size(max = 200, message = "Item name must not exceed 200 characters")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    // @DecimalMin kiểm tra giá trị tối thiểu cho kiểu BigDecimal
    @NotNull(message = "Price is required")
    @DecimalMin(value = "1000", inclusive = true, message = "Price must be at least 1,000")
    private BigDecimal price;

    // Đây là số lượng NHẬP KHO mỗi lần, phải >= 1
    @NotNull(message = "Stock quantity is required")
    @Min(value = 1, message = "Stock quantity must be greater than 0")
    private Integer stockQuantity;

    private String supplier;

    // Đường dẫn ảnh được gán bởi controller sau khi lưu file, không từ form
    private String imagePath;

    // Hai trường chỉ hiển thị (detail.jsp), không validate, được sao chép từ entity
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
