package com.ijse.smartqueue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "crowd_levels")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CrowdLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long crowdId;

    @OneToOne
    @JoinColumn(name = "branch_id", nullable = false, unique = true)
    private Branch branch;

    @Column(nullable = false)
    @Builder.Default
    private Integer capacity = 100;

    @Column(nullable = false)
    private Integer percentageFilled;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CrowdLevelEnum level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Trend trend = Trend.STABLE;

    @Column(nullable = false)
    @Builder.Default
    private Integer avgWaitTime = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalPeopleWaiting = 0;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
