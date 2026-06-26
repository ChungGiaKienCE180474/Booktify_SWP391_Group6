package shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.domain.ContactRequest;
import shop.domain.ContactStatus;
import shop.domain.User;

import java.util.Optional;

public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {

    Page<ContactRequest> findByCustomerOrderByCreatedAtDesc(User customer, Pageable pageable);

    Optional<ContactRequest> findByIdAndCustomer(Long id, User customer);

    long countByStatus(ContactStatus status);

    @Query("""
            SELECT c
            FROM ContactRequest c
            WHERE (:status IS NULL OR c.status = :status)
              AND (
                    :keyword IS NULL
                    OR :keyword = ''
                    OR LOWER(c.subject) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(c.customer.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            ORDER BY c.createdAt DESC
            """)
    Page<ContactRequest> searchForStaff(
            @Param("keyword") String keyword,
            @Param("status") ContactStatus status,
            Pageable pageable
    );
}