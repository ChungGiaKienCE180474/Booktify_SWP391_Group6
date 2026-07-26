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
import shop.service.VppCategoryService;

@Controller
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
public class CategoryController {

    private final CategoryService categoryService;
    private final VppCategoryService vppCategoryService;

    public CategoryController(CategoryService categoryService, VppCategoryService vppCategoryService) {
        this.categoryService = categoryService;
        this.vppCategoryService = vppCategoryService;
    }

    private static final int PAGE_SIZE = 10;

    // Filters in memory and paginates via subList — fine here since the
    // number of categories is small.
    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String status,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "false") boolean all,
                       Model model) {
        java.util.List<shop.domain.Category> allCategories = categoryService.searchCategories(q, status);
        int totalItems = allCategories.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / PAGE_SIZE));
        int from;
        int to;
        if (all) {
            page = 0;
            from = 0;
            to = totalItems;
        } else {
            page = Math.max(0, Math.min(page, totalPages - 1));
            from = page * PAGE_SIZE;
            to   = Math.min(from + PAGE_SIZE, totalItems);
        }
        model.addAttribute("categories", allCategories.subList(from, to));
        model.addAttribute("q", q);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("fromItem", totalItems == 0 ? 0 : from + 1);
        model.addAttribute("toItem", to);
        model.addAttribute("viewingAll", all);

        // Stationery categories are a small, fixed set (see VppCategoryService),
        // so no search/pagination needed — just render them in the second tab.
        model.addAttribute("vppCategories", vppCategoryService.getAllCategories());

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
        // active is intentionally left untouched — toggling it goes through
        // the Remove/Restore actions on the list page, not this form.
        categoryService.saveCategory(existing);
        redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String remove(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.removeCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category removed successfully.");
        } catch (IllegalStateException e) {
            // Category still has books attached — surface the specific reason.
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred while removing the category.");
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/restore")
    public String restore(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.restoreCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category restored successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred while restoring the category.");
        }
        return "redirect:/admin/categories";
    }
}
