package com.property.controller;

import com.property.common.PageResult;
import com.property.common.Result;
import com.property.entity.*;
import com.property.security.CurrentUser;
import com.property.service.ButlerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "管家服务")
@RestController
@RequestMapping("/butler")
@RequiredArgsConstructor
public class ButlerController {

    private final ButlerService butlerService;

    // ========== 管家消息 ==========

    @Operation(summary = "获取房间消息列表")
    @GetMapping("/messages")
    public Result<PageResult<ButlerMessage>> getMessages(
            @CurrentUser SysUser user,
            @RequestParam(required = false) Long roomId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        // IDOR fix: owners can only see their own room messages;
        // employees/admins can view any room's messages
        Long effectiveRoomId;
        if (user.getRoleType() == com.property.common.Constants.ROLE_OWNER) {
            effectiveRoomId = user.getOwnerId();
        } else {
            effectiveRoomId = roomId;
        }
        Page<ButlerMessage> result = butlerService.getMessages(effectiveRoomId, PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @Operation(summary = "发送消息")
    @PostMapping("/messages")
    public Result<ButlerMessage> sendMessage(@RequestBody ButlerMessage msg) {
        return Result.success(butlerService.sendMessage(msg));
    }

    @Operation(summary = "获取未读消息数")
    @GetMapping("/messages/unread-count")
    public Result<Long> getUnreadCount(@RequestParam Long receiverId) {
        return Result.success(butlerService.getUnreadCount(receiverId));
    }

    @Operation(summary = "标记消息已读")
    @PutMapping("/messages/{id}/read")
    public Result<?> markRead(@PathVariable Long id) {
        butlerService.markAsRead(id);
        return Result.success();
    }

    // ========== 工作日程 ==========

    @Operation(summary = "获取某天日程")
    @GetMapping("/schedules")
    public Result<PageResult<WorkSchedule>> getSchedules(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<WorkSchedule> result = butlerService.getSchedules(date, PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @Operation(summary = "获取日期范围日程")
    @GetMapping("/schedules/range")
    public Result<List<WorkSchedule>> getSchedulesByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return Result.success(butlerService.getSchedulesByDateRange(start, end));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "创建日程")
    @PostMapping("/schedules")
    public Result<WorkSchedule> createSchedule(@RequestBody WorkSchedule schedule) {
        return Result.success(butlerService.createSchedule(schedule));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "更新日程状态")
    @PutMapping("/schedules/{id}/status")
    public Result<WorkSchedule> updateScheduleStatus(@PathVariable Long id, @RequestParam Integer status) {
        return Result.success(butlerService.updateScheduleStatus(id, status));
    }

    // ========== 工作流程通知 ==========

    @Operation(summary = "获取工作通知列表")
    @GetMapping("/notifications")
    public Result<PageResult<WorkNotification>> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<WorkNotification> result = butlerService.getNotifications(PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "发布工作通知")
    @PostMapping("/notifications")
    public Result<WorkNotification> publishNotification(@RequestBody WorkNotification notification) {
        return Result.success(butlerService.publishNotification(notification));
    }

    // ========== 照片公告 ==========

    @Operation(summary = "获取照片公告列表")
    @GetMapping("/announcements")
    public Result<PageResult<Announcement>> getAnnouncements(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Announcement> result = butlerService.getAnnouncements(PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "发布照片公告")
    @PostMapping("/announcements")
    public Result<Announcement> publishAnnouncement(@RequestBody Announcement ann) {
        return Result.success(butlerService.publishAnnouncement(ann));
    }
}
