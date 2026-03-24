package com.ijse.smartqueue.repository;

import com.ijse.smartqueue.entity.CrowdLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CrowdLevelRepository extends JpaRepository<CrowdLevel, Long> {

    @Query("SELECT c FROM CrowdLevel c WHERE c.branch.branchId = :branchId")
    Optional<CrowdLevel> findByBranchId(@Param("branchId") Long branchId);
}
