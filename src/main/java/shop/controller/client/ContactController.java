package shop.controller.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.ContactMessage;
import shop.domain.ContactRequest;
import shop.service.ContactService;

import java.security.Principal;
import java.util.List;

@Controller
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contact")
    public String contactPage(
            @RequestParam(defaultValue = "0") int page,
            Model model,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, 6);

        Page<ContactRequest> requestPage =
                this.contactService.getCustomerRequests(principal.getName(), pageable);

        model.addAttribute("contactRequest", new ContactRequest());
        model.addAttribute("requests", requestPage.getContent());
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

        return "contact/index";
    }

    @PostMapping("/contact")
    public String submitContact(
            @ModelAttribute ContactRequest contactRequest,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        if (contactRequest.getSubject() == null || contactRequest.getSubject().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Subject is required.");
            return "redirect:/contact";
        }

        if (contactRequest.getContent() == null || contactRequest.getContent().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Content is required.");
            return "redirect:/contact";
        }

        try {
            this.contactService.createRequest(
                    contactRequest,
                    principal.getName(),
                    images,
                    files
            );

            redirectAttributes.addFlashAttribute("success", "Support request submitted successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/contact";
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

        ContactRequest request =
                this.contactService.getCustomerRequestDetail(id, principal.getName());

        List<ContactMessage> messages = this.contactService.getMessages(request);

        model.addAttribute("request", request);
        model.addAttribute("messages", messages);
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
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/contact/" + id;
    }
}