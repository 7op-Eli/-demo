package com.property.controller;

import com.property.common.PageResult;
import com.property.common.Result;
import com.property.entity.*;
import com.property.security.CurrentUser;
import com.property.service.ConvenienceServiceMgmt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "便民服务")
@RestController
@RequestMapping("/convenience")
@RequiredArgsConstructor
public class ConvenienceController {

    private final ConvenienceServiceMgmt convenienceService;

    // ========== 便民服务项目 ==========

    @Operation(summary = "获取启用中的便民服务")
    @GetMapping("/services")
    public Result<List<ConvenienceService>> getServices() {
        return Result.success(convenienceService.getActiveServices());
    }

    @Operation(summary = "按类型获取便民服务")
    @GetMapping("/services/type/{type}")
    public Result<List<ConvenienceService>> getServicesByType(@PathVariable String type) {
        return Result.success(convenienceService.getServicesByType(type));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建便民服务项目")
    @PostMapping("/services")
    public Result<ConvenienceService> createService(@RequestBody ConvenienceService service) {
        return Result.success(convenienceService.createService(service));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新便民服务")
    @PutMapping("/services/{id}")
    public Result<ConvenienceService> updateService(@PathVariable Long id,
                                                    @RequestBody ConvenienceService service) {
        return Result.success(convenienceService.updateService(id, service));
    }

    // ========== 工具管理 ==========

    @Operation(summary = "获取可借用的工具")
    @GetMapping("/tools")
    public Result<List<Tool>> getTools() {
        return Result.success(convenienceService.getAvailableTools());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建工具")
    @PostMapping("/tools")
    public Result<Tool> createTool(@RequestBody Tool tool) {
        return Result.success(convenienceService.createTool(tool));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新工具")
    @PutMapping("/tools/{id}")
    public Result<Tool> updateTool(@PathVariable Long id, @RequestBody Tool tool) {
        return Result.success(convenienceService.updateTool(id, tool));
    }

    // ========== 工具借用 ==========

    @Operation(summary = "业主借用记录")
    @GetMapping("/usage-records")
    public Result<PageResult<ToolUsageRecord>> getUsageRecords(
            @CurrentUser SysUser user,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // IDOR fix: owners can only see their own records;
        // employees/admins can query by any ownerId or see all active records
        if (user.getRoleType() == com.property.common.Constants.ROLE_OWNER) {
            Page<ToolUsageRecord> result = convenienceService.getUsageRecordsByOwner(user.getOwnerId(),
                    PageRequest.of(page - 1, size));
            return Result.success(PageResult.of(result, result.getContent()));
        }
        if (ownerId != null) {
            Page<ToolUsageRecord> result = convenienceService.getUsageRecordsByOwner(ownerId,
                    PageRequest.of(page - 1, size));
            return Result.success(PageResult.of(result, result.getContent()));
        }
        return Result.success(PageResult.of(
                Page.empty(), convenienceService.getActiveUsageRecords()));
    }

    @Operation(summary = "借用工具")
    @PostMapping("/tools/borrow")
    public Result<ToolUsageRecord> borrowTool(@RequestBody ToolUsageRecord record) {
        return Result.success(convenienceService.borrowTool(record));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "归还工具")
    @PutMapping("/tools/return/{usageId}")
    public Result<ToolUsageRecord> returnTool(@PathVariable Long usageId) {
        return Result.success(convenienceService.returnTool(usageId));
    }

    // ========== 评价 ==========

    @Operation(summary = "提交便民服务评价")
    @PostMapping("/evaluations")
    public Result<ServiceEvaluation> submitEvaluation(@RequestBody ServiceEvaluation eval) {
        return Result.success(convenienceService.submitEvaluation(eval));
    }

    @Operation(summary = "获取服务评价列表")
    @GetMapping("/evaluations/{serviceId}")
    public Result<List<ServiceEvaluation>> getEvaluations(@PathVariable Long serviceId) {
        return Result.success(convenienceService.getEvaluationsByService(serviceId));
    }
}
