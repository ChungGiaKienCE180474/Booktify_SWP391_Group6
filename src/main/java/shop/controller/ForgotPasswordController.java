package shop.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shop.domain.OTPForm;
import shop.domain.ResetPasswordForm;
import shop.service.EmailService;
import shop.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotPasswordController {

    private final UserService userService;
    private final EmailService emailService;

    public ForgotPasswordController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/forgotpassword")
    public String getForgotPassword() {
        return "authentication/forgotpassword";
    }

    @PostMapping("/authentication/forgotpassword")
    public String recoverPassword(@RequestParam("email") String email, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng nhập email.");
            return "redirect:/forgotpassword";
        }

        boolean checkEmail = this.userService.checkEmailExist(email.trim());
        if (!checkEmail) {
            return "redirect:/forgotpassword?invalidemail";
        }

        HttpSession mySession = request.getSession();

        int otpvalue = new Random().nextInt(900000) + 100000;
        String normalizedEmail = email.trim();

        mySession.setAttribute("otp", otpvalue);
        mySession.setAttribute("email", normalizedEmail);

        try {
            emailService.sendOtpEmail(normalizedEmail, "Password Recovery OTP",
                    "Your OTP is: " + otpvalue);
        } catch (MessagingException e) {
            e.printStackTrace();
            mySession.removeAttribute("otp");
            mySession.removeAttribute("email");
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không gửi được OTP. Vui lòng thử lại sau.");
            return "redirect:/forgotpassword";
        }

        return "redirect:/authentication/enterOTP";
    }

    @GetMapping("/authentication/enterOTP")
    public String getOTP(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        Integer generatedOtp = (Integer) session.getAttribute("otp");

        // Check if the email and OTP are present in the session
        if (email == null || generatedOtp == null) {
            request.setAttribute("message",
                    "Session expired or unauthorized access. Please go through the forgot password process again.");
            return "redirect:/forgotpassword";
        }
        model.addAttribute("newOtpForm", new OTPForm());
        return "authentication/enterOTP";
    }

    @PostMapping("/authentication/enterOTP")
    public String validateOtp(HttpServletRequest request, @RequestParam("otp") int otp, Model model,
            @ModelAttribute("newOtpForm") OTPForm newOtpForm) {
        HttpSession session = request.getSession();
        Integer generatedOtp = (Integer) session.getAttribute("otp");
        Integer currentOTP = newOtpForm.getOtp();

        if (generatedOtp != null && currentOTP != null && currentOTP.equals(generatedOtp)) {
            return "redirect:/authentication/resetPassword";
        } else {
            request.setAttribute("message", "Invalid OTP. Please try again.");
            return "redirect:/authentication/enterOTP?error";
        }
    }

    @GetMapping("/authentication/resetPassword")
    public String getResetPassword(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        Integer generatedOtp = (Integer) session.getAttribute("otp");

        // Check if the email and OTP are present in the session
        if (email == null || generatedOtp == null) {
            request.setAttribute("message",
                    "Session expired or unauthorized access. Please go through the forgot password process again.");
            return "redirect:/forgotpassword";
        }

        model.addAttribute("resetPasswordForm", new ResetPasswordForm());
        return "authentication/resetPassword";
    }

    @PostMapping("/authentication/resetPassword")
    public String resetPassword(HttpServletRequest request, @RequestParam("password") String password,
            @RequestParam("confPassword") String confPassword, Model model) {
        if (password.equals(confPassword)) {
            HttpSession session = request.getSession();
            String email = (String) session.getAttribute("email");
            if (email != null) {
                this.userService.updatePassword(email, password);
                // Xóa session sau khi đổi mật khẩu thành công
                session.invalidate();
                request.setAttribute("message", "Password successfully updated!");
                return "redirect:/login?resetsuccess";
            } else {
                request.setAttribute("message", "Session expired. Please try the process again.");
                return "authentication/forgotPassword";
            }
        } else {

            return "redirect:/authentication/resetPassword?invalidpassword";
        }

    }


}

