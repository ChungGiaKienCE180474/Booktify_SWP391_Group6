package shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import shop.domain.VppCategory;

@Repository
public interface VppCategoryRepository extends JpaRepository<VppCategory, Long> {

    List<VppCategory> findAllByOrderByActiveDescNameAsc();

    List<VppCategory> findAllByActiveTrueOrderByNameAsc();

    Optional<VppCategory> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}