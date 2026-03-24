package com.ijse.smartqueue.repository;

import com.ijse.smartqueue.entity.QueueHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QueueHistoryRepository extends JpaRepository<QueueHistory, Long> {
    @Query("SELECT qh FROM QueueHistory qh WHERE qh.user.userId = :userId ORDER BY qh.joinedDate DESC")
    List<QueueHistory> findByUserIdOrderByJoinedDateDesc(@Param("userId") Long userId);

    @Query("SELECT qh FROM QueueHistory qh WHERE qh.branch.branchId = :branchId AND qh.joinedDate = :date")
    List<QueueHistory> findByBranchIdAndJoinedDate(@Param("branchId") Long branchId, @Param("date") LocalDate date);
    
    @Query("SELECT AVG(qh.waitDuration) FROM QueueHistory qh WHERE qh.branch.branchId = :branchId AND qh.joinedDate = :date")
    Double getAverageWaitTimeByBranchAndDate(@Param("branchId") Long branchId, @Param("date") LocalDate date);
    
    @Query("SELECT qh FROM QueueHistory qh WHERE qh.service.serviceId = :serviceId ORDER BY qh.joinedDate DESC")
    List<QueueHistory> findByServiceIdOrderByJoinedDateDesc(@Param("serviceId") Long serviceId);
}

