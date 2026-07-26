package shop.controller.admin;

import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.dto.StaffCreateDTO;
import shop.domain.dto.StaffDTO;
import shop.service.EmailService;
import shop.service.UserService;
import shop.service.UserSessionService;

@Controller
@RequestMapping("/admin/staff")
@PreAuthorize("hasRole('ADMIN')")
public class StaffController {

    private static final int PAGE_SIZE = 10;

    private static final int MAX_NAME_LENGTH = 150;
    private static final int MAX_ADDRESS_LENGTH = 500;

    private static final String PHONE_PATTERN =
            "^(0[35789])[0-9]{8}$";

    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,72}$";

    private final UserService userService;
    private final EmailService emailService;
    private final UserSessionService userSessionService;

    public StaffController(
            UserService userService,
            EmailService emailService,
            UserSessionService userSessionService) {

        this.userService = userService;
        this.emailService = emailService;
        this.userSessionService = userSessionService;
    }

    // =========================================================
    // STAFF LIST
    // =========================================================

    @GetMapping
    public String viewStaff(
            @RequestParam(required = false)
            String keyword,

            @RequestParam(
                    required = false,
                    defaultValue = "all"
            )
            String status,

            @RequestParam(
                    required = false,
                    defaultValue = "default"
            )
            String sort,

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            Model model) {

        if (page < 0) {
            page = 0;
        }

        Sort springSort =
                resolveSort(sort);

        Pageable pageable =
                PageRequest.of(
                        page,
                        PAGE_SIZE,
                        springSort
                );

        Page<StaffDTO> staffPage =
                userService.getStaffPage(
                        keyword,
                        status,
                        pageable
                );

        /*
         * Nếu người dùng truy cập page lớn hơn
         * tổng số trang thì đưa về trang cuối.
         */
        if (page >= staffPage.getTotalPages()
                && staffPage.getTotalPages() > 0) {

            page =
                    staffPage.getTotalPages() - 1;

            pageable =
                    PageRequest.of(
                            page,
                            PAGE_SIZE,
                            springSort
                    );

            staffPage =
                    userService.getStaffPage(
                            keyword,
                            status,
                            pageable
                    );
        }

        model.addAttribute(
                "staffPage",
                staffPage
        );

        model.addAttribute(
                "staffList",
                staffPage.getContent()
        );

        model.addAttribute(
                "currentPage",
                page
        );

        model.addAttribute(
                "totalPages",
                staffPage.getTotalPages()
        );

        model.addAttribute(
                "totalItems",
                staffPage.getTotalElements()
        );

        model.addAttribute(
                "totalStaff",
                userService.countStaff()
        );

        model.addAttribute(
                "activeStaff",
                userService.countActiveStaff()
        );

        model.addAttribute(
                "inactiveStaff",
                userService.countInactiveStaff()
        );

        model.addAttribute(
                "deletedStaff",
                userService.countDeletedStaff()
        );

        model.addAttribute(
                "keyword",
                keyword
        );

        model.addAttribute(
                "status",
                status
        );

        model.addAttribute(
                "sort",
                sort
        );

        /*
         * Dữ liệu mặc định cho form Create Staff.
         * Nếu redirect trước đó đã có dữ liệu thì giữ nguyên.
         */
        if (!model.containsAttribute(
                "createStaffData")) {

            model.addAttribute(
                    "createStaffData",
                    new StaffCreateDTO()
            );
        }

        return "admin/staff/list";
    }

    // =========================================================
    // STAFF DETAIL
    // =========================================================

    @GetMapping("/{id:\\d+}")
    public String viewStaffDetail(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        StaffDTO staff =
                userService.getStaffDTOById(id);

        if (staff == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Staff not found."
            );

            return "redirect:/admin/staff";
        }

        model.addAttribute(
                "staff",
                staff
        );

        return "admin/staff/detail";
    }

    // =========================================================
    // CREATE STAFF
    // =========================================================

    @PostMapping("/create")
    public String createStaff(
            @Valid
            @ModelAttribute("createStaffData")
            StaffCreateDTO createStaffData,

            BindingResult bindingResult,

            RedirectAttributes redirectAttributes) {

        normalizeCreateData(
                createStaffData
        );

        /*
         * Password và Confirm Password
         * phải giống nhau.
         */
        if (createStaffData.getPassword() != null
                && createStaffData.getConfirmPassword() != null
                && !createStaffData.getPassword()
                .equals(
                        createStaffData
                                .getConfirmPassword()
                )) {

            bindingResult.rejectValue(
                    "confirmPassword",
                    "password.mismatch",
                    "Password confirmation does not match."
            );
        }

        /*
         * checkEmailExist() kiểm tra toàn bộ bảng users,
         * vì vậy sẽ bắt trùng email của cả Customer và Staff.
         */
        if (createStaffData.getEmail() != null
                && !createStaffData.getEmail().isBlank()
                && userService.checkEmailExist(
                createStaffData.getEmail()
        )) {

            bindingResult.rejectValue(
                    "email",
                    "email.duplicate",
                    "This email address is already used by another customer or staff account."
            );
        }

        if (bindingResult.hasErrors()) {

            StaffCreateDTO safeData =
                    createSafeStaffData(
                            createStaffData
                    );

            redirectAttributes.addFlashAttribute(
                    "createStaffData",
                    safeData
            );

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    getFirstErrorMessage(
                            bindingResult
                    )
            );

            redirectAttributes.addFlashAttribute(
                    "openCreateModal",
                    true
            );

            return "redirect:/admin/staff";
        }

        try {

            userService.createStaff(
                    createStaffData.getFullName(),
                    createStaffData.getEmail(),
                    createStaffData.getPassword(),
                    createStaffData.getPhone(),
                    createStaffData.getAddress()
            );

        } catch (DataIntegrityViolationException exception) {

            StaffCreateDTO safeData =
                    createSafeStaffData(
                            createStaffData
                    );

            redirectAttributes.addFlashAttribute(
                    "createStaffData",
                    safeData
            );

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "This email address is already used by another customer or staff account."
            );

            redirectAttributes.addFlashAttribute(
                    "openCreateModal",
                    true
            );

            return "redirect:/admin/staff";

        } catch (IllegalArgumentException exception) {

            StaffCreateDTO safeData =
                    createSafeStaffData(
                            createStaffData
                    );

            redirectAttributes.addFlashAttribute(
                    "createStaffData",
                    safeData
            );

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            redirectAttributes.addFlashAttribute(
                    "openCreateModal",
                    true
            );

            return "redirect:/admin/staff";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Staff created successfully."
        );

        return "redirect:/admin/staff";
    }

    // =========================================================
    // UPDATE STAFF
    // =========================================================

    @PostMapping("/update")
    public String updateStaff(
            @RequestParam Long staffId,

            @RequestParam String fullName,

            @RequestParam String phone,

            @RequestParam String address,

            @RequestParam(required = false)
            String newPassword,

            RedirectAttributes redirectAttributes) {

        StaffDTO staff =
                userService.getStaffDTOById(
                        staffId
                );

        if (staff == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Staff not found."
            );

            return "redirect:/admin/staff";
        }

        String validationMessage =
                validateUpdateFields(
                        fullName,
                        phone,
                        address,
                        newPassword
                );

        if (validationMessage != null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    validationMessage
            );

            return "redirect:/admin/staff";
        }

        String normalizedFullName =
                fullName.trim();

        String normalizedPhone =
                phone.trim();

        String normalizedAddress =
                address.trim();

        userService.updateStaff(
                staffId,
                normalizedFullName,
                normalizedPhone,
                normalizedAddress
        );

        if (newPassword != null
                && !newPassword.isBlank()) {

            userService.updateStaffPassword(
                    staffId,
                    newPassword
            );

            /*
             * Nếu thay đổi mật khẩu của Staff đang đăng nhập,
             * xóa session để Staff phải đăng nhập lại.
             */
            userSessionService.logoutUserImmediately(
                    staff.getEmail()
            );
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Staff updated successfully."
        );

        return "redirect:/admin/staff";
    }

    // =========================================================
    // BAN STAFF
    // =========================================================

    @PostMapping("/ban")
    public String banStaff(
            @RequestParam Long staffId,
            RedirectAttributes redirectAttributes) {

        StaffDTO staff =
                userService.getStaffDTOById(
                        staffId
                );

        if (staff == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Staff not found."
            );

            return "redirect:/admin/staff";
        }

        /*
         * Đổi trạng thái tài khoản trước.
         */
        userService.banStaff(
                staffId
        );

        /*
         * Xóa toàn bộ session của Staff.
         * Ở request tiếp theo Staff sẽ bị logout.
         */
        try {

            userSessionService
                    .logoutUserImmediately(
                            staff.getEmail()
                    );

        } catch (Exception exception) {

            redirectAttributes.addFlashAttribute(
                    "warningMessage",
                    "The staff was banned, but the active session could not be terminated immediately."
            );
        }

        /*
         * Gửi email thông báo.
         * Nếu email lỗi, việc ban vẫn thành công.
         */
        try {

            emailService.sendStatusMail(
                    staff.getEmail(),
                    false
            );

        } catch (Exception exception) {

            redirectAttributes.addFlashAttribute(
                    "warningMessage",
                    "The staff was banned, but the notification email could not be sent."
            );

            return "redirect:/admin/staff";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Staff banned successfully. Active sessions were terminated and a notification email was sent."
        );

        return "redirect:/admin/staff";
    }

    // =========================================================
    // UNBAN STAFF
    // =========================================================

    @PostMapping("/unban")
    public String unbanStaff(
            @RequestParam Long staffId,
            RedirectAttributes redirectAttributes) {

        StaffDTO staff =
                userService.getStaffDTOById(
                        staffId
                );

        if (staff == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Staff not found."
            );

            return "redirect:/admin/staff";
        }

        userService.unbanStaff(
                staffId
        );

        try {

            emailService.sendStatusMail(
                    staff.getEmail(),
                    true
            );

        } catch (Exception exception) {

            redirectAttributes.addFlashAttribute(
                    "warningMessage",
                    "The staff was unbanned, but the notification email could not be sent."
            );

            return "redirect:/admin/staff";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Staff unbanned successfully. Notification email was sent."
        );

        return "redirect:/admin/staff";
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private Sort resolveSort(
            String sort) {

        if (sort == null) {
            return Sort.by("id").ascending();
        }

        switch (sort) {

            case "id_desc":
                return Sort.by(
                        "id"
                ).descending();

            case "name_asc":
                return Sort.by(
                        "fullName"
                ).ascending();

            case "name_desc":
                return Sort.by(
                        "fullName"
                ).descending();

            case "email_asc":
                return Sort.by(
                        "email"
                ).ascending();

            case "email_desc":
                return Sort.by(
                        "email"
                ).descending();

            default:
                return Sort.by(
                        "id"
                ).ascending();
        }
    }

    private void normalizeCreateData(
            StaffCreateDTO data) {

        if (data.getFullName() != null) {
            data.setFullName(
                    data.getFullName().trim()
            );
        }

        if (data.getEmail() != null) {
            data.setEmail(
                    data.getEmail()
                            .trim()
                            .toLowerCase()
            );
        }

        if (data.getPhone() != null) {
            data.setPhone(
                    data.getPhone().trim()
            );
        }

        if (data.getAddress() != null) {
            data.setAddress(
                    data.getAddress().trim()
            );
        }
    }

    /*
     * Chỉ giữ lại dữ liệu không nhạy cảm.
     * Không đưa password vào RedirectAttributes.
     */
    private StaffCreateDTO createSafeStaffData(
            StaffCreateDTO source) {

        StaffCreateDTO safeData =
                new StaffCreateDTO();

        safeData.setFullName(
                source.getFullName()
        );

        safeData.setEmail(
                source.getEmail()
        );

        safeData.setPhone(
                source.getPhone()
        );

        safeData.setAddress(
                source.getAddress()
        );

        return safeData;
    }

    private String getFirstErrorMessage(
            BindingResult bindingResult) {

        ObjectError firstError =
                bindingResult
                        .getAllErrors()
                        .stream()
                        .findFirst()
                        .orElse(null);

        if (firstError == null
                || firstError.getDefaultMessage() == null) {

            return "Please check the staff information and try again.";
        }

        return firstError.getDefaultMessage();
    }

    private String validateUpdateFields(
            String fullName,
            String phone,
            String address,
            String newPassword) {

        if (fullName == null
                || fullName.isBlank()) {

            return "Full name is required.";
        }

        String normalizedName =
                fullName.trim();

        if (normalizedName.length() < 3
                || normalizedName.length()
                > MAX_NAME_LENGTH) {

            return "Full name must be between 3 and 150 characters.";
        }

        if (phone == null
                || phone.isBlank()) {

            return "Phone number is required.";
        }

        if (!phone.trim().matches(
                PHONE_PATTERN)) {

            return "Please enter a valid Vietnamese phone number.";
        }

        if (address == null
                || address.isBlank()) {

            return "Address is required.";
        }

        if (address.trim().length()
                > MAX_ADDRESS_LENGTH) {

            return "Address must not exceed 500 characters.";
        }

        if (newPassword != null
                && !newPassword.isBlank()
                && !newPassword.matches(
                PASSWORD_PATTERN)) {

            return "New password must be between 8 and 72 characters and contain an uppercase letter, a lowercase letter, and a number.";
        }

        return null;
    }
}