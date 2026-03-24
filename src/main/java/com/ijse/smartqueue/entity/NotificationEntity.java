package com.ijse.smartqueue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_user", columnList = "user_id"),
    @Index(name = "idx_read_flag", columnList = "read_flag"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private NotificationType type = NotificationType.INFO;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @ManyToOne
    @JoinColumn(name = "queue_id")
    private QueueEntity queue;

    @Column(name = "read_flag", nullable = false)
    @Builder.Default
    private Boolean readFlag = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Manual setter for readFlag
    public void setReadFlag(Boolean readFlag) {
        this.readFlag = readFlag;
    }
    
    // Manual setters for other fields
    public void setUser(User user) {
        this.user = user;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
}
