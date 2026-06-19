package com.property.repository;

import com.property.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {
    List<Tool> findByStatusOrderByName(Integer status);
    List<Tool> findByCategory(String category);
}
