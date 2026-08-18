package org.example.service;

import org.example.model.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${app.url:http://localhost:3000}")
    private String appUrl;

    @Value("${spring.mail.username:no-reply@example.com}")
    private String mailFrom;

    private final JavaMailSender mailSender;

    public EmailServiceImpl(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSender = mailSenderProvider.getIfAvailable();
    }

    @Override
    public void sendConfirmationEmail(AppUser user, String token) {
        String link = appUrl + "/auth/confirm?token=" + token;
        if (mailSender != null) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
                helper.setTo(user.getEmail());
                helper.setFrom(mailFrom);
                helper.setSubject("Confirm your ExpenseTracker account");

                String name = user.getFullName() != null ? user.getFullName() : user.getEmail();
                String html = "<p>Hi " + name + ",</p>"
                        + "<p>Please confirm your account by clicking the link below:</p>"
                        + "<p><a href=\"" + link + "\">Confirm email</a></p>"
                        + "<p>If you did not register, ignore this message.</p>";

                helper.setText(html, true);
                mailSender.send(message);
                log.info("Sent confirmation email to {}", user.getEmail());
                return;
            } catch (Exception e) {
                log.error("Failed to send confirmation email, falling back to console. Error: {}", e.getMessage());
            }
        }

        // Fallback: print the confirmation link to the console
        System.out.println("[EmailService] Confirmation link for " + user.getEmail() + ": " + link);
    }
}
