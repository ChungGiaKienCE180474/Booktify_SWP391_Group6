package shop.controller.client;

import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import shop.domain.PasswordChangeForm;
import shop.domain.ProfileUpdateForm;
import shop.domain.User;
import shop.domain.dto.ProfileDTO;
import shop.service.EmailService;
import shop.service.UserService;

@Controller
public class ProfileController {

    private static final String SESSION_PROFILE_OTP = "profilePasswordOtp";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public ProfileController(UserService userService, PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @GetMapping("/profile")
    public String getProfile(
            HttpServletRequest request,
            Model model,
            @RequestParam(value = "edit", required = false) String edit,
            @RequestParam(value = "password", required = false) String password) {

        ProfileDTO profile = getCurrentProfile(request);
        if (profile == null) {
            return "redirect:/login";
        }

        populateProfileModel(model, profile, request.getSession(false));

        boolean editMode = "true".equals(edit) || "1".equals(edit)
                || Boolean.TRUE.equals(model.getAttribute("editMode"));
        boolean passwordEditMode = "edit".equals(password)
                || Boolean.TRUE.equals(model.getAttribute("passwordEditMode"));

        model.addAttribute("editMode", editMode);
        model.addAttribute("passwordEditMode", passwordEditMode);

        if (!model.containsAttribute("profileUpdateForm")) {
            model.addAttribute("profileUpdateForm", toProfileForm(profile));
        }
        if (!model.containsAttribute("passwordChangeForm")) {
            model.addAttribute("passwordChangeForm", new PasswordChangeForm());
        }

        HttpSession session = request.getSession(false);
        model.addAttribute("otpSent", session != null && session.getAttribute(SESSION_PROFILE_OTP) != null);

        return "profile/index";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @Valid @ModelAttribute("profileUpdateForm") ProfileUpdateForm profileUpdateForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        ProfileDTO profile = getCurrentProfile(request);
        if (profile == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            populateProfileModel(model, profile, request.getSession(false));
            model.addAttribute("editMode", true);
            model.addAttribute("passwordChangeForm", new PasswordChangeForm());
            model.addAttribute("otpSent", hasOtpInSession(request.getSession(false)));
            return "profile/index";
        }

        User updated = userService.updateProfile(
                profile.getEmail(),
                profileUpdateForm.getFullName(),
                profileUpdateForm.getPhone(),
                profileUpdateForm.getAddress());

        if (updated != null) {
            syncSession(request.getSession(false), updated);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công.");
        return "redirect:/profile";
    }

    @PostMapping("/profile/password/send-otp")
    public String sendPasswordOtp(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ProfileDTO profile = getCurrentProfile(request);
        if (profile == null) {
            return "redirect:/login";
        }

        int otpValue = new Random().nextInt(900000) + 100000;
        HttpSession session = request.getSession();
        session.setAttribute(SESSION_PROFILE_OTP, otpValue);

        try {
            emailService.sendOtpEmail(
                    profile.getEmail(),
                    "Booktify - Mã OTP đổi mật khẩu",
                    "Mã OTP đổi mật khẩu của bạn là: " + otpValue
                            + "\n\nVui lòng không chia sẻ mã này với bất kỳ ai.");
        } catch (MessagingException e) {
            session.removeAttribute(SESSION_PROFILE_OTP);
            redirectAttributes.addFlashAttribute("passwordErrorMessage",
                    "Không gửi được OTP. Vui lòng thử lại sau.");
            redirectAttributes.addFlashAttribute("passwordEditMode", true);
            return "redirect:/profile?password=edit";
        }

        redirectAttributes.addFlashAttribute("otpSentMessage",
                "Mã OTP đã được gửi tới email " + profile.getEmail() + ".");
        redirectAttributes.addFlashAttribute("passwordEditMode", true);
        return "redirect:/profile?password=edit";
    }

    @PostMapping("/profile/password")
    public String changePassword(
            @Valid @ModelAttribute("passwordChangeForm") PasswordChangeForm passwordChangeForm,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUserEntity(request);
        ProfileDTO profile = user != null ? userService.toProfileDTO(user) : null;
        if (user == null || profile == null) {
            return "redirect:/login";
        }

        HttpSession session = request.getSession(false);
        Integer storedOtp = session != null ? (Integer) session.getAttribute(SESSION_PROFILE_OTP) : null;

        if (storedOtp == null) {
            redirectAttributes.addFlashAttribute("passwordErrorMessage",
                    "Vui lòng gửi mã OTP trước khi đổi mật khẩu.");
            redirectAttributes.addFlashAttribute("passwordEditMode", true);
            return "redirect:/profile?password=edit";
        }

        if (bindingResult.hasErrors()) {
            populateProfileModel(model, profile, session);
            model.addAttribute("profileUpdateForm", toProfileForm(profile));
            model.addAttribute("passwordEditMode", true);
            model.addAttribute("otpSent", true);
            model.addAttribute("passwordError", true);
            return "profile/index";
        }

        if (!storedOtp.equals(passwordChangeForm.getOtp())) {
            populateProfileModel(model, profile, session);
            model.addAttribute("profileUpdateForm", toProfileForm(profile));
            model.addAttribute("passwordEditMode", true);
            model.addAttribute("otpSent", true);
            model.addAttribute("passwordErrorMessage", "Mã OTP không đúng.");
            model.addAttribute("passwordError", true);
            return "profile/index";
        }

        if (!passwordEncoder.matches(passwordChangeForm.getCurrentPassword(), user.getPassword())) {
            populateProfileModel(model, profile, session);
            model.addAttribute("profileUpdateForm", toProfileForm(profile));
            model.addAttribute("passwordEditMode", true);
            model.addAttribute("otpSent", true);
            model.addAttribute("passwordErrorMessage", "Mật khẩu hiện tại không đúng.");
            model.addAttribute("passwordError", true);
            return "profile/index";
        }

        if (!passwordChangeForm.getNewPassword().equals(passwordChangeForm.getConfirmPassword())) {
            populateProfileModel(model, profile, session);
            model.addAttribute("profileUpdateForm", toProfileForm(profile));
            model.addAttribute("passwordEditMode", true);
            model.addAttribute("otpSent", true);
            model.addAttribute("passwordErrorMessage", "Mật khẩu mới và xác nhận không khớp.");
            model.addAttribute("passwordError", true);
            return "profile/index";
        }

        userService.updatePassword(user.getEmail(), passwordChangeForm.getNewPassword());
        if (session != null) {
            session.removeAttribute(SESSION_PROFILE_OTP);
        }

        redirectAttributes.addFlashAttribute("passwordSuccessMessage", "Đổi mật khẩu thành công.");
        return "redirect:/profile";
    }

    private ProfileDTO getCurrentProfile(HttpServletRequest request) {
        User user = getCurrentUserEntity(request);
        return user != null ? userService.toProfileDTO(user) : null;
    }

    private User getCurrentUserEntity(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            return null;
        }
        return userService.getUserByEmail((String) session.getAttribute("email"));
    }

    private boolean hasOtpInSession(HttpSession session) {
        return session != null && session.getAttribute(SESSION_PROFILE_OTP) != null;
    }

    private void populateProfileModel(Model model, ProfileDTO profile, HttpSession session) {
        model.addAttribute("profile", profile);
        if (session != null) {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("fullName", session.getAttribute("fullName"));
            model.addAttribute("role", session.getAttribute("role"));
        }
    }

    private ProfileUpdateForm toProfileForm(ProfileDTO profile) {
        ProfileUpdateForm form = new ProfileUpdateForm();
        form.setFullName(profile.getFullName());
        form.setPhone(profile.getPhone());
        form.setAddress(profile.getAddress());
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
