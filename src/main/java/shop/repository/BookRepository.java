package shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import shop.domain.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByOrderByIdAsc();

    List<Book> findAllByActiveTrueOrderByIdAsc();

    List<Book> findAllByCategoryIdAndActiveTrueAndIdNotOrderByIdAsc(Long categoryId, Long excludeId);

    List<Book> findAllByCategoryIdAndActiveTrueOrderByIdAsc(Long categoryId);

    // ── ISBN ──────────────────────────────────────────────────────────────────
    /** Tìm sách theo ISBN (không phân biệt hoa thường) — dùng để validate trùng */
    Optional<Book> findByIsbnIgnoreCase(String isbn);

    boolean existsByCategoryId(Long categoryId);

    boolean existsByGenreId(Long genreId);

    // ── COUNT ─────────────────────────────────────────────────────────────────
    /** SELECT COUNT(*) FROM books WHERE active = true — không load data */
    long countByActiveTrue();

    @Query("SELECT b FROM Book b LEFT JOIN b.category c WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(COALESCE(b.isbn, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "ORDER BY b.id ASC")
    List<Book> search(@Param("q") String q);
}
