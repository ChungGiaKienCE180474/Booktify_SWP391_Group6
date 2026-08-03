package shop.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import shop.domain.Stock;

@Repository
public interface StockRepository
        extends JpaRepository<Stock, Long> {

    /*
     * ============================================================
     * FIND STOCK BY PRODUCT
     * ============================================================
     */

    /*
     * Tìm dòng stock của một Book.
     *
     * Ví dụ:
     * bookId = 5
     * → tìm dòng stock có book_id = 5.
     */
    Optional<Stock> findByBook_Id(
            Long bookId
    );

    /*
     * Tìm dòng stock của một VPP item.
     *
     * Ví dụ:
     * vppItemId = 8
     * → tìm dòng stock có vpp_item_id = 8.
     */
    Optional<Stock> findByVppItem_Id(
            Long vppItemId
    );

    /*
     * Kiểm tra Book đã có dòng stock hay chưa.
     */
    boolean existsByBook_Id(
            Long bookId
    );

    /*
     * Kiểm tra VPP đã có dòng stock hay chưa.
     */
    boolean existsByVppItem_Id(
            Long vppItemId
    );

    /*
     * ============================================================
     * FIND STOCK WITH PRODUCT INFORMATION
     * ============================================================
     */

    /*
     * Lấy toàn bộ stock và nạp luôn Book hoặc VPP tương ứng.
     *
     * Dùng cho trang:
     * /admin/stock
     */
    @Query("""
        SELECT stock
        FROM Stock stock
        LEFT JOIN FETCH stock.book book
        LEFT JOIN FETCH stock.vppItem vppItem
        ORDER BY stock.id ASC
    """)
    List<Stock> findAllWithProductsOrderByIdAsc();

    /*
     * Lấy một dòng stock theo stock ID và nạp thông tin sản phẩm.
     */
    @Query("""
        SELECT stock
        FROM Stock stock
        LEFT JOIN FETCH stock.book book
        LEFT JOIN FETCH stock.vppItem vppItem
        WHERE stock.id = :stockId
    """)
    Optional<Stock> findByIdWithProduct(
            @Param("stockId") Long stockId
    );

    /*
     * ============================================================
     * PESSIMISTIC LOCK FOR STOCK UPDATE
     * ============================================================
     *
     * Các phương thức dưới đây khóa dòng stock trong database
     * trong lúc tăng hoặc giảm số lượng.
     *
     * Điều này tránh hai thao tác cập nhật cùng lúc đọc cùng một
     * số lượng cũ rồi ghi đè lên nhau.
     */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT stock
        FROM Stock stock
        LEFT JOIN FETCH stock.book book
        LEFT JOIN FETCH stock.vppItem vppItem
        WHERE stock.id = :stockId
    """)
    Optional<Stock> findByIdForUpdate(
            @Param("stockId") Long stockId
    );

    /*
     * Khóa stock theo Book ID.
     *
     * Dùng khi đặt hàng hoặc hoàn hàng theo Book.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT stock
        FROM Stock stock
        JOIN FETCH stock.book book
        WHERE book.id = :bookId
    """)
    Optional<Stock> findByBookIdForUpdate(
            @Param("bookId") Long bookId
    );

    /*
     * Khóa stock theo VPP ID.
     *
     * Dùng khi đặt hàng hoặc hoàn hàng theo VPP.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT stock
        FROM Stock stock
        JOIN FETCH stock.vppItem vppItem
        WHERE vppItem.id = :vppItemId
    """)
    Optional<Stock> findByVppItemIdForUpdate(
            @Param("vppItemId") Long vppItemId
    );

    /*
     * ============================================================
     * LOW-STOCK QUERIES
     * ============================================================
     */

    /*
     * Lấy sản phẩm có số lượng từ 1 đến ngưỡng cảnh báo.
     *
     * Không bao gồm sản phẩm hết hàng.
     */
    @Query("""
        SELECT stock
        FROM Stock stock
        LEFT JOIN FETCH stock.book book
        LEFT JOIN FETCH stock.vppItem vppItem
        WHERE stock.quantity > 0
          AND stock.quantity <= :threshold
        ORDER BY stock.quantity ASC, stock.id ASC
    """)
    List<Stock> findLowStockItems(
            @Param("threshold") int threshold
    );

    /*
     * Lấy sản phẩm đã hết hàng.
     */
    @Query("""
        SELECT stock
        FROM Stock stock
        LEFT JOIN FETCH stock.book book
        LEFT JOIN FETCH stock.vppItem vppItem
        WHERE stock.quantity = 0
        ORDER BY stock.id ASC
    """)
    List<Stock> findOutOfStockItems();

    /*
     * Đếm số sản phẩm có tồn kho thấp hoặc đã hết.
     */
    long countByQuantityLessThanEqual(
            int threshold
    );
}