package com.ijse.smartqueue.config;

import com.twilio.Twilio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Twilio Configuration
 * Initializes Twilio SDK with credentials from application.properties
 */
@Slf4j
@Configuration
public class TwilioConfig {

    @Value("${twilio.account.sid:}")
    private String accountSid;

    @Value("${twilio.auth.token:}")
    private String authToken;

    @Value("${twilio.phone.number:}")
    private String twilioPhoneNumber;

    public TwilioConfig() {
        System.out.println("🔵 TwilioConfig bean instance created");
        System.out.flush();
    }

    /**
     * Initialize Twilio SDK with credentials
     * This method is called automatically after bean creation (@PostConstruct)
     */
    @PostConstruct
    public void initTwilio() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║     🔵 TWILIO CONFIGURATION INITIALIZATION   ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.flush();
        
        try {
            // Verify properties are loaded
            System.out.println("📝 Property Values:");
            System.out.println("   Account SID: " + (accountSid != null && !accountSid.isEmpty() ? accountSid.substring(0, Math.min(5, accountSid.length())) + "***" : "NULL/EMPTY"));
            System.out.println("   Auth Token: " + (authToken != null && !authToken.isEmpty() ? authToken.substring(0, Math.min(5, authToken.length())) + "***" : "NULL/EMPTY"));
            System.out.println("   Twilio Phone: " + (twilioPhoneNumber != null && !twilioPhoneNumber.isEmpty() ? twilioPhoneNumber : "NULL/EMPTY"));
            System.out.flush();
            
            if (accountSid == null || accountSid.isEmpty() || authToken == null || authToken.isEmpty()) {
                System.out.println("❌ ERROR: Twilio credentials are NULL or EMPTY!");
                System.out.println("   Check application.properties for:");
                System.out.println("   - twilio.account.sid");
                System.out.println("   - twilio.auth.token");
                System.out.flush();
                log.error("❌ Twilio credentials are NULL/EMPTY! Check application.properties");
                return;
            }
            
            // Initialize Twilio SDK
            System.out.println("\n🔧 Initializing Twilio SDK...");
            System.out.flush();
            
            Twilio.init(accountSid, authToken);
            
            System.out.println("✅✅ SUCCESS: Twilio SDK initialized with Account SID: " + accountSid.substring(0, Math.min(5, accountSid.length())) + "***");
            System.out.println("    Ready to send SMS messages");
            System.out.println("╚════════════════════════════════════════════╝\n");
            System.out.flush();
            
            log.info("✅ Twilio SDK initialized successfully");
        } catch (Exception e) {
            System.out.println("❌ CRITICAL ERROR: Failed to initialize Twilio SDK!");
            System.out.println("   Exception: " + e.getClass().getSimpleName());
            System.out.println("   Message: " + e.getMessage());
            e.printStackTrace(System.out);
            System.out.println("╚════════════════════════════════════════════╝\n");
            System.out.flush();
            
            log.error("❌ Failed to initialize Twilio SDK: {}", e.getMessage(), e);
        }
    }
}
