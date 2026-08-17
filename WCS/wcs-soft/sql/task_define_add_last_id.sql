-- 为 task_define 表添加 last_id 字段
-- 用于记录上一个步骤的ID，实现基于 lastId 的任务流程连线

ALTER TABLE `task_define` 
ADD COLUMN `last_id` BIGINT(20) NULL COMMENT '上一个步骤ID（用于连线）' AFTER `to_cell_code`;

-- 添加索引以提高查询性能
ALTER TABLE `task_define` 
ADD INDEX `idx_last_id` (`last_id`);

-- 添加外键约束（可选，如果需要保证数据完整性）
-- ALTER TABLE `task_define` 
-- ADD CONSTRAINT `fk_task_define_last_id` 
-- FOREIGN KEY (`last_id`) REFERENCES `task_define` (`id`) 
-- ON DELETE SET NULL ON UPDATE CASCADE;

