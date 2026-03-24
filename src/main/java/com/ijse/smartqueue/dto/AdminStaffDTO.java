package com.ijse.smartqueue.dto;

import com.ijse.smartqueue.entity.StaffRole;
import com.ijse.smartqueue.entity.StaffStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AdminStaffDTO {
    private Long staffId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Role is required")
    private StaffRole role;

    private String password; // Optional on update, required on create (handle in service)

    private StaffStatus status;

    private String phone;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

