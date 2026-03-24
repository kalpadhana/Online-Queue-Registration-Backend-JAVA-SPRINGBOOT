package com.ijse.smartqueue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_counters")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ServiceCounter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long counterId;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(nullable = false)
    private Integer counterNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CounterStatus status = CounterStatus.AVAILABLE;

    @Column
    private String currentToken;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Manual setters
    public void setCounterNumber(Integer counterNumber) {
        this.counterNumber = counterNumber;
    }

    public void setStatus(CounterStatus status) {
        this.status = status;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public void setCounterId(Long counterId) {
        this.counterId = counterId;
    }
}
