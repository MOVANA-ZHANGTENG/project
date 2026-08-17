package com.deer.wcs.task.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.task.model.DeviceTaskResult;
import com.deer.wcs.task.model.DeviceTaskResultDto;
import com.deer.wcs.task.model.DeviceTaskResultCriteria;
import org.apache.ibatis.annotations.Param;

/**
 * 设备任务回传Mapper接口
 * 
 * @author deer
 * @date 2024-11-22
 */
public interface DeviceTaskResultMapper  extends Mapper<DeviceTaskResult>
{
    /**
     * 查询设备任务回传
     *
     * @param id 设备任务回传主键
     * @return 设备任务回传
     */
    public DeviceTaskResult selectDeviceTaskResultById(Long id);
    public DeviceTaskResult getFirstState0ByTaskCode(@Param("taskCode") String taskCode);
    
    /**
     * 查询时间最早的未处理记录（state=0）
     * @return 最早的未处理记录
     */
    public DeviceTaskResult getEarliestUnprocessedRecord();

    /**
     * 查询设备任务回传列表
     * 
     * @param deviceTaskResult 设备任务回传
     * @return 设备任务回传集合
     */
    public List<DeviceTaskResultDto> findList(DeviceTaskResultCriteria criteria);

    /**
     * 新增设备任务回传
     *
     * @param deviceTaskResult 设备任务回传
     * @return 结果
     */
    public int insertDeviceTaskResult(DeviceTaskResult deviceTaskResult);

    /**
     * 修改设备任务回传
     *
     * @param deviceTaskResult 设备任务回传
     * @return 结果
     */
    public int updateDeviceTaskResult(DeviceTaskResult deviceTaskResult);

    /**
     * 删除设备任务回传
     * 
     * @param id 设备任务回传主键
     * @return 结果
     */
    public int deleteDeviceTaskResultById(Long id);

    /**
     * 批量删除设备任务回传
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeviceTaskResultByIds(Long[] ids);
}
