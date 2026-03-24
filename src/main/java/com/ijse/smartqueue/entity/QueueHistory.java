package com.ijse.smartqueue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "queue_history", indexes = {
    @Index(name = "idx_user_history", columnList = "user_id"),
    @Index(name = "idx_joined_date", columnList = "joined_date")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QueueHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column
    private String token;

    @Column(nullable = false)
    private LocalDate joinedDate;

    @Column
    private Integer waitDuration;  // in minutes

    @Column
    private Integer serviceDuration;  // in minutes

    @Column
    private String status;

    @Column
    private String priority;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

