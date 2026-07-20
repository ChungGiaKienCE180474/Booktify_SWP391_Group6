package shop.controller.admin;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import shop.domain.Voucher;
import shop.domain.dto.VoucherDTO;
import shop.service.VoucherService;
import java.util.List;

@Controller
@RequestMapping("/admin/vouchers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminVoucherController {

    private final VoucherService voucherService;

    public AdminVoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    // =====================
    // LIST
    // =====================

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Model model) {

        List<Voucher> vouchers = voucherService.getFilteredVouchers(keyword, status);

        model.addAttribute("vouchers", vouchers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "admin/voucher/list";
    }

    // =====================
    // CREATE FORM
    // =====================

    @GetMapping("/create")
    public String createForm(Model model) {

        model.addAttribute("voucherDTO", new VoucherDTO());
        model.addAttribute("formMode", "create");

        return "admin/voucher/form";
    }

    // =====================
    // CREATE
    // =====================

    @PostMapping
    public String create(
            @Valid @ModelAttribute("voucherDTO") VoucherDTO voucherDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        validateVoucher(voucherDTO, bindingResult, null);

        if (bindingResult.hasErrors()) {
            model.addAttribute("formMode", "create");
            return "admin/voucher/form";
        }

        voucherService.saveVoucher(voucherDTO);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Create Voucher Successfully");

        return "redirect:/admin/vouchers";
    }

    // =====================
    // EDIT FORM
    // =====================

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {

        Voucher voucher = voucherService.getVoucherById(id);

        VoucherDTO dto = new VoucherDTO();

        dto.setVoucherName(voucher.getVoucherName());
        dto.setVoucherCode(voucher.getVoucherCode());
        dto.setDiscountType(voucher.getDiscountType());
        dto.setDiscountValue(voucher.getDiscountValue());
        dto.setMinOrderAmount(voucher.getMinOrderAmount());
        dto.setMaxOrderAmount(voucher.getMaxOrderAmount());
        dto.setQuantity(voucher.getQuantity());
        dto.setStartDate(voucher.getStartDate());
        dto.setEndDate(voucher.getEndDate());
        dto.setDescription(voucher.getDescription());

        model.addAttribute("voucherDTO", dto);
        model.addAttribute("voucherId", id);
        model.addAttribute("formMode", "edit");

        return "admin/voucher/form";
    }

    // =====================
    // UPDATE
    // =====================

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("voucherDTO") VoucherDTO voucherDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        validateVoucher(voucherDTO, bindingResult, id);

        if (bindingResult.hasErrors()) {

            model.addAttribute("formMode", "edit");
            model.addAttribute("voucherId", id);

            return "admin/voucher/form";
        }

        voucherService.updateVoucher(id, voucherDTO);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Update Voucher Successfully");

        return "redirect:/admin/vouchers";
    }

    // =====================
    // VALIDATION COMMON
    // =====================

    private void validateVoucher(VoucherDTO voucherDTO, BindingResult bindingResult, Long id) {

        boolean exists;

        if (id == null) {

            exists = voucherService.existsByVoucherCodeIgnoreCase(
                    voucherDTO.getVoucherCode());

        } else {

            exists = voucherService.existsByVoucherCodeIgnoreCaseAndVoucherIdNot(
                    voucherDTO.getVoucherCode(),
                    id);
        }

        if (exists) {

            bindingResult.rejectValue(
                    "voucherCode",
                    "voucher.exists",
                    "Voucher code already exists.");
        }

        if (voucherDTO.getStartDate() != null &&
                voucherDTO.getEndDate() != null &&
                voucherDTO.getEndDate().isBefore(voucherDTO.getStartDate())) {

            bindingResult.rejectValue(
                    "endDate",
                    "voucher.date",
                    "End date must be after start date.");
        }

        if (voucherDTO.getMinOrderAmount() != null &&
                voucherDTO.getMaxOrderAmount() != null &&
                voucherDTO.getMaxOrderAmount()
                        .compareTo(voucherDTO.getMinOrderAmount()) < 0) {

            bindingResult.rejectValue(
                    "maxOrderAmount",
                    "voucher.amount",
                    "Maximum amount must be greater than minimum amount.");
        }

        if ("PERCENT".equals(voucherDTO.getDiscountType())
                && voucherDTO.getDiscountValue() != null
                && voucherDTO.getDiscountValue().compareTo(new BigDecimal("100")) > 0) {

            bindingResult.rejectValue(
                    "discountValue",
                    "voucher.value",
                    "Percentage discount cannot exceed 100.");
        }

        bindingResult.rejectValue(
                "endDate",
                "voucher.expired",
                "End date cannot be in the past.");

    }

    

    // =====================
    // VIEW DETAIL
    // =====================

    @GetMapping("/view/{id}")
    public String viewDetail(
            @PathVariable Long id,
            Model model) {

        Voucher voucher = voucherService.getVoucherById(id);

        model.addAttribute("voucher", voucher);

        return "admin/voucher/detail";
    }

}