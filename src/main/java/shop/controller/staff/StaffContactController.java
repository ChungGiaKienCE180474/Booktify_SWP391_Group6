package shop.controller.staff;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.ContactRequest;
import shop.domain.ContactStatus;
import shop.service.ContactService;

@Controller
@RequestMapping("/staff/contact")
@PreAuthorize("hasRole('STAFF')")
public class StaffContactController {

    private static final int PAGE_SIZE = 10;

    private final ContactService contactService;

    public StaffContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public String listContacts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        if (page < 0) {
            page = 0;
        }

        ContactStatus selectedStatus = parseStatus(status);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);

        Page<ContactRequest> requestPage =
                contactService.searchForStaff(keyword, selectedStatus, pageable);

        long totalItems = requestPage.getTotalElements();

        model.addAttribute("requests", requestPage.getContent());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", selectedStatus);
        model.addAttribute("statuses", ContactStatus.values());

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requestPage.getTotalPages());
        model.addAttribute("totalItems", totalItems);

        model.addAttribute("fromItem", totalItems == 0 ? 0 : page * PAGE_SIZE + 1);
        model.addAttribute("toItem", Math.min((page + 1) * PAGE_SIZE, (int) totalItems));

        model.addAttribute("totalContacts", contactService.countAllRequests());
        model.addAttribute("openContacts", contactService.countRequestsByStatus(ContactStatus.OPEN));
        model.addAttribute("inProgressContacts", contactService.countRequestsByStatus(ContactStatus.IN_PROGRESS));
        model.addAttribute("completeContacts", contactService.countRequestsByStatus(ContactStatus.COMPLETE));

        return "layout/staff/contact/list";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam("status") String status,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            ContactStatus contactStatus = parseRequiredStatus(status);
            contactService.updateStatus(id, contactStatus, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Status updated successfully.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/staff/contact";
    }

    @PostMapping("/{id}/chat")
    public String sendMessage(
            @PathVariable Long id,
            @RequestParam("message") String message,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            contactService.sendMessage(id, principal.getName(), message);
            redirectAttributes.addFlashAttribute("success", "Message sent successfully.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/staff/contact";
    }

    private ContactStatus parseStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return null;
        }

        try {
            return ContactStatus.valueOf(rawStatus.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private ContactStatus parseRequiredStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            throw new RuntimeException("Status is required.");
        }

        try {
            return ContactStatus.valueOf(rawStatus.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException("Invalid status.");
        }
    }
}