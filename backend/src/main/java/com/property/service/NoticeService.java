package com.property.service;

import com.property.entity.Notice;
import com.property.entity.NoticeRead;
import com.property.repository.NoticeReadRepository;
import com.property.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeReadRepository readRepository;

    public Page<Notice> getNotices(Pageable pageable) {
        return noticeRepository.findByStatusOrderByTopStatusDescPublishTimeDesc(1, pageable);
    }

    public Page<Notice> getNoticesByCategory(String category, Pageable pageable) {
        return noticeRepository.findByCategoryAndStatusOrderByPublishTimeDesc(category, 1, pageable);
    }

    public Notice getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        notice.setViewCount(notice.getViewCount() + 1);
        noticeRepository.save(notice);
        return notice;
    }

    @Transactional
    public Notice createNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    @Transactional
    public Notice updateNotice(Long id, Notice updated) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        notice.setTitle(updated.getTitle());
        notice.setContent(updated.getContent());
        notice.setCategory(updated.getCategory());
        notice.setImageUrls(updated.getImageUrls());
        notice.setAttachmentUrl(updated.getAttachmentUrl());
        notice.setTopStatus(updated.getTopStatus());
        notice.setStatus(updated.getStatus());
        return noticeRepository.save(notice);
    }

    @Transactional
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

    /** 标记已读 */
    @Transactional
    public void markAsRead(Long noticeId, Long userId) {
        if (readRepository.findByNoticeIdAndUserId(noticeId, userId).isEmpty()) {
            NoticeRead read = new NoticeRead();
            read.setNoticeId(noticeId);
            read.setUserId(userId);
            readRepository.save(read);
        }
    }

    public long getReadCount(Long noticeId) {
        return readRepository.countByNoticeId(noticeId);
    }

    public List<Notice> getNoticesByCategory(String category) {
        return noticeRepository.findByCategoryAndStatus(category, 1);
    }
}
