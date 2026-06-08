package shop.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import shop.domain.Category;
import shop.service.CategoryService;

@Controller
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("formMode", "create");
        return "admin/category/form";
    }

    @PostMapping
    public String create(@ModelAttribute("category") @Valid Category category, BindingResult bindingResult,
            Model model) {
        if (categoryService.existsByNameIgnoreCase(category.getName())) {
            bindingResult.rejectValue("name", "category.exists", "Category name already exists");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            return "admin/category/form";
        }

        categoryService.saveCategory(category);
        return "redirect:/admin/categories?created";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        model.addAttribute("category", category);
        model.addAttribute("formMode", "edit");
        return "admin/category/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("category") @Valid Category category,
            BindingResult bindingResult, Model model) {
        Category existingCategory = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (!existingCategory.getName().equalsIgnoreCase(category.getName())
                && categoryService.existsByNameIgnoreCase(category.getName())) {
            bindingResult.rejectValue("name", "category.exists", "Category name already exists");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "edit");
            return "admin/category/form";
        }

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setActive(category.isActive());
        categoryService.saveCategory(existingCategory);
        return "redirect:/admin/categories?updated";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories?deleted";
    }
}
