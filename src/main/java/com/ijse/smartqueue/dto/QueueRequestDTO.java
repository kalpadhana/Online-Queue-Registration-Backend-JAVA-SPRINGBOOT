package com.ijse.smartqueue.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QueueRequestDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    // Manual getters for Lombok compatibility
    public Long getUserId() {
        return userId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public Long getBranchId() {
        return branchId;
    }
}
