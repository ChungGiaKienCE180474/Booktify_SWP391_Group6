package shop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
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

    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByIdAsc();
    }

    public List<Category> searchCategories(String q) {
        if (!StringUtils.hasText(q)) {
            return getAllCategories();
        }
        return categoryRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByIdAsc(q, q);
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        if (bookRepository.existsByCategoryId(id)) {
            throw new IllegalStateException(
                    "Cannot delete this category because it still contains books.");
        }
        categoryRepository.deleteById(id);
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
