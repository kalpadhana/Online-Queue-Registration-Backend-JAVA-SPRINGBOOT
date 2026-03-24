package com.ijse.smartqueue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "priority_users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PriorityUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priorityId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityTier tier;

    @Column(nullable = false)
    @Builder.Default
    private Integer skipPositions = 0;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime activatedAt;

    @Column
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
