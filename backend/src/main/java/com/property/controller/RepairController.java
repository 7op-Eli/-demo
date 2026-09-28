package com.property.controller;

import com.property.common.Constants;
import com.property.common.PageResult;
import com.property.common.Result;
import com.property.entity.*;
import com.property.repository.OwnerRepository;
import com.property.security.CurrentUser;
import com.property.service.RepairService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "报事报修")
@RestController
@RequestMapping("/repair")
@RequiredArgsConstructor
public class RepairController {

    private final RepairService repairService;
    private final OwnerRepository ownerRepository;

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
            @CurrentUser SysUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RepairOrder> result = repairService.getOrdersByAssignee(user.getEmployeeId(),
                PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "客服获取自己处理的工单")
    @GetMapping("/csr-orders")
    public Result<PageResult<RepairOrder>> getCsrOrders(
            @CurrentUser SysUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RepairOrder> result = repairService.getOrdersByCsr(user.getEmployeeId(),
                PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @Operation(summary = "获取报修单详情")
    @GetMapping("/orders/{id}")
    public Result<RepairOrder> getOrder(@PathVariable Long id, @CurrentUser SysUser user) {
        RepairOrder order = repairService.getOrderById(id);
        checkOrderAccess(order, user);
        return Result.success(order);
    }

    @Operation(summary = "提交报修（业主/员工均可）")
    @PostMapping("/orders")
    public Result<RepairOrder> submitOrder(@RequestBody RepairOrder order,
                                           @CurrentUser SysUser user) {
        order.setOwnerId(getEffectiveOwnerId(user));
        return Result.success(repairService.submitOrder(order));
    }

    /** 获取有效的业主 ID：业主用自己的，员工/政府人员用系统报修业主 */
    private Long getEffectiveOwnerId(SysUser user) {
        if (user.getOwnerId() != null) return user.getOwnerId();
        // 员工/政府人员无 ownerId，使用系统报修业主（自动创建）
        return ownerRepository.findByPhone("00000000000")
                .map(Owner::getId)
                .orElseGet(() -> {
                    Owner sysOwner = new Owner();
                    sysOwner.setName("系统报修");
                    sysOwner.setPhone("00000000000");
                    sysOwner.setStatus(1);
                    return ownerRepository.save(sysOwner).getId();
                });
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
    public Result<RepairOrder> dispatch(@PathVariable Long id,
                                         @RequestBody java.util.Map<String, Object> body,
                                         @CurrentUser SysUser user) {
        Long assigneeId = body != null && body.get("assigneeId") != null
                ? Long.valueOf(body.get("assigneeId").toString()) : null;
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
            @RequestBody java.util.Map<String, String> body,
            @CurrentUser SysUser user) {
        String content = body.get("content");
        String imageUrls = body.get("imageUrls");
        return Result.success(repairService.reportProgress(id, user.getEmployeeId(), content, imageUrls));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "维修完成（可上传完成照片）")
    @PutMapping("/orders/{id}/complete")
    public Result<RepairOrder> completeRepair(@PathVariable Long id,
                                               @RequestParam(required = false) String imageUrls,
                                               @CurrentUser SysUser user) {
        return Result.success(repairService.completeRepair(id, user.getEmployeeId()));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "客服回访")
    @PutMapping("/orders/{id}/follow-up")
    public Result<RepairOrder> followUp(@PathVariable Long id,
                                         @RequestBody java.util.Map<String, String> body,
                                         @CurrentUser SysUser user) {
        String note = body != null ? body.get("note") : null;
        return Result.success(repairService.followUp(id, user.getEmployeeId(), note));
    }

    // ========== 日志 & 评价 ==========

    @Operation(summary = "获取维修日志")
    @GetMapping("/orders/{id}/logs")
    public Result<List<RepairLog>> getLogs(@PathVariable Long id, @CurrentUser SysUser user) {
        RepairOrder order = repairService.getOrderById(id);
        checkOrderAccess(order, user);
        return Result.success(repairService.getLogs(id));
    }

    @Operation(summary = "获取评价")
    @GetMapping("/orders/{id}/evaluation")
    public Result<RepairEvaluation> getEvaluation(@PathVariable Long id, @CurrentUser SysUser user) {
        RepairOrder order = repairService.getOrderById(id);
        checkOrderAccess(order, user);
        return Result.success(repairService.getEvaluation(id));
    }

    @Operation(summary = "工单展示（公开：已完成+有评价的工单，按时间倒序）")
    @GetMapping("/orders/public-feed")
    public Result<PageResult<RepairOrder>> getPublicFeed(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 获取所有已结束的工单
        Page<RepairOrder> result = repairService.getOrdersByStatus(
                com.property.common.Constants.REPAIR_FINISHED,
                PageRequest.of(page - 1, size));
        // 过滤：只返回有评价的
        List<RepairOrder> filtered = result.getContent().stream()
                .filter(o -> repairService.getEvaluation(o.getId()) != null)
                .collect(java.util.stream.Collectors.toList());
        // 清洗敏感字段（不返回业主ID和电话）
        filtered.forEach(o -> {
            o.setOwnerId(null);
            o.setContactPhone(null);
        });
        return Result.success(new PageResult<RepairOrder>() {{
            setTotal(filtered.size());
            setPage(page);
            setSize(size);
            setList(filtered);
        }});
    }

    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "业主评价")
    @PostMapping("/evaluations")
    public Result<RepairEvaluation> submitEvaluation(@RequestBody RepairEvaluation eval,
                                                      @CurrentUser SysUser user) {
        // 强制从 token 取 ownerId，防止伪造
        eval.setOwnerId(user.getOwnerId());
        return Result.success(repairService.submitEvaluation(eval));
    }

    /** 校验当前用户是否有权查看该工单：业主只能看自己的，员工/管理员可看全部 */
    private void checkOrderAccess(RepairOrder order, SysUser user) {
        if (user.getRoleType() == Constants.ROLE_OWNER
                && !order.getOwnerId().equals(user.getOwnerId())) {
            throw new AccessDeniedException("无权查看该工单");
        }
    }
}
