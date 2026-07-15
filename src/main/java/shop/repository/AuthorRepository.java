package shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.domain.Author;
import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Kiểm tra xem đã tồn tại Author theo tên chưa (không phân biệt hoa thường)
    boolean existsByAuthorNameIgnoreCase(String authorName);

    // Dùng để link tên author trong Book (String tự do) sang trang chi tiết
    // Author — chỉ match author còn active, vì trang detail author 404 nếu
    // author đã bị deactivate.
    Optional<Author> findByAuthorNameIgnoreCaseAndStatusTrue(String authorName);

    // Dùng khi edit:
    // kiểm tra trùng tên nhưng loại trừ chính bản ghi hiện tại (authorId != id)
    boolean existsByAuthorNameIgnoreCaseAndAuthorIdNot(
            String authorName,
            Long authorId);

    List<Author> findAllByOrderByAuthorIdDesc();

    List<Author> findAllByStatusTrueOrderByAuthorIdDesc();

}