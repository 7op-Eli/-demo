package com.property.repository;

import com.property.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findByStatusOrderByTopStatusDescPublishTimeDesc(Integer status, Pageable pageable);
    Page<Notice> findByCategoryAndStatusOrderByPublishTimeDesc(String category, Integer status, Pageable pageable);
    List<Notice> findByCategoryAndStatus(String category, Integer status);
}
