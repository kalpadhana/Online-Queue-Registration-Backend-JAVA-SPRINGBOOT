package com.ijse.smartqueue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "branches")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long branchId;

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @Column
    private String icon;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QueueEntity> queues = new ArrayList<>();

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ServiceCounter> serviceCounters = new ArrayList<>();

    @OneToOne(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private CrowdLevel crowdLevel;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AdminStaff> adminStaff = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "branch_services",
        joinColumns = @JoinColumn(name = "branch_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    @Builder.Default
    private List<ServiceEntity> services = new ArrayList<>();

    // Manual getter for branchId
    public Long getBranchId() {
        return branchId;
    }

    // Manual setter for isActive
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
