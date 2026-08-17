-- ======================================================
-- 库位日志表 - 数据库建表脚本
-- ======================================================
-- 版本：V1.0
-- 日期：2025-11-04
-- 说明：用于记录每个库位(cell)发生的日志
-- ======================================================

-- 创建 cell_record 表
CREATE TABLE `cell_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `cell_id` bigint(20) DEFAULT NULL COMMENT '库位ID',
  `cell_code` varchar(50) DEFAULT NULL COMMENT '库位编码',
  `ware_code` varchar(50) DEFAULT NULL COMMENT '仓库编码',
  `operation_type` varchar(50) DEFAULT NULL COMMENT '操作类型(状态变更/任务分配/禁用启用/库存变化/其他)',
  `content` text COMMENT '日志内容',
  `old_value` varchar(500) DEFAULT NULL COMMENT '变更前的值',
  `new_value` varchar(500) DEFAULT NULL COMMENT '变更后的值',
  `pallet_code` varchar(100) DEFAULT NULL COMMENT '托盘号',
  `task_id` bigint(20) DEFAULT NULL COMMENT '关联任务ID',
  `job_id` bigint(20) DEFAULT NULL COMMENT '关联作业ID',
  `rcs_car_id` bigint(20) DEFAULT NULL COMMENT '关联小车ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `create_user_name` varchar(100) DEFAULT NULL COMMENT '创建人姓名',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_user_id` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `update_user_name` varchar(100) DEFAULT NULL COMMENT '更新人姓名',
  `memo` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_cell_id` (`cell_id`),
  KEY `idx_cell_code` (`cell_code`),
  KEY `idx_ware_code` (`ware_code`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_job_id` (`job_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_pallet_code` (`pallet_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库位日志记录表';

-- ======================================================
-- 说明：
-- 1. operation_type 操作类型包括：
--    - INVEN_STATE_CHANGE: 库存状态变更
--    - TASK_STATE_CHANGE: 任务状态变更
--    - DISABLE_STATE_CHANGE: 禁用状态变更
--    - STOCK_IN: 入库
--    - STOCK_OUT: 出库
--    - OCCUPY: 占用
--    - RELEASE: 释放
--    - OTHER: 其他
-- 2. content 记录详细的操作内容
-- 3. old_value/new_value 记录变更前后的值
-- 4. 包含完整的审计字段
-- ======================================================

-- 验证脚本
DESC cell_record;
SHOW INDEX FROM cell_record;

-- ======================================================
-- 示例数据
-- ======================================================
/*
INSERT INTO `cell_record` (
  `cell_id`, 
  `cell_code`, 
  `ware_code`, 
  `operation_type`, 
  `content`, 
  `old_value`, 
  `new_value`, 
  `pallet_code`, 
  `task_id`, 
  `create_time`, 
  `create_user_id`, 
  `create_user_name`
) VALUES (
  1, 
  '1-1-1', 
  'WH01', 
  'STOCK_IN', 
  '库位入库操作', 
  '空闲', 
  '有货', 
  'PALLET-001', 
  100, 
  NOW(), 
  1, 
  '系统'
);
*/

