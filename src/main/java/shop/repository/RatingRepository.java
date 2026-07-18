package shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.domain.Book;
import shop.domain.Rating;

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
}