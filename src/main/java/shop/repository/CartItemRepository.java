package shop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import shop.domain.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndBookId(Long cartId, Long bookId);

    Optional<CartItem> findByIdAndCartUserId(Long id, long userId);
    
    Optional<CartItem> findByCartIdAndVppItemId(Long cartId, Long vppItemId);

    Optional<CartItem> findByCartIdAndBookSetId(Long cartId, Long bookSetId);

    // Used to block deleting/deactivating a book that's sitting in someone's cart.
    boolean existsByBook_Id(Long bookId);

    // Used to block deleting a book set that's sitting in someone's cart.
    boolean existsByBookSet_Id(Long bookSetId);
}
