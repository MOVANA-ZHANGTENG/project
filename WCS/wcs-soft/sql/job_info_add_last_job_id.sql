-- 为 job_info 表添加 last_job_id 字段
-- 用于记录上一个步骤的Job ID，实现基于 lastId 的任务流程连线

ALTER TABLE `job_info` 
ADD COLUMN `last_job_id` BIGINT(20) NULL COMMENT '上一个步骤的Job ID（用于任务依赖）' AFTER `job_index`;

-- 添加索引以提高查询性能
ALTER TABLE `job_info` 
ADD INDEX `idx_last_job_id` (`last_job_id`);

-- 添加外键约束（可选，如果需要保证数据完整性）
-- ALTER TABLE `job_info` 
-- ADD CONSTRAINT `fk_job_info_last_job_id` 
-- FOREIGN KEY (`last_job_id`) REFERENCES `job_info` (`id`) 
-- ON DELETE SET NULL ON UPDATE CASCADE;

