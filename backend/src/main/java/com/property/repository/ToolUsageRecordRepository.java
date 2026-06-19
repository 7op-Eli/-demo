package com.property.repository;

import com.property.entity.ToolUsageRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolUsageRecordRepository extends JpaRepository<ToolUsageRecord, Long> {
    Page<ToolUsageRecord> findByOwnerIdOrderByBorrowTimeDesc(Long ownerId, Pageable pageable);
    List<ToolUsageRecord> findByToolIdAndStatus(Long toolId, Integer status);
    List<ToolUsageRecord> findByStatus(Integer status);
}
