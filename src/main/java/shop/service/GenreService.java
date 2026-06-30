package shop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import shop.domain.Genre;
import shop.repository.GenreRepository;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAllByDeletedFalseOrderByIdAsc();
    }

    public List<Genre> getGenresByCategory(Long categoryId) {
        return genreRepository.findAllByCategoryIdAndDeletedFalseOrderByIdAsc(categoryId);
    }

    public List<Genre> getActiveGenresByCategory(Long categoryId) {
        return genreRepository.findAllByCategoryIdAndDeletedFalseAndActiveTrueOrderByNameAsc(categoryId);
    }

    public List<Genre> searchGenres(String q, Long categoryId, String status) {
        List<Genre> result;
        if (!StringUtils.hasText(q) && categoryId == null) {
            result = getAllGenres();
        } else if (!StringUtils.hasText(q)) {
            result = getGenresByCategory(categoryId);
        } else if (categoryId == null) {
            result = genreRepository.search(q);
        } else {
            result = genreRepository.searchByCategory(q, categoryId);
        }
        if ("active".equals(status)) {
            result = result.stream().filter(Genre::isActive).toList();
        } else if ("inactive".equals(status)) {
            result = result.stream().filter(g -> !g.isActive()).toList();
        }
        return result;
    }

    public Optional<Genre> getGenreById(Long id) {
        return genreRepository.findById(id);
    }

    public Genre saveGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    @Transactional
    public void removeGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Genre not found"));
        genre.setActive(false);
        genreRepository.save(genre);
    }

    @Transactional
    public void restoreGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Genre not found"));
        genre.setActive(true);
        genreRepository.save(genre);
    }

    public boolean existsByNameAndCategory(String name, Long categoryId) {
        return genreRepository.existsByNameIgnoreCaseAndCategoryIdAndDeletedFalse(name, categoryId);
    }

    public boolean existsByNameAndCategoryExcludeId(String name, Long categoryId, Long excludeId) {
        return genreRepository.existsByNameIgnoreCaseAndCategoryIdAndDeletedFalseAndIdNot(
                name, categoryId, excludeId);
    }
}
