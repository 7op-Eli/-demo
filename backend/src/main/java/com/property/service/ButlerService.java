package com.property.service;

import com.property.entity.*;
import com.property.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ButlerService {

    private final ButlerMessageRepository messageRepository;
    private final WorkScheduleRepository scheduleRepository;
    private final WorkNotificationRepository notificationRepository;
    private final AnnouncementRepository announcementRepository;

    // ========== 管家消息 ==========

    public Page<ButlerMessage> getMessages(Long roomId, Pageable pageable) {
        return messageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable);
    }

    @Transactional
    public ButlerMessage sendMessage(ButlerMessage msg) {
        return messageRepository.save(msg);
    }

    public long getUnreadCount(Long receiverId) {
        return messageRepository.countByReceiverIdAndIsRead(receiverId, 0);
    }

    @Transactional
    public void markAsRead(Long messageId) {
        ButlerMessage msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("消息不存在"));
        msg.setIsRead(1);
        msg.setReadAt(java.time.LocalDateTime.now());
        messageRepository.save(msg);
    }

    // ========== 工作日程 ==========

    public Page<WorkSchedule> getSchedules(LocalDate date, Pageable pageable) {
        return scheduleRepository.findByScheduleDateOrderByStartTime(date, pageable);
    }

    public List<WorkSchedule> getSchedulesByDateRange(LocalDate start, LocalDate end) {
        return scheduleRepository.findByScheduleDateBetween(start, end);
    }

    public List<WorkSchedule> getSchedulesByAssignee(Long employeeId) {
        return scheduleRepository.findByAssigneeId(employeeId);
    }

    @Transactional
    public WorkSchedule createSchedule(WorkSchedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public WorkSchedule updateScheduleStatus(Long id, Integer status) {
        WorkSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("日程不存在"));
        schedule.setStatus(status);
        return scheduleRepository.save(schedule);
    }

    // ========== 工作流程通知 ==========

    public Page<WorkNotification> getNotifications(Pageable pageable) {
        return notificationRepository.findByStatusOrderByPublishTimeDesc(1, pageable);
    }

    @Transactional
    public WorkNotification publishNotification(WorkNotification notification) {
        return notificationRepository.save(notification);
    }

    // ========== 照片公告 ==========

    public Page<Announcement> getAnnouncements(Pageable pageable) {
        return announcementRepository.findByStatusOrderByPublishTimeDesc(1, pageable);
    }

    @Transactional
    public Announcement publishAnnouncement(Announcement ann) {
        return announcementRepository.save(ann);
    }
}
