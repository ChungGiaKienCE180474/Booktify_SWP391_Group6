package shop.repository;

import java.util.List;

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

    boolean existsByIsbnIgnoreCase(String isbn);

    boolean existsByCategoryId(Long categoryId);

    boolean existsByGenreId(Long genreId);

    @Query("SELECT b FROM Book b LEFT JOIN b.category c WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(COALESCE(b.isbn, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(COALESCE(c.name, '')) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "ORDER BY b.id ASC")
    List<Book> search(@Param("q") String q);
}
