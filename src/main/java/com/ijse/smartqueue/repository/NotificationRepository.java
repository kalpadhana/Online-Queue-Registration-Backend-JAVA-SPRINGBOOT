package com.ijse.smartqueue.repository;

import com.ijse.smartqueue.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    // Get all notifications for a specific user
    List<NotificationEntity> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    // Get only 'UNREAD' notifications for a user
    List<NotificationEntity> findByUser_UserIdAndReadFlagFalse(Long userId);
}

