package com.ijse.smartqueue.dto;

import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CrowdLevelDTO {
    private Long crowdId;
    private Long branchId;
    private Integer capacity;
    private Integer percentageFilled;
    private String level;
    private String trend;
    private Integer avgWaitTime;
    private Integer totalPeopleWaiting;
    private LocalDateTime lastUpdated;
}

