package org.example.service;

import jakarta.mail.internet.MimeMessage;
import org.example.model.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

        System.out.println("[EmailService] Confirmation link for " + user.getEmail() + ": " + link);
    }

    @Override
    public void sendPasswordResetEmail(AppUser user, String token) {
        String encodedEmail = URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8);
        String link = appUrl + "/reset-password?token=" + token + "&email=" + encodedEmail;

        if (mailSender != null) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
                helper.setTo(user.getEmail());
                helper.setFrom(mailFrom);
                helper.setSubject("Reset your ExpenseTracker password");

                String name = user.getFullName() != null ? user.getFullName() : user.getEmail();
                String html = "<p>Hi " + name + ",</p>"
                        + "<p>Use the link below to reset your password:</p>"
                        + "<p><a href=\"" + link + "\">Reset password</a></p>"
                        + "<p>This link expires in 30 minutes.</p>"
                        + "<p>If you did not request this reset, ignore this message.</p>";

                helper.setText(html, true);
                mailSender.send(message);
                log.info("Sent password reset email to {}", user.getEmail());
                return;
            } catch (Exception e) {
                log.error("Failed to send password reset email, falling back to console. Error: {}", e.getMessage());
            }
        }

        System.out.println("[EmailService] Password reset link for " + user.getEmail() + ": " + link);
    }
}
