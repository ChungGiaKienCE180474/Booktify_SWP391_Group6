package shop.controller.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shop.service.UserSessionService;
import shop.domain.dto.CustomerDTO;
import shop.domain.dto.OrderDTO;
import shop.service.EmailService;
import shop.service.OrderService;
import shop.service.UserService;

@Controller
@RequestMapping("/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController {

    private static final int PAGE_SIZE = 10;
    private final UserSessionService userSessionService;
    private final UserService userService;
    private final EmailService emailService;
    private final OrderService orderService;

    public CustomerController(
            UserService userService,
            EmailService emailService,
            OrderService orderService,
            UserSessionService userSessionService) {

        this.userService = userService;
        this.emailService = emailService;
        this.orderService = orderService;
        this.userSessionService = userSessionService;
    }

    @GetMapping
    public String viewCustomers(
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
                    required = false,
                    defaultValue = "0"
            )
            int page,

            Model model) {

        if (page < 0) {
            page = 0;
        }

        Sort springSort;

        switch (sort) {

            case "id_desc":
                springSort =
                        Sort.by("id").descending();
                break;

            case "name_asc":
                springSort =
                        Sort.by("fullName").ascending();
                break;

            case "name_desc":
                springSort =
                        Sort.by("fullName").descending();
                break;

            case "email_asc":
                springSort =
                        Sort.by("email").ascending();
                break;

            case "email_desc":
                springSort =
                        Sort.by("email").descending();
                break;

            default:
                springSort =
                        Sort.by("id").ascending();
                break;
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        PAGE_SIZE,
                        springSort
                );

        Page<CustomerDTO> customerPage =
                userService.getCustomersPage(
                        keyword,
                        status,
                        pageable
                );

        if (page >= customerPage.getTotalPages()
                && customerPage.getTotalPages() > 0) {

            page =
                    customerPage.getTotalPages() - 1;

            pageable =
                    PageRequest.of(
                            page,
                            PAGE_SIZE,
                            springSort
                    );

            customerPage =
                    userService.getCustomersPage(
                            keyword,
                            status,
                            pageable
                    );
        }

        model.addAttribute(
                "customerPage",
                customerPage
        );

        model.addAttribute(
                "customers",
                customerPage.getContent()
        );

        model.addAttribute(
                "currentPage",
                page
        );

        model.addAttribute(
                "totalPages",
                customerPage.getTotalPages()
        );

        model.addAttribute(
                "totalItems",
                customerPage.getTotalElements()
        );

        model.addAttribute(
                "totalCustomers",
                userService.countCustomers()
        );

        model.addAttribute(
                "activeCustomers",
                userService.countActiveCustomers()
        );

        model.addAttribute(
                "inactiveCustomers",
                userService.countInactiveCustomers()
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

        return "admin/customer/list";
    }

    @GetMapping("/{id:\\d+}")
    public String viewCustomerDetail(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        CustomerDTO customer =
                userService.getCustomerDTOById(id);

        if (customer == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Customer not found."
            );

            return "redirect:/admin/customers";
        }

        model.addAttribute(
                "customer",
                customer
        );

        model.addAttribute(
                "orders",
                orderService.getOrdersForCustomer(id)
        );

        return "admin/customer/detail";
    }

    /**
     * Returns the selected customer's order history for the customer detail
     * modal.
     */
    @GetMapping("/{id:\\d+}/orders")
    @ResponseBody
    public ResponseEntity<List<OrderDTO>> getCustomerOrders(
            @PathVariable Long id) {

        CustomerDTO customer =
                userService.getCustomerDTOById(id);

        if (customer == null) {
            return ResponseEntity.notFound().build();
        }

        List<OrderDTO> orders =
                orderService.getOrdersForCustomer(id);

        return ResponseEntity.ok(orders);
    }

    @PostMapping("/ban")
    public String banCustomer(
            @RequestParam Long userId,
            RedirectAttributes redirectAttributes) {

        CustomerDTO customer =
                userService.getCustomerDTOById(
                        userId
                );

        if (customer == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Customer not found."
            );

            return "redirect:/admin/customers";
        }

        userService.banUser(
                userId
        );

        String warningMessage = null;

        try {
            userSessionService.logoutUserImmediately(
                    customer.getEmail()
            );
        } catch (Exception exception) {
            warningMessage =
                    "The customer was banned, but the active session could not be terminated immediately.";
        }

        try {
            emailService.sendStatusMail(
                    customer.getEmail(),
                    false
            );
        } catch (Exception exception) {
            warningMessage = warningMessage == null
                    ? "The customer was banned, but the notification email could not be sent."
                    : warningMessage
                    + " The notification email could not be sent.";
        }

        if (warningMessage != null) {
            redirectAttributes.addFlashAttribute(
                    "warningMessage",
                    warningMessage
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Customer banned successfully. Active sessions were terminated and a notification email was sent."
            );
        }

        return "redirect:/admin/customers";
    }

    @PostMapping("/unban")
    public String unbanCustomer(
            @RequestParam Long userId,
            RedirectAttributes redirectAttributes) {

        CustomerDTO customer =
                userService.getCustomerDTOById(userId);

        if (customer == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Customer not found."
            );

            return "redirect:/admin/customers";
        }

        userService.unbanUser(userId);

        try {

            emailService.sendStatusMail(
                    customer.getEmail(),
                    true
            );

        } catch (Exception exception) {

            redirectAttributes.addFlashAttribute(
                    "warningMessage",
                    "The customer was unbanned, but the notification email could not be sent."
            );

            return "redirect:/admin/customers";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Customer unbanned successfully."
        );

        return "redirect:/admin/customers";
    }

    @PostMapping("/delete")
    public String deleteCustomer(
            @RequestParam Long userId,
            RedirectAttributes redirectAttributes) {

        CustomerDTO customer =
                userService.getCustomerDTOById(userId);

        if (customer == null) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Customer not found."
            );

            return "redirect:/admin/customers";
        }

        try {

            userService.softDeleteCustomer(userId);

            userSessionService.logoutUserImmediately(
                    customer.getEmail()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Customer deleted successfully. Active sessions were terminated."
            );

        } catch (Exception exception) {

            /*
             * Nếu tài khoản đã được soft-delete nhưng xóa session hoặc
             * thao tác phụ thất bại, trạng thái khóa vẫn được giữ lại.
             */
            if (userService.getCustomerDTOById(userId) == null) {
                redirectAttributes.addFlashAttribute(
                        "warningMessage",
                        "The customer was deleted, but the active session could not be terminated immediately."
                );
            } else {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "An error occurred while deleting the customer."
                );
            }
        }

        return "redirect:/admin/customers";
    }
}