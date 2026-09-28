package com.property.repository;

import com.property.entity.GovernmentFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GovernmentFeedbackRepository extends JpaRepository<GovernmentFeedback, Long> {
    Page<GovernmentFeedback> findByOfficialIdOrderByCreatedAtDesc(Long officialId, Pageable pageable);
    Page<GovernmentFeedback> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
