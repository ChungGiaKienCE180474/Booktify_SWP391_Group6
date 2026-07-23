package shop.controller.client;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import shop.domain.PasswordChangeForm;
import shop.domain.ProfileUpdateForm;
import shop.domain.User;
import shop.domain.dto.ProfileDTO;
import shop.service.UserService;

@Controller
public class ProfileController {

    private static final String ADMIN_PROFILE_BASE = "/admin/profile";
    private static final String CUSTOMER_PROFILE_BASE = "/profile";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping({ "/profile", "/admin/profile" })
    public String getProfile(
            HttpServletRequest request,
            Model model,
            @RequestParam(value = "edit", required = false) String edit,
            @RequestParam(value = "password", required = false) String password) {

        ProfileDTO profile = getCurrentProfile(request);
        if (profile == null) {
            return "redirect:/login";
        }

        String profileBase = resolveProfileBase(request);
        populateProfileModel(model, profile, request.getSession(false), profileBase);

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

        return resolveProfileView(request);
    }

    @PostMapping({ "/profile/update", "/admin/profile/update" })
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

        String profileBase = resolveProfileBase(request);

        if (bindingResult.hasErrors()) {
            populateProfileModel(model, profile, request.getSession(false), profileBase);
            model.addAttribute("editMode", true);
            model.addAttribute("passwordChangeForm", new PasswordChangeForm());
            return resolveProfileView(request);
        }

        User updated = userService.updateProfile(
                profile.getEmail(),
                profileUpdateForm.getFullName(),
                profileUpdateForm.getPhone(),
                profileUpdateForm.getAddress());

        if (updated != null) {
            syncSession(request.getSession(false), updated);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Your information has been updated.");
        return "redirect:" + profileBase;
    }

    @PostMapping({ "/profile/password", "/admin/profile/password" })
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

        String profileBase = resolveProfileBase(request);
        HttpSession session = request.getSession(false);

        if (bindingResult.hasErrors()) {
            populateProfileModel(model, profile, session, profileBase);
            model.addAttribute("profileUpdateForm", toProfileForm(profile));
            model.addAttribute("passwordEditMode", true);
            model.addAttribute("passwordError", true);
            return resolveProfileView(request);
        }

        boolean googleAccount = user.isGoogleAccount();
        String currentPassword = passwordChangeForm.getCurrentPassword();

        if (!googleAccount) {
            if (currentPassword == null || currentPassword.isBlank()) {
                populateProfileModel(model, profile, session, profileBase);
                model.addAttribute("profileUpdateForm", toProfileForm(profile));
                model.addAttribute("passwordEditMode", true);
                model.addAttribute("passwordErrorMessage", "Please enter your current password.");
                model.addAttribute("passwordError", true);
                return resolveProfileView(request);
            }
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                populateProfileModel(model, profile, session, profileBase);
                model.addAttribute("profileUpdateForm", toProfileForm(profile));
                model.addAttribute("passwordEditMode", true);
                model.addAttribute("passwordErrorMessage", "Your current password is incorrect.");
                model.addAttribute("passwordError", true);
                return resolveProfileView(request);
            }
        }

        if (!passwordChangeForm.getNewPassword().equals(passwordChangeForm.getConfirmPassword())) {
            populateProfileModel(model, profile, session, profileBase);
            model.addAttribute("profileUpdateForm", toProfileForm(profile));
            model.addAttribute("passwordEditMode", true);
            model.addAttribute("passwordErrorMessage", "The new password and confirmation do not match.");
            model.addAttribute("passwordError", true);
            return resolveProfileView(request);
        }

        userService.updatePassword(user.getEmail(), passwordChangeForm.getNewPassword());

        String successMessage = googleAccount
                ? "Password set successfully. You can now log in with your email and password."
                : "Password changed successfully.";
        redirectAttributes.addFlashAttribute("passwordSuccessMessage", successMessage);
        return "redirect:" + profileBase;
    }

    private String resolveProfileBase(HttpServletRequest request) {
        return isAdminProfileRequest(request) ? ADMIN_PROFILE_BASE : CUSTOMER_PROFILE_BASE;
    }

    private String resolveProfileView(HttpServletRequest request) {
        return isAdminProfileRequest(request) ? "admin/profile/index" : "profile/index";
    }

    private boolean isAdminProfileRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.startsWith("/admin/profile");
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

    private void populateProfileModel(Model model, ProfileDTO profile, HttpSession session, String profileBase) {
        model.addAttribute("profile", profile);
        model.addAttribute("profileBase", profileBase);
        model.addAttribute("adminProfile", ADMIN_PROFILE_BASE.equals(profileBase));
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
