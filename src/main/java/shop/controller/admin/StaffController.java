package shop.controller.admin;

import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.dto.StaffDTO;
import shop.service.EmailService;
import shop.service.UserService;

@Controller
@RequestMapping("/admin/staff")
@PreAuthorize("hasRole('ADMIN')")
public class StaffController {

    private static final int PAGE_SIZE = 10;

    private final UserService userService;
    private final EmailService emailService;

    public StaffController(UserService userService,
                           EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping
    public String viewStaff(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(required = false, defaultValue = "all") String staffRole,
            @RequestParam(required = false, defaultValue = "default") String sort,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Sort springSort;

        switch (sort) {
            case "id_desc":
                springSort = Sort.by("id").descending();
                break;
            case "name_asc":
                springSort = Sort.by("fullName").ascending();
                break;
            case "name_desc":
                springSort = Sort.by("fullName").descending();
                break;
            case "email_asc":
                springSort = Sort.by("email").ascending();
                break;
            case "email_desc":
                springSort = Sort.by("email").descending();
                break;
            default:
                springSort = Sort.by("id").ascending();
        }

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, springSort);

        Page<StaffDTO> staffPage =
                userService.getStaffPage(
                        keyword,
                        status,
                        staffRole,
                        pageable
                );

        model.addAttribute("staffPage", staffPage);
        model.addAttribute("staffList", staffPage.getContent());

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", staffPage.getTotalPages());
        model.addAttribute("totalItems", staffPage.getTotalElements());

        model.addAttribute("totalStaff", userService.countStaff());
        model.addAttribute("activeStaff", userService.countActiveStaff());
        model.addAttribute("inactiveStaff", userService.countInactiveStaff());
        model.addAttribute("deletedStaff", userService.countDeletedStaff());

        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("staffRole", staffRole);
        model.addAttribute("sort", sort);

        return "admin/staff/list";
    }

    @GetMapping("/deleted")
    public String deletedStaff(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "all") String staffRole,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        Page<StaffDTO> deletedPage =
                userService.getDeletedStaffPage(
                        keyword,
                        staffRole,
                        pageable
                );

        model.addAttribute("staffList", deletedPage.getContent());
        model.addAttribute("staffPage", deletedPage);

        model.addAttribute("deletedMode", true);

        return "admin/staff/deleted";
    }

    @PostMapping("/create")
    public String createStaff(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String staffRole,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            RedirectAttributes redirectAttributes) {

        if (userService.checkEmailExist(email)) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Email already exists."
            );

            return "redirect:/admin/staff";
        }

        userService.createStaff(
                fullName,
                email,
                password,
                phone,
                address,
                staffRole
        );

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Staff created successfully."
        );

        return "redirect:/admin/staff";
    }

    @PostMapping("/update")
    public String updateStaff(
            @RequestParam Long staffId,
            @RequestParam String fullName,
            @RequestParam String staffRole,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String newPassword,
            RedirectAttributes redirectAttributes) {

        StaffDTO staff = userService.getStaffDTOById(staffId);

        if (staff == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Staff not found."
            );
            return "redirect:/admin/staff";
        }

        userService.updateStaff(
                staffId,
                fullName,
                phone,
                address,
                staffRole
        );

        if (newPassword != null
                && !newPassword.trim().isEmpty()) {

            userService.updateStaffPassword(
                    staffId,
                    newPassword
            );
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Staff updated successfully."
        );

        return "redirect:/admin/staff";
    }

    @PostMapping("/ban")
    public String banStaff(
            @RequestParam Long staffId,
            RedirectAttributes redirectAttributes) {

        StaffDTO staff =
                userService.getStaffDTOById(staffId);

        if (staff == null) {
            return "redirect:/admin/staff";
        }

        userService.banStaff(staffId);

        try {
            emailService.sendStatusMail(
                    staff.getEmail(),
                    false
            );
        } catch (Exception ignored) {
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Staff banned successfully."
        );

        return "redirect:/admin/staff";
    }

    @PostMapping("/unban")
    public String unbanStaff(
            @RequestParam Long staffId,
            RedirectAttributes redirectAttributes) {

        StaffDTO staff =
                userService.getStaffDTOById(staffId);

        if (staff == null) {
            return "redirect:/admin/staff";
        }

        userService.unbanStaff(staffId);

        try {
            emailService.sendStatusMail(
                    staff.getEmail(),
                    true
            );
        } catch (Exception ignored) {
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Staff unbanned successfully."
        );

        return "redirect:/admin/staff";
    }

    @PostMapping("/delete")
    public String deleteStaff(
            @RequestParam Long staffId,
            RedirectAttributes redirectAttributes) {

        userService.softDeleteStaff(staffId);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Staff moved to recycle bin."
        );

        return "redirect:/admin/staff";
    }

    @PostMapping("/restore")
    public String restoreStaff(
            @RequestParam Long staffId,
            RedirectAttributes redirectAttributes) {

        userService.restoreStaff(staffId);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Staff restored successfully."
        );

        return "redirect:/admin/staff/deleted";
    }
}