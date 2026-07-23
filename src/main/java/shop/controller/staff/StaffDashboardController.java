package shop.controller.staff;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import shop.repository.ContactRequestRepository;
import shop.repository.SupplierRepository;
import shop.repository.VppItemRepository;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasRole('STAFF')")
public class StaffDashboardController {

    private final ContactRequestRepository contactRequestRepository;
    private final VppItemRepository vppItemRepository;
    private final SupplierRepository supplierRepository;

    public StaffDashboardController(
            ContactRequestRepository contactRequestRepository,
            VppItemRepository vppItemRepository,
            SupplierRepository supplierRepository
    ) {
        this.contactRequestRepository = contactRequestRepository;
        this.vppItemRepository = vppItemRepository;
        this.supplierRepository = supplierRepository;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalContacts", contactRequestRepository.count());
        model.addAttribute("totalVppItems", vppItemRepository.count());
        model.addAttribute("totalSuppliers", supplierRepository.count());

        return "staff/dashboard/index";
    }
}