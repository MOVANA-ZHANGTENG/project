package com.deer.wcs.base.dao;

import java.util.List;
import com.deer.wcs.common.core.mapper.Mapper;
import com.deer.wcs.base.model.DeviceValue;
import com.deer.wcs.base.model.DeviceValueDto;
import com.deer.wcs.base.model.DeviceValueCriteria;

/**
 * 属性Mapper接口
 * 
 * @author deer
 * @date 2024-05-16
 */
public interface DeviceValueMapper  extends Mapper<DeviceValue>
{
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
     * @param deviceValue 属性
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
     * 删除属性
     * 
     * @param id 属性主键
     * @return 结果
     */
    public int deleteDeviceValueById(Long id);

    /**
     * 批量删除属性
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDeviceValueByIds(Long[] ids);

    void deleteByDeviceIds(Long[] deviceIds);
}
