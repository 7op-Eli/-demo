package com.property.repository;

import com.property.entity.ButlerMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ButlerMessageRepository extends JpaRepository<ButlerMessage, Long> {
    Page<ButlerMessage> findByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);
    List<ButlerMessage> findByReceiverIdAndIsRead(Long receiverId, Integer isRead);
    long countByReceiverIdAndIsRead(Long receiverId, Integer isRead);
}
