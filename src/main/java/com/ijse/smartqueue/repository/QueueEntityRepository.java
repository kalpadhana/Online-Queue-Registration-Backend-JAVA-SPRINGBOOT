package com.ijse.smartqueue.repository;

import com.ijse.smartqueue.entity.QueueEntity;
import com.ijse.smartqueue.entity.QueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueEntityRepository extends JpaRepository<QueueEntity, Long> {
    Optional<QueueEntity> findByToken(String token);

    @Query("SELECT q FROM QueueEntity q WHERE q.branch.branchId = :branchId AND q.status = :status")
    List<QueueEntity> findByBranchIdAndStatus(@Param("branchId") Long branchId, @Param("status") QueueStatus status);

    @Query("SELECT q FROM QueueEntity q WHERE q.user.userId = :userId")
    List<QueueEntity> findByUserId(@Param("userId") Long userId);

    @Query("SELECT q FROM QueueEntity q WHERE q.service.serviceId = :serviceId AND q.branch.branchId = :branchId AND q.status = :status")
    List<QueueEntity> findByServiceIdAndBranchIdAndStatus(@Param("serviceId") Long serviceId, @Param("branchId") Long branchId, @Param("status") QueueStatus status);

    @Query("SELECT COUNT(q) FROM QueueEntity q WHERE q.branch.branchId = :branchId AND q.status = :status")
    Integer countByBranchIdAndStatus(@Param("branchId") Long branchId, @Param("status") QueueStatus status);

    @Query("SELECT COUNT(q) FROM QueueEntity q WHERE q.branch.branchId = :branchId AND q.status IN (:statuses)")
    Integer countByBranchIdAndStatusIn(@Param("branchId") Long branchId, @Param("statuses") List<QueueStatus> statuses);
}
