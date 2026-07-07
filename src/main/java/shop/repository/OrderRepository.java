package shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByOrderCode(String orderCode);

    Optional<Order> findByOrderCode(String orderCode);

    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdWithItemsOrderByCreatedAtDesc(@Param("userId") long userId);

    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id AND o.user.id = :userId")
    Optional<Order> findByIdAndUserIdWithItems(@Param("id") Long id, @Param("userId") long userId);

    @Query("SELECT o FROM Order o JOIN FETCH o.user JOIN FETCH o.items ORDER BY o.createdAt DESC")
    List<Order> findAllWithUserAndItemsOrderByCreatedAtDesc();

    @Query("SELECT o FROM Order o JOIN FETCH o.user JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithUserAndItems(@Param("id") Long id);

    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") long userId);

    long countByUserId(long userId);
}
