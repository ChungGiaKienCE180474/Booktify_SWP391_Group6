package shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.domain.Book;
import shop.domain.Rating;
import shop.domain.VppItem;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Integer> {

    List<Rating> findByBook_Id(Long bookId);

    List<Rating> findByBook_IdAndStatus(Long bookId, String status);

    boolean existsByBook_IdAndCustomer_Id(Long bookId, Long customerId);

    Optional<Rating> findByBook_IdAndCustomer_Id(Long bookId, Long customerId);

    Optional<Rating> findByBook_IdAndCustomer_IdAndStatus(
            Long bookId,
            Long customerId,
            String status);

    boolean existsByBook_IdAndCustomer_IdAndStatus(
            Long bookId,
            Long customerId,
            String status);

    @Query("""
                SELECT DISTINCT r.book
                FROM Rating r
                WHERE
                    :keyword IS NULL
                    OR :keyword = ''
                    OR LOWER(r.book.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(r.book.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)

    List<Book> findBooksHasReview(
            @Param("keyword") String keyword);

    List<Rating> findByVppItem_Id(Long vppItemId);

    List<Rating> findByVppItem_IdAndStatus(
            Long vppItemId,
            String status);

    Optional<Rating> findByVppItem_IdAndCustomer_Id(
            Long vppItemId,
            Long customerId);

    Optional<Rating> findByVppItem_IdAndCustomer_IdAndStatus(
            Long vppItemId,
            Long customerId,
            String status);

    boolean existsByVppItem_IdAndCustomer_Id(
            Long vppItemId,
            Long customerId);

    boolean existsByVppItem_IdAndCustomer_IdAndStatus(
            Long vppItemId,
            Long customerId,
            String status);

    @Query("""
            SELECT DISTINCT r.vppItem
            FROM Rating r
            WHERE
                r.vppItem IS NOT NULL
                AND (
                    :keyword IS NULL
                    OR :keyword = ''
                    OR LOWER(r.vppItem.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(r.vppItem.supplier, ''))
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            """)
    List<VppItem> findVppItemsHasReview(
            @Param("keyword") String keyword);

    List<Rating> findByVppItem_IdAndStatusNot(
            Long vppItemId,
            String status);
}