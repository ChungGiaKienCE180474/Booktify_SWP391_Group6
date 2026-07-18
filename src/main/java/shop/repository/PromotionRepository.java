package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shop.domain.Promotion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository
        extends JpaRepository<Promotion, Long> {

    List<Promotion> findAllByOrderByIdDesc();

    List<Promotion> findAllByActiveTrueOrderByIdDesc();

    List<Promotion> findAllByActiveTrueOrderByEndDateAsc();

    boolean existsByNameIgnoreCase(String name);

    @Query("""
            SELECT DISTINCT promotion
            FROM Promotion promotion
            LEFT JOIN FETCH promotion.applicableCategories
            ORDER BY promotion.id DESC
            """)
    List<Promotion> findAllWithCategories();

    @Query("""
            SELECT DISTINCT promotion
            FROM Promotion promotion
            LEFT JOIN FETCH promotion.applicableCategories
            WHERE promotion.id = :promotionId
            """)
    Optional<Promotion> findByIdWithCategories(
            @Param("promotionId") Long promotionId
    );

    @Query("""
            SELECT DISTINCT promotion
            FROM Promotion promotion
            LEFT JOIN FETCH promotion.applicableCategories
            WHERE promotion.active = true
              AND promotion.startDate <= CURRENT_TIMESTAMP
              AND promotion.endDate >= CURRENT_TIMESTAMP
            ORDER BY promotion.endDate ASC
            """)
    List<Promotion> findActivePromotions();

    @Query("""
            SELECT DISTINCT promotion
            FROM Promotion promotion
            JOIN promotion.applicableCategories category
            WHERE promotion.active = true
              AND category.id = :categoryId
              AND promotion.startDate <= CURRENT_TIMESTAMP
              AND promotion.endDate >= CURRENT_TIMESTAMP
            ORDER BY promotion.endDate ASC
            """)
    List<Promotion> findActivePromotionsByCategoryId(
            @Param("categoryId") Long categoryId
    );

    @Query("""
            SELECT CASE
                       WHEN COUNT(promotion) > 0 THEN true
                       ELSE false
                   END
            FROM Promotion promotion
            JOIN promotion.applicableCategories category
            WHERE promotion.active = true
              AND category.id = :categoryId
              AND promotion.startDate <= :endDate
              AND promotion.endDate >= :startDate
              AND (
                    :excludePromotionId IS NULL
                    OR promotion.id <> :excludePromotionId
                  )
            """)
    boolean existsConflictingPromotion(
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("excludePromotionId") Long excludePromotionId
    );
}