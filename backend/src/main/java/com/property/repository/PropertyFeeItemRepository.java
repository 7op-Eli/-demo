package com.property.repository;

import com.property.entity.PropertyFeeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyFeeItemRepository extends JpaRepository<PropertyFeeItem, Long> {
    List<PropertyFeeItem> findByStatus(Integer status);
}
