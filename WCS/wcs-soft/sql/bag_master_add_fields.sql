-- =============================================
-- 物料延期和冻结接口 - 数据库表结构更新脚本
-- 日期：2025-11-08
-- 说明：为bag_master表添加延期小时数和冻结状态字段
-- =============================================

-- 添加延期小时数字段
ALTER TABLE `bag_master` ADD COLUMN `delay_hours` INT DEFAULT 0 COMMENT '延期小时数（累计）';

-- 添加冻结状态字段
ALTER TABLE `bag_master` ADD COLUMN `lock_status` VARCHAR(10) DEFAULT '正常' COMMENT '冻结状态：正常/冻结';

-- 创建索引，提高查询效率
CREATE INDEX idx_lock_status ON bag_master(lock_status);
CREATE INDEX idx_pallet_code ON bag_master(pallet_code);

-- 更新已有数据的默认值（如果需要）
UPDATE `bag_master` SET `delay_hours` = 0 WHERE `delay_hours` IS NULL;
UPDATE `bag_master` SET `lock_status` = '正常' WHERE `lock_status` IS NULL OR `lock_status` = '';

