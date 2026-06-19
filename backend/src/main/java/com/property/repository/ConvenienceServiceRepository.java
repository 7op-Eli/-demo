package com.property.repository;

import com.property.entity.ConvenienceService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConvenienceServiceRepository extends JpaRepository<ConvenienceService, Long> {
    List<ConvenienceService> findByStatusOrderBySortOrder(Integer status);
    List<ConvenienceService> findByServiceTypeAndStatus(String serviceType, Integer status);
}
