package com.ijse.smartqueue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_preferences")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long preferenceId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private String theme = "dark";

    @Column(nullable = false)
    @Builder.Default
    private String language = "English";

    @Column(nullable = false)
    @Builder.Default
    private String timeFormat = "12-hour";

    @Column(nullable = false)
    @Builder.Default
    private Boolean autoRefresh = true;

    // Notification Settings
    @Column(nullable = false)
    @Builder.Default
    private Boolean queueUpdates = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean waitTimeAlerts = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean promotions = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean systemNotifications = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean emailNotifications = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean smsNotifications = false;

    // Privacy Settings
    @Column(nullable = false)
    @Builder.Default
    private Boolean profilePublic = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean allowDataCollection = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean showOnlineStatus = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean shareAnalytics = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
