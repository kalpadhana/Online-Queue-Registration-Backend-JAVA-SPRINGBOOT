package com.ijse.smartqueue.service.custom.impl;

import com.ijse.smartqueue.service.custom.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@smartqueue.com}")
    private String fromEmail;
    
    @Value("${spring.mail.properties.mail.from.name:Smart Queue System}")
    private String fromName;

    @Override
    public void sendQueueCalledEmail(String userEmail, String userName, String token, String serviceName, String branchName) {
        String subject = "Your Queue Number is Called! 🎉 - " + token;
        
        String htmlBody = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #28a745;'>Your Time is Ready! ⏰</h2>" +
                "<p>Hello " + userName + ",</p>" +
                "<p>Great news! Your queue number has been called.</p>" +
                "<div style='background-color: #fff; padding: 15px; margin: 20px 0; border-left: 4px solid #28a745;'>" +
                "<p><strong>Queue Details:</strong></p>" +
                "<ul>" +
                "<li><strong>Your Token:</strong> <span style='font-size: 24px; color: #007bff;'>" + token + "</span></li>" +
                "<li><strong>Service:</strong> " + serviceName + "</li>" +
                "<li><strong>Location:</strong> " + branchName + "</li>" +
                "</ul>" +
                "</div>" +
                "<p style='color: #d9534f;'><strong>⚠️ Please come to the counter immediately!</strong></p>" +
                "<p>If you have any questions, please contact our support team.</p>" +
                "<p>Best regards,<br/><strong>Smart Queue System</strong></p>" +
                "</div></body></html>";
        
        sendHtmlEmail(userEmail, subject, htmlBody);
    }

    @Override
    public void sendQueueRemovedEmail(String userEmail, String userName, String token, String reason) {
        String subject = "Queue Removed - " + token;
        
        String htmlBody = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #dc3545;'>Queue Removed</h2>" +
                "<p>Hello " + userName + ",</p>" +
                "<p>Your queue entry has been removed from the system.</p>" +
                "<div style='background-color: #fff; padding: 15px; margin: 20px 0; border-left: 4px solid #dc3545;'>" +
                "<p><strong>Queue Details:</strong></p>" +
                "<ul>" +
                "<li><strong>Token:</strong> " + token + "</li>" +
                "<li><strong>Reason:</strong> " + (reason != null ? reason : "Removed by admin") + "</li>" +
                "</ul>" +
                "</div>" +
                "<p>If you need to join a queue again, please visit our website or mobile app.</p>" +
                "<p>Best regards,<br/><strong>Smart Queue System</strong></p>" +
                "</div></body></html>";
        
        sendHtmlEmail(userEmail, subject, htmlBody);
    }

    @Override
    public void sendQueueConfirmationEmail(String userEmail, String userName, String token, String serviceName, String branchName, Integer position) {
        String subject = "Queue Confirmation - Your Token: " + token;
        
        String htmlBody = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #007bff;'>Welcome to the Queue! ✅</h2>" +
                "<p>Hello " + userName + ",</p>" +
                "<p>Thank you for joining our queue. Your token has been issued successfully.</p>" +
                "<div style='background-color: #fff; padding: 15px; margin: 20px 0; border-left: 4px solid #007bff;'>" +
                "<p><strong>Your Queue Information:</strong></p>" +
                "<ul>" +
                "<li><strong>Your Token:</strong> <span style='font-size: 24px; color: #007bff;'>" + token + "</span></li>" +
                "<li><strong>Service:</strong> " + serviceName + "</li>" +
                "<li><strong>Location:</strong> " + branchName + "</li>" +
                "<li><strong>Your Position:</strong> #" + position + " in queue</li>" +
                "</ul>" +
                "</div>" +
                "<p><strong>Important:</strong> Keep your token number safe. You'll need it when your turn is called.</p>" +
                "<p>You will receive an email notification when your queue is called.</p>" +
                "<p>Best regards,<br/><strong>Smart Queue System</strong></p>" +
                "</div></body></html>";
        
        sendHtmlEmail(userEmail, subject, htmlBody);
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║  [EmailServiceImpl] sendHtmlEmail                       ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
            System.out.println("📧 Email Details:");
            System.out.println("   TO Address: " + (to != null ? to : "[NULL] ❌ CRITICAL!"));
            System.out.println("   FROM Address: " + fromEmail);
            System.out.println("   FROM Name: " + fromName);
            System.out.println("   Subject: " + subject);
            System.out.println("   TO is NULL: " + (to == null));
            System.out.println("   TO is EMPTY: " + (to != null && to.isEmpty()));
            
            // CRITICAL CHECK
            if (to == null || to.isEmpty()) {
                System.out.println("❌ CRITICAL ERROR: TO address is NULL or EMPTY!");
                System.out.println("   ❌ Email will NOT be sent!");
                System.out.println("   ❌ Check that user.getEmail() is being passed correctly!");
                return;
            }
            
            if (mailSender == null) {
                System.out.println("❌ ERROR: JavaMailSender is null!");
                return;
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // VERIFY BEFORE SENDING
            System.out.println("\n✉️ Setting email properties:");
            System.out.println("   🔄 setFrom(" + fromEmail + ", " + fromName + ")");
            helper.setFrom(fromEmail, fromName);
            
            System.out.println("   🔄 setTo(" + to + ")");
            helper.setTo(to);
            
            System.out.println("   🔄 setSubject(...)");
            helper.setSubject(subject);
            
            System.out.println("   🔄 setText(..., true)");
            helper.setText(htmlBody, true);
            
            System.out.println("\n🚀 Sending MimeMessage...");
            mailSender.send(message);
            
            System.out.println("✅ [EMAIL] HTML Email sent successfully!");
            System.out.println("   FROM: " + fromEmail);
            System.out.println("   TO: " + to);
            System.out.println("   Subject: " + subject);
            System.out.println("╔════════════════════════════════════════════════════════╗\n");
            
        } catch (Exception e) {
            System.out.println("\n❌ [EMAIL] FAILED to send HTML email");
            System.out.println("   TO: " + to);
            System.out.println("   Exception: " + e.getClass().getName());
            System.out.println("   Message: " + e.getMessage());
            e.printStackTrace();
            System.out.println("╔════════════════════════════════════════════════════════╗\n");
        }
    }
}
