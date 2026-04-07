package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.service.TwilioService;
import com.ijse.smartqueue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Test controller to debug Twilio SMS sending
 * Use this to test SMS sending without going through queue flow
 */
@RestController
@RequestMapping("/api/v1/test/twilio")
@RequiredArgsConstructor
public class TwilioTestController {

    private final TwilioService twilioService;
    private final QueueService queueService;

    /**
     * Test SMS endpoint
     * Usage: GET /api/v1/test/twilio/send-sms?phone=+94779649968
     */
    @GetMapping("/send-sms")
    public ResponseEntity<String> testSendSMS(@RequestParam String phone) {
        System.out.println("\n\n╔════════════════════════════════════════════╗");
        System.out.println("║     🔵 TEST ENDPOINT: testSendSMS           ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("📱 Testing SMS sending to: " + phone);
        System.out.flush();
        
        try {
            boolean sent = twilioService.sendQueueReadySMS(
                    phone,
                    "TEST-TOKEN-123",
                    "Test Service",
                    "Test Branch"
            );
            
            if (sent) {
                System.out.println("✅ TEST RESULT: SMS SENT SUCCESSFULLY!");
                System.out.flush();
                return ResponseEntity.ok("✅ SMS sent successfully to " + phone);
            } else {
                System.out.println("❌ TEST RESULT: SMS FAILED!");
                System.out.flush();
                return ResponseEntity.status(500).body("❌ SMS failed to send");
            }
        } catch (Exception e) {
            System.out.println("❌ TEST EXCEPTION: " + e.getMessage());
            e.printStackTrace(System.out);
            System.out.flush();
            return ResponseEntity.status(500).body("❌ Exception: " + e.getMessage());
        }
    }

    /**
     * Simple health check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        System.out.println("✅ Twilio Test Controller is accessible");
        System.out.flush();
        return ResponseEntity.ok("Twilio Test Controller is running");
    }

    /**
     * Test calling a queue directly
     * Usage: GET /api/v1/test/twilio/call-queue/1
     */
    @GetMapping("/call-queue/{queueId}")
    public ResponseEntity<String> testCallQueue(@PathVariable Long queueId) {
        System.out.println("\n\n╔════════════════════════════════════════════╗");
        System.out.println("║   🔵 TEST ENDPOINT: testCallQueue           ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("📞 Testing queue call for queueId: " + queueId);
        System.out.flush();
        
        try {
            System.out.println("🔵 Calling queueService.callQueue(" + queueId + ")...");
            System.out.flush();
            
            queueService.callQueue(queueId);
            
            System.out.println("✅ TEST RESULT: Queue called successfully!");
            System.out.flush();
            return ResponseEntity.ok("✅ Queue " + queueId + " called successfully");
        } catch (Exception e) {
            System.out.println("❌ TEST EXCEPTION: " + e.getClass().getSimpleName());
            System.out.println("❌ Message: " + e.getMessage());
            e.printStackTrace(System.out);
            System.out.flush();
            return ResponseEntity.status(500).body("❌ Error calling queue: " + e.getMessage());
        }
    }

    /**
     * Check if Twilio credentials are loaded
     * Usage: GET /api/v1/test/twilio/config
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getConfig() {
        Map<String, String> config = new LinkedHashMap<>();
        
        try {
            java.lang.reflect.Field accountSidField = twilioService.getClass().getDeclaredField("accountSid");
            accountSidField.setAccessible(true);
            String accountSid = (String) accountSidField.get(twilioService);
            config.put("accountSid", accountSid == null || accountSid.isEmpty() ? "❌ NOT LOADED (empty)" : "✅ LOADED: " + accountSid.substring(0, Math.min(10, accountSid.length())) + "***");
            
            java.lang.reflect.Field authTokenField = twilioService.getClass().getDeclaredField("authToken");
            authTokenField.setAccessible(true);
            String authToken = (String) authTokenField.get(twilioService);
            config.put("authToken", authToken == null || authToken.isEmpty() ? "❌ NOT LOADED (empty)" : "✅ LOADED: " + authToken.substring(0, Math.min(10, authToken.length())) + "***");
            
            java.lang.reflect.Field twilioPhoneField = twilioService.getClass().getDeclaredField("twilioPhoneNumber");
            twilioPhoneField.setAccessible(true);
            String twilioPhone = (String) twilioPhoneField.get(twilioService);
            config.put("twilioPhoneNumber", twilioPhone == null || twilioPhone.isEmpty() ? "❌ NOT LOADED (empty)" : "✅ LOADED: " + twilioPhone);
        } catch (Exception e) {
            config.put("error", "❌ Could not inspect Twilio config: " + e.getMessage());
        }
        
        return ResponseEntity.ok().body(config);
    }
}
