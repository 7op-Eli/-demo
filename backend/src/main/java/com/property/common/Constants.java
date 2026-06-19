package com.property.common;

/**
 * 系统常量
 */
public interface Constants {

    /* ========== 角色类型 ========== */
    int ROLE_ADMIN = 3;      // 系统管理员
    int ROLE_EMPLOYEE = 2;   // 物业员工
    int ROLE_OWNER = 1;      // 业主

    /* ========== 用户状态 ========== */
    int USER_STATUS_NORMAL = 1;
    int USER_STATUS_DISABLED = 0;

    /* ========== 报修单状态 ========== */
    int REPAIR_SUBMITTED = 1;        // 已提交
    int REPAIR_CS_ACCEPTED = 2;      // 客服已接单
    int REPAIR_DISPATCHED = 3;       // 已派单
    int REPAIR_IN_PROGRESS = 4;      // 维修中
    int REPAIR_COMPLETED = 5;        // 维修完成
    int REPAIR_FOLLOW_UP = 6;        // 回访中
    int REPAIR_FINISHED = 7;         // 已结束
    int REPAIR_CANCELLED = 8;        // 已取消

    /* ========== 缴费状态 ========== */
    int FEE_UNPAID = 0;      // 未缴费
    int FEE_PAID = 1;        // 已缴费
    int FEE_OVERDUE = 2;     // 逾期

    /* ========== 访客状态 ========== */
    int VISITOR_PENDING = 0;     // 待审核
    int VISITOR_APPROVED = 1;    // 已通过
    int VISITOR_INSIDE = 2;      // 已入园
    int VISITOR_LEFT = 3;        // 已离开
    int VISITOR_REJECTED = 4;    // 已拒绝

    /* ========== 通知分类 ========== */
    String NOTICE_GOV = "GOV";           // 政府公告
    String NOTICE_LAW = "LAW";           // 法律法规
    String NOTICE_COMMUNITY = "COMMUNITY"; // 小区通知
    String NOTICE_EMERGENCY = "EMERGENCY"; // 紧急通知

    /* ========== 消息类型 ========== */
    String MSG_TEXT = "TEXT";
    String MSG_IMAGE = "IMAGE";
    String MSG_NOTICE = "NOTICE";
}
