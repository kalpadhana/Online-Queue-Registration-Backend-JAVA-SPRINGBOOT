package com.ijse.smartqueue.repository;

import com.ijse.smartqueue.entity.ServiceCounter;
import com.ijse.smartqueue.entity.CounterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceCounterRepository extends JpaRepository<ServiceCounter, Long> {

    @Query("SELECT sc FROM ServiceCounter sc WHERE sc.branch.branchId = :branchId")
    List<ServiceCounter> findByBranchId(@Param("branchId") Long branchId);

    @Query("SELECT sc FROM ServiceCounter sc WHERE sc.service.serviceId = :serviceId")
    List<ServiceCounter> findByServiceId(@Param("serviceId") Long serviceId);

    @Query("SELECT sc FROM ServiceCounter sc WHERE sc.branch.branchId = :branchId AND sc.service.serviceId = :serviceId")
    List<ServiceCounter> findByBranchIdAndServiceId(@Param("branchId") Long branchId, @Param("serviceId") Long serviceId);

    List<ServiceCounter> findByStatus(CounterStatus status);
    Optional<ServiceCounter> findByCurrentToken(String token);
}
