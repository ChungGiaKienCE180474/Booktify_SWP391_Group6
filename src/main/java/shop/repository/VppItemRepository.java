package shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.domain.VppItem;

public interface VppItemRepository extends JpaRepository<VppItem, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query(
            value = """
                    SELECT item
                    FROM VppItem item
                    LEFT JOIN item.category category
                    WHERE
                        (
                            :keyword IS NULL
                            OR :keyword = ''
                            OR LOWER(item.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(COALESCE(item.supplier, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(COALESCE(item.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        )
                        AND (:categoryId IS NULL OR category.id = :categoryId)
                        AND (:status IS NULL OR :status = '' OR LOWER(item.status) = LOWER(:status))
                        AND (:showDeleted = true OR item.deleted = false)
                    ORDER BY
                        CASE
                            WHEN LOWER(item.status) = 'active' THEN 0
                            ELSE 1
                        END,
                        item.id DESC
                    """,
            countQuery = """
                    SELECT COUNT(item)
                    FROM VppItem item
                    LEFT JOIN item.category category
                    WHERE
                        (
                            :keyword IS NULL
                            OR :keyword = ''
                            OR LOWER(item.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(COALESCE(item.supplier, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(COALESCE(item.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        )
                        AND (:categoryId IS NULL OR category.id = :categoryId)
                        AND (:status IS NULL OR :status = '' OR LOWER(item.status) = LOWER(:status))
                        AND (:showDeleted = true OR item.deleted = false)
                    """
    )
    Page<VppItem> searchForAdmin(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("status") String status,
            @Param("showDeleted") boolean showDeleted,
            Pageable pageable
    );

    @Query(
            value = """
                    SELECT item
                    FROM VppItem item
                    JOIN item.category category
                    WHERE item.deleted = false
                        AND LOWER(item.status) = 'active'
                        AND category.active = true
                        AND (
                            :keyword IS NULL
                            OR :keyword = ''
                            OR LOWER(item.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(COALESCE(item.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        )
                        AND (:categoryId IS NULL OR category.id = :categoryId)
                        AND (:inStockOnly = false OR item.stockQuantity > 0)
                    ORDER BY item.id DESC
                    """,
            countQuery = """
                    SELECT COUNT(item)
                    FROM VppItem item
                    JOIN item.category category
                    WHERE item.deleted = false
                        AND LOWER(item.status) = 'active'
                        AND category.active = true
                        AND (
                            :keyword IS NULL
                            OR :keyword = ''
                            OR LOWER(item.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(COALESCE(item.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        )
                        AND (:categoryId IS NULL OR category.id = :categoryId)
                        AND (:inStockOnly = false OR item.stockQuantity > 0)
                    """
    )
    Page<VppItem> searchForCustomer(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("inStockOnly") boolean inStockOnly,
            Pageable pageable
    );
}