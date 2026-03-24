package com.ijse.smartqueue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CounterDTO {
    private Long counterId;

    @NotBlank(message = "Counter number is required")
    private Integer counterNumber;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Branch ID is required")
    private Long branchId;

    private Boolean isActive;
}

