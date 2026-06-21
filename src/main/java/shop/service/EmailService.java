package shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendStatusMail(String to, boolean active) {

        String subject = "Booktify Account Status";

        String content;

        if (active) {
            content = """
                    Your account has been restored.

                    You can now log in and continue using Booktify.

                    Regards,
                    Booktify Team
                    """;
        } else {
            content = """
                    Your account has been suspended by the administrator.

                    If you think this is a mistake,
                    please contact support.

                    Regards,
                    Booktify Team
                    """;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }
}