package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.DeviceInfoMapper;
import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.model.DeviceInfoCriteria;
import com.deer.wcs.base.model.DeviceInfoDto;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备Service业务层处理
 * 
 * @author deer
 * @date 2024-05-16
 */
@Service
public class DeviceInfoServiceImpl  extends AbstractService<DeviceInfo, Long>  implements DeviceInfoService
{
    @Autowired
    private DeviceInfoMapper deviceInfoMapper;
    @Autowired
    private DeviceValueService deviceValueService;

    @Override
    public void save(DeviceInfo model) {
        model.setIsOnline(0);
        model.setState(0);
        model.setVersion(0);
        model.setIsDelete(0);
        super.save(model);
    }


    /**
     * 查询设备
     *
     * @param id 设备主键
     * @return 设备
     */
    @Override
    public DeviceInfo selectDeviceInfoById(Long id)
    {
        return deviceInfoMapper.selectDeviceInfoById(id);
    }

    /**
     * 查询设备列表
     * 
     * @param criteria
     * @return 设备
     */
    @Override
    public List<DeviceInfoDto> findList(DeviceInfoCriteria criteria)
    {
        return deviceInfoMapper.findList(criteria);
    }

    /**
     * 新增设备
     *
     * @param deviceInfo 设备
     * @return 结果
     */
    @Override
    public int insertDeviceInfo(DeviceInfo deviceInfo)
    {
        deviceInfo.setCreateTime(DateUtil.getNowDateTimeString());
        return deviceInfoMapper.insertDeviceInfo(deviceInfo);
    }

    /**
     * 修改设备
     *
     * @param deviceInfo 设备
     * @return 结果
     */
    @Override
    public int updateDeviceInfo(DeviceInfo deviceInfo)
    {
        deviceInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        return deviceInfoMapper.updateDeviceInfo(deviceInfo);
    }

    /**
     * 批量删除设备
     * 
     * @param ids 需要删除的设备主键
     * @return 结果
     */
    @Override
    public int deleteDeviceInfoByIds(Long[] ids)
    {
        return deviceInfoMapper.deleteDeviceInfoByIds(ids);
    }

    /**
     * 删除设备信息
     * 
     * @param id 设备主键
     * @return 结果
     */
    @Override
    public int deleteDeviceInfoById(Long id)
    {
        return deviceInfoMapper.deleteDeviceInfoById(id);
    }

    @Override
    public DeviceInfo findByCode(String code) {
        return deviceInfoMapper.findByCode(code);
    }
}
