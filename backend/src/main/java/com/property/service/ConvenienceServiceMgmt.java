package com.property.service;

import com.property.entity.*;
import com.property.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConvenienceServiceMgmt {

    private final ConvenienceServiceRepository serviceRepository;
    private final ToolRepository toolRepository;
    private final ToolUsageRecordRepository usageRepository;
    private final ServiceEvaluationRepository evaluationRepository;

    // ========== 便民服务项目 ==========

    public List<ConvenienceService> getActiveServices() {
        return serviceRepository.findByStatusOrderBySortOrder(1);
    }

    public List<ConvenienceService> getServicesByType(String type) {
        return serviceRepository.findByServiceTypeAndStatus(type, 1);
    }

    @Transactional
    public ConvenienceService createService(ConvenienceService service) {
        return serviceRepository.save(service);
    }

    @Transactional
    public ConvenienceService updateService(Long id, ConvenienceService updated) {
        ConvenienceService svc = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("便民服务不存在"));
        svc.setName(updated.getName());
        svc.setDescription(updated.getDescription());
        svc.setServiceType(updated.getServiceType());
        svc.setIcon(updated.getIcon());
        svc.setContactPhone(updated.getContactPhone());
        svc.setServiceTime(updated.getServiceTime());
        svc.setStatus(updated.getStatus());
        svc.setSortOrder(updated.getSortOrder());
        return serviceRepository.save(svc);
    }

    // ========== 工具管理 ==========

    public List<Tool> getAvailableTools() {
        return toolRepository.findByStatusOrderByName(1);
    }

    @Transactional
    public Tool createTool(Tool tool) {
        if (tool.getAvailableQuantity() == null) {
            tool.setAvailableQuantity(tool.getTotalQuantity());
        }
        return toolRepository.save(tool);
    }

    @Transactional
    public Tool updateTool(Long id, Tool updated) {
        Tool tool = toolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("工具不存在"));
        tool.setName(updated.getName());
        tool.setCategory(updated.getCategory());
        tool.setDescription(updated.getDescription());
        int diff = updated.getTotalQuantity() - tool.getTotalQuantity();
        tool.setTotalQuantity(updated.getTotalQuantity());
        tool.setAvailableQuantity(tool.getAvailableQuantity() + diff);
        tool.setUnit(updated.getUnit());
        tool.setImageUrl(updated.getImageUrl());
        tool.setStatus(updated.getStatus());
        return toolRepository.save(tool);
    }

    // ========== 工具借用 ==========

    public Page<ToolUsageRecord> getUsageRecordsByOwner(Long ownerId, Pageable pageable) {
        return usageRepository.findByOwnerIdOrderByBorrowTimeDesc(ownerId, pageable);
    }

    public List<ToolUsageRecord> getActiveUsageRecords() {
        return usageRepository.findByStatus(0);
    }

    @Transactional
    public ToolUsageRecord borrowTool(ToolUsageRecord record) {
        Tool tool = toolRepository.findById(record.getToolId())
                .orElseThrow(() -> new RuntimeException("工具不存在"));
        if (tool.getAvailableQuantity() < record.getQuantity()) {
            throw new RuntimeException("工具库存不足，当前可用: " + tool.getAvailableQuantity());
        }
        tool.setAvailableQuantity(tool.getAvailableQuantity() - record.getQuantity());
        toolRepository.save(tool);

        record.setBorrowTime(LocalDateTime.now());
        record.setStatus(0); // 借用中
        return usageRepository.save(record);
    }

    @Transactional
    public ToolUsageRecord returnTool(Long usageId) {
        ToolUsageRecord record = usageRepository.findById(usageId)
                .orElseThrow(() -> new RuntimeException("借用记录不存在"));

        Tool tool = toolRepository.findById(record.getToolId())
                .orElseThrow(() -> new RuntimeException("工具不存在"));
        tool.setAvailableQuantity(tool.getAvailableQuantity() + record.getQuantity());
        toolRepository.save(tool);

        record.setStatus(1); // 已归还
        record.setActualReturnTime(LocalDateTime.now());
        return usageRepository.save(record);
    }

    // ========== 评价 ==========

    @Transactional
    public ServiceEvaluation submitEvaluation(ServiceEvaluation eval) {
        return evaluationRepository.save(eval);
    }

    public List<ServiceEvaluation> getEvaluationsByService(Long serviceId) {
        return evaluationRepository.findByServiceId(serviceId);
    }
}
