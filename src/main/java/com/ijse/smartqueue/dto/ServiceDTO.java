package com.ijse.smartqueue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServiceDTO {
    private Long serviceId;

    @NotBlank(message = "Service name is required")
    private String name;

    @Size(max = 255, message = "Description is too long")
    private String description;

    private String icon;
    private Integer avgWaitTime;
    private Boolean isActive;

    // Explicit getter for serviceId
    public Long getServiceId() {
        return serviceId;
    }
}
