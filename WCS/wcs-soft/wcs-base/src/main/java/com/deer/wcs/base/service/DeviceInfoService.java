package com.deer.wcs.base.service;

import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.model.DeviceInfoCriteria;
import com.deer.wcs.base.model.DeviceInfoDto;
import com.deer.wcs.common.core.service.Service;

import java.util.List;

/**
 * 设备Service接口
 * 
 * @author deer
 * @date 2024-05-16
 */
public interface DeviceInfoService   extends Service<DeviceInfo, Long>
{
    /**
     * 查询设备
     *
     * @param id 设备主键
     * @return 设备
     */
    public DeviceInfo selectDeviceInfoById(Long id);

    /**
     * 查询设备列表
     * 
     * @param criteria
     * @return 设备集合
     */
    public List<DeviceInfoDto> findList(DeviceInfoCriteria criteria);

    /**
     * 新增设备
     *
     * @param deviceInfo 设备
     * @return 结果
     */
    public int insertDeviceInfo(DeviceInfo deviceInfo);

    /**
     * 修改设备
     *
     * @param deviceInfo 设备
     * @return 结果
     */
    public int updateDeviceInfo(DeviceInfo deviceInfo);

    /**
     * 批量删除设备
     * 
     * @param ids 需要删除的设备主键集合
     * @return 结果
     */
    public int deleteDeviceInfoByIds(Long[] ids);

    /**
     * 删除设备信息
     * 
     * @param id 设备主键
     * @return 结果
     */
    public int deleteDeviceInfoById(Long id);

    DeviceInfo findByCode(String code);

}
