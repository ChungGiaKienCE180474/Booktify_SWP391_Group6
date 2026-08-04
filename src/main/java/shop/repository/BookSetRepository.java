package shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import shop.domain.BookSet;

@Repository
public interface BookSetRepository extends JpaRepository<BookSet, Long> {

    /** Sách này có đang là thành phần của bất kỳ book set nào không. */
    boolean existsByItems_Book_Id(Long bookId); // kiếm sách có trong set
        
    // Lấy book set theo id, kèm theo danh sách các item và thông tin sách của từng item
    @Query("""
            SELECT DISTINCT s FROM BookSet s
            LEFT JOIN FETCH s.items i
            LEFT JOIN FETCH i.book
            WHERE s.id = :id
            """)
    Optional<BookSet> findByIdWithItems(@Param("id") Long id);

    // Lấy tất cả book set đang active, kèm theo danh sách các item và thông tin sách của từng item
    @Query("""
            SELECT DISTINCT s FROM BookSet s
            LEFT JOIN FETCH s.items i
            LEFT JOIN FETCH i.book
            WHERE s.active = true
            ORDER BY s.name ASC
            """)
    List<BookSet> findAllActiveWithItems();

    // Lấy tất cả book set, kèm theo danh sách các item và thông tin sách của từng item
    @Query("""
            SELECT DISTINCT s FROM BookSet s
            LEFT JOIN FETCH s.items i
            LEFT JOIN FETCH i.book
            ORDER BY s.updatedAt DESC
            """)
    List<BookSet> findAllWithItems();
// Tìm kiếm book set đang active theo từ khóa và tag, kèm theo danh sách các item và thông tin sách của từng item
    @Query("""
            SELECT DISTINCT s FROM BookSet s
            LEFT JOIN FETCH s.items i
            LEFT JOIN FETCH i.book
            WHERE s.active = true
              AND (:tag IS NULL OR :tag = '' OR s.gradeLevel = :tag)
              AND (
                    :keyword IS NULL OR :keyword = ''
                    OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(s.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(s.gradeLevel, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            ORDER BY s.name ASC
            """)
    List<BookSet> searchActive(
            @Param("keyword") String keyword,
            @Param("tag") String tag
    );
    // Tìm kiếm tất cả book set theo từ khóa và tag, kèm theo danh sách các item và thông tin sách của từng item
    @Query("""
            SELECT DISTINCT s.gradeLevel FROM BookSet s
            WHERE s.active = true
              AND s.gradeLevel IS NOT NULL
              AND TRIM(s.gradeLevel) <> ''
            ORDER BY s.gradeLevel ASC
            """)
    List<String> findDistinctActiveTags();

    @Query("""
            SELECT DISTINCT s.gradeLevel FROM BookSet s
            WHERE s.gradeLevel IS NOT NULL
              AND TRIM(s.gradeLevel) <> ''
            ORDER BY s.gradeLevel ASC
            """)
    List<String> findDistinctTags();
}
