package shop.controller.client;

import java.util.Properties;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
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
import shop.domain.User;
import shop.domain.dto.RegisterDTO;
import shop.service.UserService;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class RegisterController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public RegisterController(UserService userService, PasswordEncoder passwordEncoder, ObjectMapper objectMapper) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
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

        // Thiết lập thuộc tính mail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("khangkien.060603@gmail.com", "vinu qibz jtdl blqg");
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("khangkien.060603@gmail.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Register OTP");
            message.setText("Your OTP is: " + otpValue);

            Transport.send(message);
            System.out.println("OTP email sent successfully");

        } catch (MessagingException e) {
            e.printStackTrace();
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
            User user = this.userService.registerDTOtoUser(registerDTO);
            String hashPassword = this.passwordEncoder.encode(registerDTO.getPassword());
            user.setPassword(hashPassword);
            user.setRole(this.userService.getRoleByName(RoleName.CUSTOMER));
            user.setStatus(true);

            this.userService.handleSaveUser(user);
            session.invalidate();
            return "redirect:/login?registersuccess";
        } else {
            request.setAttribute("message", "Invalid OTP. Please try again.");
            return "authentication/enterRegisterOTP";
        }
    }
}
