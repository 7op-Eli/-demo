package com.property.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "visitor")
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "visitor_name", nullable = false, length = 50)
    private String visitorName;

    @Column(name = "visitor_phone", length = 20)
    private String visitorPhone;

    @Column(name = "id_card", length = 18)
    private String idCard;

    @Column(name = "vehicle_plate", length = 20)
    private String vehiclePlate;

    @Column(name = "vehicle_type", length = 50)
    private String vehicleType;

    @Column(name = "visit_purpose", length = 200)
    private String visitPurpose;

    @Column(name = "visit_count")
    private Integer visitCount = 1;

    @Column(name = "access_code", length = 20)
    private String accessCode;

    @Column(nullable = false)
    private Integer status = 0;

    @Column(name = "expected_arrival")
    private LocalDateTime expectedArrival;

    @Column(name = "actual_arrival")
    private LocalDateTime actualArrival;

    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Column(name = "audit_by")
    private Long auditBy;

    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Column(name = "audit_remark", length = 200)
    private String auditRemark;

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
