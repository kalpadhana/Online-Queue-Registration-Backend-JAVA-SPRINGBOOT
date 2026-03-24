package com.ijse.smartqueue.repository;

import com.ijse.smartqueue.entity.PriorityUser;
import com.ijse.smartqueue.entity.PriorityTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriorityUserRepository extends JpaRepository<PriorityUser, Long> {
    @Query("SELECT pu FROM PriorityUser pu WHERE pu.user.userId = :userId")
    Optional<PriorityUser> findByUserId(@Param("userId") Long userId);

    List<PriorityUser> findByTier(PriorityTier tier);
    List<PriorityUser> findByIsActiveTrue();
    List<PriorityUser> findByExpiresAtBefore(LocalDateTime dateTime);
}
