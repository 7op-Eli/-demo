package com.property.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "butler_message")
public class ButlerMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "sender_role", nullable = false)
    private Integer senderRole;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "message_type", nullable = false)
    private Integer messageType = 1;

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    @Column(name = "is_read", nullable = false)
    private Integer isRead = 0;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
