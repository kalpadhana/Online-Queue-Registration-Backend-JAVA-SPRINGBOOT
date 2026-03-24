package com.ijse.smartqueue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String icon;

    @Column(nullable = false)
    private Integer avgWaitTime = 0;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QueueEntity> queues = new ArrayList<>();

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceCounter> serviceCounters = new ArrayList<>();

    @ManyToMany(mappedBy = "services")
    private List<Branch> branches = new ArrayList<>();

    // Manual getter for serviceId
    public Long getServiceId() {
        return serviceId;
    }

    // Manual getter for avgWaitTime
    public Integer getAvgWaitTime() {
        return avgWaitTime;
    }
    
    // Manual setter for name
    public void setName(String name) {
        this.name = name;
    }
}

