package com.ijse.smartqueue.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BranchDTO {
    private Long branchId;
    private String name;
    private String address;
    private String icon;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<ServiceDTO> services;
}

