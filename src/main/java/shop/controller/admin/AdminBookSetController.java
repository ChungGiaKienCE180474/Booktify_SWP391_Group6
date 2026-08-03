package shop.controller.admin;

import java.util.ArrayList;
import java.util.List;

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
import shop.domain.BookSet;
import shop.domain.dto.BookSetForm;
import shop.domain.dto.BookSetItemForm;
import shop.service.BookService;
import shop.service.BookSetService;

@Controller
@RequestMapping("/admin/book-sets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookSetController {

    private final BookSetService bookSetService;
    private final BookService bookService;

    public AdminBookSetController(
            BookSetService bookSetService,
            BookService bookService) {
        this.bookSetService = bookSetService;
        this.bookService = bookService;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            Model model) {
        List<BookSet> sets = bookSetService.filterForAdmin(q, status);
        model.addAttribute("bookSets", sets);
        model.addAttribute("q", q);
        model.addAttribute("status", status);
        model.addAttribute("availableQtyMap", sets.stream().collect(
                java.util.stream.Collectors.toMap(
                        BookSet::getId,
                        bookSetService::getAvailableSetQuantity,
                        (a, b) -> a,
                        java.util.LinkedHashMap::new
                )
        ));
        model.addAttribute("retailTotalMap", sets.stream().collect(
                java.util.stream.Collectors.toMap(
                        BookSet::getId,
                        s -> bookSetService.formatMoney(bookSetService.getRetailTotal(s)),
                        (a, b) -> a,
                        java.util.LinkedHashMap::new
                )
        ));
        model.addAttribute("tagLabelMap", sets.stream().collect(
                java.util.stream.Collectors.toMap(
                        BookSet::getId,
                        s -> bookSetService.tagLabel(s.getGradeLevel()),
                        (a, b) -> a,
                        java.util.LinkedHashMap::new
                )
        ));
        return "admin/book-set/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        BookSetForm form = new BookSetForm();
        form.getItems().add(new BookSetItemForm());
        form.getItems().add(new BookSetItemForm());
        prepareFormModel(model, form, "create");
        return "admin/book-set/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable long id,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            BookSet bookSet = bookSetService.getByIdWithItems(id);
            BookSetForm form = bookSetService.toForm(bookSet);
            if (form.getItems().isEmpty()) {
                form.getItems().add(new BookSetItemForm());
                form.getItems().add(new BookSetItemForm());
            }
            prepareFormModel(model, form, "edit");
            return "admin/book-set/form";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/book-sets";
        }
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("bookSetForm") BookSetForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (form.getItems() == null) {
            form.setItems(new ArrayList<>());
        }

        if (bindingResult.hasErrors()) {
            prepareFormModel(model, form, form.getId() == null ? "create" : "edit");
            return "admin/book-set/form";
        }

        try {
            BookSet saved = bookSetService.saveFromForm(form);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Book set \"" + saved.getName() + "\" saved."
            );
            return "redirect:/admin/book-sets";
        } catch (IllegalArgumentException ex) {
            String msg = ex.getMessage() == null ? "Invalid book set." : ex.getMessage();
            String lower = msg.toLowerCase();
            if (lower.contains("name")) {
                bindingResult.rejectValue("name", "invalid", msg);
            } else if (lower.contains("price")) {
                bindingResult.rejectValue("setPrice", "invalid", msg);
            } else {
                bindingResult.rejectValue("items", "invalid", msg);
            }
            prepareFormModel(model, form, form.getId() == null ? "create" : "edit");
            return "admin/book-set/form";
        } catch (Exception ex) {
            // Chặn mọi lỗi runtime khác (vd DataIntegrityViolation) → tránh Whitelabel 500
            bindingResult.rejectValue("items", "invalid",
                    "An unexpected error occurred while saving the book set.");
            prepareFormModel(model, form, form.getId() == null ? "create" : "edit");
            return "admin/book-set/form";
        }
    }

    @PostMapping("/{id}/status")
    public String toggleStatus(
            @PathVariable long id,
            @RequestParam boolean active,
            RedirectAttributes redirectAttributes) {
        try {
            bookSetService.setActive(id, active);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    active ? "Book set activated." : "Book set deactivated."
            );
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred while updating the status.");
        }
        return "redirect:/admin/book-sets";
    }

    // Soft-delete (ẩn) book set — giống Book. Chặn nếu đang trong giỏ khách.
    @PostMapping("/{id}/delete")
    public String remove(
            @PathVariable long id,
            RedirectAttributes redirectAttributes) {
        try {
            bookSetService.removeBookSet(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book set deleted successfully.");
        } catch (IllegalStateException ex) {
            // Đang nằm trong giỏ — báo lý do cụ thể
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred while deleting the book set.");
        }
        return "redirect:/admin/book-sets";
    }

    // Khôi phục book set đã ẩn — giống Book.
    @PostMapping("/{id}/restore")
    public String restore(
            @PathVariable long id,
            RedirectAttributes redirectAttributes) {
        try {
            bookSetService.restoreBookSet(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book set restored successfully.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "An unexpected error occurred while restoring the book set.");
        }
        return "redirect:/admin/book-sets";
    }

    private void prepareFormModel(Model model, BookSetForm form, String formMode) {
        model.addAttribute("bookSetForm", form);
        model.addAttribute("formMode", formMode);
        model.addAttribute("activeBooks", bookService.getActiveBooks());
    }
}
