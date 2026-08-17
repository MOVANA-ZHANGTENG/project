# JobInfo lastJobId 功能实现说明

## 功能概述

本次修改实现了根据 TaskDefine 的 lastId 逻辑，在创建 JobInfo 时自动设置 lastJobId 字段，用于表示任务步骤之间的依赖关系。

## 修改内容

### 1. 数据库变更

**文件：** `sql/job_info_add_last_job_id.sql`

为 `job_info` 表添加了 `last_job_id` 字段：
- 字段类型：BIGINT(20)
- 允许为空：YES
- 说明：记录上一个步骤的 Job ID，用于任务依赖
- 索引：添加了 `idx_last_job_id` 索引以提高查询性能

**执行步骤：**
```sql
-- 执行以下SQL脚本
source sql/job_info_add_last_job_id.sql;
```

### 2. Java 模型修改

**文件：** `wcs-task/src/main/java/com/deer/wcs/task/model/JobInfo.java`

添加了 lastJobId 字段及其 getter/setter 方法：
- 新增字段：`private Long lastJobId;`
- 新增方法：`getLastJobId()` 和 `setLastJobId(Long lastJobId)`
- 更新了 `toString()` 方法，包含 lastJobId 字段

### 3. 业务逻辑修改

#### 3.1 TransactionTask.java

**文件：** `wcs-task/src/main/java/com/deer/wcs/task/task/TransactionTask.java`

修改了 `createJobInfoForTaskNew()` 方法，采用**两步处理**的方式：

**第一步：** 创建所有 JobInfo 并建立映射
- 遍历所有 TaskDefine，创建对应的 JobInfo（不设置 lastJobId）
- 创建对应的 JobHandle
- 使用 HashMap 维护 TaskDefine.id -> JobInfo 对象的映射关系

**第二步：** 根据 lastId 更新 lastJobId
- 再次遍历所有 TaskDefine
- 如果 TaskDefine 有 lastId，从映射中查找对应的前置 JobInfo
- 更新当前 JobInfo 的 lastJobId 字段并保存到数据库
- 如果引用的 lastId 不存在，记录警告日志但不中断流程

## 工作原理

1. **任务定义阶段：** TaskDefine 表中配置 lastId 字段，指向上一个步骤的 TaskDefine ID

2. **任务拆分阶段（两步处理）：** 
   
   **第一步 - 创建所有 JobInfo：**
   - 系统读取所有 TaskDefine（不需要排序）
   - 遍历 TaskDefine 列表，对每个 TaskDefine：
     - 创建 JobInfo（不设置 lastJobId）
     - 创建对应的 JobHandle
     - 将 TaskDefine.id -> JobInfo 对象存入映射表
   
   **第二步 - 设置 lastJobId：**
   - 再次遍历 TaskDefine 列表
   - 如果 TaskDefine 有 lastId，从映射表中查找对应的前置 JobInfo
   - 更新当前 JobInfo 的 lastJobId 字段

3. **任务执行阶段：** 可根据 lastJobId 判断前置任务是否完成，实现任务依赖控制

## 使用示例

### 配置任务定义

```sql
-- 假设有三个步骤的任务流程
INSERT INTO task_define (id, ware_code, type, name, job_index, last_id) VALUES
(1, 'WH001', 'INBOUND', '步骤1-接收', 1, NULL),          -- 第一个步骤，无前置
(2, 'WH001', 'INBOUND', '步骤2-验收', 2, 1),            -- 依赖步骤1
(3, 'WH001', 'INBOUND', '步骤3-入库', 3, 2);            -- 依赖步骤2
```

### 生成的 JobInfo

当系统根据上述 TaskDefine 生成 JobInfo 时：
- JobInfo 1: lastJobId = NULL（第一个步骤）
- JobInfo 2: lastJobId = JobInfo 1 的 ID
- JobInfo 3: lastJobId = JobInfo 2 的 ID

## 注意事项

1. **数据库迁移：** 在生产环境部署前，必须先执行 `job_info_add_last_job_id.sql` 脚本
2. **历史数据：** 已存在的 JobInfo 记录的 lastJobId 字段将为 NULL
3. **两步处理：** 采用两步处理的方式，先创建所有 JobInfo，再设置 lastJobId，确保任意顺序的 TaskDefine 都能正确处理
4. **空值处理：** 如果 lastId 引用了不存在的 TaskDefine，系统会记录警告日志，但不会中断流程
5. **循环依赖：** 系统不会自动检测循环依赖，请在配置 TaskDefine 时避免 A->B->C->A 这样的循环引用

## 测试建议

1. 测试简单链式依赖：A -> B -> C
2. 测试并行任务（多个任务无依赖）
3. 测试首个任务（lastId 为 NULL）
4. 测试 lastId 引用不存在的 TaskDefine 的情况
5. 验证日志输出是否正确记录警告信息

## 相关文件清单

- `sql/job_info_add_last_job_id.sql` - 数据库迁移脚本
- `sql/README_job_info_last_job_id.md` - 功能说明文档
- `wcs-task/src/main/java/com/deer/wcs/task/model/JobInfo.java` - JobInfo 模型
- `wcs-task/src/main/java/com/deer/wcs/task/task/TransactionTask.java` - 事务处理逻辑
- `wcs-task/src/main/java/com/deer/wcs/task/task/WcsTaskNew.java` - WCS 新任务处理（调用 TransactionTask）

## 版本信息

- 修改日期：2025-10-27
- 修改人：AI Assistant
- 版本：v1.0

