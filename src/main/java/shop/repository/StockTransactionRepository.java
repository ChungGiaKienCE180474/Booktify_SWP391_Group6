package shop.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import shop.domain.StockMovementType;
import shop.domain.StockProductType;
import shop.domain.StockTransaction;

@Repository
public interface StockTransactionRepository
        extends JpaRepository<StockTransaction, Long> {

    /*
     * ============================================================
     * SEARCH STOCK HISTORY
     * ============================================================
     *
     * Cho phép lọc lịch sử kho theo:
     * - Loại sản phẩm: BOOK hoặc VPP
     * - ID sản phẩm
     * - Loại biến động
     * - Từ khóa
     */

    @Query(
            value = """
                SELECT transaction
                FROM StockTransaction transaction

                LEFT JOIN FETCH transaction.book book
                LEFT JOIN FETCH transaction.vppItem vppItem
                LEFT JOIN FETCH transaction.performedBy performedBy

                WHERE (
                    :productType IS NULL
                    OR transaction.productType = :productType
                )

                AND (
                    :productId IS NULL
                    OR book.id = :productId
                    OR vppItem.id = :productId
                )

                AND (
                    :movementType IS NULL
                    OR transaction.movementType = :movementType
                )

                AND (
                    :keyword IS NULL
                    OR :keyword = ''

                    OR LOWER(
                        COALESCE(book.title, '')
                    ) LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                    OR LOWER(
                        COALESCE(vppItem.name, '')
                    ) LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                    OR LOWER(
                        COALESCE(transaction.referenceCode, '')
                    ) LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                    OR LOWER(
                        COALESCE(transaction.reason, '')
                    ) LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                    OR LOWER(
                        COALESCE(transaction.note, '')
                    ) LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                    OR LOWER(
                        COALESCE(transaction.performedByName, '')
                    ) LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )
                )

                ORDER BY
                    transaction.createdAt DESC,
                    transaction.id DESC
            """,

            countQuery = """
                SELECT COUNT(transaction)
                FROM StockTransaction transaction

                LEFT JOIN transaction.book book
                LEFT JOIN transaction.vppItem vppItem

                WHERE (
                    :productType IS NULL
                    OR transaction.productType = :productType
                )

                AND (
                    :productId IS NULL
                    OR book.id = :productId
                    OR vppItem.id = :productId
                )

                AND (
                    :movementType IS NULL
                    OR transaction.movementType = :movementType
                )

                AND (
                    :keyword IS NULL
                    OR :keyword = ''

                    OR LOWER(
                        COALESCE(book.title, '')
                    ) LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                    OR LOWER(
                        COALESCE(vppItem.name, '')
                    ) LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                    OR LOWER(
                        COALESCE(transaction.referenceCode, '')
                    ) LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                    OR LOWER(
                        COALESCE(transaction.reason, '')
                    ) LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                    OR LOWER(
                        COALESCE(transaction.note, '')
                    ) LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )

                    OR LOWER(
                        COALESCE(transaction.performedByName, '')
                    ) LIKE LOWER(
                        CONCAT('%', :keyword, '%')
                    )
                )
            """
    )
    Page<StockTransaction> search(
            @Param("productType")
            StockProductType productType,

            @Param("productId")
            Long productId,

            @Param("movementType")
            StockMovementType movementType,

            @Param("keyword")
            String keyword,

            Pageable pageable
    );

    /*
     * Lấy năm giao dịch kho mới nhất.
     *
     * Dùng cho phần Recent Stock Movements.
     */
    List<StockTransaction>
    findTop5ByOrderByCreatedAtDesc();

    /*
     * Đếm số giao dịch trong một khoảng thời gian.
     *
     * Ví dụ:
     * từ đầu ngày hôm nay đến đầu ngày mai.
     */
    long countByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    /*
     * Đếm giao dịch theo loại biến động.
     *
     * Ví dụ:
     * ADJUSTMENT, STOCK_IN hoặc STOCK_OUT.
     */
    long countByMovementType(
            StockMovementType movementType
    );
}