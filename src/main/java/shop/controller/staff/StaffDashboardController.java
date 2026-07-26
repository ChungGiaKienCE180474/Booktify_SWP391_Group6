package shop.controller.staff;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import shop.domain.ContactStatus;
import shop.domain.OrderStatus;
import shop.domain.Voucher;
import shop.domain.dto.OrderDTO;
import shop.repository.ContactRequestRepository;
import shop.repository.SupplierRepository;
import shop.repository.VppItemRepository;
import shop.service.ContactService;
import shop.service.OrderService;
import shop.service.RatingService;
import shop.service.VoucherService;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasRole('STAFF')")
public class StaffDashboardController {

    private static final int RECENT_ORDERS_LIMIT = 5;

    private final ContactRequestRepository contactRequestRepository;
    private final VppItemRepository vppItemRepository;
    private final SupplierRepository supplierRepository;
    private final OrderService orderService;
    private final VoucherService voucherService;
    private final RatingService ratingService;
    private final ContactService contactService;

    public StaffDashboardController(
            ContactRequestRepository contactRequestRepository,
            VppItemRepository vppItemRepository,
            SupplierRepository supplierRepository,
            OrderService orderService,
            VoucherService voucherService,
            RatingService ratingService,
            ContactService contactService
    ) {
        this.contactRequestRepository = contactRequestRepository;
        this.vppItemRepository = vppItemRepository;
        this.supplierRepository = supplierRepository;
        this.orderService = orderService;
        this.voucherService = voucherService;
        this.ratingService = ratingService;
        this.contactService = contactService;
    }

    @GetMapping
    public String dashboard(Model model) {

        // Orders
        List<OrderDTO> orders = orderService.searchOrders(null, "all", "default");
        long pendingOrders = orders.stream()
                .filter(o -> OrderStatus.PENDING.name().equalsIgnoreCase(o.getStatus()))
                .count();

        // Vouchers
        List<Voucher> vouchers = voucherService.getFilteredVouchers(null, null);
        long activeVouchers = vouchers.stream()
                .filter(v -> "ACTIVE".equals(v.getStatus()))
                .count();

        // Reviews (products that currently have at least one review)
        long reviewedProducts = ratingService.getBooksHasReview(null).size()
                + ratingService.getVppItemsHasReview(null).size();

        // Contacts
        long openContacts = contactService.countRequestsByStatus(ContactStatus.OPEN);

        model.addAttribute("totalOrders", orders.size());
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("recentOrders",
                orders.size() > RECENT_ORDERS_LIMIT
                        ? orders.subList(0, RECENT_ORDERS_LIMIT)
                        : orders);

        model.addAttribute("totalVouchers", vouchers.size());
        model.addAttribute("activeVouchers", activeVouchers);

        model.addAttribute("reviewedProducts", reviewedProducts);

        model.addAttribute("totalContacts", contactRequestRepository.count());
        model.addAttribute("openContacts", openContacts);

        model.addAttribute("totalVppItems", vppItemRepository.count());
        model.addAttribute("totalSuppliers", supplierRepository.count());

        return "staff/dashboard/index";
    }
}
