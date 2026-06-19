package com.property.repository;

import com.property.entity.RepairEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepairEvaluationRepository extends JpaRepository<RepairEvaluation, Long> {
    Optional<RepairEvaluation> findByOrderId(Long orderId);
}
