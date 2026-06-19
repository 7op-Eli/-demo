package com.property.service;

import cn.hutool.core.date.DateUtil;
import com.property.common.Constants;
import com.property.entity.*;
import com.property.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RepairService {

    private final RepairOrderRepository orderRepository;
    private final RepairLogRepository logRepository;
    private final RepairEvaluationRepository evaluationRepository;

    // ========== 报修单 ==========

    public Page<RepairOrder> getOrdersByOwner(Long ownerId, Pageable pageable) {
        return orderRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId, pageable);
    }

    public Page<RepairOrder> getOrdersByStatus(Integer status, Pageable pageable) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    public Page<RepairOrder> getOrdersByAssignee(Long employeeId, Pageable pageable) {
        return orderRepository.findByAssigneeIdOrderByCreatedAtDesc(employeeId, pageable);
    }

    public Page<RepairOrder> getOrdersByCsr(Long csrId, Pageable pageable) {
        return orderRepository.findByCsrIdOrderByCreatedAtDesc(csrId, pageable);
    }

    public RepairOrder getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("报修单不存在"));
    }

    /** 业主提交报修 */
    @Transactional
    public RepairOrder submitOrder(RepairOrder order) {
        order.setOrderNo(generateOrderNo());
        order.setStatus(Constants.REPAIR_SUBMITTED);
        return orderRepository.save(order);
    }

    /** 客服接单 */
    @Transactional
    public RepairOrder csAccept(Long orderId, Long csrId) {
        RepairOrder order = getOrderById(orderId);
        order.setStatus(Constants.REPAIR_CS_ACCEPTED);
        order.setCsrId(csrId);
        addLog(orderId, csrId, "cs_accepted", "客服已接单");
        return orderRepository.save(order);
    }

    /** 客服派单 */
    @Transactional
    public RepairOrder dispatch(Long orderId, Long assigneeId, Long csrId) {
        RepairOrder order = getOrderById(orderId);
        order.setStatus(Constants.REPAIR_DISPATCHED);
        order.setAssigneeId(assigneeId);
        order.setDispatchTime(LocalDateTime.now());
        addLog(orderId, csrId, "dispatched", "已派单给维修人员");
        return orderRepository.save(order);
    }

    /** 维修接单 */
    @Transactional
    public RepairOrder acceptRepair(Long orderId, Long employeeId) {
        RepairOrder order = getOrderById(orderId);
        order.setStatus(Constants.REPAIR_IN_PROGRESS);
        order.setAcceptTime(LocalDateTime.now());
        addLog(orderId, employeeId, "accepted", "维修人员已接单");
        return orderRepository.save(order);
    }

    /** 维修过程上报 */
    @Transactional
    public RepairLog reportProgress(Long orderId, Long employeeId, String content, String imageUrls) {
        return addLog(orderId, employeeId, "progress_update", content, imageUrls);
    }

    /** 维修完成 */
    @Transactional
    public RepairOrder completeRepair(Long orderId, Long employeeId) {
        RepairOrder order = getOrderById(orderId);
        order.setStatus(Constants.REPAIR_COMPLETED);
        order.setCompleteTime(LocalDateTime.now());
        addLog(orderId, employeeId, "completed", "维修完成");
        return orderRepository.save(order);
    }

    /** 客服回访 */
    @Transactional
    public RepairOrder followUp(Long orderId, Long csrId, String note) {
        RepairOrder order = getOrderById(orderId);
        order.setStatus(Constants.REPAIR_FOLLOW_UP);
        order.setFollowUpTime(LocalDateTime.now());
        order.setFollowUpNote(note);
        addLog(orderId, csrId, "follow_up", "回访记录: " + note);
        return orderRepository.save(order);
    }

    /** 业主评价后结束 */
    @Transactional
    public RepairOrder finishOrder(Long orderId) {
        RepairOrder order = getOrderById(orderId);
        order.setStatus(Constants.REPAIR_FINISHED);
        return orderRepository.save(order);
    }

    // ========== 评价 ==========

    public RepairEvaluation getEvaluation(Long orderId) {
        return evaluationRepository.findByOrderId(orderId).orElse(null);
    }

    @Transactional
    public RepairEvaluation submitEvaluation(RepairEvaluation eval) {
        RepairEvaluation saved = evaluationRepository.save(eval);
        // 评价后自动结束工单
        finishOrder(eval.getOrderId());
        return saved;
    }

    // ========== 日志 ==========

    public List<RepairLog> getLogs(Long orderId) {
        return logRepository.findByOrderIdOrderByCreatedAtAsc(orderId);
    }

    private RepairLog addLog(Long orderId, Long operatorId, String action, String content) {
        return addLog(orderId, operatorId, action, content, null);
    }

    private RepairLog addLog(Long orderId, Long operatorId, String action, String content, String imageUrls) {
        RepairLog log = new RepairLog();
        log.setOrderId(orderId);
        log.setOperatorId(operatorId);
        log.setAction(action);
        log.setContent(content);
        log.setImageUrls(imageUrls);
        return logRepository.save(log);
    }

    public long countByStatus(Integer status) {
        return orderRepository.countByStatus(status);
    }

    private String generateOrderNo() {
        return "RP" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss")
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
