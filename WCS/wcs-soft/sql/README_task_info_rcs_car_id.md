# TaskInfo 表添加 RCS 车辆ID 字段升级说明

## 修改目的
为 `task_info` 表添加 `rcs_car_id` 字段，用于关联 RCS 车辆信息，支持任务与车辆的绑定关系。

## 涉及的修改

### 1. 数据库修改
- 文件：`sql/task_info_add_rcs_car_id.sql`
- 内容：
  - 添加 `rcs_car_id` 字段（BIGINT, 可为空）
  - 添加索引 `idx_rcs_car_id` 以优化查询性能
  - 可选：外键约束（根据业务需要决定是否添加）

### 2. 后端修改

#### 2.1 实体类
- **TaskInfo.java**
  - 添加 `rcsCarId` 字段（Long 类型）
  - 添加对应的 getter/setter 方法

- **TaskInfoDto.java**
  - 添加 `rcsCarName` 字段（String 类型）
  - 添加对应的 getter/setter 方法

#### 2.2 Mapper 配置
- **TaskInfoMapper.xml**
  - 在 `TaskInfoResult` 中添加 `rcsCarId` 字段映射
  - 在 `Dto` 中添加 `rcsCarName` 字段映射
  - 在 `findList` 查询中：
    - 关联 `rcs_car_info` 表
    - 添加 `rcsCarId` 查询条件
  - 在 `insertTaskInfo` 中添加 `rcsCarId` 字段
  - 在 `updateTaskInfo` 中添加 `rcsCarId` 字段

### 3. 前端修改

#### 3.1 API 导入
- 导入 `listRcsCarInfo` 接口

#### 3.2 数据管理
- 添加 `rcsCarInfos` 数组用于存储车辆列表
- 在 `queryParams` 中添加 `rcsCarId` 查询参数
- 在 `form` 中添加 `rcsCarId` 字段
- 添加 `getRcsCarInfos()` 方法获取车辆列表

#### 3.3 UI 组件
- **查询区域**：添加车辆下拉框筛选条件
- **新增/编辑对话框**：添加车辆选择下拉框
- **表格展示**：添加"车辆信息"列，显示车辆名称和ID

## 执行步骤

### 1. 备份数据库
```sql
-- 备份 task_info 表
CREATE TABLE task_info_backup_20251030 AS SELECT * FROM task_info;
```

### 2. 执行升级脚本
```sql
-- 执行数据库升级脚本
source sql/task_info_add_rcs_car_id.sql;
```

### 3. 验证数据库修改
```sql
-- 检查字段是否添加成功
DESC task_info;

-- 检查索引是否创建成功
SHOW INDEX FROM task_info WHERE Key_name = 'idx_rcs_car_id';
```

### 4. 部署后端代码
- 编译项目：`mvn clean package -DskipTests`
- 重启 WCS 服务

### 5. 部署前端代码
- 构建前端：`npm run build`
- 部署 dist 目录到服务器

### 6. 功能测试
1. **查询功能**：
   - 使用车辆下拉框筛选任务
   - 验证车辆信息在表格中正确显示

2. **新增功能**：
   - 创建新任务时选择车辆
   - 验证任务创建成功且 rcs_car_id 保存正确

3. **修改功能**：
   - 修改任务的车辆信息
   - 验证修改后数据正确

4. **展示功能**：
   - 确认车辆名称和ID在列表中正确显示
   - 未绑定车辆的任务显示"-"

## 注意事项

1. **数据完整性**：
   - `rcs_car_id` 字段设置为可为空，允许任务不绑定车辆
   - 如需强制绑定，可在业务层添加校验

2. **性能优化**：
   - 已为 `rcs_car_id` 字段添加索引
   - 关联查询使用 LEFT JOIN，不影响无车辆的任务查询

3. **兼容性**：
   - 旧数据的 `rcs_car_id` 为 NULL
   - 前端已处理 NULL 值显示

4. **回滚方案**：
   如需回滚，执行以下语句：
   ```sql
   -- 删除索引
   ALTER TABLE task_info DROP INDEX idx_rcs_car_id;
   
   -- 删除字段
   ALTER TABLE task_info DROP COLUMN rcs_car_id;
   ```

## 功能说明

### 车辆选择
- 支持模糊搜索（filterable）
- 可清空选择（clearable）
- 下拉列表显示车辆名称

### 车辆展示
- 表格中显示车辆名称（加粗）
- 下方显示车辆ID（灰色小字）
- 未绑定车辆显示灰色"-"

### 查询筛选
- 可按车辆ID筛选任务
- 支持与其他条件组合查询

## 相关文件清单

### 后端文件
- `wcs-task/src/main/java/com/deer/wcs/task/model/TaskInfo.java`
- `wcs-task/src/main/java/com/deer/wcs/task/model/TaskInfoDto.java`
- `wcs-task/src/main/java/com/deer/wcs/task/mapper/TaskInfoMapper.xml`

### 前端文件
- `wcs-ui/src/views/wcs-task/TaskInfo/index.vue`
- `wcs-ui/src/api/wcs-rcs/RcsCarInfo.js` (已存在)

### SQL 文件
- `sql/task_info_add_rcs_car_id.sql`
- `sql/README_task_info_rcs_car_id.md`

## 更新日期
2025-10-30

