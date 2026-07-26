package shop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import shop.domain.Category;
import shop.repository.BookRepository;
import shop.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public CategoryService(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    // Includes inactive categories — also used by GlobalModelAdvice to build
    // the site-wide navigation menu, so don't filter this down to active-only.
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByIdAsc();
    }

    public List<Category> searchCategories(String q, String status) {
        List<Category> result;
        if (!StringUtils.hasText(q)) {
            result = getAllCategories();
        } else {
            result = categoryRepository
                    .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByIdAsc(q, q);
        }
        if ("active".equals(status)) {
            result = result.stream().filter(Category::isActive).toList();
        } else if ("inactive".equals(status)) {
            result = result.stream().filter(c -> !c.isActive()).toList();
        }
        return result;
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Blocks soft-deleting a category that still has books assigned to it —
    // those books would otherwise be left pointing at a hidden category.
    @Transactional
    public void removeCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        if (bookRepository.existsByCategoryId(id)) {
            throw new IllegalStateException(
                    "Cannot delete this category: it still has books assigned to it.");
        }
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Transactional
    public void restoreCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setActive(true);
        categoryRepository.save(category);
    }

    public long countCategories() {
        return categoryRepository.count();
    }

    public long countActiveCategories() {
        return categoryRepository.findAll().stream().filter(Category::isActive).count();
    }

    public boolean existsByNameIgnoreCase(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }
}
