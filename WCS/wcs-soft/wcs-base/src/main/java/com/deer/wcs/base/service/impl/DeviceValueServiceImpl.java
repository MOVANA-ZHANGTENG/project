package com.deer.wcs.base.service.impl;

import com.deer.wcs.base.dao.DeviceValueMapper;
import com.deer.wcs.base.model.DeviceValue;
import com.deer.wcs.base.model.DeviceValueCriteria;
import com.deer.wcs.base.model.DeviceValueDto;
import com.deer.wcs.base.model.ValueData;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.base.utils.PLCUtils;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 属性Service业务层处理
 * 
 * @author deer
 * @date 2024-05-16
 */
@Service
public class DeviceValueServiceImpl  extends AbstractService<DeviceValue, Long>  implements DeviceValueService
{
    private static final Logger log = LoggerFactory.getLogger(DeviceValueServiceImpl.class);
    @Autowired
    private DeviceValueMapper deviceValueMapper;
    @Autowired
    private DeviceInfoService deviceInfoService;


    @Autowired
    private RedisCache redisCache;

    @Autowired
    private PLCUtils plcUtils;

    @Override
    public void save(DeviceValue model) {
        model.setBitIndex(0);
        model.setVersion(0);
        model.setIsDelete(0);
        super.save(model);
    }
    @Override
    public int update(DeviceValue model) {
        model.setBitIndex(0);
        super.update(model);
        redisCache.deleteObject(redisKey(model.getDeviceCode(),model.getCode()));
        return 1;
    }



    private static String  redisKey(String deviceCode, String valueCode){
        return "device_value_model:"+deviceCode+"-"+valueCode;
    }


    private DeviceValue getByCode(String deviceCode, String valueCode){
        DeviceValue deviceValue;
        Object o = redisCache.getCacheObject(redisKey(deviceCode,valueCode));
        if(o!=null){
            deviceValue=(DeviceValue)o;
        }
        Condition condition = new Condition(DeviceValue.class);
        condition.createCriteria().andEqualTo("deviceCode",deviceCode)
                .andEqualTo("code",valueCode)
                .andEqualTo("isDelete",0);
        List<DeviceValue> deviceValueList = super.findByCondition(condition);
        if(deviceValueList.size()==0){
            throw new ServiceException("未找到该属性："+deviceCode+"-"+valueCode);
        }
        if(deviceValueList.size()>1){
            throw new ServiceException("该属性重复定义："+deviceCode+"-"+valueCode);
        }
        deviceValue=deviceValueList.get(0);
        redisCache.setCacheObject(redisKey(deviceCode,valueCode),deviceValue);
        return deviceValue;
    }


    /**
     *
     * @param deviceCode  设备编码
     * @param valueCode   值编码
     * @return
     */
    @Override
    public Object readValueByCode(String deviceCode, String valueCode) {
        DeviceValue deviceValue=getByCode(deviceCode,valueCode);
        ValueData<Object> data =  plcUtils.plcRead(deviceValue);
        return data.getData();
    }

    @Override
    public Boolean writeValueByCode(String deviceCode, String valueCode,Object object) {
        DeviceValue deviceValue=getByCode(deviceCode,valueCode);
        plcUtils.plcWrite(deviceValue,new ValueData<Object>(object));
        return true;
    }

    @Override
    public Boolean batchWriteValueByCode(String deviceCode, Map<String, Object> valueCodeMap) {
        try {
            // 获取所有需要写入的设备值
            Map<String, DeviceValue> deviceValueMap = new HashMap<>();
            for (String valueCode : valueCodeMap.keySet()) {
                DeviceValue deviceValue = getByCode(deviceCode, valueCode);
                deviceValueMap.put(valueCode, deviceValue);
            }
            
            // 按通讯类型分组，同类型的可以一起处理
            Map<Integer, List<Map.Entry<String, Object>>> groupedValues = valueCodeMap.entrySet().stream()
                    .collect(Collectors.groupingBy(entry -> {
                        DeviceValue dv = deviceValueMap.get(entry.getKey());
                        return dv != null ? dv.getComType() : -1;
                    }));
            
            // 批量处理每一种通讯类型
            for (Map.Entry<Integer, List<Map.Entry<String, Object>>> entry : groupedValues.entrySet()) {
                Integer comType = entry.getKey();
                if (comType == -1) continue;
                
                // 这里可以根据不同的通讯类型实现真正的批量写入
                // 目前先按单条写入，但通过一次获取所有deviceValue减少数据库查询
                for (Map.Entry<String, Object> valueEntry : entry.getValue()) {
                    String valueCode = valueEntry.getKey();
                    Object value = valueEntry.getValue();
                    DeviceValue deviceValue = deviceValueMap.get(valueCode);
                    if (deviceValue != null) {
                        plcUtils.plcWrite(deviceValue, new ValueData<>(value));
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("批量写入设备值失败: deviceCode={}", deviceCode, e);
            return false;
        }
    }

    /**
     * 查询属性
     *
     * @param id 属性主键
     * @return 属性
     */
    @Override
    public DeviceValue selectDeviceValueById(Long id)
    {
        return deviceValueMapper.selectDeviceValueById(id);
    }

    /**
     * 查询属性列表
     * 
     * @param criteria
     * @return 属性
     */
    @Override
    public List<DeviceValueDto> findList(DeviceValueCriteria criteria)
    {
        return deviceValueMapper.findList(criteria);
    }

    /**
     * 新增属性
     *
     * @param deviceValue 属性
     * @return 结果
     */
    @Override
    public int insertDeviceValue(DeviceValue deviceValue)
    {
        deviceValue.setCreateTime(DateUtil.getNowDateTimeString());
        return deviceValueMapper.insertDeviceValue(deviceValue);
    }

    /**
     * 修改属性
     *
     * @param deviceValue 属性
     * @return 结果
     */
    @Override
    public int updateDeviceValue(DeviceValue deviceValue)
    {
        deviceValue.setUpdateTime(DateUtil.getNowDateTimeString());
        return deviceValueMapper.updateDeviceValue(deviceValue);
    }

    /**
     * 批量删除属性
     * 
     * @param ids 需要删除的属性主键
     * @return 结果
     */
    @Override
    public int deleteDeviceValueByIds(Long[] ids)
    {
        return deviceValueMapper.deleteDeviceValueByIds(ids);
    }

    /**
     * 删除属性信息
     * 
     * @param id 属性主键
     * @return 结果
     */
    @Override
    public int deleteDeviceValueById(Long id)
    {
        return deviceValueMapper.deleteDeviceValueById(id);
    }

    @Override
    public void deleteByDeviceIds(Long[] deviceIds) {
        deviceValueMapper.deleteByDeviceIds(deviceIds);
    }
}
