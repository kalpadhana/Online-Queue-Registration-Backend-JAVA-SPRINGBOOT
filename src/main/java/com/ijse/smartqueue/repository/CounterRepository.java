package com.ijse.smartqueue.repository;

import com.ijse.smartqueue.entity.ServiceCounter;
import com.ijse.smartqueue.entity.CounterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CounterRepository extends JpaRepository<ServiceCounter, Long> {

    // Get all counters for a specific service
    List<ServiceCounter> findByService_ServiceId(Long serviceId);

    // Find counters by status
    List<ServiceCounter> findByStatus(CounterStatus status);
}

