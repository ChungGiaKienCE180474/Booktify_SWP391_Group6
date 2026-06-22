package shop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import shop.domain.Genre;
import shop.repository.BookRepository;
import shop.repository.GenreRepository;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    public GenreService(GenreRepository genreRepository, BookRepository bookRepository) {
        this.genreRepository = genreRepository;
        this.bookRepository = bookRepository;
    }

    // ── Danh sách chưa xóa mềm ───────────────────────────────────────────────
    public List<Genre> getAllGenres() {
        return genreRepository.findAllByDeletedFalseOrderByIdAsc();
    }

    // ── Filter theo category ──────────────────────────────────────────────────
    public List<Genre> getGenresByCategory(Long categoryId) {
        return genreRepository.findAllByCategoryIdAndDeletedFalseOrderByIdAsc(categoryId);
    }

    // ── Active genres cho Book form dropdown ─────────────────────────────────
    public List<Genre> getActiveGenresByCategory(Long categoryId) {
        return genreRepository.findAllByCategoryIdAndDeletedFalseAndActiveTrueOrderByNameAsc(categoryId);
    }

    // ── Search ────────────────────────────────────────────────────────────────
    public List<Genre> searchGenres(String q, Long categoryId) {
        if (!StringUtils.hasText(q) && categoryId == null) {
            return getAllGenres();
        }
        if (!StringUtils.hasText(q)) {
            return getGenresByCategory(categoryId);
        }
        if (categoryId == null) {
            return genreRepository.search(q);
        }
        return genreRepository.searchByCategory(q, categoryId);
    }

    // ── Tìm theo ID ───────────────────────────────────────────────────────────
    public Optional<Genre> getGenreById(Long id) {
        return genreRepository.findById(id);
    }

    // ── Lưu ──────────────────────────────────────────────────────────────────
    public Genre saveGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    // ── Soft delete ───────────────────────────────────────────────────────────
    @Transactional
    public void deleteGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Genre not found"));
        if (bookRepository.existsByGenreId(id)) {
            throw new IllegalStateException(
                    "Cannot delete this genre because it still contains books.");
        }
        genre.setDeleted(true);
        genre.setActive(false);
        genreRepository.save(genre);
    }

    // ── Validation: trùng tên trong cùng category (create) ───────────────────
    public boolean existsByNameAndCategory(String name, Long categoryId) {
        return genreRepository.existsByNameIgnoreCaseAndCategoryIdAndDeletedFalse(name, categoryId);
    }

    // ── Validation: trùng tên trong cùng category (edit) ─────────────────────
    public boolean existsByNameAndCategoryExcludeId(String name, Long categoryId, Long excludeId) {
        return genreRepository.existsByNameIgnoreCaseAndCategoryIdAndDeletedFalseAndIdNot(
                name, categoryId, excludeId);
    }
}
