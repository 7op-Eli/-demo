package com.property.controller;

import com.property.common.PageResult;
import com.property.common.Result;
import com.property.entity.*;
import com.property.security.CurrentUser;
import com.property.service.RepairService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "报事报修")
@RestController
@RequestMapping("/repair")
@RequiredArgsConstructor
public class RepairController {

    private final RepairService repairService;

    @Operation(summary = "业主获取自己的报修单")
    @GetMapping("/my-orders")
    public Result<PageResult<RepairOrder>> getMyOrders(
            @CurrentUser SysUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // IDOR fix: derive ownerId from authenticated user, not request param
        Page<RepairOrder> result = repairService.getOrdersByOwner(user.getOwnerId(),
                PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "按状态查询报修单")
    @GetMapping("/orders")
    public Result<PageResult<RepairOrder>> getOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RepairOrder> result;
        if (status != null) {
            result = repairService.getOrdersByStatus(status, PageRequest.of(page - 1, size));
        } else {
            result = repairService.getOrdersByStatus(null, PageRequest.of(page - 1, size));
        }
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "维修人员获取指派给自己的工单")
    @GetMapping("/assigned-orders")
    public Result<PageResult<RepairOrder>> getAssignedOrders(
            @RequestParam Long employeeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RepairOrder> result = repairService.getOrdersByAssignee(employeeId,
                PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "客服获取自己处理的工单")
    @GetMapping("/csr-orders")
    public Result<PageResult<RepairOrder>> getCsrOrders(
            @RequestParam Long csrId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RepairOrder> result = repairService.getOrdersByCsr(csrId,
                PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @Operation(summary = "获取报修单详情")
    @GetMapping("/orders/{id}")
    public Result<RepairOrder> getOrder(@PathVariable Long id) {
        return Result.success(repairService.getOrderById(id));
    }

    @Operation(summary = "业主提交报修")
    @PostMapping("/orders")
    public Result<RepairOrder> submitOrder(@RequestBody RepairOrder order,
                                           @CurrentUser SysUser user) {
        order.setOwnerId(user.getOwnerId());
        return Result.success(repairService.submitOrder(order));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "客服接单")
    @PutMapping("/orders/{id}/cs-accept")
    public Result<RepairOrder> csAccept(@PathVariable Long id, @CurrentUser SysUser user) {
        return Result.success(repairService.csAccept(id, user.getEmployeeId()));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "客服派单")
    @PutMapping("/orders/{id}/dispatch")
    public Result<RepairOrder> dispatch(@PathVariable Long id, @RequestParam Long assigneeId,
                                        @CurrentUser SysUser user) {
        return Result.success(repairService.dispatch(id, assigneeId, user.getEmployeeId()));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "维修接单")
    @PutMapping("/orders/{id}/accept")
    public Result<RepairOrder> acceptRepair(@PathVariable Long id, @CurrentUser SysUser user) {
        return Result.success(repairService.acceptRepair(id, user.getEmployeeId()));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "维修过程上报")
    @PostMapping("/orders/{id}/progress")
    public Result<RepairLog> reportProgress(
            @PathVariable Long id,
            @RequestParam String content,
            @RequestParam(required = false) String imageUrls,
            @CurrentUser SysUser user) {
        return Result.success(repairService.reportProgress(id, user.getEmployeeId(), content, imageUrls));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "维修完成")
    @PutMapping("/orders/{id}/complete")
    public Result<RepairOrder> completeRepair(@PathVariable Long id, @CurrentUser SysUser user) {
        return Result.success(repairService.completeRepair(id, user.getEmployeeId()));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "客服回访")
    @PutMapping("/orders/{id}/follow-up")
    public Result<RepairOrder> followUp(@PathVariable Long id, @RequestParam String note,
                                        @CurrentUser SysUser user) {
        return Result.success(repairService.followUp(id, user.getEmployeeId(), note));
    }

    // ========== 日志 & 评价 ==========

    @Operation(summary = "获取维修日志")
    @GetMapping("/orders/{id}/logs")
    public Result<List<RepairLog>> getLogs(@PathVariable Long id) {
        return Result.success(repairService.getLogs(id));
    }

    @Operation(summary = "获取评价")
    @GetMapping("/orders/{id}/evaluation")
    public Result<RepairEvaluation> getEvaluation(@PathVariable Long id) {
        return Result.success(repairService.getEvaluation(id));
    }

    @Operation(summary = "业主评价")
    @PostMapping("/evaluations")
    public Result<RepairEvaluation> submitEvaluation(@RequestBody RepairEvaluation eval) {
        return Result.success(repairService.submitEvaluation(eval));
    }
}
