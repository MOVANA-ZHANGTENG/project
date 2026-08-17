package com.deer.wcs.base.service;

import java.util.List;
import java.util.Map;

import com.deer.wcs.base.model.ValueData;
import com.deer.wcs.common.core.service.Service;
import com.deer.wcs.base.model.DeviceValue;
import com.deer.wcs.base.model.DeviceValueDto;
import com.deer.wcs.base.model.DeviceValueCriteria;

/**
 * 属性Service接口
 * 
 * @author deer
 * @date 2024-05-16
 */
public interface DeviceValueService   extends Service<DeviceValue, Long>
{
    Object readValueByCode(String deviceCode, String valueCode);
    Boolean writeValueByCode(String deviceCode, String valueCode,Object object);
    
    /**
     * 批量写入数据，减少设备通讯次数
     * @param deviceCode 设备编码
     * @param valueCodeMap 值编码和值的映射
     * @return 是否成功
     */
    Boolean batchWriteValueByCode(String deviceCode, Map<String, Object> valueCodeMap);

    /**
     * 查询属性
     *
     * @param id 属性主键
     * @return 属性
     */
    public DeviceValue selectDeviceValueById(Long id);

    /**
     * 查询属性列表
     * 
     * @param criteria
     * @return 属性集合
     */
    public List<DeviceValueDto> findList(DeviceValueCriteria criteria);

    /**
     * 新增属性
     *
     * @param deviceValue 属性
     * @return 结果
     */
    public int insertDeviceValue(DeviceValue deviceValue);

    /**
     * 修改属性
     *
     * @param deviceValue 属性
     * @return 结果
     */
    public int updateDeviceValue(DeviceValue deviceValue);

    /**
     * 批量删除属性
     * 
     * @param ids 需要删除的属性主键集合
     * @return 结果
     */
    public int deleteDeviceValueByIds(Long[] ids);

    /**
     * 删除属性信息
     * 
     * @param id 属性主键
     * @return 结果
     */
    public int deleteDeviceValueById(Long id);

    void deleteByDeviceIds(Long[] deviceIds);
}
