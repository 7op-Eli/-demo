package com.property.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "property_fee_bill")
public class PropertyFeeBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_no", nullable = false, unique = true, length = 50)
    private String billNo;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "fee_item_id", nullable = false)
    private Long feeItemId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "bill_period_start", nullable = false)
    private LocalDate billPeriodStart;

    @Column(name = "bill_period_end", nullable = false)
    private LocalDate billPeriodEnd;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private Integer status = 0;

    @Column(length = 500)
    private String remark;

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
