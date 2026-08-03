// Package service — xử lý gửi email (OTP đăng ký, thông báo trạng thái tài khoản)
package shop.service;

// MessagingException: lỗi khi tạo/gửi email MIME
import jakarta.mail.MessagingException;
// MimeMessage: email hỗ trợ HTML/UTF-8 (dùng cho OTP)
import jakarta.mail.internet.MimeMessage;

// @Value: inject giá trị từ application.properties (spring.mail.username)
import org.springframework.beans.factory.annotation.Value;
// SimpleMailMessage: email text thuần, không HTML
import org.springframework.mail.SimpleMailMessage;
// JavaMailSender: interface Spring gửi mail qua SMTP
import org.springframework.mail.javamail.JavaMailSender;
// MimeMessageHelper: helper tạo MimeMessage (set from, to, subject, body)
import org.springframework.mail.javamail.MimeMessageHelper;
// @Service: đánh dấu class là Spring bean, inject vào RegisterController
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // JavaMailSender được cấu hình trong application.properties (host, port, username, password)
    private final JavaMailSender mailSender;
    // Email người gửi, lấy từ spring.mail.username (ví dụ: booktify@gmail.com)
    private final String senderEmail;

    // Constructor injection: Spring inject mailSender và email gửi từ config
    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String senderEmail) {

        this.mailSender = mailSender;
        this.senderEmail = senderEmail;
    }

    // Gửi email thông báo tài khoản bị khóa (ban) hoặc được mở lại (unban) — dùng bởi admin
    public void sendStatusMail(String to, boolean active) {

        // Validate email người nhận không được rỗng
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException(
                    "Recipient email must not be empty."
            );
        }

        // Tiêu đề email thay đổi theo trạng thái active/inactive
        String subject = active
                ? "Booktify Account Restored"
                : "Booktify Account Suspended";

        String content;

        // Nội dung email khi tài khoản được khôi phục
        if (active) {
            content = """
                    Hello,

                    Your Booktify account has been restored.

                    You can now log in and continue using Booktify.

                    Regards,
                    Booktify Team
                    """;
        } else {
            // Nội dung email khi tài khoản bị suspend
            content = """
                    Hello,

                    Your Booktify account has been suspended by the administrator.

                    If you believe this is a mistake,
                    please contact Booktify support.

                    Regards,
                    Booktify Team
                    """;
        }

        // Tạo message email dạng text đơn giản
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);   // Địa chỉ gửi
        message.setTo(to.trim());       // Địa chỉ nhận (trim khoảng trắng)
        message.setSubject(subject);    // Tiêu đề
        message.setText(content);       // Nội dung plain text

        // Gửi email qua SMTP (đồng bộ, ném exception nếu lỗi)
        mailSender.send(message);
    }

    // Gửi email OTP — được RegisterController gọi sau khi user submit form đăng ký
    public void sendOtpEmail(
            String to,
            String subject,
            String content) throws MessagingException {

        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException(
                    "Recipient email must not be empty."
            );
        }

        // Tạo MimeMessage (linh hoạt hơn SimpleMailMessage, hỗ trợ encoding UTF-8)
        MimeMessage message = mailSender.createMimeMessage();

        // false = không multipart (chỉ text), "UTF-8" = encoding tiếng Việt/ký tự đặc biệt
        MimeMessageHelper helper =
                new MimeMessageHelper(message, false, "UTF-8");

        helper.setFrom(senderEmail);
        helper.setTo(to.trim());
        helper.setSubject(subject);
        // false = nội dung plain text (không phải HTML)
        helper.setText(content, false);

        // Gửi email; MessagingException được RegisterController catch để xử lý lỗi
        mailSender.send(message);
    }
}
