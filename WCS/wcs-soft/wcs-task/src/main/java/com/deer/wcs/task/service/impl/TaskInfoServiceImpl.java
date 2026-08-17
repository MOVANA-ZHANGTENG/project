package com.deer.wcs.task.service.impl;

import com.deer.wcs.base.model.AreaInfo;
import com.deer.wcs.base.model.WareInfo;
import com.deer.wcs.base.service.AreaInfoService;
import com.deer.wcs.base.service.WareInfoService;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.system.service.BillRecordService;
import com.deer.wcs.task.dao.TaskInfoMapper;
import com.deer.wcs.task.model.*;
import com.deer.wcs.task.service.TaskInfoListHistoryService;
import com.deer.wcs.task.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 任务Service业务层处理
 * 
 * @author deer
 * @date 2024-04-30
 */
@Service
public class TaskInfoServiceImpl  extends AbstractService<TaskInfo, Long>  implements TaskInfoService
{
    @Autowired
    private TaskInfoMapper taskInfoMapper;
    @Autowired
    private TaskInfoListHistoryService taskInfoListHistoryService;

    @Autowired
    private AutoService autoService;
    @Autowired
    private BillRecordService billRecordService;
    @Autowired
    private WareInfoService wareInfoService;
    @Autowired
    private AreaInfoService areaInfoService;

    @Override
    public void updateMemo(TaskInfo taskInfo, String msg) {
        if(msg==null){
            msg="";
        }
        if(  taskInfo.getMemo()==null || !taskInfo.getMemo().equals(msg)){
            taskInfo.setMemo(msg);
            billRecordService.createTaskRecord(taskInfo.getId(), msg);
            super.update(taskInfo);

        }
    }

    @Override
    public void save(TaskInfo model) {
        if(model.getWareCode()!=null && !model.getWareCode().equals("")){
            WareInfo wareInfo = wareInfoService.findBy("code",model.getWareCode());
            if(wareInfo!=null){
                model.setWareName(wareInfo.getName());
            }
        }
        if(model.getAreaCode()!=null && !model.getAreaCode().equals("")){
            AreaInfo areaInfo = areaInfoService.findBy("code",model.getAreaCode());
            if(areaInfo!=null){
                model.setAreaName(areaInfo.getName());
            }
        }
        model.setState(0);
        model.setVersion(0);
        model.setMemo("等待任务执行");
        model.setId(autoService.getTaskInfoId());
        model.setCreateTime(DateUtil.getNowDateTimeString());
        if(model.getPriority()==null){
            model.setPriority(10);
        }
        super.save(model);
        billRecordService.createTaskRecord(model.getId(), "任务已创建！");
//        saveTaskListHistory(model);
    }

    @Override
    public int update(TaskInfo model) {
        Condition condition = new Condition(TaskInfo.class);
        Example.Criteria criteria = condition.createCriteria();
        criteria.andEqualTo("id", model.getId());
        criteria.andEqualTo("version", model.getVersion());
        model.setVersion(model.getVersion() + 1);
        int count = super.updateByConditionSelective(model, condition);
        if (count == 0) {
            throw new ServiceException("请重试", 500);
        }
       // saveTaskListHistory(model);
        return super.update(model);
    }

    private void saveTaskListHistory(TaskInfo taskInfo){
        TaskInfoListHistory his = new TaskInfoListHistory();
        his.setTaskId(taskInfo.getId());
        his.setTaskNo(taskInfo.getTaskNo());
        his.setWmsTaskNo(taskInfo.getWmsTaskNo());
        his.setWareCode(taskInfo.getWareCode());
        his.setWareName(taskInfo.getWareName());
        his.setAreaCode(taskInfo.getAreaCode());
        his.setAreaName(taskInfo.getAreaName());
        his.setType(taskInfo.getType());
        his.setPalletCode(taskInfo.getPalletCode());
        his.setFromCellCode(taskInfo.getFromCellCode());
        his.setToCellCode(taskInfo.getToCellCode());
        his.setCreateTime(taskInfo.getCreateTime());
        his.setFinishTime(DateUtil.getNowDateTimeString());
        his.setState(taskInfo.getState());
        his.setVersion(0L);
        his.setMemo(taskInfo.getMemo());
        his.setPalletType(taskInfo.getPalletType());
        his.setPalletHeight(taskInfo.getPalletHeight());
        his.setRemark1(taskInfo.getRemark1());
        his.setRemark2(taskInfo.getRemark2());
        his.setRemark3(taskInfo.getRemark3());

        taskInfoListHistoryService.save(his);
    }

    @Override
    public List<TaskInfo> findTaskInfoState(String wareCode, Integer state) {
        return taskInfoMapper.findTaskInfoState(wareCode,state);
    }

    /**
     * 查询任务
     *
     * @param id 任务主键
     * @return 任务
     */
    @Override
    public TaskInfo selectTaskInfoById(Long id)
    {
        return taskInfoMapper.selectTaskInfoById(id);
    }

    @Override
    public TaskInfo getTaskInfoByPalletCode(String palletCode) {
        return super.findBy("palletCode",palletCode);
    }

    /**
     * 查询任务列表
     * 
     * @param criteria
     * @return 任务
     */
    @Override
    public List<TaskInfoDto> findList(TaskInfoCriteria criteria)
    {
        return taskInfoMapper.findList(criteria);
    }

    /**
     * 新增任务
     *
     * @param taskInfo 任务
     * @return 结果
     */
    @Override
    public int insertTaskInfo(TaskInfo taskInfo)
    {
        taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return taskInfoMapper.insertTaskInfo(taskInfo);
    }

    /**
     * 修改任务
     *
     * @param taskInfo 任务
     * @return 结果
     */
    @Override
    public int updateTaskInfo(TaskInfo taskInfo)
    {
        return taskInfoMapper.updateTaskInfo(taskInfo);
    }

    /**
     * 批量删除任务
     * 
     * @param ids 需要删除的任务主键
     * @return 结果
     */
    @Override
    public int deleteTaskInfoByIds(Long[] ids)
    {
        return taskInfoMapper.deleteTaskInfoByIds(ids);
    }

    /**
     * 删除任务信息
     * 
     * @param id 任务主键
     * @return 结果
     */
    @Override
    public int deleteTaskInfoById(Long id)
    {
        return taskInfoMapper.deleteTaskInfoById(id);
    }

    /**
     * 用于保存已经完成的任务
     * @param taskId
     */
    @Override
    public void recordHistory(Long taskId) {
        taskInfoMapper.recordHistory(taskId);
    }

    /**
     * 统计指定仓库7天内任务类型分布
     * @param wareCode 仓库编码
     * @return 统计结果
     */
    @Override
    public TaskStatisticsDto getSevenDaysStatistics(String wareCode) {
        TaskStatisticsDto result = new TaskStatisticsDto();
        
        // 生成最近7天的日期
        List<String> dates = new ArrayList<>();
        List<String> fullDates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat shortSdf = new SimpleDateFormat("MM-dd");
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -6); // 从6天前开始
        
        for (int i = 0; i < 7; i++) {
            Date date = calendar.getTime();
            dates.add(shortSdf.format(date));
            fullDates.add(sdf.format(date));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        // 设置日期范围
        String startDate = fullDates.get(0) + " 00:00:00";
        String endDate = fullDates.get(fullDates.size() - 1) + " 23:59:59";
        
        // 查询数据库
        List<Map<String, Object>> statistics = taskInfoMapper.selectSevenDaysStatistics(
            wareCode, startDate, endDate
        );
        
        // 整理数据：按任务类型分组
        Map<String, TaskStatisticsDto.TaskTypeStatistics> taskTypeMap = new LinkedHashMap<>();
        
        for (Map<String, Object> row : statistics) {
            String date = (String) row.get("date");
            String taskTypeCode = (String) row.get("taskTypeCode");
            String taskTypeName = (String) row.get("taskTypeName");
            Integer count = convertToInt(row.get("count"));
            
            // 如果任务类型不存在，创建
            if (!taskTypeMap.containsKey(taskTypeCode)) {
                TaskStatisticsDto.TaskTypeStatistics typeStats = new TaskStatisticsDto.TaskTypeStatistics();
                typeStats.setCode(taskTypeCode);
                typeStats.setName(taskTypeName != null ? taskTypeName : "未知类型");
                // 初始化7天的数据为0
                List<Integer> data = new ArrayList<>(Collections.nCopies(7, 0));
                typeStats.setData(data);
                taskTypeMap.put(taskTypeCode, typeStats);
            }
            
            // 找到日期对应的索引
            int dateIndex = fullDates.indexOf(date);
            if (dateIndex >= 0) {
                taskTypeMap.get(taskTypeCode).getData().set(dateIndex, count);
            }
        }
        
        // 设置返回结果
        result.setDates(dates);
        result.setTaskTypes(new ArrayList<>(taskTypeMap.values()));
        
        return result;
    }

    /**
     * 统计指定仓库的任务基础数据（总数、今日新增等）
     * @param wareCode 仓库编码
     * @return 统计结果
     */
    @Override
    public TaskBasicStatisticsDto getBasicStatistics(String wareCode) {
        TaskBasicStatisticsDto result = new TaskBasicStatisticsDto();
        
        // 获取今天的起止时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        String todayStart = today + " 00:00:00";
        String todayEnd = today + " 23:59:59";
        
        // 查询数据库
        Map<String, Object> statistics = taskInfoMapper.selectBasicStatistics(
            wareCode, todayStart, todayEnd
        );
        
        // 组装返回结果
        if (statistics != null) {
            result.setTotal(convertToInt(statistics.get("total")));
            result.setToday(convertToInt(statistics.get("today")));
            result.setRunning(convertToInt(statistics.get("running")));
            result.setCompleted(convertToInt(statistics.get("completed")));
            result.setFailed(convertToInt(statistics.get("failed")));
        } else {
            // 如果查询结果为空，返回0
            result.setTotal(0);
            result.setToday(0);
            result.setRunning(0);
            result.setCompleted(0);
            result.setFailed(0);
        }
        
        return result;
    }

    /**
     * 将数据库返回的数值转换为Integer
     * 兼容Long、BigDecimal等类型
     */
    private Integer convertToInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        if (value instanceof java.math.BigDecimal) {
            return ((java.math.BigDecimal) value).intValue();
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }
}
