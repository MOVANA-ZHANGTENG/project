-- ====================================================================
-- 修复 ds_plan_pallet 表中 bag_master_id 重复数据问题
-- 问题：同一个 bag_master_id 存在多条记录，导致查询异常
-- 解决：保留最新记录（ID最大），删除旧记录，并添加唯一索引
-- 日期：2025-11-05
-- ====================================================================

-- 步骤1：查询重复的 bag_master_id（先检查数据）
-- ====================================================================
SELECT 
    bag_master_id, 
    COUNT(*) as record_count,
    MIN(id) as min_id,
    MAX(id) as max_id,
    MIN(create_time) as first_create_time,
    MAX(create_time) as last_create_time
FROM ds_plan_pallet
WHERE bag_master_id IS NOT NULL
GROUP BY bag_master_id
HAVING COUNT(*) > 1
ORDER BY record_count DESC;

-- 步骤2：查看重复记录的详细信息
-- ====================================================================
SELECT 
    dpp.*
FROM ds_plan_pallet dpp
WHERE dpp.bag_master_id IN (
    SELECT bag_master_id 
    FROM ds_plan_pallet 
    WHERE bag_master_id IS NOT NULL
    GROUP BY bag_master_id 
    HAVING COUNT(*) > 1
)
ORDER BY dpp.bag_master_id, dpp.id DESC;

-- 步骤3：备份重复数据（重要！执行删除前先备份）
-- ====================================================================
CREATE TABLE IF NOT EXISTS ds_plan_pallet_backup_20251105 AS
SELECT * FROM ds_plan_pallet
WHERE bag_master_id IN (
    SELECT bag_master_id 
    FROM ds_plan_pallet 
    WHERE bag_master_id IS NOT NULL
    GROUP BY bag_master_id 
    HAVING COUNT(*) > 1
);

-- 验证备份
SELECT COUNT(*) as backup_count FROM ds_plan_pallet_backup_20251105;

-- 步骤4：删除重复数据（保留ID最大的记录，即最新的记录）
-- ====================================================================
-- 方法1：使用自连接删除（适用于MySQL 5.7+）
DELETE t1 FROM ds_plan_pallet t1
INNER JOIN ds_plan_pallet t2 
ON t1.bag_master_id = t2.bag_master_id 
WHERE t1.id < t2.id 
  AND t1.bag_master_id IS NOT NULL;

-- 方法2：如果方法1不支持，使用子查询（适用于所有MySQL版本）
-- DELETE FROM ds_plan_pallet
-- WHERE id IN (
--     SELECT id FROM (
--         SELECT t1.id
--         FROM ds_plan_pallet t1
--         WHERE EXISTS (
--             SELECT 1 FROM ds_plan_pallet t2
--             WHERE t1.bag_master_id = t2.bag_master_id
--               AND t1.id < t2.id
--               AND t1.bag_master_id IS NOT NULL
--         )
--     ) AS temp
-- );

-- 步骤5：验证清理结果（应该没有重复了）
-- ====================================================================
SELECT 
    bag_master_id, 
    COUNT(*) as record_count
FROM ds_plan_pallet
WHERE bag_master_id IS NOT NULL
GROUP BY bag_master_id
HAVING COUNT(*) > 1;

-- 如果上面查询无结果，说明清理成功

-- 步骤6：为 bag_master_id 添加唯一索引（防止未来再次出现重复）
-- ====================================================================
-- 先检查是否已存在该索引
SELECT 
    INDEX_NAME,
    COLUMN_NAME,
    NON_UNIQUE
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'ds_plan_pallet'
  AND COLUMN_NAME = 'bag_master_id';

-- 如果不存在唯一索引，则添加
-- 注意：如果 bag_master_id 允许为 NULL，则多个 NULL 值不会违反唯一约束
ALTER TABLE ds_plan_pallet 
ADD UNIQUE INDEX uk_bag_master_id (bag_master_id);

-- 步骤7：最终验证
-- ====================================================================
-- 查看表结构，确认索引已添加
SHOW INDEX FROM ds_plan_pallet;

-- 查看总记录数
SELECT 
    COUNT(*) as total_records,
    COUNT(DISTINCT bag_master_id) as unique_bag_master_ids,
    COUNT(*) - COUNT(DISTINCT bag_master_id) as duplicate_count
FROM ds_plan_pallet
WHERE bag_master_id IS NOT NULL;

-- ====================================================================
-- 执行说明：
-- 1. 先执行步骤1和步骤2，查看重复数据情况
-- 2. 执行步骤3，备份重复数据
-- 3. 执行步骤4，删除重复数据（二选一，推荐方法1）
-- 4. 执行步骤5，验证清理结果
-- 5. 执行步骤6，添加唯一索引
-- 6. 执行步骤7，最终验证
-- ====================================================================

-- 如果需要回滚，执行以下SQL：
-- INSERT INTO ds_plan_pallet 
-- SELECT * FROM ds_plan_pallet_backup_20251105
-- WHERE id NOT IN (SELECT id FROM ds_plan_pallet);

