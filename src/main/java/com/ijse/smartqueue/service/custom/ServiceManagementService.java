package com.ijse.smartqueue.service.custom;

import com.ijse.smartqueue.dto.ServiceDTO;

import java.util.List;

public interface ServiceManagementService {
    void saveService(ServiceDTO serviceDTO);

    void updateService(ServiceDTO serviceDTO);

    void deleteService(Integer serviceId);

    ServiceDTO getServiceDetails(Integer serviceId);

    List<ServiceDTO> getAllServices();
}