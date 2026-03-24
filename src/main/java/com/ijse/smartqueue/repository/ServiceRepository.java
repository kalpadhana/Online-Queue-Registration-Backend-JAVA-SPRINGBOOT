package com.ijse.smartqueue.repository;

import com.ijse.smartqueue.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    // Find the last added service (useful for ID tracking)
    ServiceEntity findTopByOrderByServiceIdDesc();

    // Check if a service name exists
    boolean existsByName(String name);
}

