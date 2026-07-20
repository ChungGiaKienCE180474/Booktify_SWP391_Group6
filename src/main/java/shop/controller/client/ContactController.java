package shop.controller.client;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.ContactAttachment;
import shop.domain.ContactMessage;
import shop.domain.ContactRequest;
import shop.domain.ContactStatus;
import shop.service.ContactService;

@Controller
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contact")
    public String contactPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) ContactStatus status,
            Model model,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        if (page < 0) {
            page = 0;
        }

        Pageable pageable = PageRequest.of(page, 6);

        Page<ContactRequest> requestPage = this.contactService.getCustomerRequests(
                principal.getName(),
                status,
                pageable
        );

        List<ContactRequest> requests = requestPage.getContent();

        Map<Long, List<ContactMessage>> messagesByRequestId = new HashMap<>();
        Map<Long, List<ContactAttachment>> attachmentsByRequestId = new HashMap<>();

        for (ContactRequest request : requests) {
            messagesByRequestId.put(
                    request.getId(),
                    this.contactService.getMessages(request)
            );

            attachmentsByRequestId.put(
                    request.getId(),
                    this.contactService.getAttachments(request)
            );
        }

        model.addAttribute("requests", requests);
        model.addAttribute("messagesByRequestId", messagesByRequestId);
        model.addAttribute("attachmentsByRequestId", attachmentsByRequestId);

        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedStatusName", status == null ? "" : status.name());

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requestPage.getTotalPages());
        model.addAttribute("totalItems", requestPage.getTotalElements());

        return "contact/index";
    }

    @GetMapping("/contact/create")
    public String createContactPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        model.addAttribute("contactRequest", new ContactRequest());

        return "contact/create";
    }

    @PostMapping("/contact")
    public String submitContact(
            @ModelAttribute("contactRequest") ContactRequest contactRequest,
            @RequestParam(value = "attachmentFiles", required = false) MultipartFile[] attachmentFiles,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            this.contactService.createRequest(
                    contactRequest,
                    principal.getName(),
                    attachmentFiles
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Support request submitted successfully."
            );

            return "redirect:/contact";
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/contact/create";
        }
    }

    @GetMapping("/contact/{id}")
    public String customerDetail(
            @PathVariable Long id,
            Model model,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        ContactRequest request = this.contactService.getCustomerRequestDetail(
                id,
                principal.getName()
        );

        model.addAttribute("request", request);
        model.addAttribute("messages", this.contactService.getMessages(request));
        model.addAttribute("attachments", this.contactService.getAttachments(request));
        model.addAttribute("currentPrincipalName", principal.getName());

        return "contact/detail";
    }

    @PostMapping("/contact/{id}/chat")
    public String customerSendMessage(
            @PathVariable Long id,
            @RequestParam("message") String message,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            this.contactService.getCustomerRequestDetail(id, principal.getName());
            this.contactService.sendMessage(id, principal.getName(), message);

            redirectAttributes.addFlashAttribute("success", "Message sent successfully.");
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }

        return "redirect:/contact?chatId=" + id;
    }
}