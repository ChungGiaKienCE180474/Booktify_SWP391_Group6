package shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import shop.domain.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    List<Genre> findAllByDeletedFalseOrderByIdAsc();

    List<Genre> findAllByCategoryIdAndDeletedFalseOrderByIdAsc(Long categoryId);

    List<Genre> findAllByCategoryIdAndDeletedFalseAndActiveTrueOrderByNameAsc(Long categoryId);

    @Query("SELECT g FROM Genre g LEFT JOIN g.category c WHERE g.deleted = false AND (" +
           "LOWER(g.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(COALESCE(g.description, '')) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY g.id ASC")
    List<Genre> search(@Param("q") String q);

    @Query("SELECT g FROM Genre g WHERE g.deleted = false AND g.category.id = :categoryId AND (" +
           "LOWER(g.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(COALESCE(g.description, '')) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY g.id ASC")
    List<Genre> searchByCategory(@Param("q") String q, @Param("categoryId") Long categoryId);

    boolean existsByNameIgnoreCaseAndCategoryIdAndDeletedFalse(String name, Long categoryId);

    boolean existsByNameIgnoreCaseAndCategoryIdAndDeletedFalseAndIdNot(
            String name, Long categoryId, Long id);
}
