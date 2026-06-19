package com.property.repository;

import com.property.entity.WorkNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkNotificationRepository extends JpaRepository<WorkNotification, Long> {
    Page<WorkNotification> findByStatusOrderByPublishTimeDesc(Integer status, Pageable pageable);
}
