// Khai báo package chứa controller phía client (người dùng)
package shop.controller.client;

// Import thư viện sinh số ngẫu nhiên để tạo mã OTP
import java.util.Random;

// @Controller: đánh dấu class này là Spring MVC Controller, xử lý HTTP request và trả về view
import org.springframework.stereotype.Controller;
// Model: đối tượng truyền dữ liệu từ controller sang trang Thymeleaf/HTML
import org.springframework.ui.Model;
// BindingResult: chứa kết quả validate form (@Valid), báo lỗi nếu dữ liệu không hợp lệ
import org.springframework.validation.BindingResult;
// @GetMapping: ánh xạ HTTP GET tới method handler
import org.springframework.web.bind.annotation.GetMapping;
// @ModelAttribute: bind dữ liệu form vào object Java
import org.springframework.web.bind.annotation.ModelAttribute;
// @PostMapping: ánh xạ HTTP POST tới method handler
import org.springframework.web.bind.annotation.PostMapping;
// @RequestParam: lấy tham số từ query string hoặc form POST
import org.springframework.web.bind.annotation.RequestParam;

// Jackson: xử lý lỗi khi chuyển object sang JSON
import com.fasterxml.jackson.core.JsonProcessingException;
// ObjectMapper: chuyển đổi giữa Java object và JSON
import com.fasterxml.jackson.databind.ObjectMapper;

// OTPForm: form nhập mã OTP trên trang xác thực
import shop.domain.OTPForm;
// RoleName: enum tên vai trò (CUSTOMER, ADMIN, STAFF...)
import shop.domain.RoleName;
// RegisterDTO: object chứa dữ liệu đăng ký (email, password, fullName...)
import shop.domain.dto.RegisterDTO;
// EmailService: gửi email OTP
import shop.service.EmailService;
// UserService: logic nghiệp vụ liên quan user (đăng ký, kiểm tra email...)
import shop.service.UserService;
// MessagingException: lỗi khi gửi email
import jakarta.mail.MessagingException;
// HttpServletRequest: đại diện HTTP request hiện tại
import jakarta.servlet.http.HttpServletRequest;
// HttpSession: lưu trữ dữ liệu tạm giữa các request (OTP, thông tin đăng ký...)
import jakarta.servlet.http.HttpSession;
// @Valid: kích hoạt Bean Validation trên RegisterDTO
import jakarta.validation.Valid;

// Đánh dấu class là Spring Controller
@Controller
public class RegisterController {
    // Dependency UserService: inject qua constructor, dùng kiểm tra email và tạo user
    private final UserService userService;
    // ObjectMapper: chuyển RegisterDTO ↔ JSON để lưu vào session
    private final ObjectMapper objectMapper;
    // EmailService: gửi mã OTP qua email
    private final EmailService emailService;

    // Constructor injection: Spring tự inject 3 service khi khởi tạo controller
    public RegisterController(UserService userService, ObjectMapper objectMapper,
            EmailService emailService) {
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    // GET /register — hiển thị trang form đăng ký
    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        // Thêm object RegisterDTO rỗng vào model để Thymeleaf bind form (th:object)
        model.addAttribute("registerUser", new RegisterDTO());
        // Trả về template authentication/register.html
        return "authentication/register";
    }

    // POST /register — xử lý submit form đăng ký
    @PostMapping("/register")
    public String registerUser(Model model, @ModelAttribute("registerUser") @Valid RegisterDTO registerDTO,
            BindingResult bindingResult, HttpServletRequest request) {
        // Nếu validate thất bại (@NotBlank, @Email... trên RegisterDTO)
        if (bindingResult.hasErrors()) {
            // Quay lại trang đăng ký, Spring tự giữ lại lỗi và dữ liệu đã nhập
            return "authentication/register";
        }

        // Kiểm tra email đã tồn tại trong database chưa
        if (userService.checkEmailExist(registerDTO.getEmail())) {
            // setAttribute: gắn thông báo lỗi (redirect thường dùng flash attribute, đây là attribute thường)
            request.setAttribute("message", "Email is already registered. Try logging in.");
            // Chuyển hướng về /register?exist để hiển thị thông báo email trùng
            return "redirect:/register?exist";
        }

        // So sánh mật khẩu và xác nhận mật khẩu có khớp không
        if (registerDTO.getConfirmPassword().equals(registerDTO.getPassword()) == false) {
            // Chuyển hướng với query ?password nếu hai mật khẩu không khớp
            return "redirect:/register?password";
        }

        // Lấy session hiện tại (tạo mới nếu chưa có) để lưu OTP và dữ liệu đăng ký tạm
        HttpSession mySession = request.getSession();
        // Sinh OTP ngẫu nhiên từ 0 đến 999998 (6 chữ số tối đa)
        int otpValue = new Random().nextInt(999999);
        // Lấy email người dùng nhập để gửi OTP
        String email = registerDTO.getEmail();

        // Chuyển RegisterDTO thành chuỗi JSON và lưu vào session (session không lưu trực tiếp object phức tạp an toàn)
        try {
            // writeValueAsString: serialize RegisterDTO → JSON string
            String registerDTOJson = objectMapper.writeValueAsString(registerDTO);
            // Lưu JSON vào session với key "registerDTO"
            mySession.setAttribute("registerDTO", registerDTOJson);
        } catch (JsonProcessingException e) {
            // In stack trace ra console để debug
            e.printStackTrace();
            request.setAttribute("message", "Error processing registration. Please try again.");
            return "authentication/register";
        }

        // Lưu mã OTP và email vào session để đối chiếu khi user nhập OTP
        mySession.setAttribute("otp", otpValue);
        mySession.setAttribute("email", email);

        // Gửi email chứa mã OTP cho người dùng
        try {
            // sendOtpEmail(to, subject, body): gửi mail với nội dung OTP
            emailService.sendOtpEmail(email, "Register OTP", "Your OTP is: " + otpValue);
        } catch (MessagingException e) {
            // Gửi mail thất bại → xóa dữ liệu session để tránh trạng thái nửa vời
            e.printStackTrace();
            mySession.removeAttribute("otp");
            mySession.removeAttribute("email");
            mySession.removeAttribute("registerDTO");
            request.setAttribute("message", "Failed to send OTP. Please try again.");
            return "authentication/register";
        }

        // OTP gửi thành công → chuyển user sang trang nhập OTP
        return "redirect:/authentication/enterRegisterOTP";
    }

    // GET /authentication/enterRegisterOTP — hiển thị trang nhập OTP
    @GetMapping("/authentication/enterRegisterOTP")
    public String getOTPPage(Model model, HttpServletRequest request) {
        // Lấy session để đọc dữ liệu đăng ký đã lưu trước đó
        HttpSession session = request.getSession();
        // Đọc chuỗi JSON RegisterDTO từ session
        String registerDTOJson = (String) session.getAttribute("registerDTO");

        // Nếu không có dữ liệu đăng ký trong session → session hết hạn hoặc user vào URL trực tiếp
        if (registerDTOJson == null) {
            request.setAttribute("message", "Session expired. Please register again.");
            return "redirect:/register";
        }

        // Thêm form OTP rỗng vào model cho trang nhập mã
        model.addAttribute("newOtpForm", new OTPForm());
        return "authentication/enterRegisterOTP";
    }

    // POST /authentication/enterRegisterOTP — xác thực OTP và hoàn tất đăng ký
    @PostMapping("/authentication/enterRegisterOTP")
    public String validateOtp(HttpServletRequest request, @RequestParam("otp") int otp, Model model) {
        // Lấy session chứa OTP và dữ liệu đăng ký
        HttpSession session = request.getSession();
        // OTP đã sinh và lưu ở bước register (kiểu Integer vì session có thể null)
        Integer generatedOtp = (Integer) session.getAttribute("otp");
        // JSON RegisterDTO đã lưu ở bước register
        String registerDTOJson = (String) session.getAttribute("registerDTO");

        RegisterDTO registerDTO = null;
        try {
            // Deserialize JSON → RegisterDTO để lấy email, password, fullName...
            registerDTO = objectMapper.readValue(registerDTOJson, RegisterDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            request.setAttribute("message", "Session error. Please try again.");
            return "authentication/register";
        }

        // OTP khớp và có dữ liệu đăng ký hợp lệ → tạo tài khoản
        if (generatedOtp != null && generatedOtp.equals(otp) && registerDTO != null) {
            // registerNewUser: hash password, gán role CUSTOMER, lưu DB
            this.userService.registerNewUser(registerDTO, RoleName.CUSTOMER);
            // Hủy session sau đăng ký thành công (xóa OTP và dữ liệu nhạy cảm)
            session.invalidate();
            // Chuyển về trang login với tham số báo đăng ký thành công
            return "redirect:/login?registersuccess";
        } else {
            // OTP sai hoặc thiếu dữ liệu → báo lỗi và ở lại trang nhập OTP
            request.setAttribute("message", "Invalid OTP. Please try again.");
            return "authentication/enterRegisterOTP";
        }
    }
}
