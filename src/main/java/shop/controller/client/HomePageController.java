package shop.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomePageController {

    @GetMapping("/")
    public String getHomePage(Model model, HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("fullName", session.getAttribute("fullName"));
            model.addAttribute("role", session.getAttribute("role"));
        }
        return "homepage/index";
    }
}
