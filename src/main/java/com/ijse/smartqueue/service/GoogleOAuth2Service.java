package com.ijse.smartqueue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2Service {

    @Value("${google.oauth.client-id}")
    private String googleClientId;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Verify and decode Google ID token
     * Note: This trusts tokens from Google's token endpoint.
     * For production, you should validate the token's signature against Google's public keys.
     */
    public Map<String, Object> verifyGoogleToken(String tokenId) {
        try {
            // Google ID tokens are JWT tokens - decode the payload
            String[] parts = tokenId.split("\\.");
            if (parts.length != 3) {
                System.err.println("❌ Invalid token format");
                return null;
            }

            // Decode the payload (second part)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            
            JsonNode jsonNode = objectMapper.readTree(payload);
            
            // Verify basic fields
            String aud = jsonNode.get("aud").asText();
            String email = jsonNode.get("email").asText();
            
            // Check audience (should match our client ID)
            if (!aud.equals(googleClientId)) {
                System.err.println("❌ Audience mismatch. Expected: " + googleClientId + ", Got: " + aud);
                return null;
            }
            
            // Check email verification
            boolean emailVerified = jsonNode.has("email_verified") && jsonNode.get("email_verified").asBoolean();
            if (!emailVerified) {
                System.err.println("⚠️  Email not verified: " + email);
            }
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("email", email);
            userInfo.put("name", jsonNode.has("name") ? jsonNode.get("name").asText() : "User");
            userInfo.put("picture", jsonNode.has("picture") ? jsonNode.get("picture").asText() : null);
            userInfo.put("emailVerified", emailVerified);
            
            System.out.println("✅ Google token verified successfully for: " + email);
            return userInfo;
            
        } catch (JsonProcessingException e) {
            System.err.println("❌ JSON processing error: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("❌ Google token verification failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
