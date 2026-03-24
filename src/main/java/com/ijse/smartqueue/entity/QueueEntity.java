package com.ijse.smartqueue.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "queues", indexes = {
    @Index(name = "idx_token", columnList = "token"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_branch", columnList = "branch_id")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QueueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long queueId;

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false)
    private Integer position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueStatus status = QueueStatus.WAITING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityLevel priority = PriorityLevel.NORMAL;

    @Column(nullable = false)
    private LocalDateTime joinedTime;

    @Column
    private LocalDateTime calledTime;

    @Column
    private LocalDateTime serveStartTime;

    @Column
    private LocalDateTime completedTime;

    @Column
    private Integer estimatedWaitTime = 0;

    @Column
    private Integer actualServiceDuration = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Manual setters since Lombok @Setter isn't being generated
    public void setUser(User user) {
        this.user = user;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
    }

    public void setPriority(PriorityLevel priority) {
        this.priority = priority;
    }

    public void setJoinedTime(LocalDateTime joinedTime) {
        this.joinedTime = joinedTime;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Manual getters
    public ServiceEntity getService() {
        return service;
    }

    public QueueStatus getStatus() {
        return status;
    }

    // Additional setters/getters for completeness
    public void setCalledTime(LocalDateTime calledTime) {
        this.calledTime = calledTime;
    }

    public LocalDateTime getCalledTime() {
        return calledTime;
    }

    public void setServeStartTime(LocalDateTime serveStartTime) {
        this.serveStartTime = serveStartTime;
    }

    public LocalDateTime getServeStartTime() {
        return serveStartTime;
    }

    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }

    public LocalDateTime getCompletedTime() {
        return completedTime;
    }

    public void setActualServiceDuration(Integer actualServiceDuration) {
        this.actualServiceDuration = actualServiceDuration;
    }

    public Integer getActualServiceDuration() {
        return actualServiceDuration;
    }
}

