package com.property.controller;

import com.property.common.PageResult;
import com.property.common.Result;
import com.property.entity.Visitor;
import com.property.security.CurrentUser;
import com.property.entity.SysUser;
import com.property.service.VisitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "访客通行")
@RestController
@RequestMapping("/visitor")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @Operation(summary = "业主获取访客记录")
    @GetMapping("/my-visitors")
    public Result<PageResult<Visitor>> getMyVisitors(
            @RequestParam Long ownerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Visitor> result = visitorService.getVisitorsByOwner(ownerId,
                PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "按状态查看访客")
    @GetMapping("/visitors")
    public Result<PageResult<Visitor>> getVisitorsByStatus(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (status == null) status = 0; // 默认待审核
        Page<Visitor> result = visitorService.getVisitorsByStatus(status,
                PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @Operation(summary = "业主登记访客")
    @PostMapping("/register")
    public Result<Visitor> register(@RequestBody Visitor visitor) {
        return Result.success(visitorService.registerVisitor(visitor));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "审核通过")
    @PutMapping("/{id}/approve")
    public Result<Visitor> approve(@PathVariable Long id, @CurrentUser SysUser user) {
        return Result.success(visitorService.approveVisitor(id, user.getEmployeeId()));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "拒绝来访")
    @PutMapping("/{id}/reject")
    public Result<Visitor> reject(@PathVariable Long id, @RequestParam String remark,
                                  @CurrentUser SysUser user) {
        return Result.success(visitorService.rejectVisitor(id, user.getEmployeeId(), remark));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "开门/进入")
    @PutMapping("/{id}/enter")
    public Result<Visitor> enter(@PathVariable Long id) {
        return Result.success(visitorService.enter(id));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN')")
    @Operation(summary = "离开登记")
    @PutMapping("/{id}/depart")
    public Result<Visitor> depart(@PathVariable Long id) {
        return Result.success(visitorService.depart(id));
    }
}
