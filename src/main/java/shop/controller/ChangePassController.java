package shop.controller;

import shop.domain.PasswordChangeForm;
import shop.domain.User;
import shop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ChangePassController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public ChangePassController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/changepass")
    public String showChangePassForm(Model model) {
        model.addAttribute("passwordChangeForm", new PasswordChangeForm());
        return "authentication/changepass";
    }

    @PostMapping("/changepass")
    public String changePassword(
            @Valid PasswordChangeForm passwordChangeForm,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "All fields are required.");
            return "authentication/changepass";
        }

        HttpSession session = request.getSession(false);
        String email = session != null ? (String) session.getAttribute("email") : null;

        if (email == null || email.isEmpty()) {
            model.addAttribute("errorMessage", "Session has expired or user is not logged in.");
            return "authentication/changepass";
        }

        User user = userService.getUserByEmail(email);
        if (user == null) {
            model.addAttribute("errorMessage", "User not found.");
            return "authentication/changepass";
        }

        if (!passwordEncoder.matches(passwordChangeForm.getCurrentPassword(), user.getPassword())) {
            model.addAttribute("errorMessage", "Current password is incorrect.");
            return "authentication/changepass";
        }

        if (!passwordChangeForm.getNewPassword().equals(passwordChangeForm.getConfirmPassword())) {
            model.addAttribute("errorMessage", "New password and confirmation password do not match.");
            return "authentication/changepass";
        }

        userService.updatePassword(user.getEmail(), passwordChangeForm.getNewPassword());
        model.addAttribute("successMessage", "Password has been successfully changed.");

        return "authentication/changepass";
    }
}
