package com.ijse.smartqueue.service.custom.impl;

import com.ijse.smartqueue.dto.NotificationDTO;
import com.ijse.smartqueue.entity.NotificationEntity;
import com.ijse.smartqueue.entity.NotificationType;
import com.ijse.smartqueue.entity.QueueEntity;
import com.ijse.smartqueue.entity.User;
import com.ijse.smartqueue.repository.NotificationRepository;
import com.ijse.smartqueue.repository.QueueEntityRepository;
import com.ijse.smartqueue.repository.UserRepository;
import com.ijse.smartqueue.service.custom.NotificationService;
import com.ijse.smartqueue.service.custom.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@SuppressWarnings("null")
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final QueueEntityRepository queueEntityRepository;
    private final EmailService emailService;

    @Override
    public void createNotification(NotificationDTO notificationDTO) {
        User user = userRepository.findById(notificationDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        QueueEntity queue = null;
        if (notificationDTO.getQueueId() != null) {
            queue = queueEntityRepository.findById(notificationDTO.getQueueId())
                    .orElse(null);
        }

        NotificationEntity notification = NotificationEntity.builder()
                .user(user)
                .title(notificationDTO.getTitle())
                .message(notificationDTO.getMessage())
                .type(notificationDTO.getType() != null ? NotificationType.valueOf(notificationDTO.getType()) : NotificationType.INFO)
                .queue(queue)
                .readFlag(false)
                .build();

        notificationRepository.save(notification);
        
        // Send email notification
        sendEmailNotification(user, notificationDTO, queue);
    }

    @Override
    public void markAsRead(Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setReadFlag(true);
        notificationRepository.save(notification);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new RuntimeException("Notification not found");
        }
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public List<NotificationDTO> getNotificationsByUser(Long userId) {
        // Find existing method in repository or use simple find by user id
        // The repository has findByUser_UserIdOrderByCreatedAtDesc(Long userId) but I need to check the method name in repository
        // Assuming findByUser_UserIdOrderByCreatedAtDesc is correct from read_file output
        List<NotificationEntity> notifications = notificationRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);
        
        return notifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private NotificationDTO convertToDTO(NotificationEntity entity) {
        return NotificationDTO.builder()
                .notificationId(entity.getNotificationId())
                .userId(entity.getUser().getUserId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .type(entity.getType().name())
                .queueId(entity.getQueue() != null ? entity.getQueue().getQueueId() : null)
                .isRead(entity.getReadFlag())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private void sendEmailNotification(User user, NotificationDTO notificationDTO, QueueEntity queue) {
        try {
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                System.out.println("User email not available, skipping email notification");
                return;
            }

            String userName = user.getFullName() != null ? user.getFullName() : user.getEmail();
            
            // Check the notification title to determine which email to send
            if ("Time Ready".equals(notificationDTO.getTitle())) {
                // Queue called notification
                if (queue != null) {
                    emailService.sendQueueCalledEmail(
                        user.getEmail(),
                        userName,
                        queue.getToken(),
                        queue.getService().getName(),
                        queue.getBranch().getName()
                    );
                }
            } else if ("Queue Removed".equals(notificationDTO.getTitle())) {
                // Queue removed notification
                if (queue != null) {
                    emailService.sendQueueRemovedEmail(
                        user.getEmail(),
                        userName,
                        queue.getToken(),
                        notificationDTO.getMessage()
                    );
                }
            } else {
                // Generic notification
                emailService.sendSimpleEmail(
                    user.getEmail(),
                    notificationDTO.getTitle(),
                    notificationDTO.getMessage()
                );
            }
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


