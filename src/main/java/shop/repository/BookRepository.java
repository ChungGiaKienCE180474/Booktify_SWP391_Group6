package shop.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import shop.domain.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // NEW: Lock the selected book while updating its stock.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findByIdForUpdate(@Param("id") Long id);

    List<Book> findAllByOrderByIdAsc();

    List<Book> findAllByActiveTrueOrderByIdAsc();

    // Used for the "related books" suggestions on the detail page.
    List<Book> findAllByCategoryIdAndActiveTrueAndIdNotOrderByIdAsc(
            Long categoryId,
            Long excludeId
    );

    List<Book> findAllByCategoryIdAndActiveTrueOrderByIdAsc(
            Long categoryId
    );

    Optional<Book> findByIsbnIgnoreCase(String isbn);

    // Guards against deleting a category that still has books attached.
    boolean existsByCategoryId(Long categoryId);

    boolean existsByGenres_Id(Long genreId);

    long countByActiveTrue();

    // Quick keyword search across title/author/isbn/category name.
    @Query("SELECT b FROM Book b LEFT JOIN b.category c LEFT JOIN b.author a WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(COALESCE(a.authorName, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(COALESCE(b.isbn, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "ORDER BY b.id ASC")
    List<Book> search(@Param("q") String q);

    // hasGenreFilter lets the caller skip the genre condition entirely instead
    // of binding a null/empty list into the IN clause, which JPQL can't handle.
    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN b.category c " +
           "LEFT JOIN b.author a " +
           "LEFT JOIN b.genres g " +
           "WHERE (:q IS NULL OR :q = '' OR " +
           "       LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "       LOWER(COALESCE(a.authorName, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "       LOWER(COALESCE(b.isbn, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "       LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "       LOWER(COALESCE(g.name, '')) LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "AND (:categoryId IS NULL OR c.id = :categoryId) " +
           "AND (:hasGenreFilter = false OR g.id IN :genreIds) " +
           "ORDER BY b.id ASC")
    List<Book> filterBooks(
            @Param("q") String q,
            @Param("categoryId") Long categoryId,
            @Param("genreIds") List<Long> genreIds,
            @Param("hasGenreFilter") boolean hasGenreFilter
    );
}