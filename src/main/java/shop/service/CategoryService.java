package shop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import shop.domain.Category;
import shop.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByIdDesc();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
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
