package com.property.controller;

import com.property.common.PageResult;
import com.property.common.Result;
import com.property.entity.Notice;
import com.property.entity.SysUser;
import com.property.security.CurrentUser;
import com.property.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "小区通知公告")
@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "获取通知公告列表")
    @GetMapping
    public Result<PageResult<Notice>> getNotices(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Notice> result;
        if (category != null) {
            result = noticeService.getNoticesByCategory(category,
                    PageRequest.of(page - 1, size));
        } else {
            result = noticeService.getNotices(PageRequest.of(page - 1, size));
        }
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @Operation(summary = "获取公告详情")
    @GetMapping("/{id}")
    public Result<Notice> getNotice(@PathVariable Long id,
                                    @CurrentUser SysUser user) {
        Notice notice = noticeService.getNotice(id);
        if (user != null) {
            noticeService.markAsRead(id, user.getId());
        }
        return Result.success(notice);
    }

    @Operation(summary = "获取某分类下所有公告（公开接口）")
    @GetMapping("/public/category/{category}")
    public Result<List<Notice>> getPublicByCategory(@PathVariable String category) {
        return Result.success(noticeService.getNoticesByCategory(category));
    }

    @Operation(summary = "获取已读数量")
    @GetMapping("/{id}/read-count")
    public Result<Long> getReadCount(@PathVariable Long id) {
        return Result.success(noticeService.getReadCount(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "发布公告")
    @PostMapping
    public Result<Notice> createNotice(@RequestBody Notice notice) {
        return Result.success(noticeService.createNotice(notice));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新公告")
    @PutMapping("/{id}")
    public Result<Notice> updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        return Result.success(noticeService.updateNotice(id, notice));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    public Result<?> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.success();
    }
}
