package com.ijse.smartqueue.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    private Long userId;

    @NotBlank(message = "Name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Phone is required")
    private String phone;

    private Long preferredBranchId;
    private String preferredBranchName;
    private Boolean isActive;
    private java.time.LocalDateTime memberSince;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    // Explicit getters for Lombok compatibility
    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
