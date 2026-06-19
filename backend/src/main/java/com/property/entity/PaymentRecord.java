package com.property.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payment_record")
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_id", nullable = false)
    private Long billId;

    @Column(name = "pay_no", nullable = false, unique = true, length = 50)
    private String payNo;

    @Column(name = "pay_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal payAmount;

    @Column(name = "pay_method")
    private Integer payMethod = 1;

    @Column(name = "pay_status")
    private Integer payStatus = 0;

    @Column(name = "pay_time")
    private LocalDateTime payTime;

    @Column(name = "operator_id")
    private Long operatorId;

    @Column(length = 200)
    private String remark;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
