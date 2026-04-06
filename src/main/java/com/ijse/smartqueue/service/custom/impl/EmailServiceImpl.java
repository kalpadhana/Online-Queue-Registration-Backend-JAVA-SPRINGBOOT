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
            System.err.println("📧 [EMAIL] Attempting to send HTML email");
            System.err.println("   To: " + to);
            System.err.println("   From: " + fromEmail);
            System.err.println("   Subject: " + subject);
            System.err.println("   Using MailSender: " + (mailSender != null ? "✅ Available" : "❌ NULL"));
            
            if (mailSender == null) {
                System.err.println("❌ ERROR: JavaMailSender is null! Email config not loaded.");
                return;
            }
            
            MimeMessage message = mailSender.createMimeMessage();
            System.err.println("   MimeMessage created: " + (message != null ? "✅" : "❌"));
            
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            System.err.println("   MimeMessageHelper created: ✅");
            
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            
            System.err.println("   Sending email...");
            mailSender.send(message);
            System.err.println("✅ [EMAIL] HTML Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ [EMAIL] FAILED to send HTML email to " + to);
            System.err.println("   Exception type: " + e.getClass().getName());
            System.err.println("   Message: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("   Caused by: " + e.getCause().getMessage());
            }
            e.printStackTrace();
        }
    }
}
