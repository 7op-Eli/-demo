package com.property.repository;

import com.property.entity.WorkSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    List<WorkSchedule> findByScheduleDateBetween(LocalDate start, LocalDate end);
    List<WorkSchedule> findByAssigneeId(Long assigneeId);
    Page<WorkSchedule> findByScheduleDateOrderByStartTime(LocalDate date, Pageable pageable);
}
