package com.shopflow.notification.application.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("ShopFlow - Verify your account");

            String htmlContent = """
                    <div style='font-family: Arial, sans-serif; padding: 20px; color: #333;'>
                        <h2>Welcome to ShopFlow</h2>
                        <p>Your OTP:</p>
                        <h1 style='color: #0056b3; letter-spacing: 5px;'>%s</h1>
                        <p>This code will expire after 5 minutes. Please dont send to anyone.</p>
                        <p>If you dont request this, please ignore."
                    </div>
                    """.formatted(otp);

            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Send email success to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Error when sending {}: {}", toEmail, e.getMessage());
        }
    }

}
