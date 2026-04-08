package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.dto.NotificationDTO;
import com.ijse.smartqueue.service.custom.NotificationService;
import com.ijse.smartqueue.service.custom.EmailService;
import com.ijse.smartqueue.util.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@CrossOrigin
public class NotificationController {

    private final NotificationService notificationService;
    private final EmailService emailService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    @PostMapping
    public ResponseEntity<APIResponse<Void>> createNotification(@RequestBody NotificationDTO notificationDTO) {
        notificationService.createNotification(notificationDTO);
        return ResponseEntity.ok(new APIResponse<>(200, "Notification created", null));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<APIResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Notification marked as read", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(new APIResponse<>(200, "Notification deleted", null));
    }

    @PostMapping("/test-email")
    public ResponseEntity<APIResponse<String>> sendTestEmail(@RequestParam String email) {
        try {
            emailService.sendSimpleEmail(
                email, 
                "🧪 Test Email from Smart Queue System",
                "✅ Hello!\n\nThis is a test email to verify that the SMTP server is working correctly.\n\nIf you received this email, the email configuration is working properly!\n\nBest regards,\nSmart Queue System Team"
            );
            return ResponseEntity.ok(new APIResponse<>(200, "Test email sent successfully!", "Check your inbox for the test email"));
        } catch (Exception e) {
            System.err.println("❌ Test email failed: " + e.getMessage());
            return ResponseEntity.status(500).body(new APIResponse<>(500, "Failed to send test email", "Error: " + e.getMessage()));
        }
    }
}