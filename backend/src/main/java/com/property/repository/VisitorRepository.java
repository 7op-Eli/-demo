package com.property.repository;

import com.property.entity.Visitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    Page<Visitor> findByOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);
    Page<Visitor> findByStatusOrderByCreatedAtDesc(Integer status, Pageable pageable);
    List<Visitor> findByVehiclePlateContaining(String plate);
    long countByStatus(Integer status);
}
