package com.property.service;

import com.property.common.Constants;
import com.property.entity.Visitor;
import com.property.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;

    public Page<Visitor> getVisitorsByOwner(Long ownerId, Pageable pageable) {
        return visitorRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId, pageable);
    }

    public Page<Visitor> getVisitorsByStatus(Integer status, Pageable pageable) {
        return visitorRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    /** 业主登记访客 */
    @Transactional
    public Visitor registerVisitor(Visitor visitor) {
        visitor.setAccessCode(generateAccessCode());
        visitor.setStatus(Constants.VISITOR_PENDING);
        return visitorRepository.save(visitor);
    }

    /** 审核通过 */
    @Transactional
    public Visitor approveVisitor(Long id, Long employeeId) {
        Visitor visitor = getVisitor(id);
        visitor.setStatus(Constants.VISITOR_APPROVED);
        visitor.setAuditBy(employeeId);
        visitor.setAuditTime(LocalDateTime.now());
        return visitorRepository.save(visitor);
    }

    /** 拒绝 */
    @Transactional
    public Visitor rejectVisitor(Long id, Long employeeId, String remark) {
        Visitor visitor = getVisitor(id);
        visitor.setStatus(Constants.VISITOR_REJECTED);
        visitor.setAuditBy(employeeId);
        visitor.setAuditTime(LocalDateTime.now());
        visitor.setAuditRemark(remark);
        return visitorRepository.save(visitor);
    }

    /** 开门/进入 */
    @Transactional
    public Visitor enter(Long id) {
        Visitor visitor = getVisitor(id);
        visitor.setStatus(Constants.VISITOR_INSIDE);
        visitor.setActualArrival(LocalDateTime.now());
        return visitorRepository.save(visitor);
    }

    /** 离开登记 */
    @Transactional
    public Visitor depart(Long id) {
        Visitor visitor = getVisitor(id);
        visitor.setStatus(Constants.VISITOR_LEFT);
        visitor.setDepartureTime(LocalDateTime.now());
        return visitorRepository.save(visitor);
    }

    public Visitor getVisitor(Long id) {
        return visitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("访客记录不存在"));
    }

    private String generateAccessCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
