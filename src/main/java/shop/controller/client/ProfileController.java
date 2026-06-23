package shop.controller.client;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import shop.domain.PasswordChangeForm;
import shop.domain.ProfileUpdateForm;
import shop.domain.User;
import shop.service.UserService;

@Controller
public class ProfileController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile")
    public String getProfile(HttpServletRequest request, Model model) {
        User user = getCurrentUser(request);
        if (user == null) {
            return "redirect:/login";
        }

        populateProfileModel(model, user, request.getSession(false));
        if (!model.containsAttribute("profileUpdateForm")) {
            model.addAttribute("profileUpdateForm", toProfileForm(user));
        }
        if (!model.containsAttribute("passwordChangeForm")) {
            model.addAttribute("passwordChangeForm", new PasswordChangeForm());
        }

        return "profile/index";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute("profileUpdateForm") ProfileUpdateForm profileUpdateForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(request);
        if (user == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            populateProfileModel(model, user, request.getSession(false));
            model.addAttribute("passwordChangeForm", new PasswordChangeForm());
            return "profile/index";
        }

        User updated = userService.updateProfile(
                user.getEmail(),
                profileUpdateForm.getFullName(),
                profileUpdateForm.getPhone(),
                profileUpdateForm.getAddress());

        if (updated != null) {
            syncSession(request.getSession(false), updated);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công.");
        return "redirect:/profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(
            @Valid @ModelAttribute("passwordChangeForm") PasswordChangeForm passwordChangeForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(request);
        if (user == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            populateProfileModel(model, user, request.getSession(false));
            model.addAttribute("profileUpdateForm", toProfileForm(user));
            model.addAttribute("passwordError", true);
            return "profile/index";
        }

        if (!passwordEncoder.matches(passwordChangeForm.getCurrentPassword(), user.getPassword())) {
            populateProfileModel(model, user, request.getSession(false));
            model.addAttribute("profileUpdateForm", toProfileForm(user));
            model.addAttribute("passwordErrorMessage", "Mật khẩu hiện tại không đúng.");
            model.addAttribute("passwordError", true);
            return "profile/index";
        }

        if (!passwordChangeForm.getNewPassword().equals(passwordChangeForm.getConfirmPassword())) {
            populateProfileModel(model, user, request.getSession(false));
            model.addAttribute("profileUpdateForm", toProfileForm(user));
            model.addAttribute("passwordErrorMessage", "Mật khẩu mới và xác nhận không khớp.");
            model.addAttribute("passwordError", true);
            return "profile/index";
        }

        userService.updatePassword(user.getEmail(), passwordChangeForm.getNewPassword());
        redirectAttributes.addFlashAttribute("passwordSuccessMessage", "Đổi mật khẩu thành công.");
        return "redirect:/profile#password-section";
    }

    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            return null;
        }
        return userService.getUserByEmail((String) session.getAttribute("email"));
    }

    private void populateProfileModel(Model model, User user, HttpSession session) {
        model.addAttribute("user", user);
        if (session != null) {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("fullName", session.getAttribute("fullName"));
            model.addAttribute("role", session.getAttribute("role"));
        }
    }

    private ProfileUpdateForm toProfileForm(User user) {
        ProfileUpdateForm form = new ProfileUpdateForm();
        form.setFullName(user.getFullName());
        form.setPhone(user.getPhone());
        form.setAddress(user.getAddress());
        return form;
    }

    private void syncSession(HttpSession session, User user) {
        if (session == null) {
            return;
        }
        session.setAttribute("fullName", user.getFullName());
        session.setAttribute("avatar", user.getAvatar());
    }
}
