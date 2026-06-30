package shop.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import shop.domain.Author;
import shop.domain.dto.AuthorDTO;
import shop.service.AuthorService;

@Controller
@RequestMapping("/admin/authors")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuthorController {

    // Inject service để xử lý business logic (create, update, check duplicate,...)
    private final AuthorService authorService;

    public AdminAuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // ================= LIST =================
    @GetMapping
    public String list(Model model) {
        // Lấy toàn bộ author từ DB thông qua service
        model.addAttribute("authors", authorService.getAllAuthors());
        return "admin/author/list";
    }

    // ================= CREATE FORM =================
    @GetMapping("/create")
    public String createForm(Model model) {

        // Tạo object rỗng để form binding dữ liệu từ JSP
        model.addAttribute("authorDTO", new AuthorDTO());
        // dùng để JSP biết đang ở mode create hay edit
        model.addAttribute("formMode", "create");
        return "admin/author/form";
    }

    // ================= CREATE ACTION =================
    @PostMapping
    public String create(
            @Valid @ModelAttribute("authorDTO") AuthorDTO authorDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // check duplicate
        if (authorService.existsByAuthorNameIgnoreCase(authorDTO.getAuthorName())) {
            bindingResult.rejectValue(
                    "authorName",
                    "author.exists",
                    "Author name already exists.");
        }
        // Nếu có lỗi validate → quay lại form
        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            return "admin/author/form";
        }

        // Gọi service để lưu dữ liệu xuống DB
        authorService.saveAuthor(authorDTO);

        // Flash message hiển thị 1 lần sau redirect
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Create Author Successfully");

        // redict trả về list
        return "redirect:/admin/authors";
    }

    // ================= EDIT FORM =================
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {

        // lấy author theo id
        Author author = authorService.getAuthorById(id);

        // Convert entity sang DTO đưa lên form
        AuthorDTO dto = new AuthorDTO();
        dto.setAuthorName(author.getAuthorName());
        dto.setBiography(author.getBiography());
        dto.setNationality(author.getNationality());
        dto.setProfileImage(author.getProfileImage());

        // gửi data ra form.jsp
        model.addAttribute("authorDTO", dto);
        model.addAttribute("formMode", "edit");
        model.addAttribute("authorId", id);

        return "admin/author/form";
    }

    // ================= UPDATE ACTION =================
    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("authorDTO") AuthorDTO authorDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // check duplicate (exclude itself)
        if (authorService.existsByAuthorNameIgnoreCaseAndIdNot(
                authorDTO.getAuthorName(), id)) {

            bindingResult.rejectValue(
                    "authorName",
                    "author.exists",
                    "Author name already exists.");
        }

        // Nếu lỗi → quay lại form edit
        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "edit");
            model.addAttribute("authorId", id);
            return "admin/author/form";
        }

        // Gọi service update dữ liệu
        authorService.updateAuthor(id, authorDTO);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Update Author Successfully");

        // quay về list
        return "redirect:/admin/authors";
    }

    // ================= DELETE =================
    @PostMapping("/{id}/delete")
    public String deleteAuthor(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        authorService.deleteAuthor(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Delete Author Successfully");

        return "redirect:/admin/authors";
    }
}