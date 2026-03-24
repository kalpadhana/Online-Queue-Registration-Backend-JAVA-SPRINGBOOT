package com.ijse.smartqueue.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserPreferencesDTO {
    private Long preferenceId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private String theme;
    private String language;
    private String timeFormat;
    private Boolean autoRefresh;
    
    // Notification Settings
    private Boolean queueUpdates;
    private Boolean waitTimeAlerts;
    private Boolean promotions;
    private Boolean systemNotifications;
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    
    // Privacy Settings
    private Boolean profilePublic;
    private Boolean allowDataCollection;
    private Boolean showOnlineStatus;
    private Boolean shareAnalytics;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

