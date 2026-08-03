package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shop.domain.StationeryItem;

import java.util.List;
import java.util.Optional;

/**
 * Repository truy cập bảng stationery_items.
 *
 * Spring Data JPA tự sinh SQL từ tên phương thức (derived query).
 * Tất cả truy vấn đều lọc deleted=false để hỗ trợ soft delete:
 * bản ghi bị "xóa" vẫn còn trong DB nhưng không được trả về.
 */
@Repository
public interface StationeryRepository extends JpaRepository<StationeryItem, Long> {

    /**
     * Kiểm tra tên đã tồn tại chưa (dùng khi TẠO MỚI).
     * Chỉ kiểm tra các bản ghi chưa bị xóa mềm.
     */
    boolean existsByNameAndDeletedFalse(String name);

    /**
     * Kiểm tra tên đã tồn tại chưa, bỏ qua chính bản ghi đang sửa (dùng khi CẬP NHẬT).
     * Nếu không loại trừ id hiện tại, form sửa sẽ báo lỗi khi giữ nguyên tên.
     */
    boolean existsByNameAndIdNotAndDeletedFalse(String name, Long id);

    /**
     * Đếm số mặt hàng đang dùng một danh mục, dùng để chặn xóa danh mục còn sản phẩm.
     */
    long countByCategoryAndDeletedFalse(String category);

    /**
     * Tìm kiếm mặt hàng theo từ khóa và/hoặc danh mục.
     *
     * Dùng native query PostgreSQL vì JPQL không hỗ trợ ::text cast.
     * Khi keyword hoặc category là NULL, điều kiện tương ứng bị bỏ qua (tìm tất cả).
     * name::text: ép kiểu sang TEXT để dùng LIKE với PostgreSQL varchar.
     */
    @Query(value = "SELECT * FROM stationery_items s WHERE s.deleted = false AND " +
           "(:keyword IS NULL OR LOWER(s.name::text) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR s.category = :category) " +
           "ORDER BY s.created_at DESC", nativeQuery = true)
    List<StationeryItem> search(@Param("keyword") String keyword,
                                @Param("category") String category);

    /**
     * Lấy danh sách tên danh mục đang được dùng (từ cột category trong stationery_items).
     * Dùng để populate dropdown lọc trên list.jsp, chỉ hiển thị danh mục có hàng thật.
     */
    @Query("SELECT DISTINCT s.category FROM StationeryItem s WHERE s.deleted = false ORDER BY s.category")
    List<String> findAllCategories();

    /**
     * Tìm mặt hàng theo id và chưa bị xóa mềm.
     * Trả về Optional.empty() nếu id không tồn tại hoặc đã bị soft-delete.
     */
    Optional<StationeryItem> findByIdAndDeletedFalse(Long id);
}
