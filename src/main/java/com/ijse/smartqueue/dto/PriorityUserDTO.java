package com.ijse.smartqueue.dto;

import com.ijse.smartqueue.entity.PriorityTier;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PriorityUserDTO {
    private Long priorityId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull
    private PriorityTier tier;
    
    private Integer skipPositions;
    private String benefits;
    private Boolean isActive;
    
    private LocalDateTime activatedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}

