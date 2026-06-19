package com.property.repository;

import com.property.entity.RepairLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairLogRepository extends JpaRepository<RepairLog, Long> {
    List<RepairLog> findByOrderIdOrderByCreatedAtAsc(Long orderId);
}
