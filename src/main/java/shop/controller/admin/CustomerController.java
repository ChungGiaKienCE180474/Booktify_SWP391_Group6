package shop.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shop.domain.dto.CustomerDTO;
import shop.service.EmailService;
import shop.service.UserService;

@Controller
@RequestMapping("/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class CustomerController {

    private static final int PAGE_SIZE = 10;

    private final UserService userService;
    private final EmailService emailService;

    public CustomerController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping
    public String viewCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(required = false, defaultValue = "default") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            Model model) {

        if (page < 0) {
            page = 0;
        }

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
                break;
        }

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, springSort);
        Page<CustomerDTO> customerPage = userService.getCustomersPage(keyword, status, pageable);

        if (page >= customerPage.getTotalPages() && customerPage.getTotalPages() > 0) {
            page = customerPage.getTotalPages() - 1;
            pageable = PageRequest.of(page, PAGE_SIZE, springSort);
            customerPage = userService.getCustomersPage(keyword, status, pageable);
        }

        model.addAttribute("customerPage", customerPage);
        model.addAttribute("customers", customerPage.getContent());

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("totalItems", customerPage.getTotalElements());

        model.addAttribute("totalCustomers", userService.countCustomers());
        model.addAttribute("activeCustomers", userService.countActiveCustomers());
        model.addAttribute("inactiveCustomers", userService.countInactiveCustomers());

        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);

        return "admin/customer/list";
    }

    @GetMapping("/{id}")
    public String viewCustomerDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        CustomerDTO customer = userService.getCustomerDTOById(id);

        if (customer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Customer not found.");
            return "redirect:/admin/customers";
        }

        model.addAttribute("customer", customer);
        return "admin/customer/detail";
    }

    @PostMapping("/ban")
    public String banCustomer(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
        CustomerDTO customer = userService.getCustomerDTOById(userId);

        if (customer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Customer not found.");
            return "redirect:/admin/customers";
        }

        userService.banUser(userId);

        try {
            emailService.sendStatusMail(customer.getEmail(), false);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "warningMessage",
                    "Customer was banned, but email could not be sent.");
            return "redirect:/admin/customers";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Customer banned successfully.");
        return "redirect:/admin/customers";
    }

    @PostMapping("/unban")
    public String unbanCustomer(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
        CustomerDTO customer = userService.getCustomerDTOById(userId);

        if (customer == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Customer not found.");
            return "redirect:/admin/customers";
        }

        userService.unbanUser(userId);

        try {
            emailService.sendStatusMail(customer.getEmail(), true);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "warningMessage",
                    "Customer was unbanned, but email could not be sent.");
            return "redirect:/admin/customers";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Customer unbanned successfully.");
        return "redirect:/admin/customers";
    }
}