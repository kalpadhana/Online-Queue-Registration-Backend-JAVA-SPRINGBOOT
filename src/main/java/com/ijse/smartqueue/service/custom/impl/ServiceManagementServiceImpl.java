package com.ijse.smartqueue.service.custom.impl;

import com.ijse.smartqueue.dto.ServiceDTO;
import com.ijse.smartqueue.entity.ServiceEntity;
import com.ijse.smartqueue.exception.CustomException;
import com.ijse.smartqueue.repository.ServiceRepository;
import com.ijse.smartqueue.service.custom.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveService(ServiceDTO serviceDTO) {
        ServiceEntity service = modelMapper.map(serviceDTO, ServiceEntity.class);
        serviceRepository.save(service);
    }

    @Override
    public void updateService(ServiceDTO serviceDTO) {
        if (!serviceRepository.existsById(serviceDTO.getServiceId())) {
            throw new CustomException("Service not found");
        }
        ServiceEntity service = modelMapper.map(serviceDTO, ServiceEntity.class);
        serviceRepository.save(service);
    }

    @Override
    public void deleteService(Integer serviceId) {
        serviceRepository.deleteById(Long.valueOf(serviceId));
    }

    @Override
    public ServiceDTO getServiceDetails(Integer serviceId) {
        return serviceRepository.findById(Long.valueOf(serviceId))
                .map(s -> modelMapper.map(s, ServiceDTO.class))
                .orElseThrow(() -> new CustomException("Service not found"));
    }

    @Override
    public List<ServiceDTO> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(s -> modelMapper.map(s, ServiceDTO.class))
                .toList();
    }
}


