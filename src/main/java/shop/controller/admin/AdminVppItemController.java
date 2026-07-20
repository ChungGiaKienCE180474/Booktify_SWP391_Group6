package shop.controller.admin;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.dto.VppItemDTO;
import shop.service.SupplierService;
import shop.service.VppCategoryService;
import shop.service.VppItemService;

@Controller
@RequestMapping({"/admin/vpp", "/admin/vpp/items"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminVppItemController {

    private static final int PAGE_SIZE = 10;

    private final VppItemService itemService;
    private final VppCategoryService categoryService;
    private final SupplierService supplierService;

    public AdminVppItemController(
            VppItemService itemService,
            VppCategoryService categoryService,
            SupplierService supplierService
    ) {
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.supplierService = supplierService;
    }

    @GetMapping
    public String list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "showDeleted", defaultValue = "false") boolean showDeleted,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        if (page < 0) {
            page = 0;
        }

        Page<VppItemDTO> itemPage = itemService.searchForAdmin(
                q,
                categoryId,
                status,
                showDeleted,
                PageRequest.of(page, PAGE_SIZE)
        );

        long totalItems = itemPage.getTotalElements();

        int fromItem = totalItems == 0 ? 0 : page * PAGE_SIZE + 1;
        int toItem = Math.min((page + 1) * PAGE_SIZE, (int) totalItems);

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("categories", categoryService.getFixedVppCategories());

        model.addAttribute("q", q);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("showDeleted", showDeleted);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("fromItem", fromItem);
        model.addAttribute("toItem", toItem);

        return "admin/vpp/item/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("itemDTO", new VppItemDTO());

        loadFormData(model, "create");

        return "admin/vpp/item/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("itemDTO") VppItemDTO itemDTO,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            loadFormData(model, "create");
            return "admin/vpp/item/form";
        }

        try {
            itemService.create(itemDTO, imageFile);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "VPP product created successfully."
            );

            return "redirect:/admin/vpp";

        } catch (IllegalArgumentException | IllegalStateException ex) {
            bindingResult.reject("vppError", ex.getMessage());

            loadFormData(model, "create");

            return "admin/vpp/item/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            VppItemDTO item = itemService.getById(id);

            model.addAttribute("itemDTO", item);
            model.addAttribute("itemId", id);

            loadFormData(model, "edit");

            return "admin/vpp/item/form";

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/vpp";
        }
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("itemDTO") VppItemDTO itemDTO,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        VppItemDTO oldItem = itemService.getById(id);

        if (bindingResult.hasErrors()) {
            itemDTO.setImagePath(oldItem.getImagePath());
            itemDTO.setHasImage(oldItem.isHasImage());

            model.addAttribute("itemId", id);
            loadFormData(model, "edit");

            return "admin/vpp/item/form";
        }

        try {
            itemService.update(id, itemDTO, imageFile);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "VPP product updated successfully."
            );

            return "redirect:/admin/vpp";

        } catch (IllegalArgumentException | IllegalStateException ex) {
            bindingResult.reject("vppError", ex.getMessage());

            itemDTO.setImagePath(oldItem.getImagePath());
            itemDTO.setHasImage(oldItem.isHasImage());

            model.addAttribute("itemId", id);
            loadFormData(model, "edit");

            return "admin/vpp/item/form";
        }
    }

    @PostMapping("/{id}/hide")
    public String hide(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        itemService.hide(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "VPP product hidden successfully."
        );

        return "redirect:/admin/vpp";
    }

    @PostMapping("/{id}/restore")
    public String restore(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        itemService.restore(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "VPP product restored successfully."
        );

        return "redirect:/admin/vpp";
    }

    @PostMapping("/{id}/delete")
    public String deleteAsHidden(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        itemService.hide(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "VPP product has been hidden successfully."
        );

        return "redirect:/admin/vpp";
    }

    private void loadFormData(Model model, String formMode) {
        model.addAttribute("categories", categoryService.getFixedVppCategories());
        model.addAttribute("activeSuppliers", supplierService.getAllActive());
        model.addAttribute("formMode", formMode);
    }
}