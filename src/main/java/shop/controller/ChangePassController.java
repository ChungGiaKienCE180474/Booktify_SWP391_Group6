package shop.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import shop.domain.PasswordChangeForm;
import shop.domain.User;
import shop.service.UserService;

@Controller
public class ChangePassController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public ChangePassController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/changepass")
    public String showChangePassForm() {
        return "redirect:/profile#password-section";
    }

    @PostMapping("/changepass")
    public String changePassword(
            @Valid PasswordChangeForm passwordChangeForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        HttpSession session = request.getSession(false);
        String email = session != null ? (String) session.getAttribute("email") : null;

        if (email == null || email.isBlank()) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("passwordErrorMessage", "Vui lòng điền đầy đủ thông tin.");
            return "redirect:/profile#password-section";
        }

        User user = userService.getUserByEmail(email);
        if (user == null) {
            return "redirect:/login";
        }

        if (!passwordEncoder.matches(passwordChangeForm.getCurrentPassword(), user.getPassword())) {
            redirectAttributes.addFlashAttribute("passwordErrorMessage", "Mật khẩu hiện tại không đúng.");
            return "redirect:/profile#password-section";
        }

        if (!passwordChangeForm.getNewPassword().equals(passwordChangeForm.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("passwordErrorMessage", "Mật khẩu mới và xác nhận không khớp.");
            return "redirect:/profile#password-section";
        }

        userService.updatePassword(user.getEmail(), passwordChangeForm.getNewPassword());
        redirectAttributes.addFlashAttribute("passwordSuccessMessage", "Đổi mật khẩu thành công.");
        return "redirect:/profile#password-section";
    }
}
