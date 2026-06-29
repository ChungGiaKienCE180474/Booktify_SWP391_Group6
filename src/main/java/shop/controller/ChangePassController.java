package shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ChangePassController {

    @GetMapping("/changepass")
    public String showChangePassForm() {
        return "redirect:/profile?password=edit";
    }

    @PostMapping("/changepass")
    public String changePassword() {
        return "redirect:/profile?password=edit";
    }
}
