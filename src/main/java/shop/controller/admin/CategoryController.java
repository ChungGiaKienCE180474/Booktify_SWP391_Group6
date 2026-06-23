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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("categories", categoryService.searchCategories(q));
        model.addAttribute("q", q);
        return "admin/category/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("formMode", "create");
        return "admin/category/form";
    }

    @PostMapping
    public String create(@ModelAttribute("category") @Valid Category category,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (categoryService.existsByNameIgnoreCase(category.getName())) {
            bindingResult.rejectValue("name", "category.exists", "Category name already exists");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            return "admin/category/form";
        }
        categoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Category created successfully.");
        return "redirect:/admin/categories";
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
    public String update(@PathVariable Long id,
            @ModelAttribute("category") @Valid Category category,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        Category existing = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (!existing.getName().equalsIgnoreCase(category.getName())
                && categoryService.existsByNameIgnoreCase(category.getName())) {
            bindingResult.rejectValue("name", "category.exists", "Category name already exists");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "edit");
            return "admin/category/form";
        }

        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        existing.setActive(category.isActive());
        categoryService.saveCategory(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred while deleting the category.");
        }
        return "redirect:/admin/categories";
    }
}
