package com.ijse.smartqueue.service.custom;

import com.ijse.smartqueue.dto.CounterDTO;

import java.util.List;

public interface CounterService {
    void saveCounter(CounterDTO counterDTO);

    void updateCounter(CounterDTO counterDTO);

    void deleteCounter(Integer counterId);

    void updateCounterStatus(Integer counterId, String status);

    List<CounterDTO> getCountersByService(Integer serviceId);

    List<CounterDTO> getAllCounters();
}