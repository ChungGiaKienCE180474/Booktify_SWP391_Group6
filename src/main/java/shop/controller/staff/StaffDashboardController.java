package shop.controller.staff;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import shop.domain.ContactStatus;
import shop.service.ContactService;

import java.security.Principal;

@Controller
public class StaffDashboardController {

    private final ContactService contactService;

    public StaffDashboardController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/staff")
    public String dashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        model.addAttribute("totalContacts", this.contactService.countAllRequests());
        model.addAttribute("openContacts", this.contactService.countRequestsByStatus(ContactStatus.OPEN));
        model.addAttribute("inProgressContacts", this.contactService.countRequestsByStatus(ContactStatus.IN_PROGRESS));
        model.addAttribute("completeContacts", this.contactService.countRequestsByStatus(ContactStatus.COMPLETE));

        model.addAttribute("currentPrincipalName", principal.getName());

        return "staff/dashboard/index";
    }
}