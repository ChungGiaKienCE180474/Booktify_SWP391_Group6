// Package controller phía client — quản lý trang hồ sơ cá nhân
package shop.controller.client;

// PasswordEncoder: mã hóa và so khớp mật khẩu (BCrypt)
import org.springframework.security.crypto.password.PasswordEncoder;
// @Controller: class xử lý request HTTP và trả view
import org.springframework.stereotype.Controller;
// Model: truyền dữ liệu sang template Thymeleaf
import org.springframework.ui.Model;
// BindingResult: chứa lỗi validate form
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
// RedirectAttributes: truyền flash message sau redirect (chỉ sống 1 lần)
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
// Form đổi mật khẩu (currentPassword, newPassword, confirmPassword)
import shop.domain.PasswordChangeForm;
// Form cập nhật thông tin (fullName, phone, address)
import shop.domain.ProfileUpdateForm;
// Entity User trong database
import shop.domain.User;
// DTO hiển thị profile (không chứa password)
import shop.domain.dto.ProfileDTO;
import shop.service.UserService;

@Controller
public class ProfileController {

    // URL gốc trang profile của admin
    private static final String ADMIN_PROFILE_BASE = "/admin/profile";
    // URL gốc trang profile của customer
    private static final String CUSTOMER_PROFILE_BASE = "/profile";

    // Service xử lý logic user (cập nhật profile, đổi mật khẩu...)
    private final UserService userService;
    // Encoder để verify mật khẩu hiện tại và hash mật khẩu mới
    private final PasswordEncoder passwordEncoder;

    // Spring inject UserService và PasswordEncoder qua constructor
    public ProfileController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // GET /profile hoặc GET /admin/profile — hiển thị trang hồ sơ
    @GetMapping({ "/profile", "/admin/profile" })
    public String getProfile(
            HttpServletRequest request,
            Model model,
            // ?edit=true hoặc ?edit=1 → bật chế độ sửa thông tin
            @RequestParam(value = "edit", required = false) String edit,
            // ?password=edit → bật form đổi mật khẩu
            @RequestParam(value = "password", required = false) String password) {

        // Lấy thông tin profile user đang đăng nhập từ session
        ProfileDTO profile = getCurrentProfile(request);
        // Chưa đăng nhập → chuyển về trang login
        if (profile == null) {
            return "redirect:/login";
        }

        // Xác định URL base theo role (admin hay customer)
        String profileBase = resolveProfileBase(request);
        // Đưa profile, username, role... vào model cho view
        populateProfileModel(model, profile, request.getSession(false), profileBase);

        // Bật chế độ edit nếu query edit=true/1 hoặc model đã có editMode (sau lỗi validate)
        boolean editMode = "true".equals(edit) || "1".equals(edit)
                || Boolean.TRUE.equals(model.getAttribute("editMode"));
        // Bật form đổi mật khẩu nếu ?password=edit hoặc model đã có passwordEditMode
        boolean passwordEditMode = "edit".equals(password)
                || Boolean.TRUE.equals(model.getAttribute("passwordEditMode"));

        model.addAttribute("editMode", editMode);
        model.addAttribute("passwordEditMode", passwordEditMode);

        // Nếu chưa có form cập nhật (lần đầu load trang) → tạo từ dữ liệu profile hiện tại
        if (!model.containsAttribute("profileUpdateForm")) {
            model.addAttribute("profileUpdateForm", toProfileForm(profile));
        }
        // Form đổi mật khẩu rỗng cho lần đầu mở trang
        if (!model.containsAttribute("passwordChangeForm")) {
            model.addAttribute("passwordChangeForm", new PasswordChangeForm());
        }

        // Trả view admin/profile/index hoặc profile/index tùy URL
        return resolveProfileView(request);
    }

    // POST /profile/update hoặc POST /admin/profile/update — cập nhật thông tin cá nhân
    @PostMapping({ "/profile/update", "/admin/profile/update" })
    public String updateProfile(
            // Bind và validate form cập nhật profile
            @Valid @ModelAttribute("profileUpdateForm") ProfileUpdateForm profileUpdateForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        ProfileDTO profile = getCurrentProfile(request);
        if (profile == null) {
            return "redirect:/login";
        }

        String profileBase = resolveProfileBase(request);

        // Validate thất bại → hiển thị lại trang với lỗi và bật editMode
        if (bindingResult.hasErrors()) {
            populateProfileModel(model, profile, request.getSession(false), profileBase);
            model.addAttribute("editMode", true);
            model.addAttribute("passwordChangeForm", new PasswordChangeForm());
            return resolveProfileView(request);
        }

        // Gọi service cập nhật fullName, phone, address theo email user
        User updated = userService.updateProfile(
                profile.getEmail(),
                profileUpdateForm.getFullName(),
                profileUpdateForm.getPhone(),
                profileUpdateForm.getAddress());

        // Cập nhật session (fullName, avatar) để header/navbar hiển thị đúng ngay
        if (updated != null) {
            syncSession(request.getSession(false), updated);
        }

        // Flash message thành công, chỉ hiện 1 lần sau redirect
        redirectAttributes.addFlashAttribute("successMessage", "Your information has been updated.");
        return "redirect:" + profileBase;
    }

    // POST /profile/password hoặc POST /admin/profile/password — đổi hoặc đặt mật khẩu
    @PostMapping({ "/profile/password", "/admin/profile/password" })
    public String changePassword(
            @Valid @ModelAttribute("passwordChangeForm") PasswordChangeForm passwordChangeForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Lấy entity User đầy đủ (có password hash) từ session
        User user = getCurrentUserEntity(request);
        ProfileDTO profile = user != null ? userService.toProfileDTO(user) : null;
        if (user == null || profile == null) {
            return "redirect:/login";
        }

        String profileBase = resolveProfileBase(request);
        HttpSession session = request.getSession(false);

        // Lỗi validate (@NotBlank, @Size trên PasswordChangeForm)
        if (bindingResult.hasErrors()) {
            populateProfileModel(model, profile, session, profileBase);
            model.addAttribute("profileUpdateForm", toProfileForm(profile));
            model.addAttribute("passwordEditMode", true);
            model.addAttribute("passwordError", true);
            return resolveProfileView(request);
        }

        // Tài khoản Google OAuth có thể chưa có mật khẩu local
        boolean googleAccount = user.isGoogleAccount();
        String currentPassword = passwordChangeForm.getCurrentPassword();

        // Tài khoản thường (không phải Google) bắt buộc nhập và verify mật khẩu hiện tại
        if (!googleAccount) {
            if (currentPassword == null || currentPassword.isBlank()) {
                populateProfileModel(model, profile, session, profileBase);
                model.addAttribute("profileUpdateForm", toProfileForm(profile));
                model.addAttribute("passwordEditMode", true);
                model.addAttribute("passwordErrorMessage", "Please enter your current password.");
                model.addAttribute("passwordError", true);
                return resolveProfileView(request);
            }
            // passwordEncoder.matches: so sánh plain text với hash BCrypt trong DB
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                populateProfileModel(model, profile, session, profileBase);
                model.addAttribute("profileUpdateForm", toProfileForm(profile));
                model.addAttribute("passwordEditMode", true);
                model.addAttribute("passwordErrorMessage", "Your current password is incorrect.");
                model.addAttribute("passwordError", true);
                return resolveProfileView(request);
            }
        }

        // Kiểm tra mật khẩu mới và xác nhận có khớp không
        if (!passwordChangeForm.getNewPassword().equals(passwordChangeForm.getConfirmPassword())) {
            populateProfileModel(model, profile, session, profileBase);
            model.addAttribute("profileUpdateForm", toProfileForm(profile));
            model.addAttribute("passwordEditMode", true);
            model.addAttribute("passwordErrorMessage", "The new password and confirmation do not match.");
            model.addAttribute("passwordError", true);
            return resolveProfileView(request);
        }

        // Hash và lưu mật khẩu mới vào database
        userService.updatePassword(user.getEmail(), passwordChangeForm.getNewPassword());

        // Thông báo khác nhau: Google account "đặt" mật khẩu lần đầu vs đổi mật khẩu thường
        String successMessage = googleAccount
                ? "Password set successfully. You can now log in with your email and password."
                : "Password changed successfully.";
        redirectAttributes.addFlashAttribute("passwordSuccessMessage", successMessage);
        return "redirect:" + profileBase;
    }

    // Trả URL base profile: /admin/profile nếu request từ admin, ngược lại /profile
    private String resolveProfileBase(HttpServletRequest request) {
        return isAdminProfileRequest(request) ? ADMIN_PROFILE_BASE : CUSTOMER_PROFILE_BASE;
    }

    // Trả tên template view tương ứng admin hoặc customer
    private String resolveProfileView(HttpServletRequest request) {
        return isAdminProfileRequest(request) ? "admin/profile/index" : "profile/index";
    }

    // Kiểm tra URI có bắt đầu bằng /admin/profile không
    private boolean isAdminProfileRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.startsWith("/admin/profile");
    }

    // Lấy ProfileDTO của user đang đăng nhập; null nếu chưa login
    private ProfileDTO getCurrentProfile(HttpServletRequest request) {
        User user = getCurrentUserEntity(request);
        return user != null ? userService.toProfileDTO(user) : null;
    }

    // Lấy User entity từ email lưu trong session (key "email")
    private User getCurrentUserEntity(HttpServletRequest request) {
        // getSession(false): không tạo session mới nếu chưa có
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            return null;
        }
        return userService.getUserByEmail((String) session.getAttribute("email"));
    }

    // Đưa dữ liệu chung vào model cho trang profile
    private void populateProfileModel(Model model, ProfileDTO profile, HttpSession session, String profileBase) {
        model.addAttribute("profile", profile);
        model.addAttribute("profileBase", profileBase);
        // Flag để template biết đang ở profile admin hay customer
        model.addAttribute("adminProfile", ADMIN_PROFILE_BASE.equals(profileBase));
        if (session != null) {
            // Dữ liệu session dùng cho navbar/header
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("fullName", session.getAttribute("fullName"));
            model.addAttribute("role", session.getAttribute("role"));
        }
    }

    // Chuyển ProfileDTO → ProfileUpdateForm để pre-fill form sửa thông tin
    private ProfileUpdateForm toProfileForm(ProfileDTO profile) {
        ProfileUpdateForm form = new ProfileUpdateForm();
        form.setFullName(profile.getFullName());
        form.setPhone(profile.getPhone());
        form.setAddress(profile.getAddress());
        return form;
    }

    // Đồng bộ session sau khi cập nhật profile (tên hiển thị, avatar)
    private void syncSession(HttpSession session, User user) {
        if (session == null) {
            return;
        }
        session.setAttribute("fullName", user.getFullName());
        session.setAttribute("avatar", user.getAvatar());
    }
}
