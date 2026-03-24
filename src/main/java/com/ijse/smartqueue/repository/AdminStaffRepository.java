package com.ijse.smartqueue.repository;

import com.ijse.smartqueue.entity.AdminStaff;
import com.ijse.smartqueue.entity.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminStaffRepository extends JpaRepository<AdminStaff, Long> {
    Optional<AdminStaff> findByEmail(String email);

    @Query("SELECT a FROM AdminStaff a WHERE a.branch.branchId = :branchId")
    List<AdminStaff> findByBranchId(@Param("branchId") Long branchId);

    List<AdminStaff> findByRole(StaffRole role);
    List<AdminStaff> findByIsActiveTrue();
}
