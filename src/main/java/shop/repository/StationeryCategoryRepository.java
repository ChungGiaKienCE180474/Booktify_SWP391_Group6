package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.domain.StationeryCategory;

/**
 * Repository truy cập bảng stationery_categories.
 * Danh mục không dùng soft delete (xóa thật), nhưng bị chặn nếu còn sản phẩm đang dùng.
 */
@Repository
public interface StationeryCategoryRepository extends JpaRepository<StationeryCategory, Long> {

    /** Kiểm tra tên danh mục đã tồn tại chưa (dùng khi TẠO MỚI). */
    boolean existsByName(String name);

    /**
     * Kiểm tra tên đã tồn tại chưa, bỏ qua chính danh mục đang sửa (dùng khi CẬP NHẬT).
     * Nếu không loại trừ id hiện tại, form sửa sẽ báo lỗi khi giữ nguyên tên.
     */
    boolean existsByNameAndIdNot(String name, Long id);
}
