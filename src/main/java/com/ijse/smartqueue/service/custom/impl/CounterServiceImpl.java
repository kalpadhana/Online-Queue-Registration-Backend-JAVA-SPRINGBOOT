package com.ijse.smartqueue.service.custom.impl;

import com.ijse.smartqueue.dto.CounterDTO;
import com.ijse.smartqueue.entity.ServiceCounter;
import com.ijse.smartqueue.entity.ServiceEntity;
import com.ijse.smartqueue.entity.CounterStatus;
import com.ijse.smartqueue.exception.CustomException;
import com.ijse.smartqueue.repository.ServiceCounterRepository;
import com.ijse.smartqueue.repository.ServiceRepository;
import com.ijse.smartqueue.service.custom.CounterService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CounterServiceImpl implements CounterService {

    private final ServiceCounterRepository counterRepository;
    private final ServiceRepository serviceRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveCounter(CounterDTO counterDTO) {
        ServiceEntity service = serviceRepository.findById(counterDTO.getServiceId())
                .orElseThrow(() -> new CustomException("Service not found for this counter"));

        ServiceCounter counter = new ServiceCounter();
        counter.setCounterNumber(counterDTO.getCounterNumber());
        counter.setStatus(CounterStatus.AVAILABLE);
        counter.setService(service);
        counter.setIsActive(true);
        counterRepository.save(counter);
    }

    @Override
    public void updateCounter(CounterDTO counterDTO) {
        if (!counterRepository.existsById(counterDTO.getCounterId())) {
            throw new CustomException("Counter not found");
        }

        ServiceEntity service = serviceRepository.findById(counterDTO.getServiceId())
                .orElseThrow(() -> new CustomException("Service not found"));

        ServiceCounter counter = new ServiceCounter();
        counter.setCounterId(counterDTO.getCounterId());
        counter.setCounterNumber(counterDTO.getCounterNumber());
        counter.setStatus(CounterStatus.valueOf(counterDTO.getStatus()));
        counter.setService(service);
        counter.setIsActive(counterDTO.getIsActive() != null ? counterDTO.getIsActive() : true);
        counterRepository.save(counter);
    }

    @Override
    public void deleteCounter(Integer counterId) {
        if (!counterRepository.existsById(Long.valueOf(counterId))) {
            throw new CustomException("Counter not found");
        }
        counterRepository.deleteById(Long.valueOf(counterId));
    }

    @Override
    public void updateCounterStatus(Integer counterId, String status) {
        ServiceCounter counter = counterRepository.findById(Long.valueOf(counterId))
                .orElseThrow(() -> new CustomException("Counter not found"));
        counter.setStatus(CounterStatus.valueOf(status));
        counterRepository.save(counter);
    }

    @Override
    public List<CounterDTO> getCountersByService(Integer serviceId) {
        return counterRepository.findByServiceId(Long.valueOf(serviceId)).stream()
                .map(counter -> modelMapper.map(counter, CounterDTO.class))
                .toList();
    }

    @Override
    public List<CounterDTO> getAllCounters() {
        return counterRepository.findAll().stream()
                .map(counter -> modelMapper.map(counter, CounterDTO.class))
                .toList();
    }
}


