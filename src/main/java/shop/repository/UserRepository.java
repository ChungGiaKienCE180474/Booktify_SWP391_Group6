package shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    User findByEmail(String email);

    User findByEmailIgnoreCase(String email);

    List<User> findByRole_Name(String roleName);

    long countByRole_Name(String roleName);

    long countByRole_NameAndStatus(String roleName, boolean status);

    @Query("""
            SELECT u FROM User u
            WHERE u.role.name = 'CUSTOMER'
            AND (
                :keyword IS NULL OR :keyword = ''
                OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(u.phone, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(u.address, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (
                :status = 'all'
                OR (:status = 'active' AND u.status = true)
                OR (:status = 'inactive' AND u.status = false)
            )
            """)
    Page<User> searchCustomers(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );
}
