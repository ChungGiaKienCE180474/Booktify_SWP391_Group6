package shop.controller.staff;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.domain.dto.SupplierDTO;
import shop.service.SupplierService;

@Controller
@RequestMapping("/staff/suppliers")
@PreAuthorize("hasRole('STAFF')")
public class StaffSupplierController {

    private final SupplierService supplierService;

    public StaffSupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public String list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "status", required = false) String status,
            Model model
    ) {
        List<SupplierDTO> suppliers = supplierService.searchSuppliers(q, status);

        model.addAttribute("suppliers", suppliers);
        model.addAttribute("q", q);
        model.addAttribute("status", status);

        return "layout/staff/supplier/list";
    }
}