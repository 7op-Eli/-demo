package com.property.repository;

import com.property.entity.RepairOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {
    Page<RepairOrder> findByOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);
    Page<RepairOrder> findByStatusOrderByCreatedAtDesc(Integer status, Pageable pageable);
    Page<RepairOrder> findByAssigneeIdOrderByCreatedAtDesc(Long assigneeId, Pageable pageable);
    Page<RepairOrder> findByCsrIdOrderByCreatedAtDesc(Long csrId, Pageable pageable);
    List<RepairOrder> findByStatusIn(List<Integer> statuses);
    long countByStatus(Integer status);
}
