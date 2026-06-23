package shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import shop.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderByIdAsc();

    boolean existsByNameIgnoreCase(String name);

    Category findByNameIgnoreCase(String name);

    List<Category> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByIdAsc(
            String name, String description);
}
