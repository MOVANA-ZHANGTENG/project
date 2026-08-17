-- 为 task_info 表添加 rcs_car_id 字段
-- 用于关联 RCS 车辆信息
-- 执行时间：根据实际情况调整

-- 1. 检查字段是否存在，如果不存在则添加
ALTER TABLE `task_info` 
ADD COLUMN `rcs_car_id` BIGINT(20) NULL DEFAULT NULL COMMENT 'RCS车辆ID' AFTER `priority`;

-- 2. 添加索引以优化查询性能
ALTER TABLE `task_info` 
ADD INDEX `idx_rcs_car_id` (`rcs_car_id`);

-- 3. 如果需要添加外键约束，可以使用以下语句（可选）
-- ALTER TABLE `task_info` 
-- ADD CONSTRAINT `fk_task_info_rcs_car` 
-- FOREIGN KEY (`rcs_car_id`) REFERENCES `rcs_car_info`(`id`) 
-- ON DELETE SET NULL ON UPDATE CASCADE;

-- 执行说明：
-- 1. 此脚本会在 task_info 表中添加 rcs_car_id 字段
-- 2. 该字段用于记录执行该任务的车辆ID
-- 3. 外键约束是可选的，根据实际业务需求决定是否添加

