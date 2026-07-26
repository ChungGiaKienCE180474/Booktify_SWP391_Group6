package shop.controller.staff;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import shop.domain.Voucher;
import shop.service.VoucherService;

@Controller
@RequestMapping("/staff/vouchers")
@PreAuthorize("hasRole('STAFF')")
public class StaffVoucherController {

    private final VoucherService voucherService;

    public StaffVoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    // View list of vouchers (UC-16.1)
    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Model model) {

        List<Voucher> vouchers = voucherService.getFilteredVouchers(keyword, status);

        model.addAttribute("vouchers", vouchers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "layout/staff/voucher/list";
    }

    // View voucher detail (UC-16.2)
    @GetMapping("/view/{id}")
    public String viewDetail(@PathVariable Long id, Model model) {

        Voucher voucher = voucherService.getVoucherById(id);

        model.addAttribute("voucher", voucher);

        return "layout/staff/voucher/detail";
    }
}
