package shop.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import shop.domain.Category;
import shop.repository.CategoryRepository;

@Component
@Order(3)
public class CategoryDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public CategoryDataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        List<CategorySeed> seeds = List.of(
                new CategorySeed("Literature", "Classic and modern literature"),
                new CategorySeed("Business", "Business, finance, and entrepreneurship"),
                new CategorySeed("Children", "Books for children and young readers"),
                new CategorySeed("Self-Help", "Personal growth and productivity"),
                new CategorySeed("Science", "Science and technology titles"),
                new CategorySeed("Languages", "Language learning resources"));

        for (CategorySeed seed : seeds) {
            if (categoryRepository.existsByNameIgnoreCase(seed.name())) {
                continue;
            }

            Category category = new Category();
            category.setName(seed.name());
            category.setDescription(seed.description());
            category.setActive(true);
            categoryRepository.save(category);
        }
    }

    private record CategorySeed(String name, String description) {

    }
}
