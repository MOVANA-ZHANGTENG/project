-- ====================================================================
-- 脚本名称：更新托盘是否为空箱状态
-- 脚本说明：根据 bag_master 表中的托盘关联记录，更新 pallet_info 表的 is_empty 字段
-- 创建时间：2025-10-18
-- 业务逻辑：
--   1. 如果托盘在 bag_master 中有关联的货物记录，设置 is_empty = "0"（有货）
--   2. 如果托盘在 bag_master 中没有关联记录，设置 is_empty = "1"（空箱）
-- ====================================================================

-- 方式一：使用 CASE WHEN 和子查询（适用于大部分数据库）
UPDATE pallet_info p
SET p.is_empty = CASE 
    WHEN EXISTS (
        SELECT 1 
        FROM bag_master b 
        WHERE b.pallet_code = p.code 
          AND b.ware_code = p.ware_code
    ) THEN '0'  -- 有货物，不是空箱
    ELSE '1'    -- 无货物，是空箱
END,
p.update_time = NOW(),
p.update_user_name = 'system';


-- ====================================================================
-- 方式二：分步更新（更清晰，推荐）
-- ====================================================================

-- 第一步：将所有托盘先设置为空箱
UPDATE pallet_info 
SET is_empty = '1',
    update_time = NOW(),
    update_user_name = 'system'
WHERE is_empty != '1' OR is_empty IS NULL;

-- 第二步：将有货物记录的托盘设置为有货
UPDATE pallet_info p
INNER JOIN (
    SELECT DISTINCT 
        b.pallet_code,
        b.ware_code
    FROM bag_master b
    WHERE b.pallet_code IS NOT NULL
      AND b.pallet_code != ''
) b ON p.code = b.pallet_code AND p.ware_code = b.ware_code
SET p.is_empty = '0',
    p.update_time = NOW(),
    p.update_user_name = 'system';


-- ====================================================================
-- 方式三：使用 LEFT JOIN（性能较好）
-- ====================================================================

UPDATE pallet_info p
LEFT JOIN (
    SELECT 
        pallet_code,
        ware_code,
        COUNT(*) as bag_count
    FROM bag_master
    WHERE pallet_code IS NOT NULL
      AND pallet_code != ''
    GROUP BY pallet_code, ware_code
) b ON p.code = b.pallet_code AND p.ware_code = b.ware_code
SET p.is_empty = CASE 
    WHEN b.bag_count > 0 THEN '0'  -- 有货物
    ELSE '1'                        -- 无货物
END,
p.update_time = NOW(),
p.update_user_name = 'system';


-- ====================================================================
-- 查询验证 SQL
-- ====================================================================

-- 查询托盘状态统计
SELECT 
    is_empty,
    CASE 
        WHEN is_empty = '1' THEN '空箱'
        WHEN is_empty = '0' THEN '有货'
        ELSE '未知'
    END as status_name,
    COUNT(*) as count
FROM pallet_info
GROUP BY is_empty;

-- 查询有货物但标记为空箱的异常数据
SELECT 
    p.code as pallet_code,
    p.ware_code,
    p.is_empty,
    COUNT(b.id) as bag_count
FROM pallet_info p
LEFT JOIN bag_master b ON p.code = b.pallet_code AND p.ware_code = b.ware_code
WHERE p.is_empty = '1'
GROUP BY p.code, p.ware_code, p.is_empty
HAVING COUNT(b.id) > 0;

-- 查询无货物但标记为有货的异常数据
SELECT 
    p.code as pallet_code,
    p.ware_code,
    p.is_empty,
    COUNT(b.id) as bag_count
FROM pallet_info p
LEFT JOIN bag_master b ON p.code = b.pallet_code AND p.ware_code = b.ware_code
WHERE p.is_empty = '0'
GROUP BY p.code, p.ware_code, p.is_empty
HAVING COUNT(b.id) = 0;

-- 查询托盘和货物的详细关联情况
SELECT 
    p.code as pallet_code,
    p.ware_code,
    p.cell_code,
    p.is_empty,
    CASE 
        WHEN p.is_empty = '1' THEN '空箱'
        WHEN p.is_empty = '0' THEN '有货'
        ELSE '未知'
    END as status_name,
    COUNT(b.id) as bag_count,
    GROUP_CONCAT(b.code) as bag_codes
FROM pallet_info p
LEFT JOIN bag_master b ON p.code = b.pallet_code AND p.ware_code = b.ware_code
GROUP BY p.code, p.ware_code, p.cell_code, p.is_empty
ORDER BY p.code;


-- ====================================================================
-- 定时任务 SQL（可选）
-- 如果需要定期同步，可以创建存储过程或定时任务
-- ====================================================================

DELIMITER $$

CREATE PROCEDURE sync_pallet_is_empty()
BEGIN
    -- 记录开始时间
    DECLARE start_time DATETIME DEFAULT NOW();
    DECLARE affected_rows INT DEFAULT 0;
    
    -- 更新托盘状态
    UPDATE pallet_info p
    LEFT JOIN (
        SELECT 
            pallet_code,
            ware_code,
            COUNT(*) as bag_count
        FROM bag_master
        WHERE pallet_code IS NOT NULL
          AND pallet_code != ''
        GROUP BY pallet_code, ware_code
    ) b ON p.code = b.pallet_code AND p.ware_code = b.ware_code
    SET p.is_empty = CASE 
        WHEN b.bag_count > 0 THEN '0'
        ELSE '1'
    END,
    p.update_time = NOW(),
    p.update_user_name = 'system_sync';
    
    -- 获取影响行数
    SET affected_rows = ROW_COUNT();
    
    -- 记录日志（假设有日志表）
    -- INSERT INTO sync_log (task_name, start_time, end_time, affected_rows, status)
    -- VALUES ('sync_pallet_is_empty', start_time, NOW(), affected_rows, 'SUCCESS');
    
    -- 返回结果
    SELECT 
        'sync_pallet_is_empty' as task_name,
        start_time,
        NOW() as end_time,
        affected_rows,
        'SUCCESS' as status;
        
END$$

DELIMITER ;

-- 调用存储过程
-- CALL sync_pallet_is_empty();

-- 删除存储过程（如果需要）
-- DROP PROCEDURE IF EXISTS sync_pallet_is_empty;


-- ====================================================================
-- 回滚脚本（谨慎使用）
-- ====================================================================

-- 将所有托盘状态重置为 NULL
-- UPDATE pallet_info 
-- SET is_empty = NULL,
--     update_time = NOW(),
--     update_user_name = 'system_rollback';


-- ====================================================================
-- 使用建议
-- ====================================================================

/*
推荐使用方式二（分步更新），原因：
1. 逻辑清晰，易于理解和维护
2. 可以分步执行，便于问题排查
3. 执行效率较高
4. 可以在两步之间查看中间结果

执行步骤：
1. 备份数据（可选但推荐）
   CREATE TABLE pallet_info_backup AS SELECT * FROM pallet_info;

2. 执行更新脚本（方式二）
   -- 先设置所有为空箱
   -- 再将有货物的设置为有货

3. 验证数据
   -- 运行查询验证 SQL

4. 如果有问题，可以从备份恢复
   -- 或执行回滚脚本

性能优化建议：
1. 确保以下字段有索引：
   - pallet_info.code
   - pallet_info.ware_code
   - bag_master.pallet_code
   - bag_master.ware_code

2. 如果数据量大，建议分批更新：
   WHERE p.id BETWEEN 1 AND 10000
   WHERE p.id BETWEEN 10001 AND 20000
   ...

3. 在业务低峰期执行

4. 执行前锁表或使用事务
   START TRANSACTION;
   -- 执行更新语句
   COMMIT;
*/

