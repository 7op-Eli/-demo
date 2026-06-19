package com.property.repository;

import com.property.entity.ServiceEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceEvaluationRepository extends JpaRepository<ServiceEvaluation, Long> {
    List<ServiceEvaluation> findByServiceId(Long serviceId);
    List<ServiceEvaluation> findByOwnerId(Long ownerId);
}
