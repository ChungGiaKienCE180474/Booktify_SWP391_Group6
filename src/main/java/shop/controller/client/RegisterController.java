package shop.controller.client;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import shop.domain.OTPForm;
import shop.domain.RoleName;
import shop.domain.dto.RegisterDTO;
import shop.service.EmailService;
import shop.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class RegisterController {
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public RegisterController(UserService userService, ObjectMapper objectMapper,
            EmailService emailService) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("registerUser", new RegisterDTO());
        return "authentication/register";
    }

    @PostMapping("/register")
    public String registerUser(Model model, @ModelAttribute("registerUser") @Valid RegisterDTO registerDTO,
            BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {

            return "authentication/register";
        }

        if (userService.checkEmailExist(registerDTO.getEmail())) {
            request.setAttribute("message", "Email is already registered. Try logging in.");
            return "redirect:/register?exist";
        }

        if (registerDTO.getConfirmPassword().equals(registerDTO.getPassword()) == false) {
            return "redirect:/register?password";
        }

        HttpSession mySession = request.getSession();
        int otpValue = new Random().nextInt(999999);
        String email = registerDTO.getEmail();

        // Chuyển đổi RegisterDTO thành JSON và lưu vào session
        try {
            String registerDTOJson = objectMapper.writeValueAsString(registerDTO);
            mySession.setAttribute("registerDTO", registerDTOJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            request.setAttribute("message", "Error processing registration. Please try again.");
            return "authentication/register";
        }

        // Lưu OTP và email vào session
        mySession.setAttribute("otp", otpValue);
        mySession.setAttribute("email", email);

        try {
            emailService.sendOtpEmail(email, "Register OTP", "Your OTP is: " + otpValue);
        } catch (MessagingException e) {
            e.printStackTrace();
            mySession.removeAttribute("otp");
            mySession.removeAttribute("email");
            mySession.removeAttribute("registerDTO");
            request.setAttribute("message", "Failed to send OTP. Please try again.");
            return "authentication/register";
        }

        return "redirect:/authentication/enterRegisterOTP";
    }

    @GetMapping("/authentication/enterRegisterOTP")
    public String getOTPPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String registerDTOJson = (String) session.getAttribute("registerDTO"); // Lấy JSON từ session

        if (registerDTOJson == null) {
            request.setAttribute("message", "Session expired. Please register again.");
            return "redirect:/register";
        }

        model.addAttribute("newOtpForm", new OTPForm());
        return "authentication/enterRegisterOTP";
    }

    @PostMapping("/authentication/enterRegisterOTP")
    public String validateOtp(HttpServletRequest request, @RequestParam("otp") int otp, Model model) {
        HttpSession session = request.getSession();
        Integer generatedOtp = (Integer) session.getAttribute("otp");
        String registerDTOJson = (String) session.getAttribute("registerDTO"); // Lấy JSON từ session

        RegisterDTO registerDTO = null;
        try {
            // Chuyển đổi JSON thành RegisterDTO
            registerDTO = objectMapper.readValue(registerDTOJson, RegisterDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            request.setAttribute("message", "Session error. Please try again.");
            return "authentication/register";
        }

        if (generatedOtp != null && generatedOtp.equals(otp) && registerDTO != null) {
            this.userService.registerNewUser(registerDTO, RoleName.CUSTOMER);
            session.invalidate();
            return "redirect:/login?registersuccess";
        } else {
            request.setAttribute("message", "Invalid OTP. Please try again.");
            return "authentication/enterRegisterOTP";
        }
    }
}
