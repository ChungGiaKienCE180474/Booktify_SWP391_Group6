package shop.controller.admin;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.dto.SupplierDTO;
import shop.service.SupplierService;

@Controller
@RequestMapping("/admin/suppliers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSupplierController {

    private final SupplierService supplierService;

    public AdminSupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public String list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "status", required = false) String status,
            Model model
    ) {
        List<SupplierDTO> suppliers = supplierService.searchSuppliers(q, status);

        model.addAttribute("suppliers", suppliers);
        model.addAttribute("q", q);
        model.addAttribute("status", status);

        return "admin/supplier/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("supplierDTO", new SupplierDTO());
        model.addAttribute("formMode", "create");

        return "admin/supplier/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("supplierDTO") SupplierDTO supplierDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            return "admin/supplier/form";
        }

        try {
            supplierService.create(supplierDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Supplier created successfully."
            );

            return "redirect:/admin/suppliers";

        } catch (IllegalArgumentException ex) {
            bindingResult.reject("supplierError", ex.getMessage());
            model.addAttribute("formMode", "create");

            return "admin/supplier/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        SupplierDTO supplier = supplierService.getById(id).orElse(null);

        if (supplier == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Supplier not found.");
            return "redirect:/admin/suppliers";
        }

        model.addAttribute("supplierDTO", supplier);
        model.addAttribute("supplierId", id);
        model.addAttribute("formMode", "edit");

        return "admin/supplier/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("supplierDTO") SupplierDTO supplierDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("supplierId", id);
            model.addAttribute("formMode", "edit");

            return "admin/supplier/form";
        }

        try {
            supplierService.update(id, supplierDTO);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Supplier updated successfully."
            );

            return "redirect:/admin/suppliers";

        } catch (IllegalArgumentException | IllegalStateException ex) {
            bindingResult.reject("supplierError", ex.getMessage());
            model.addAttribute("supplierId", id);
            model.addAttribute("formMode", "edit");

            return "admin/supplier/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            supplierService.remove(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Supplier hidden successfully."
            );

        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/suppliers";
    }

    @PostMapping("/{id}/restore")
    public String restore(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            supplierService.restore(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Supplier restored successfully."
            );

        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/suppliers";
    }
}