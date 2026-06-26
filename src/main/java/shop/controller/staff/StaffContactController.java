package shop.controller.staff;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.ContactMessage;
import shop.domain.ContactRequest;
import shop.domain.ContactStatus;
import shop.service.ContactService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/staff/contacts")
public class StaffContactController {

    private final ContactService contactService;

    public StaffContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public String listContacts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) ContactStatus status,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, 7);

        Page<ContactRequest> requestPage =
                this.contactService.searchForStaff(keyword, status, pageable);

        model.addAttribute("requests", requestPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", ContactStatus.values());

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requestPage.getTotalPages());
        model.addAttribute("totalItems", requestPage.getTotalElements());
        model.addAttribute("pageSize", requestPage.getSize());

        long totalItems = requestPage.getTotalElements();

        if (totalItems > 0) {
            model.addAttribute("startItem", requestPage.getNumber() * requestPage.getSize() + 1);
            model.addAttribute("endItem", Math.min(
                    (requestPage.getNumber() + 1) * requestPage.getSize(),
                    totalItems
            ));
        } else {
            model.addAttribute("startItem", 0);
            model.addAttribute("endItem", 0);
        }

        model.addAttribute("totalContacts", this.contactService.countAllRequests());
        model.addAttribute("openContacts", this.contactService.countRequestsByStatus(ContactStatus.OPEN));
        model.addAttribute("inProgressContacts", this.contactService.countRequestsByStatus(ContactStatus.IN_PROGRESS));
        model.addAttribute("completeContacts", this.contactService.countRequestsByStatus(ContactStatus.COMPLETE));

        model.addAttribute("currentPrincipalName", principal.getName());

        return "contact/stafflist";
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        ContactRequest request = this.contactService.getRequestById(id);
        List<ContactMessage> messages = this.contactService.getMessages(request);

        model.addAttribute("request", request);
        model.addAttribute("messages", messages);
        model.addAttribute("attachments", this.contactService.getAttachments(request));
        model.addAttribute("statuses", ContactStatus.values());
        model.addAttribute("currentPrincipalName", principal.getName());

        return "contact/staffdetail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam("status") ContactStatus status,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            this.contactService.updateStatus(id, status, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Status updated successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/staff/contacts/" + id;
    }

    @PostMapping("/{id}/chat")
    public String staffSendMessage(
            @PathVariable Long id,
            @RequestParam("message") String message,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            this.contactService.sendMessage(id, principal.getName(), message);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/staff/contacts/" + id;
    }
}