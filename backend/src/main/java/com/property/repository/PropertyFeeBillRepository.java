package com.property.repository;

import com.property.entity.PropertyFeeBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PropertyFeeBillRepository extends JpaRepository<PropertyFeeBill, Long> {
    Page<PropertyFeeBill> findByOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);
    Page<PropertyFeeBill> findByStatus(Integer status, Pageable pageable);
    List<PropertyFeeBill> findByStatusNot(Integer status);
    List<PropertyFeeBill> findByDueDateBeforeAndStatus(LocalDate date, Integer status);
}
