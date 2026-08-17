package com.deer.wcs.base.dao;

import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.model.DeviceInfoCriteria;
import com.deer.wcs.base.model.DeviceInfoDto;
import com.deer.wcs.common.core.mapper.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 设备Mapper接口
 * 
 * @author deer
 * @date 2024-05-16
 */
public interface DeviceInfoMapper  extends Mapper<DeviceInfo>
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
     * @param deviceInfo 设备
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
     * 删除设备
     * 
     * @param id 设备主键
     * @return 结果
     */
    public int deleteDeviceInfoById(Long id);

    /**
     * 批量删除设备
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeviceInfoByIds(Long[] ids);

    DeviceInfo findByCode(@Param("code") String code);
}
