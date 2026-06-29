package shop.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.User;
import shop.domain.dto.StationeryItemDTO;
import shop.service.StationeryCategoryService;
import shop.service.StationeryService;
import shop.service.UserService;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller xử lý các request cho mặt hàng văn phòng phẩm.
 * Ánh xạ tất cả URL bắt đầu bằng /stationery.
 *
 * Luồng chính:
 *   GET  /stationery          → Danh sách (UC-17.1)
 *   GET  /stationery/{id}     → Chi tiết  (UC-17.2)
 *   GET/POST /stationery/create      → Tạo mới  (UC-17.3)
 *   GET/POST /stationery/{id}/edit   → Cập nhật (UC-17.4)
 *   POST /stationery/{id}/delete     → Xóa mềm  (UC-17.5)
 */
@Controller
@RequestMapping("/stationery")
public class StationeryController {

    private final StationeryService stationeryService;
    private final StationeryCategoryService categoryService;
    private final UserService userService;

    public StationeryController(StationeryService stationeryService,
                                StationeryCategoryService categoryService,
                                UserService userService) {
        this.stationeryService = stationeryService;
        this.categoryService   = categoryService;
        this.userService       = userService;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /** Lấy User hiện tại từ session; trả về null nếu chưa đăng nhập. */
    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) return null;
        return userService.getUserByEmail((String) session.getAttribute("email"));
    }

    /** Đưa thông tin user từ session vào Model để header JSP hiển thị tên đăng nhập. */
    private void addUserToModel(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("fullName", session.getAttribute("fullName"));
            model.addAttribute("role",     session.getAttribute("role"));
        }
    }

    /** Đưa danh sách danh mục vào Model để dropdown trong form create/edit hoạt động. */
    private void addCategoriesToModel(Model model) {
        model.addAttribute("categories", categoryService.getAll());
    }

    /**
     * Thay thế thông báo lỗi "typeMismatch" mặc định của Spring (tiếng Anh, khó hiểu)
     * bằng thông báo tiếng Việt thân thiện.
     *
     * Lỗi typeMismatch xảy ra khi người dùng nhập chữ vào ô price/stockQuantity (kiểu số).
     * Spring tạo ra lỗi với code = "typeMismatch.stationeryItem.price" (chuỗi dài),
     * nên phải dùng fe.getCodes()[0] chứ không dùng fe.getCode() để so sánh chính xác.
     */
    private void fixTypeMismatchErrors(BindingResult result) {
        for (FieldError fe : List.copyOf(result.getFieldErrors())) {
            // getCodes()[0] là code cụ thể nhất, bắt đầu bằng "typeMismatch."
            if (fe.getCodes() != null && fe.getCodes().length > 0
                    && fe.getCodes()[0].startsWith("typeMismatch")) {
                String field = fe.getField();
                String msg = "price".equals(field)
                    ? "Please enter a valid price (e.g., 15000)"
                    : "Please enter a valid integer for quantity";
                result.rejectValue(field, "invalid.number", msg);
            }
        }
    }

    /**
     * Lưu file ảnh upload lên server, trả về đường dẫn web để lưu vào DB.
     *
     * Dùng getRealPath() để lấy đường dẫn thật trên file system (tương thích cả dev và deploy).
     * UUID làm prefix tên file để tránh trùng tên khi nhiều người upload cùng tên file.
     * replaceAll("[^a-zA-Z0-9._-]", "_") loại bỏ ký tự đặc biệt/Unicode trong tên file gốc.
     *
     * @return đường dẫn web dạng /images/stationery/uuid_tenfile.jpg, hoặc null nếu không chọn ảnh
     */
    private String saveImage(MultipartFile file, HttpServletRequest request) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String uploadDir = request.getServletContext().getRealPath("/resources/images/stationery");
        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir); // tạo thư mục nếu chưa có
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename()
                .replaceAll("[^a-zA-Z0-9._-]", "_");
        Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        return "/images/stationery/" + filename;
    }

    // ── UC-17.1: Danh sách ────────────────────────────────────────────────────

    /**
     * Hiển thị danh sách mặt hàng, hỗ trợ lọc theo keyword và category.
     * Khi không có tham số, trả về toàn bộ danh sách (search() xử lý null).
     */
    @GetMapping
    public String listStationery(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            Model model, HttpServletRequest request) {

        model.addAttribute("items",      stationeryService.search(keyword, category));
        model.addAttribute("categories", categoryService.getAll()); // cho dropdown lọc
        model.addAttribute("keyword",    keyword);   // giữ lại giá trị tìm kiếm trên form
        model.addAttribute("category",   category);
        addUserToModel(model, request);
        return "stationery/list";
    }

    // ── UC-17.2: Chi tiết ─────────────────────────────────────────────────────

    /** Hiển thị chi tiết mặt hàng. Redirect về danh sách nếu id không tồn tại hoặc đã xóa. */
    @GetMapping("/{id}")
    public String viewDetail(@PathVariable Long id, Model model,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {

        Optional<StationeryItemDTO> item = stationeryService.getById(id);
        if (item.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Item not found or has been deleted.");
            return "redirect:/stationery";
        }
        model.addAttribute("item", item.get());
        addUserToModel(model, request);
        return "stationery/detail";
    }

    // ── UC-17.3: Tạo mới ─────────────────────────────────────────────────────

    /** Hiển thị form tạo mới với một DTO rỗng và danh sách danh mục cho dropdown. */
    @GetMapping("/create")
    public String showCreateForm(Model model, HttpServletRequest request) {
        model.addAttribute("stationeryItem", new StationeryItemDTO());
        addCategoriesToModel(model);
        addUserToModel(model, request);
        return "stationery/create";
    }

    /**
     * Xử lý form tạo mới.
     * Thứ tự kiểm tra:
     *   1. Validation annotation (@NotBlank, @DecimalMin...) trên DTO
     *   2. Kiểm tra tên trùng (duplicate check) trong DB
     *   3. Lưu ảnh nếu có upload
     *   4. Gọi service tạo entity và log
     */
    @PostMapping("/create")
    public String createStationery(
            @Valid @ModelAttribute("stationeryItem") StationeryItemDTO itemDTO,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        fixTypeMismatchErrors(bindingResult);

        // Kiểm tra trùng tên chỉ khi trường name không có lỗi validation khác
        if (!bindingResult.hasFieldErrors("name") &&
                stationeryService.isDuplicateName(itemDTO.getName())) {
            bindingResult.rejectValue("name", "duplicate",
                "An item with this name already exists.");
        }

        if (bindingResult.hasErrors()) {
            addCategoriesToModel(model); // cần load lại danh mục khi re-render form
            addUserToModel(model, request);
            return "stationery/create";
        }

        User currentUser = getCurrentUser(request);
        if (currentUser == null) return "redirect:/login";

        try {
            String imagePath = saveImage(imageFile, request);
            if (imagePath != null) itemDTO.setImagePath(imagePath); // gán đường dẫn ảnh vào DTO trước khi truyền xuống service
            stationeryService.create(itemDTO, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                "Item '" + itemDTO.getName() + "' created successfully!");
            return "redirect:/stationery";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            addCategoriesToModel(model);
            addUserToModel(model, request);
            return "stationery/create";
        }
    }

    // ── UC-17.4: Cập nhật ────────────────────────────────────────────────────

    /** Hiển thị form sửa với dữ liệu hiện tại của mặt hàng. */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {

        Optional<StationeryItemDTO> item = stationeryService.getById(id);
        if (item.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Item not found or has been deleted.");
            return "redirect:/stationery";
        }
        model.addAttribute("stationeryItem", item.get());
        addCategoriesToModel(model);
        addUserToModel(model, request);
        return "stationery/edit";
    }

    /**
     * Xử lý form cập nhật mặt hàng.
     * Cần gán lại id vào DTO khi có lỗi để form action URL (/${id}/edit) vẫn đúng.
     */
    @PostMapping("/{id}/edit")
    public String updateStationery(
            @PathVariable Long id,
            @Valid @ModelAttribute("stationeryItem") StationeryItemDTO updatedDTO,
            BindingResult bindingResult,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        fixTypeMismatchErrors(bindingResult);

        // Kiểm tra trùng với mặt hàng KHÁC (loại trừ chính id đang sửa)
        if (!bindingResult.hasFieldErrors("name") &&
                stationeryService.isDuplicateNameForUpdate(updatedDTO.getName(), id)) {
            bindingResult.rejectValue("name", "duplicate",
                "An item with this name already exists.");
        }

        if (bindingResult.hasErrors()) {
            updatedDTO.setId(id); // cần id để form action = /stationery/{id}/edit không bị null
            addCategoriesToModel(model);
            addUserToModel(model, request);
            return "stationery/edit";
        }

        User currentUser = getCurrentUser(request);
        if (currentUser == null) return "redirect:/login";

        try {
            String imagePath = saveImage(imageFile, request);
            if (imagePath != null) updatedDTO.setImagePath(imagePath); // null = giữ nguyên ảnh cũ
            stationeryService.update(id, updatedDTO, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                "Item updated successfully!");
            return "redirect:/stationery/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            updatedDTO.setId(id);
            addCategoriesToModel(model);
            addUserToModel(model, request);
            return "stationery/edit";
        }
    }

    // ── UC-17.5: Xóa mềm ─────────────────────────────────────────────────────

    /**
     * Xóa mềm mặt hàng: đặt cờ deleted=true, bản ghi vẫn còn trong DB.
     * Yêu cầu đăng nhập; nếu chưa đăng nhập → redirect về trang login.
     */
    @PostMapping("/{id}/delete")
    public String deleteStationery(@PathVariable Long id,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser(request);
        if (currentUser == null) return "redirect:/login";

        try {
            stationeryService.delete(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                "Item deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/stationery";
    }
}
