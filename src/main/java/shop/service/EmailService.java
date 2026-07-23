package shop.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String senderEmail;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String senderEmail) {

        this.mailSender = mailSender;
        this.senderEmail = senderEmail;
    }

    public void sendStatusMail(String to, boolean active) {

        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException(
                    "Recipient email must not be empty."
            );
        }

        String subject = active
                ? "Booktify Account Restored"
                : "Booktify Account Suspended";

        String content;

        if (active) {
            content = """
                    Hello,

                    Your Booktify account has been restored.

                    You can now log in and continue using Booktify.

                    Regards,
                    Booktify Team
                    """;
        } else {
            content = """
                    Hello,

                    Your Booktify account has been suspended by the administrator.

                    If you believe this is a mistake,
                    please contact Booktify support.

                    Regards,
                    Booktify Team
                    """;
        }

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(to.trim());
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

    public void sendOtpEmail(
            String to,
            String subject,
            String content) throws MessagingException {

        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException(
                    "Recipient email must not be empty."
            );
        }

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, false, "UTF-8");

        helper.setFrom(senderEmail);
        helper.setTo(to.trim());
        helper.setSubject(subject);
        helper.setText(content, false);

        mailSender.send(message);
    }
}