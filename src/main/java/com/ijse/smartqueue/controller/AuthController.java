package com.ijse.smartqueue.controller;

import com.ijse.smartqueue.dto.LoginRequest;
import com.ijse.smartqueue.dto.AuthResponse;
import com.ijse.smartqueue.entity.User;
import com.ijse.smartqueue.repository.UserRepository;
import com.ijse.smartqueue.service.GoogleOAuth2Service;
import com.ijse.smartqueue.service.JwtTokenProvider;
import com.ijse.smartqueue.util.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://localhost:5174"})
public class AuthController {

    private final GoogleOAuth2Service googleOAuth2Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /**
     * Google OAuth2 Login - Verify token and create/update user
     */
    @PostMapping("/google/login")
    public ResponseEntity<APIResponse<AuthResponse>> googleLogin(@RequestBody LoginRequest request) {
        try {
            System.out.println("🔵 Google login request received");

            // Verify Google token
            Map<String, Object> userInfo = googleOAuth2Service.verifyGoogleToken(request.getToken());
            
            if (userInfo == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new APIResponse<>(401, "Invalid Google token", null));
            }

            String email = (String) userInfo.get("email");
            String fullName = (String) userInfo.get("name");
            String picture = (String) userInfo.get("picture");

            System.out.println("✅ Google token verified - Email: " + email);

            // Find existing user or create new one
            Optional<User> existingUser = userRepository.findByEmail(email);
            User user;

            if (existingUser.isPresent()) {
                // Update existing user
                user = existingUser.get();
                System.out.println("✅ User found - returning existing user");
            } else {
                // Create new user with auto-registration
                user = new User();
                user.setEmail(email);
                user.setFullName(fullName);
                user.setPhone(""); // Phone not provided by Google
                user.setPassword("GOOGLE_OAUTH"); // Mark as OAuth user
                user.setIsActive(true);
                user.setMemberSince(LocalDateTime.now());
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                
                userRepository.save(user);
                System.out.println("✅ New user created - Email: " + email);
            }

            // Generate JWT token
            String jwtToken = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail(), user.getFullName());

            // Prepare response
            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(jwtToken);
            authResponse.setUserId(user.getUserId());
            authResponse.setEmail(user.getEmail());
            authResponse.setFullName(user.getFullName());
            authResponse.setMessage("Login successful");

            System.out.println("✅ JWT token generated and returning to client");

            return ResponseEntity.ok(new APIResponse<>(200, "Google login successful", authResponse));

        } catch (Exception e) {
            System.err.println("❌ Google login failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(500, "Login failed: " + e.getMessage(), null));
        }
    }

    /**
     * Verify JWT token
     */
    @GetMapping("/verify")
    public ResponseEntity<APIResponse<Map<String, Object>>> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new APIResponse<>(401, "Missing or invalid token", null));
            }

            String token = authHeader.substring(7);
            boolean isValid = jwtTokenProvider.validateToken(token);

            if (isValid) {
                Map<String, Object> response = Map.of(
                        "valid", true,
                        "userId", jwtTokenProvider.getUserIdFromToken(token),
                        "email", jwtTokenProvider.getEmailFromToken(token)
                );
                return ResponseEntity.ok(new APIResponse<>(200, "Token is valid", response));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new APIResponse<>(401, "Invalid or expired token", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new APIResponse<>(401, "Token verification failed", null));
        }
    }

    /**
     * Logout (client-side - just remove token from localStorage)
     */
    @PostMapping("/logout")
    public ResponseEntity<APIResponse<Void>> logout() {
        System.out.println("🔵 User logged out");
        return ResponseEntity.ok(new APIResponse<>(200, "Logged out successfully", null));
    }
}
