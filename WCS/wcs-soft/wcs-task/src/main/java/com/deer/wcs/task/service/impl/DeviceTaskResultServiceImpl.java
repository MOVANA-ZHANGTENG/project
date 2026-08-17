package com.deer.wcs.task.service.impl;

import java.util.List;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.deer.wcs.task.dao.DeviceTaskResultMapper;
import com.deer.wcs.task.model.DeviceTaskResult;
import com.deer.wcs.task.model.DeviceTaskResultDto;
import com.deer.wcs.task.model.DeviceTaskResultCriteria;
import com.deer.wcs.task.service.DeviceTaskResultService;

/**
 * 设备任务回传Service业务层处理
 * 
 * @author deer
 * @date 2024-11-22
 */
@Service
public class DeviceTaskResultServiceImpl  extends AbstractService<DeviceTaskResult, Long>  implements DeviceTaskResultService
{
    @Autowired
    private DeviceTaskResultMapper deviceTaskResultMapper;

    @Override
    public void save(DeviceTaskResult deviceTaskResult){
        deviceTaskResult.setCreateTime(DateUtil.getNowDateTimeString());
        super.save(deviceTaskResult);
    }

    @Override
    public DeviceTaskResult getFirstState0ByTaskCode(String taskCode) {
        return deviceTaskResultMapper.getFirstState0ByTaskCode(taskCode);
    }

    @Override
    public DeviceTaskResult getEarliestUnprocessedRecord() {
        return deviceTaskResultMapper.getEarliestUnprocessedRecord();
    }

    /**
     * 查询设备任务回传
     *
     * @param id 设备任务回传主键
     * @return 设备任务回传
     */
    @Override
    public DeviceTaskResult selectDeviceTaskResultById(Long id)
    {
        return deviceTaskResultMapper.selectDeviceTaskResultById(id);
    }

    /**
     * 查询设备任务回传列表
     * 
     * @param criteria
     * @return 设备任务回传
     */
    @Override
    public List<DeviceTaskResultDto> findList(DeviceTaskResultCriteria criteria)
    {
        return deviceTaskResultMapper.findList(criteria);
    }

    /**
     * 新增设备任务回传
     *
     * @param deviceTaskResult 设备任务回传
     * @return 结果
     */
    @Override
    public int insertDeviceTaskResult(DeviceTaskResult deviceTaskResult)
    {
        deviceTaskResult.setCreateTime(DateUtil.getNowDateTimeString());
        return deviceTaskResultMapper.insertDeviceTaskResult(deviceTaskResult);
    }

    /**
     * 修改设备任务回传
     *
     * @param deviceTaskResult 设备任务回传
     * @return 结果
     */
    @Override
    public int updateDeviceTaskResult(DeviceTaskResult deviceTaskResult)
    {
        return deviceTaskResultMapper.updateDeviceTaskResult(deviceTaskResult);
    }

    /**
     * 批量删除设备任务回传
     * 
     * @param ids 需要删除的设备任务回传主键
     * @return 结果
     */
    @Override
    public int deleteDeviceTaskResultByIds(Long[] ids)
    {
        return deviceTaskResultMapper.deleteDeviceTaskResultByIds(ids);
    }

    /**
     * 删除设备任务回传信息
     * 
     * @param id 设备任务回传主键
     * @return 结果
     */
    @Override
    public int deleteDeviceTaskResultById(Long id)
    {
        return deviceTaskResultMapper.deleteDeviceTaskResultById(id);
    }
}
