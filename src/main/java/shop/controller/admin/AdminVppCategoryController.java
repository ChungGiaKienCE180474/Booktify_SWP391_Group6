package shop.controller.admin;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.dto.VppCategoryDTO;
import shop.service.VppCategoryService;

@Controller
@RequestMapping("/admin/vpp/categories")
@PreAuthorize("hasRole('ADMIN')")
public class AdminVppCategoryController {

    private final VppCategoryService categoryService;

    public AdminVppCategoryController(VppCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());

        return "admin/vpp/category/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("categoryDTO", new VppCategoryDTO());
        model.addAttribute("formMode", "create");

        return "admin/vpp/category/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("categoryDTO") VppCategoryDTO categoryDTO,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            return "admin/vpp/category/form";
        }

        try {
            categoryService.create(categoryDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "VPP category created successfully."
            );

            return "redirect:/admin/vpp/categories";

        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("name", "duplicate", ex.getMessage());
            model.addAttribute("formMode", "create");
            return "admin/vpp/category/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         Model model) {
        VppCategoryDTO category = categoryService.getById(id);

        model.addAttribute("category", category);

        return "admin/vpp/category/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           Model model) {
        VppCategoryDTO category = categoryService.getById(id);

        model.addAttribute("categoryDTO", category);
        model.addAttribute("categoryId", id);
        model.addAttribute("formMode", "edit");

        return "admin/vpp/category/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("categoryDTO") VppCategoryDTO categoryDTO,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryId", id);
            model.addAttribute("formMode", "edit");

            return "admin/vpp/category/form";
        }

        try {
            categoryService.update(id, categoryDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "VPP category updated successfully."
            );

            return "redirect:/admin/vpp/categories";

        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("name", "duplicate", ex.getMessage());

            model.addAttribute("categoryId", id);
            model.addAttribute("formMode", "edit");

            return "admin/vpp/category/form";
        }
    }

    @PostMapping("/{id}/hide")
    public String hide(@PathVariable Long id,
                       RedirectAttributes redirectAttributes) {
        categoryService.hide(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "VPP category hidden successfully."
        );

        return "redirect:/admin/vpp/categories";
    }

    @PostMapping("/{id}/restore")
    public String restore(@PathVariable Long id,
                          RedirectAttributes redirectAttributes) {
        categoryService.restore(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "VPP category restored successfully."
        );

        return "redirect:/admin/vpp/categories";
    }
}