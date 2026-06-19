package com.property.controller;

import com.property.common.PageResult;
import com.property.common.Result;
import com.property.entity.*;
import com.property.security.CurrentUser;
import com.property.service.PropertyFeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "物业缴费")
@RestController
@RequestMapping("/fee")
@RequiredArgsConstructor
public class PropertyFeeController {

    private final PropertyFeeService feeService;

    // ========== 费用项目 ==========

    @Operation(summary = "获取费用项目列表")
    @GetMapping("/items")
    public Result<List<PropertyFeeItem>> getFeeItems() {
        return Result.success(feeService.getAllFeeItems());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "创建费用项目")
    @PostMapping("/items")
    public Result<PropertyFeeItem> createFeeItem(@RequestBody PropertyFeeItem item) {
        return Result.success(feeService.createFeeItem(item));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "更新费用项目")
    @PutMapping("/items/{id}")
    public Result<PropertyFeeItem> updateFeeItem(@PathVariable Long id, @RequestBody PropertyFeeItem item) {
        return Result.success(feeService.updateFeeItem(id, item));
    }

    // ========== 账单 ==========

    @Operation(summary = "获取业主账单")
    @GetMapping("/bills")
    public Result<PageResult<PropertyFeeBill>> getBills(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PropertyFeeBill> result;
        if (ownerId != null) {
            result = feeService.getBillsByOwner(ownerId, PageRequest.of(page - 1, size));
        } else if (status != null) {
            result = feeService.getBillsByStatus(status, PageRequest.of(page - 1, size));
        } else {
            result = feeService.getBillsByStatus(null, PageRequest.of(page - 1, size));
        }
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @Operation(summary = "生成账单")
    @PostMapping("/bills")
    public Result<PropertyFeeBill> createBill(@RequestBody PropertyFeeBill bill) {
        return Result.success(feeService.createBill(bill));
    }

    @Operation(summary = "缴费")
    @PostMapping("/bills/{id}/pay")
    public Result<PropertyFeeBill> payBill(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam Integer payMethod,
            @CurrentUser SysUser user) {
        return Result.success(feeService.payBill(id, amount, payMethod, user.getId()));
    }

    @Operation(summary = "获取支付记录")
    @GetMapping("/bills/{id}/payments")
    public Result<List<PaymentRecord>> getPayments(@PathVariable Long id) {
        return Result.success(feeService.getPaymentRecords(id));
    }
}
