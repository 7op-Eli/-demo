package com.property.repository;

import com.property.entity.NoticeRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoticeReadRepository extends JpaRepository<NoticeRead, Long> {
    Optional<NoticeRead> findByNoticeIdAndUserId(Long noticeId, Long userId);
    long countByNoticeId(Long noticeId);
}
