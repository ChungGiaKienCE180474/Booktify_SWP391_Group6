package shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.domain.Rating;

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
            
}