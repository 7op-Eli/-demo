package com.property.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "repair_order")
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, unique = true, length = 50)
    private String orderNo;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "contact_name", length = 50)
    private String contactName;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "repair_type", length = 50)
    private String repairType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_urls", columnDefinition = "JSON")
    private String imageUrls;

    @Column(name = "urgency_level")
    private Integer urgencyLevel = 1;

    @Column(nullable = false)
    private Integer status = 0;

    @Column(name = "assignee_id")
    private Long assigneeId;

    @Column(name = "estimated_cost", precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "actual_cost", precision = 10, scale = 2)
    private BigDecimal actualCost;

    @Column(name = "csr_id")
    private Long csrId;

    @Column(name = "dispatch_time")
    private LocalDateTime dispatchTime;

    @Column(name = "accept_time")
    private LocalDateTime acceptTime;

    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    @Column(name = "follow_up_time")
    private LocalDateTime followUpTime;

    @Column(name = "follow_up_note", columnDefinition = "TEXT")
    private String followUpNote;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
