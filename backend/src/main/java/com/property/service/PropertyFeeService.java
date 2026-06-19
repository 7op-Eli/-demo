package com.property.service;

import cn.hutool.core.date.DateUtil;
import com.property.entity.*;
import com.property.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyFeeService {

    private final PropertyFeeItemRepository itemRepository;
    private final PropertyFeeBillRepository billRepository;
    private final PaymentRecordRepository paymentRepository;

    // ========== 费用项目 ==========

    public List<PropertyFeeItem> getAllFeeItems() {
        return itemRepository.findByStatus(1);
    }

    @Transactional
    public PropertyFeeItem createFeeItem(PropertyFeeItem item) {
        return itemRepository.save(item);
    }

    @Transactional
    public PropertyFeeItem updateFeeItem(Long id, PropertyFeeItem updated) {
        PropertyFeeItem item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("费用项目不存在"));
        item.setName(updated.getName());
        item.setUnitPrice(updated.getUnitPrice());
        item.setUnit(updated.getUnit());
        item.setDescription(updated.getDescription());
        item.setStatus(updated.getStatus());
        return itemRepository.save(item);
    }

    // ========== 账单管理 ==========

    public Page<PropertyFeeBill> getBillsByOwner(Long ownerId, Pageable pageable) {
        return billRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId, pageable);
    }

    public Page<PropertyFeeBill> getBillsByStatus(Integer status, Pageable pageable) {
        return billRepository.findByStatus(status, pageable);
    }

    @Transactional
    public PropertyFeeBill createBill(PropertyFeeBill bill) {
        bill.setBillNo(generateBillNo());
        return billRepository.save(bill);
    }

    @Transactional
    public PropertyFeeBill payBill(Long billId, BigDecimal amount, Integer payMethod, Long operatorId) {
        PropertyFeeBill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("账单不存在"));

        BigDecimal newPaid = bill.getPaidAmount().add(amount);
        bill.setPaidAmount(newPaid);

        if (newPaid.compareTo(bill.getAmount()) >= 0) {
            bill.setStatus(2); // 已缴清
        } else if (newPaid.compareTo(BigDecimal.ZERO) > 0) {
            bill.setStatus(1); // 部分缴纳
        }

        billRepository.save(bill);

        // 生成支付记录
        PaymentRecord record = new PaymentRecord();
        record.setBillId(billId);
        record.setPayNo(generatePayNo());
        record.setPayAmount(amount);
        record.setPayMethod(payMethod);
        record.setPayStatus(1);
        record.setPayTime(LocalDateTime.now());
        record.setOperatorId(operatorId);
        paymentRepository.save(record);

        return bill;
    }

    public List<PaymentRecord> getPaymentRecords(Long billId) {
        return paymentRepository.findByBillId(billId);
    }

    /** 检查逾期账单 */
    @Transactional
    public void checkOverdueBills() {
        List<PropertyFeeBill> overdue = billRepository.findByDueDateBeforeAndStatus(LocalDate.now(), 0);
        overdue.forEach(b -> {
            b.setStatus(3); // 已逾期
            billRepository.save(b);
        });
    }

    private String generateBillNo() {
        return "BILL" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss")
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String generatePayNo() {
        return "PAY" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss")
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
