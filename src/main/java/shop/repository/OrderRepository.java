package shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.domain.Order;

/**
 * Repository truy vấn bảng {@code orders} và quan hệ {@code order_items}.
 * <p>
 * Các query dùng JOIN FETCH để tránh N+1 khi load items, book, vppItem, user.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /** Kiểm tra mã đơn unique khi sinh orderCode trong OrderService. */
    boolean existsByOrderCode(String orderCode);

    Optional<Order> findByOrderCode(String orderCode);

    /** Load đơn kèm items theo user — dùng cho trang lịch sử (có items). */
    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdWithItemsOrderByCreatedAtDesc(@Param("userId") long userId);

    /**
     * Chi tiết đơn thuộc user cụ thể — OrderService.getOrderForUser().
     * JOIN FETCH book/vppItem để hiển thị ảnh sản phẩm trên trang detail.
     */
    @Query("""
            SELECT o FROM Order o
            JOIN FETCH o.items i
            LEFT JOIN FETCH i.book
            LEFT JOIN FETCH i.vppItem
            WHERE o.id = :id AND o.user.id = :userId
            """)
    Optional<Order> findByIdAndUserIdWithItems(@Param("id") Long id, @Param("userId") long userId);

    /** Tất cả đơn kèm user + items — Admin/Staff searchOrders(). */
    @Query("SELECT o FROM Order o JOIN FETCH o.user JOIN FETCH o.items ORDER BY o.createdAt DESC")
    List<Order> findAllWithUserAndItemsOrderByCreatedAtDesc();

    /** Chi tiết đơn theo id (không lọc user) — Admin/Staff getOrderById(). */
    @Query("""
            SELECT o FROM Order o
            JOIN FETCH o.user
            JOIN FETCH o.items i
            LEFT JOIN FETCH i.book
            LEFT JOIN FETCH i.vppItem
            WHERE o.id = :id
            """)
    Optional<Order> findByIdWithUserAndItems(@Param("id") Long id);

    /** Danh sách đơn user (không fetch items) — getOrdersForUser() summary list. */
    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") long userId);

    long countByUserId(long userId);

    /** Kiểm tra user đã mua sách với status cụ thể — dùng cho Rating (đánh giá sau mua). */
    boolean existsByUser_IdAndItems_Book_IdAndStatus(
            Long userId,
            Long bookId,
            String status);

    /** Kiểm tra user đã mua VPP với status cụ thể. */
    boolean existsByUser_IdAndItems_VppItem_IdAndStatus(
            Long userId,
            Long vppItemId,
            String status);
}
