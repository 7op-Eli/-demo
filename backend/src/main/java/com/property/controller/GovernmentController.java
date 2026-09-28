package com.property.controller;

import com.property.common.PageResult;
import com.property.common.Result;
import com.property.entity.*;
import com.property.repository.GovernmentFeedbackRepository;
import com.property.repository.GovernmentOfficialRepository;
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
import java.util.stream.Collectors;

@Tag(name = "政府端")
@RestController
@RequestMapping("/government")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GOVERNMENT')")
public class GovernmentController {

    private final GovernmentFeedbackRepository feedbackRepository;
    private final GovernmentOfficialRepository officialRepository;
    private final RepairService repairService;

    // ========== 反馈 ==========

    @Operation(summary = "提交反馈给物业")
    @PostMapping("/feedbacks")
    public Result<GovernmentFeedback> submitFeedback(@RequestBody GovernmentFeedback feedback,
                                                      @CurrentUser SysUser user) {
        Long officialId = user.getGovernmentOfficialId();
        if (officialId == null) {
            throw new com.property.common.BusinessException("政府人员身份未关联，请联系管理员");
        }
        feedback.setOfficialId(officialId);
        feedback.setStatus(0); // 待处理
        return Result.success(feedbackRepository.save(feedback));
    }

    @Operation(summary = "查看我的反馈列表")
    @GetMapping("/feedbacks")
    public Result<PageResult<GovernmentFeedback>> getMyFeedbacks(
            @CurrentUser SysUser user,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long officialId = user.getGovernmentOfficialId();
        if (officialId == null) {
            throw new com.property.common.BusinessException("政府人员身份未关联，请联系管理员");
        }
        Page<GovernmentFeedback> result = feedbackRepository
                .findByOfficialIdOrderByCreatedAtDesc(officialId, PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    // ========== 工单展示（复用） ==========

    @Operation(summary = "工单展示（公开：已完成+有评价，脱敏）")
    @GetMapping("/orders/public-feed")
    public Result<PageResult<RepairOrder>> getPublicFeed(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<RepairOrder> result = repairService.getOrdersByStatus(
                com.property.common.Constants.REPAIR_FINISHED,
                PageRequest.of(page - 1, size));
        List<RepairOrder> filtered = result.getContent().stream()
                .filter(o -> repairService.getEvaluation(o.getId()) != null)
                .collect(Collectors.toList());
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
}
