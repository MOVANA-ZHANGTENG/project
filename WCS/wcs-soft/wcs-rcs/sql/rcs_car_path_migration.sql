-- ============================================
-- rcs_car_path 表结构改造 SQL 迁移脚本
-- 
-- 改造说明：
-- 1. 将 cell_id, next_id 改为 from_cell_id, to_cell_id
-- 2. 之前表示单个节点，现在表示两个节点之间的路径段
-- 3. 状态含义：0-未执行 1-已占用 2-执行中 3-已完成
-- 
-- 执行顺序：
-- 1. 备份数据
-- 2. 删除旧表
-- 3. 创建新表结构
-- ============================================

-- ============================================
-- 步骤1: 备份旧表数据
-- ============================================
-- 创建备份表
CREATE TABLE IF NOT EXISTS `rcs_car_path_backup` LIKE `rcs_car_path`;

-- 备份数据（如果表中有数据）
INSERT INTO `rcs_car_path_backup` SELECT * FROM `rcs_car_path`;

-- ============================================
-- 步骤2: 删除旧表
-- ============================================
DROP TABLE IF EXISTS `rcs_car_path`;

-- ============================================
-- 步骤3: 创建新表结构
-- ============================================
CREATE TABLE `rcs_car_path` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `rcs_car_id` bigint(20) DEFAULT NULL COMMENT '小车ID',
  `job_id` bigint(20) DEFAULT NULL COMMENT '作业ID',
  `task_id` bigint(20) DEFAULT NULL COMMENT '任务ID',
  `from_cell_id` bigint(20) DEFAULT NULL COMMENT '起点Cell ID',
  `to_cell_id` bigint(20) DEFAULT NULL COMMENT '终点Cell ID',
  `state` int(11) DEFAULT 0 COMMENT '路径状态：0-未执行 1-已占用 2-执行中 3-已完成',
  `ware_code` varchar(50) DEFAULT NULL COMMENT '仓库编码',
  `z` int(11) DEFAULT NULL COMMENT '楼层',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `allot_time` datetime DEFAULT NULL COMMENT '分配时间',
  `cmd_time` datetime DEFAULT NULL COMMENT '下发时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`),
  KEY `idx_rcs_car_id` (`rcs_car_id`),
  KEY `idx_job_id` (`job_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_from_cell_id` (`from_cell_id`),
  KEY `idx_to_cell_id` (`to_cell_id`),
  KEY `idx_state` (`state`),
  KEY `idx_ware_code_z` (`ware_code`, `z`),
  KEY `idx_path_segment` (`from_cell_id`, `to_cell_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='RCS小车路径表（路径段模式）';

-- ============================================
-- 说明：数据迁移（可选）
-- ============================================
-- 注意：由于表结构从"节点"改为"路径段"，旧数据无法直接迁移
-- 建议：
-- 1. 如果不需要保留旧路径数据，直接使用新表即可
-- 2. 如果需要保留，可以查看 rcs_car_path_backup 表
-- 3. 系统运行后会自动生成新的路径段数据

-- ============================================
-- 清理备份表（可选，根据需要执行）
-- ============================================
-- 如果确认数据迁移成功，可以删除备份表
-- DROP TABLE IF EXISTS `rcs_car_path_backup`;

-- ============================================
-- 验证
-- ============================================
-- 查看新表结构
DESC `rcs_car_path`;

-- 查看索引
SHOW INDEX FROM `rcs_car_path`;

