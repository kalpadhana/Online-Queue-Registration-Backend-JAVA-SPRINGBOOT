package com.ijse.smartqueue.dto;

import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QueueDTO {
    private Long queueId;
    private String token;
    private Long userId;
    private Long serviceId;
    private Long branchId;
    private Integer position;
    private String status;
    private String priority;
    private Boolean isPriority;
    private LocalDateTime joinedTime;
    private LocalDateTime calledTime;
    private LocalDateTime serveStartTime;
    private LocalDateTime completedTime;
    private Integer estimatedWaitTime;
    private Integer actualServiceDuration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

