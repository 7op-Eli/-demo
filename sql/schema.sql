-- ============================================================
-- 物业管理系统 数据库建表脚本
-- 数据库: property_management
-- 字符集: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `property_management` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `property_management`;

-- ============================================================
-- 1. 系统管理模块
-- ============================================================

-- 系统用户表（统一登录）
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `role_type` TINYINT NOT NULL COMMENT '角色类型: 1=业主 2=员工 3=管理员',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1=启用 0=禁用',
  `owner_id` BIGINT DEFAULT NULL COMMENT '关联业主ID',
  `employee_id` BIGINT DEFAULT NULL COMMENT '关联员工ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 角色表
CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE `sys_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `code` VARCHAR(100) NOT NULL COMMENT '权限编码',
  `type` TINYINT DEFAULT 1 COMMENT '类型: 1=菜单 2=按钮 3=接口',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父级权限ID',
  `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE `sys_role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ============================================================
-- 2. 基础信息模块（楼栋/房间/业主/员工）
-- ============================================================

-- 楼栋表
CREATE TABLE `building` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '楼栋ID',
  `name` VARCHAR(100) NOT NULL COMMENT '楼栋名称（如：A栋、1号楼）',
  `address` VARCHAR(200) DEFAULT NULL COMMENT '楼栋地址',
  `total_floors` INT DEFAULT NULL COMMENT '总楼层数',
  `total_rooms` INT DEFAULT NULL COMMENT '总户数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼栋表';

-- 房间/单元表
CREATE TABLE `room` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '房间ID',
  `building_id` BIGINT NOT NULL COMMENT '所属楼栋',
  `unit_no` VARCHAR(50) DEFAULT NULL COMMENT '单元号',
  `room_no` VARCHAR(50) NOT NULL COMMENT '房号',
  `floor` INT DEFAULT NULL COMMENT '所在楼层',
  `area` DECIMAL(10,2) DEFAULT NULL COMMENT '建筑面积(㎡)',
  `owner_name` VARCHAR(50) DEFAULT NULL COMMENT '业主姓名',
  `owner_phone` VARCHAR(20) DEFAULT NULL COMMENT '业主电话',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1=已售 2=未售 3=出租',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_room_no` (`room_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间/单元表';

-- 业主信息表
CREATE TABLE `owner` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '业主ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '关联系统用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '业主姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `gender` TINYINT DEFAULT NULL COMMENT '性别: 1=男 2=女',
  `birthday` DATE DEFAULT NULL COMMENT '出生日期',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `room_id` BIGINT DEFAULT NULL COMMENT '关联房间ID',
  `owner_type` TINYINT DEFAULT 1 COMMENT '业主类型: 1=自住 2=出租 3=空置',
  `emergency_contact` VARCHAR(50) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1=正常 0=注销',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业主信息表';

-- 员工表
CREATE TABLE `employee` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '关联系统用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '员工姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `position` VARCHAR(100) DEFAULT NULL COMMENT '职位',
  `department` VARCHAR(100) DEFAULT NULL COMMENT '部门',
  `entry_date` DATE DEFAULT NULL COMMENT '入职日期',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1=在职 0=离职',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- ============================================================
-- 3. 管家服务模块
-- ============================================================

-- 管家消息（业主与管家日常交流）
CREATE TABLE `butler_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` BIGINT NOT NULL COMMENT '发送者ID（系统用户ID）',
  `sender_role` TINYINT NOT NULL COMMENT '发送者角色: 1=业主 2=员工(管家)',
  `receiver_id` BIGINT DEFAULT NULL COMMENT '接收者ID',
  `room_id` BIGINT DEFAULT NULL COMMENT '关联房间ID',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `message_type` TINYINT DEFAULT 1 COMMENT '消息类型: 1=文本 2=图片 3=语音 4=文件',
  `attachment_url` VARCHAR(500) DEFAULT NULL COMMENT '附件URL',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 0=未读 1=已读',
  `read_at` DATETIME DEFAULT NULL COMMENT '读取时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_sender` (`sender_id`),
  KEY `idx_room` (`room_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管家消息表';

-- 工作日程安排表
CREATE TABLE `work_schedule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日程ID',
  `title` VARCHAR(200) NOT NULL COMMENT '工作标题',
  `content` TEXT COMMENT '工作内容描述',
  `schedule_date` DATE NOT NULL COMMENT '安排日期',
  `start_time` TIME DEFAULT NULL COMMENT '开始时间',
  `end_time` TIME DEFAULT NULL COMMENT '结束时间',
  `assignee_id` BIGINT DEFAULT NULL COMMENT '负责人（员工ID）',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0=待开始 1=进行中 2=已完成 3=已取消',
  `priority` TINYINT DEFAULT 1 COMMENT '优先级: 1=普通 2=重要 3=紧急',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_date` (`schedule_date`),
  KEY `idx_assignee` (`assignee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作日程安排表';

-- 工作流程通知表
CREATE TABLE `work_notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
  `content` TEXT COMMENT '通知内容',
  `notification_type` VARCHAR(50) DEFAULT NULL COMMENT '通知类型: maintenance=维修 cleaning=清洁 security=安保 inspection=巡检 other=其他',
  `image_urls` JSON DEFAULT NULL COMMENT '照片URL数组',
  `publisher_id` BIGINT DEFAULT NULL COMMENT '发布人（员工ID）',
  `publish_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1=已发布 0=草稿',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_type` (`notification_type`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作流程通知表';

-- 照片公告表
CREATE TABLE `announcement` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
  `description` TEXT COMMENT '公告描述',
  `image_urls` JSON DEFAULT NULL COMMENT '图片URL数组',
  `publisher_id` BIGINT DEFAULT NULL COMMENT '发布人（员工ID）',
  `publish_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1=已发布 0=草稿',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='照片公告表';

-- ============================================================
-- 4. 物业缴费模块
-- ============================================================

-- 费用项目表
CREATE TABLE `property_fee_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '费用项目ID',
  `name` VARCHAR(100) NOT NULL COMMENT '费用项目名称（如：物业费、水费、电费、停车费）',
  `unit_price` DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
  `unit` VARCHAR(20) DEFAULT NULL COMMENT '计费单位（如：元/㎡·月）',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '费用说明',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1=启用 0=停用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用项目表';

-- 缴费账单表
CREATE TABLE `property_fee_bill` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '账单ID',
  `bill_no` VARCHAR(50) NOT NULL COMMENT '账单编号',
  `owner_id` BIGINT NOT NULL COMMENT '业主ID',
  `room_id` BIGINT NOT NULL COMMENT '房间ID',
  `fee_item_id` BIGINT NOT NULL COMMENT '费用项目ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '应收金额',
  `paid_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '已付金额',
  `bill_period_start` DATE NOT NULL COMMENT '账单周期开始',
  `bill_period_end` DATE NOT NULL COMMENT '账单周期结束',
  `due_date` DATE NOT NULL COMMENT '缴费截止日',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0=未缴 1=部分缴纳 2=已缴清 3=已逾期',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_no` (`bill_no`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_fee_item` (`fee_item_id`),
  KEY `idx_status` (`status`),
  KEY `idx_due_date` (`due_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='缴费账单表';

-- 支付记录表
CREATE TABLE `payment_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `bill_id` BIGINT NOT NULL COMMENT '关联账单ID',
  `pay_no` VARCHAR(50) NOT NULL COMMENT '支付流水号',
  `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
  `pay_method` TINYINT DEFAULT 1 COMMENT '支付方式: 1=微信支付 2=支付宝 3=银行转账 4=现金 5=刷卡',
  `pay_status` TINYINT DEFAULT 0 COMMENT '支付状态: 0=待支付 1=支付成功 2=支付失败 3=已退款',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人（管理员收款时记录）',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_no` (`pay_no`),
  KEY `idx_bill_id` (`bill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ============================================================
-- 5. 报事报修模块
-- ============================================================

-- 报修单表
CREATE TABLE `repair_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报修单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '工单编号',
  `owner_id` BIGINT NOT NULL COMMENT '报修业主ID',
  `room_id` BIGINT DEFAULT NULL COMMENT '房间ID',
  `contact_name` VARCHAR(50) DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `repair_type` VARCHAR(50) DEFAULT NULL COMMENT '维修类型: water=水电 plumbing=管道 electric=电气 appliance=家电 structural=土建 other=其他',
  `description` TEXT NOT NULL COMMENT '问题描述',
  `image_urls` JSON DEFAULT NULL COMMENT '问题图片URL数组',
  `urgency_level` TINYINT DEFAULT 1 COMMENT '紧急程度: 1=普通 2=紧急 3=非常紧急',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0=待接单 1=已接单 2=已派单 3=维修中 4=已完成 5=回访中 6=已结束 7=已取消',
  `assignee_id` BIGINT DEFAULT NULL COMMENT '维修人员ID（员工ID）',
  `estimated_cost` DECIMAL(10,2) DEFAULT NULL COMMENT '预估费用',
  `actual_cost` DECIMAL(10,2) DEFAULT NULL COMMENT '实际费用',
  `csr_id` BIGINT DEFAULT NULL COMMENT '客服人员ID',
  `dispatch_time` DATETIME DEFAULT NULL COMMENT '派单时间',
  `accept_time` DATETIME DEFAULT NULL COMMENT '接单时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '维修完成时间',
  `follow_up_time` DATETIME DEFAULT NULL COMMENT '回访时间',
  `follow_up_note` TEXT COMMENT '回访记录',
  `cancel_reason` VARCHAR(500) DEFAULT NULL COMMENT '取消原因',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_assignee` (`assignee_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修单表';

-- 维修日志表（过程上报）
CREATE TABLE `repair_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `order_id` BIGINT NOT NULL COMMENT '报修单ID',
  `operator_id` BIGINT NOT NULL COMMENT '操作人（员工ID）',
  `action` VARCHAR(50) NOT NULL COMMENT '操作动作: dispatched=派单 accepted=接单 start_repair=开始维修 progress_update=进度更新 parts_replaced=更换配件 completed=维修完成 follow_up=回访',
  `content` TEXT COMMENT '操作内容/说明',
  `image_urls` JSON DEFAULT NULL COMMENT '现场图片',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修日志表';

-- 报修评价表
CREATE TABLE `repair_evaluation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` BIGINT NOT NULL COMMENT '报修单ID',
  `owner_id` BIGINT NOT NULL COMMENT '业主ID',
  `rating` TINYINT NOT NULL COMMENT '评分: 1-5星',
  `comment` TEXT COMMENT '评价内容',
  `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名: 0=公开 1=匿名',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修评价表';

-- ============================================================
-- 6. 访客通行模块
-- ============================================================

-- 访客记录表
CREATE TABLE `visitor` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '访客ID',
  `owner_id` BIGINT DEFAULT NULL COMMENT '受访业主ID',
  `room_id` BIGINT DEFAULT NULL COMMENT '受访房间ID',
  `visitor_name` VARCHAR(50) NOT NULL COMMENT '访客姓名',
  `visitor_phone` VARCHAR(20) DEFAULT NULL COMMENT '访客电话',
  `id_card` VARCHAR(18) DEFAULT NULL COMMENT '访客身份证号',
  `vehicle_plate` VARCHAR(20) DEFAULT NULL COMMENT '访客车辆车牌号',
  `vehicle_type` VARCHAR(50) DEFAULT NULL COMMENT '车辆类型: car=小轿车 suv=SUV van=面包车 truck=货车 other=其他',
  `visit_purpose` VARCHAR(200) DEFAULT NULL COMMENT '来访事由',
  `visit_count` INT DEFAULT 1 COMMENT '访客人数',
  `access_code` VARCHAR(20) DEFAULT NULL COMMENT '开门验证码/二维码',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0=待审核 1=已通过 2=已进入 3=已离开 4=已拒绝',
  `expected_arrival` DATETIME DEFAULT NULL COMMENT '预计到达时间',
  `actual_arrival` DATETIME DEFAULT NULL COMMENT '实际到达时间',
  `departure_time` DATETIME DEFAULT NULL COMMENT '离开时间',
  `audit_by` BIGINT DEFAULT NULL COMMENT '审核人（员工ID）',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `audit_remark` VARCHAR(200) DEFAULT NULL COMMENT '审核备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_phone` (`visitor_phone`),
  KEY `idx_plate` (`vehicle_plate`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访客记录表';

-- ============================================================
-- 7. 小区通知公告模块
-- ============================================================

-- 通知公告表
CREATE TABLE `notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` VARCHAR(200) NOT NULL COMMENT '公告标题',
  `content` TEXT NOT NULL COMMENT '公告内容',
  `category` VARCHAR(50) DEFAULT 'community' COMMENT '分类: community=小区通知 government=政府公告 law=法律法规 notice=物业通知 emergency=紧急通知 other=其他',
  `image_urls` JSON DEFAULT NULL COMMENT '图片URL数组',
  `attachment_url` VARCHAR(500) DEFAULT NULL COMMENT '附件URL',
  `publisher_id` BIGINT DEFAULT NULL COMMENT '发布人（员工ID）',
  `publish_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1=已发布 0=草稿',
  `top_status` TINYINT DEFAULT 0 COMMENT '置顶: 0=普通 1=置顶',
  `view_count` INT DEFAULT 0 COMMENT '阅读次数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_top_status` (`top_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- 通知已读记录表
CREATE TABLE `notice_read` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `notice_id` BIGINT NOT NULL COMMENT '公告ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `read_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '读取时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_notice_user` (`notice_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知已读记录表';

-- ============================================================
-- 8. 便民服务模块
-- ============================================================

-- 便民服务项目表
CREATE TABLE `convenience_service` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '服务ID',
  `name` VARCHAR(100) NOT NULL COMMENT '服务名称',
  `description` TEXT COMMENT '服务描述',
  `service_type` VARCHAR(50) DEFAULT NULL COMMENT '服务类型: info=信息告知 risk=风险告知 tool=工具借用 other=其他',
  `icon` VARCHAR(200) DEFAULT NULL COMMENT '图标URL',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `service_time` VARCHAR(200) DEFAULT NULL COMMENT '服务时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1=启用 0=停用',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_type` (`service_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='便民服务项目表';

-- 工具/物件表
CREATE TABLE `tool` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '工具ID',
  `name` VARCHAR(100) NOT NULL COMMENT '工具名称',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '工具分类',
  `description` TEXT COMMENT '工具描述',
  `total_quantity` INT DEFAULT 0 COMMENT '总数量',
  `available_quantity` INT DEFAULT 0 COMMENT '可用数量',
  `unit` VARCHAR(20) DEFAULT '' COMMENT '单位',
  `image_url` VARCHAR(500) DEFAULT NULL COMMENT '图片URL',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1=可借用 0=不可借用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具/物件表';

-- 工具使用登记表
CREATE TABLE `tool_usage_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '登记ID',
  `tool_id` BIGINT NOT NULL COMMENT '工具ID',
  `owner_id` BIGINT DEFAULT NULL COMMENT '借用业主ID',
  `borrower_name` VARCHAR(50) NOT NULL COMMENT '借用人姓名',
  `borrower_phone` VARCHAR(20) NOT NULL COMMENT '借用人电话',
  `quantity` INT DEFAULT 1 COMMENT '借用数量',
  `borrow_time` DATETIME NOT NULL COMMENT '借用时间',
  `expected_return_time` DATETIME DEFAULT NULL COMMENT '预计归还时间',
  `actual_return_time` DATETIME DEFAULT NULL COMMENT '实际归还时间',
  `purpose` VARCHAR(500) DEFAULT NULL COMMENT '借用用途',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0=借用中 1=已归还 2=逾期未还 3=损坏',
  `operator_id` BIGINT DEFAULT NULL COMMENT '经办人（员工ID）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_tool_id` (`tool_id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具使用登记表';

-- 便民服务评价表
CREATE TABLE `service_evaluation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `service_id` BIGINT DEFAULT NULL COMMENT '关联便民服务ID',
  `tool_usage_id` BIGINT DEFAULT NULL COMMENT '关联工具借用记录ID',
  `owner_id` BIGINT NOT NULL COMMENT '业主ID',
  `rating` TINYINT NOT NULL COMMENT '评分: 1-5星',
  `comment` TEXT COMMENT '评价内容',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='便民服务评价表';

-- ============================================================
-- 9. 商城服务模块（接口预留）
-- ============================================================

-- 商品分类表（预留）
CREATE TABLE `mall_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父分类ID',
  `icon` VARCHAR(200) DEFAULT NULL COMMENT '图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1=启用 0=停用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城-商品分类表';

-- 商品表（预留）
CREATE TABLE `mall_product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `description` TEXT COMMENT '商品描述',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `stock` INT DEFAULT 0 COMMENT '库存',
  `image_urls` JSON DEFAULT NULL COMMENT '商品图片',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 1=上架 0=下架',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城-商品表';

-- 购物车表（预留）
CREATE TABLE `mall_cart` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `owner_id` BIGINT NOT NULL COMMENT '业主ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `quantity` INT DEFAULT 1 COMMENT '数量',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城-购物车表';

-- 订单表（预留）
CREATE TABLE `mall_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
  `owner_id` BIGINT NOT NULL COMMENT '业主ID',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0=待付款 1=已付款 2=配送中 3=已完成 4=已取消 5=已退款',
  `pay_method` TINYINT DEFAULT NULL COMMENT '支付方式: 1=微信 2=支付宝',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `delivery_address` VARCHAR(500) DEFAULT NULL COMMENT '配送地址',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城-订单表';

-- 订单明细表（预留）
CREATE TABLE `mall_order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `quantity` INT NOT NULL COMMENT '数量',
  `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城-订单明细表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 默认角色
INSERT INTO `sys_role` (`name`, `code`, `description`) VALUES
('业主', 'OWNER', '小区业主'),
('物业员工', 'EMPLOYEE', '物业公司员工'),
('系统管理员', 'ADMIN', '系统管理员');

-- 默认管理员账号 (密码: admin123)
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role_type`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 3, 1);
