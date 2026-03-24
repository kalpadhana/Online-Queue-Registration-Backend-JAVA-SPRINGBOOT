package com.ijse.smartqueue.service.custom;

import com.ijse.smartqueue.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {
    void createNotification(NotificationDTO notificationDTO);

    void markAsRead(Long notificationId);

    void deleteNotification(Long notificationId);

    List<NotificationDTO> getNotificationsByUser(Long userId);
}