package com.property.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "id_card", length = 18)
    private String idCard;

    @Column
    private Integer gender;

    @Column
    private LocalDate birthday;

    @Column(length = 100)
    private String email;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "owner_type")
    private Integer ownerType = 1;

    @Column(name = "emergency_contact", length = 50)
    private String emergencyContact;

    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    @Column(nullable = false)
    private Integer status = 1;

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
