package com.ijse.smartqueue.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service class to handle Twilio SMS and Call functionality
 * Sends SMS notifications to users when their queue is called
 */
@Slf4j
@Service
public class TwilioService {

    @Value("${twilio.account.sid:}")
    private String accountSid;

    @Value("${twilio.auth.token:}")
    private String authToken;

    @Value("${twilio.phone.number:}")
    private String twilioPhoneNumber;

    public TwilioService() {
        System.out.println("🟢 TwilioService instance created");
        System.out.flush();
    }

    /**
     * Format phone number to E.164 format required by Twilio
     */
    private String formatPhoneNumber(String phoneNumber) {
        System.out.println("   🔍 formatPhoneNumber() CALLED with: " + phoneNumber);
        System.out.flush();
        
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            System.out.println("   ❌ Phone number is null or empty");
            System.out.flush();
            return null;
        }

        // Remove all non-digit characters except leading +
        String cleaned = phoneNumber.replaceAll("[^+\\d]", "");
        System.out.println("   ✅ After cleaning: " + cleaned);
        System.out.flush();
        
        if (cleaned.isEmpty()) {
            System.out.println("   ❌ Phone number contains no digits");
            System.out.flush();
            return null;
        }

        // Check if already in E.164 format
        if (cleaned.startsWith("+")) {
            String digitsOnly = cleaned.substring(1);
            if (digitsOnly.length() >= 7 && digitsOnly.length() <= 15) {
                System.out.println("   ✅ Valid E.164 format: " + cleaned);
                System.out.flush();
                return cleaned;
            } else {
                System.out.println("   ❌ Invalid digit count: " + digitsOnly.length());
                System.out.flush();
                return null;
            }
        }

        // Handle Sri Lankan numbers
        if (cleaned.length() == 10) {
            if (cleaned.startsWith("0")) {
                cleaned = cleaned.substring(1);
            }
            cleaned = "+94" + cleaned;
            System.out.println("   ✅ Formatted as Sri Lankan (10 digits): " + cleaned);
            System.out.flush();
            return cleaned;
        } else if (cleaned.length() == 9) {
            cleaned = "+94" + cleaned;
            System.out.println("   ✅ Formatted as Sri Lankan (9 digits): " + cleaned);
            System.out.flush();
            return cleaned;
        } else if (cleaned.length() == 11 && cleaned.startsWith("94")) {
            cleaned = "+" + cleaned;
            System.out.println("   ✅ Added + to country code: " + cleaned);
            System.out.flush();
            return cleaned;
        } else if (cleaned.length() >= 7 && cleaned.length() <= 15) {
            cleaned = "+" + cleaned;
            System.out.println("   ✅ Added + sign: " + cleaned);
            System.out.flush();
            return cleaned;
        } else {
            System.out.println("   ❌ Invalid phone length: " + cleaned.length() + " digits");
            System.out.flush();
            return null;
        }
    }

    /**
     * Send SMS notification to user that their queue is ready
     */
    public boolean sendQueueReadySMS(String userPhoneNumber, String queueToken, String serviceName, String branchName) {
        System.err.println("\n🔴 TWILIO: sendQueueReadySMS() CALLED");
        System.err.flush();
        log.info("\n\n╔════════════════════════════════════════════╗");
        log.info("║  🔴 TWILIO SERVICE: sendQueueReadySMS()    ║");
        log.info("╚════════════════════════════════════════════╝");
        System.out.println("\n\n╔════════════════════════════════════════════╗");
        System.out.println("║  🔴 TWILIO SERVICE: sendQueueReadySMS()    ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.flush();
        
        try {
            // Step 1: Check Twilio credentials are loaded
            System.err.println("🔴 STEP 1: Checking credentials...");
            System.err.flush();
            
            String credStatus1 = accountSid != null && !accountSid.isEmpty() ? "✅ LOADED (" + accountSid.substring(0, Math.min(5, accountSid.length())) + "***)" : "❌ NULL/EMPTY";
            String credStatus2 = authToken != null && !authToken.isEmpty() ? "✅ LOADED (" + authToken.substring(0, Math.min(5, authToken.length())) + "***)" : "❌ NULL/EMPTY";
            String credStatus3 = twilioPhoneNumber != null && !twilioPhoneNumber.isEmpty() ? "✅ " + twilioPhoneNumber : "❌ NULL/EMPTY";
            
            System.err.println("   accountSid: " + credStatus1);
            System.err.println("   authToken: " + credStatus2);
            System.err.println("   twilioPhone: " + credStatus3);
            System.err.flush();
            
            log.info("\nSTEP 1️⃣: Checking Twilio credentials...");
            log.info("   ├─ accountSid: {}", credStatus1);
            log.info("   ├─ authToken: {}", credStatus2);
            log.info("   └─ twilioPhoneNumber: {}", credStatus3);
            System.out.println("\nSTEP 1️⃣: Checking Twilio credentials...");
            System.out.println("   ├─ accountSid: " + credStatus1);
            System.out.println("   ├─ authToken: " + credStatus2);
            System.out.println("   └─ twilioPhoneNumber: " + credStatus3);
            System.out.flush();
            
            if (accountSid == null || accountSid.isEmpty() || authToken == null || authToken.isEmpty() || twilioPhoneNumber == null || twilioPhoneNumber.isEmpty()) {
                System.err.println("❌ STEP 1 FAILED: Empty credentials!");
                System.err.flush();
                log.error("\n❌ STEP 1 FAILED: Twilio credentials not loaded!");
                log.error("╚════════════════════════════════════════════╝\n");
                System.out.println("\n❌ STEP 1 FAILED: Twilio credentials not loaded!");
                System.out.println("╚════════════════════════════════════════════╝\n");
                System.out.flush();
                return false;
            }
            
            System.err.println("✅ STEP 1 PASSED");
            System.err.flush();
            log.info("✅ STEP 1 PASSED: All credentials loaded\n");
            System.out.println("✅ STEP 1 PASSED: All credentials loaded\n");
            System.out.flush();
            
            // Step 2: Validate and format phone number
            System.err.println("🔴 STEP 2: Formatting phone " + userPhoneNumber);
            System.err.flush();
            
            log.info("STEP 2️⃣: Formatting and validating phone number...");
            log.info("   ├─ Input: {}", userPhoneNumber);
            System.out.println("STEP 2️⃣: Formatting and validating phone number...");
            System.out.println("   ├─ Input: " + userPhoneNumber);
            System.out.flush();
            
            if (userPhoneNumber == null || userPhoneNumber.trim().isEmpty()) {
                System.err.println("❌ STEP 2: Phone is null/empty");
                System.err.flush();
                log.error("   └─ ❌ Phone is null/empty");
                log.error("\n❌ STEP 2 FAILED: Invalid phone");
                log.error("╚════════════════════════════════════════════╝\n");
                System.out.println("   └─ ❌ Phone is null/empty");
                System.out.println("\n❌ STEP 2 FAILED: Invalid phone");
                System.out.println("╚════════════════════════════════════════════╝\n");
                System.out.flush();
                return false;
            }

            String formattedPhoneNumber = formatPhoneNumber(userPhoneNumber);
            System.err.println("   Formatted: " + formattedPhoneNumber);
            System.err.flush();
            
            log.info("   └─ Output: {}", formattedPhoneNumber);
            System.out.println("   └─ Output: " + formattedPhoneNumber);
            System.out.flush();
            
            if (formattedPhoneNumber == null || formattedPhoneNumber.isEmpty()) {
                System.err.println("❌ STEP 2 FAILED: Format returned null");
                System.err.flush();
                log.error("\n❌ STEP 2 FAILED: Phone formatting failed");
                log.error("╚════════════════════════════════════════════╝\n");
                System.out.println("\n❌ STEP 2 FAILED: Phone formatting failed");
                System.out.println("╚════════════════════════════════════════════╝\n");
                System.out.flush();
                return false;
            }

            // Validate E.164 pattern
            if (!formattedPhoneNumber.matches("^\\+[1-9]\\d{1,14}$")) {
                System.err.println("❌ STEP 2: E.164 validation failed for: " + formattedPhoneNumber);
                System.err.flush();
                log.error("❌ E.164 pattern validation failed");
                log.error("\n❌ STEP 2 FAILED: E.164 format invalid");
                log.error("╚════════════════════════════════════════════╝\n");
                System.out.println("❌ E.164 pattern validation failed");
                System.out.println("\n❌ STEP 2 FAILED: E.164 format invalid");
                System.out.println("╚════════════════════════════════════════════╝\n");
                System.out.flush();
                return false;
            }

            System.err.println("✅ STEP 2 PASSED");
            System.err.flush();
            log.info("✅ STEP 2 PASSED: Phone formatted and validated\n");
            System.out.println("✅ STEP 2 PASSED: Phone formatted and validated\n");
            System.out.flush();

            // Step 3: Initialize Twilio SDK
            System.err.println("🔴 STEP 3: Initializing Twilio...");
            System.err.flush();
            
            log.info("STEP 3️⃣: Initializing Twilio SDK...");
            System.out.println("STEP 3️⃣: Initializing Twilio SDK...");
            System.out.flush();
            
            Twilio.init(accountSid, authToken);
            System.err.println("✅ Twilio.init() succeeded");
            System.err.flush();
            
            log.info("   └─ ✅ Twilio.init() completed\n");
            System.out.println("   └─ ✅ Twilio.init() completed\n");
            System.out.flush();

            // Step 4: Prepare SMS message
            System.err.println("🔴 STEP 4: Preparing message...");
            System.err.flush();
            
            log.info("STEP 4️⃣: Preparing SMS message...");
            String messageBody = String.format(
                "📱 Your queue is ready! Token: %s\nService: %s\nBranch: %s\nPlease come to the counter now.",
                queueToken, serviceName, branchName
            );
            log.info("   ├─ To: {}", formattedPhoneNumber);
            log.info("   ├─ From: {}", twilioPhoneNumber);
            log.info("   └─ Body: {}...", messageBody.substring(0, Math.min(50, messageBody.length())));
            System.out.println("STEP 4️⃣: Preparing SMS message...");
            System.out.println("   ├─ To: " + formattedPhoneNumber);
            System.out.println("   ├─ From: " + twilioPhoneNumber);
            System.out.println("   └─ Body: " + messageBody.substring(0, Math.min(50, messageBody.length())) + "...");
            System.out.flush();

            // Step 5: Send SMS to Twilio API
            System.err.println("🔴 STEP 5: Sending to Twilio API...");
            System.err.flush();
            
            log.info("\nSTEP 5️⃣: Sending SMS to Twilio API...");
            log.info("   ⏳ Creating message...");
            System.out.println("\nSTEP 5️⃣: Sending SMS to Twilio API...");
            System.out.println("   ⏳ Creating message...");
            System.out.flush();
            
            Message message = Message.creator(
                    new PhoneNumber(formattedPhoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    messageBody)
                    .create();

            System.err.println("✅ Message created! SID: " + message.getSid());
            System.err.flush();
            
            log.info("   ✅ Message created successfully!");
            log.info("   ├─ Message SID: {}", message.getSid());
            log.info("   ├─ Status: {}", message.getStatus());
            log.info("   ├─ To: {}", message.getTo());
            log.info("   └─ From: {}", message.getFrom());
            System.out.println("   ✅ Message created successfully!");
            System.out.println("   ├─ Message SID: " + message.getSid());
            System.out.println("   ├─ Status: " + message.getStatus());
            System.out.println("   ├─ To: " + message.getTo());
            System.out.println("   └─ From: " + message.getFrom());
            System.out.flush();

            System.err.println("✅ SMS COMPLETELY SUCCESSFUL!");
            System.err.flush();
            
            log.info("\n╔════════════════════════════════════════════╗");
            log.info("║ 🎉 SUCCESS: SMS SENT TO TWILIO SUCCESSFULLY  ║");
            log.info("╚════════════════════════════════════════════╝\n");
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║ 🎉 SUCCESS: SMS SENT TO TWILIO SUCCESSFULLY  ║");
            System.out.println("╚════════════════════════════════════════════╝\n");
            System.out.flush();
            
            log.info("✅✅ SMS sent to Twilio: To={}, SID={}, Status={}", formattedPhoneNumber, message.getSid(), message.getStatus());
            return true;

        } catch (Exception e) {
            log.error("\n❌ EXCEPTION CAUGHT!");
            log.error("   ├─ Type: {}", e.getClass().getSimpleName());
            log.error("   ├─ Message: {}", e.getMessage());
            log.error("   └─ Cause: {}", (e.getCause() != null ? e.getCause().getMessage() : "None"));
            log.error("   Stack Trace:", e);
            System.out.println("\n❌ EXCEPTION CAUGHT!");
            System.out.println("   ├─ Type: " + e.getClass().getSimpleName());
            System.out.println("   ├─ Message: " + e.getMessage());
            System.out.println("   └─ Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "None"));
            e.printStackTrace(System.out);
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║ ❌ FAILED: SMS NOT SENT - EXCEPTION OCCURRED ║");
            System.out.println("╚════════════════════════════════════════════╝\n");
            System.out.flush();
            
            log.error("❌ TWILIO ERROR: Failed to send SMS: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send reminder SMS to user
     */
    public boolean sendPositionReminderSMS(String userPhoneNumber, int position) {
        try {
            if (userPhoneNumber == null || userPhoneNumber.trim().isEmpty()) {
                return false;
            }

            String formattedPhoneNumber = formatPhoneNumber(userPhoneNumber);
            if (formattedPhoneNumber == null) {
                return false;
            }

            Twilio.init(accountSid, authToken);

            String messageBody = String.format(
                "📍 Queue Update: You are at position %d. Please check back soon!",
                position
            );

            Message message = Message.creator(
                    new PhoneNumber(formattedPhoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    messageBody)
                    .create();

            log.info("✅✅ Position reminder SMS sent to {}", formattedPhoneNumber);
            return true;

        } catch (Exception e) {
            log.error("❌ TWILIO ERROR: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send cancellation SMS to user
     */
    public boolean sendQueueCancelledSMS(String userPhoneNumber, String queueToken) {
        try {
            if (userPhoneNumber == null || userPhoneNumber.trim().isEmpty()) {
                return false;
            }

            String formattedPhoneNumber = formatPhoneNumber(userPhoneNumber);
            if (formattedPhoneNumber == null) {
                return false;
            }

            Twilio.init(accountSid, authToken);

            String messageBody = String.format(
                "❌ Your queue (Token: %s) has been cancelled by admin. Please rejoin if needed.",
                queueToken
            );

            Message message = Message.creator(
                    new PhoneNumber(formattedPhoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    messageBody)
                    .create();

            log.info("✅✅ Cancellation SMS sent to {}", formattedPhoneNumber);
            return true;

        } catch (Exception e) {
            log.error("❌ TWILIO ERROR: {}", e.getMessage(), e);
            return false;
        }
    }
}
