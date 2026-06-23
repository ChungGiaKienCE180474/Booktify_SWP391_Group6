package shop.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.dto.StationeryCategoryDTO;
import shop.service.StationeryCategoryService;

/**
 * Controller xử lý các request cho danh mục văn phòng phẩm.
 * Ánh xạ tất cả URL bắt đầu bằng /stationery/categories.
 *
 * Luồng:
 *   GET  /stationery/categories              → Danh sách danh mục
 *   GET/POST /stationery/categories/create   → Tạo mới
 *   GET/POST /stationery/categories/{id}/edit → Cập nhật
 *   POST /stationery/categories/{id}/delete  → Xóa thật (chặn nếu còn sản phẩm)
 */
@Controller
@RequestMapping("/stationery/categories")
public class StationeryCategoryController {

    private final StationeryCategoryService categoryService;

    public StationeryCategoryController(StationeryCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** Đưa thông tin user từ session vào Model để header JSP hiển thị đúng. */
    private void addUserToModel(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("fullName", session.getAttribute("fullName"));
            model.addAttribute("role",     session.getAttribute("role"));
        }
    }

    // ── Danh sách ────────────────────────────────────────────────────────────
    @GetMapping
    public String list(Model model, HttpServletRequest request) {
        model.addAttribute("categories", categoryService.getAll());
        addUserToModel(model, request);
        return "stationery/category/list";
    }

    // ── Tạo mới (GET) ────────────────────────────────────────────────────────
    /** Hiển thị form tạo mới với một DTO rỗng. */
    @GetMapping("/create")
    public String showCreate(Model model, HttpServletRequest request) {
        model.addAttribute("stationeryCategory", new StationeryCategoryDTO());
        addUserToModel(model, request);
        return "stationery/category/create";
    }

    // ── Tạo mới (POST) ───────────────────────────────────────────────────────
    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("stationeryCategory") StationeryCategoryDTO categoryDTO,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra trùng tên chỉ khi trường name không có lỗi validation khác
        if (!bindingResult.hasFieldErrors("name") &&
                categoryService.isDuplicateName(categoryDTO.getName())) {
            bindingResult.rejectValue("name", "duplicate", "This category name already exists.");
        }

        if (bindingResult.hasErrors()) {
            addUserToModel(model, request);
            return "stationery/category/create";
        }

        try {
            categoryService.create(categoryDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                "Category '" + categoryDTO.getName() + "' created successfully!");
            return "redirect:/stationery/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            addUserToModel(model, request);
            return "stationery/category/create";
        }
    }

    // ── Cập nhật (GET) ───────────────────────────────────────────────────────
    /**
     * Hiển thị form sửa với dữ liệu hiện tại của danh mục.
     * Dùng Optional.map().orElseGet() để xử lý gọn cả hai nhánh (found / not found).
     */
    @GetMapping("/{id}/edit")
    public String showEdit(@PathVariable Long id, Model model,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {

        return categoryService.getById(id).map(catDTO -> {
            model.addAttribute("stationeryCategory", catDTO);
            addUserToModel(model, request);
            return "stationery/category/edit";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Category not found.");
            return "redirect:/stationery/categories";
        });
    }

    // ── Cập nhật (POST) ──────────────────────────────────────────────────────
    @PostMapping("/{id}/edit")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute("stationeryCategory") StationeryCategoryDTO updatedDTO,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra trùng với danh mục KHÁC (loại trừ chính danh mục đang sửa)
        if (!bindingResult.hasFieldErrors("name") &&
                categoryService.isDuplicateNameForUpdate(updatedDTO.getName(), id)) {
            bindingResult.rejectValue("name", "duplicate", "This category name already exists.");
        }

        if (bindingResult.hasErrors()) {
            updatedDTO.setId(id); // cần id để form action = /stationery/categories/{id}/edit không bị null
            addUserToModel(model, request);
            return "stationery/category/edit";
        }

        try {
            categoryService.update(id, updatedDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully!");
            return "redirect:/stationery/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            updatedDTO.setId(id);
            addUserToModel(model, request);
            return "stationery/category/edit";
        }
    }

    // ── Xóa (POST) ───────────────────────────────────────────────────────────
    /**
     * Xóa thật danh mục. Service sẽ ném IllegalArgumentException nếu còn sản phẩm đang dùng.
     * Thông báo lỗi/thành công được đưa vào flash attribute để hiển thị sau redirect.
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/stationery/categories";
    }
}
