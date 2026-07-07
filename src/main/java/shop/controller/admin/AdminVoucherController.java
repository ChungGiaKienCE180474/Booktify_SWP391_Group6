package shop.controller.admin;

import java.math.BigDecimal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import shop.domain.dto.VoucherDTO;
import shop.service.VoucherService;

@Controller
@RequestMapping("/admin/vouchers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminVoucherController {

    // =====================
    // DEPENDENCY
    // =====================

    private final VoucherService voucherService;

    public AdminVoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    // =====================
    // LIST
    // =====================

    @GetMapping
    public String list(Model model) {

        model.addAttribute(
                "vouchers",
                voucherService.getAllVouchers());

        return "admin/voucher/list";

    }

    // =====================
    // CREATE FORM
    // =====================

    @GetMapping("/create")
    public String createForm(Model model) {

        model.addAttribute(
                "voucherDTO",
                new VoucherDTO());

        model.addAttribute(
                "formMode",
                "create");

        return "admin/voucher/form";

    }

    // =====================
    // CREATE ACTION
    // =====================

    @PostMapping
    public String create(

            @Valid @ModelAttribute("voucherDTO") VoucherDTO voucherDTO,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes

    ) {

        // CHECK DUPLICATE CODE

        if (voucherService.existsByVoucherCodeIgnoreCase(
                voucherDTO.getVoucherCode())) {

            bindingResult.rejectValue(
                    "voucherCode",
                    "voucher.exists",
                    "Voucher code already exists.");

        }

        // CHECK DATE

        if (voucherDTO.getStartDate() != null
                &&
                voucherDTO.getEndDate() != null
                &&
                voucherDTO.getEndDate()
                        .isBefore(voucherDTO.getStartDate())) {

            bindingResult.rejectValue(
                    "endDate",
                    "voucher.date",
                    "End date must be after start date.");

        }
        if (voucherDTO.getMinOrderAmount() != null
                &&
                voucherDTO.getMaxOrderAmount() != null
                &&
                voucherDTO.getMaxOrderAmount()
                        .compareTo(voucherDTO.getMinOrderAmount()) < 0) {

            bindingResult.rejectValue(
                    "maxOrderAmount",
                    "voucher.amount",
                    "Maximum amount must be greater than minimum amount.");

        }
        if (voucherDTO.getDiscountValue() != null) {

            if (voucherDTO.getDiscountValue()
                    .compareTo(new BigDecimal("100")) > 0) {

                bindingResult.rejectValue(
                        "discountValue",
                        "voucher.value",
                        "Discount must be between 1 and 100");

            }

        }

        // HAS ERROR

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "formMode",
                    "create");

            return "admin/voucher/form";

        }

        // SAVE

        voucherService.saveVoucher(voucherDTO);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Create Voucher Successfully");

        return "redirect:/admin/vouchers";

    }

}