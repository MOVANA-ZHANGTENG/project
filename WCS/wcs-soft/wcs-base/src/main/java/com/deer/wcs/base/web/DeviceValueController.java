package com.deer.wcs.base.web;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.base.utils.PLCUtils;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.system.service.ISysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 属性Controller
 *
 * @author deer
 * @date 2024-05-16
 */
@Api("属性")
@RestController
@RequestMapping("/wcs-base/value")
public class DeviceValueController extends BaseController {

    public static final Logger log = LoggerFactory.getLogger(DeviceValueController.class);

    @Autowired
    private DeviceValueService deviceValueService;
    @Autowired
    private DeviceInfoService deviceInfoService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private PLCUtils plcUtils;
    /**
     * 查询属性列表
     */
    @ApiOperation("查询属性列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:value:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeviceValueCriteria Criteria) {
        startPage();
        List<DeviceValueDto> list = deviceValueService.findList(Criteria);
        for (DeviceValue deviceValue : list) {
            try{
                deviceValue = getValueFromRedis(deviceValue);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return getDataTable(list);
    }

    /**
     * 批量读取数据
     */
    @ApiOperation("批量读取数据")
    @GetMapping("/readPLCValueList")
    public Result readPLCValueList(String deviceCode) {
        Condition condition = new Condition(DeviceValue.class);
        condition.createCriteria().andEqualTo("isDelete",0)
                .andEqualTo("deviceCode",deviceCode);
        List<DeviceValue> deviceValueList = deviceValueService.findByCondition(condition);
        Integer total = deviceValueList.size();
        Integer success = 0;
        String msg="读取一下失败：";
        for(DeviceValue deviceValue:deviceValueList){
           // plcUtils.plcRead(deviceValue);
            try{
                plcUtils.plcRead(deviceValue);
                success++;
            }
            catch (Exception e){
                e.printStackTrace();
                msg+=deviceValue.getCode()+"【"+e.getMessage()+"】";
                msg+="\n";
            }
        }
        if(success<total){
            msg = "共读取"+total+" 成功："+success+"  "+msg;
        }else {
            msg=null;
        }

        return Result.success(msg);
    }

    /**
     * 读取数据
     */
    @ApiOperation("读取数据")
    @PostMapping("/write")
    public Result write(@RequestBody DeviceValue deviceValue) {
        String deviceCode = deviceValue.getDeviceCode();
        //只用于读取的字段不发
        if(deviceValue.getType()==0){
            return  error("只读字段");
        }
        if(deviceValue.getWriteValue()==null||deviceValue.getWriteValue().equals("")){
            return  error("没有值");
        }
        ValueData<Object> valueData ;
        switch (deviceValue.getJavaType()){
            case 0:
                // boolean
                if("true".equals(deviceValue.getWriteValue())){
                    valueData = new ValueData<Object>(true);
                }else if("false".equals(deviceValue.getWriteValue())){
                    valueData = new ValueData<Object>(false);
                }else{
                    throw new RuntimeException("传入值有误");
                }
                break;
            case 1:
                // byte
                valueData = new ValueData<Object>(deviceValue.getWriteValue().getBytes()[0]);
                break;
            case 2:
                // integer
                valueData = new ValueData<Object>(Integer.parseInt(deviceValue.getWriteValue()));
                break;
            case 3:
                // short
                valueData = new ValueData<Object>(Short.parseShort(deviceValue.getWriteValue()));
                break;
            case 4:
                // long
                valueData = new ValueData<Object>(Long.parseLong(deviceValue.getWriteValue()));
                break;
            case 5:
                // float
                valueData = new ValueData<Object>(Float.parseFloat(deviceValue.getWriteValue()));
                break;
            case 6:
                // double
                valueData = new ValueData<Object>(Double.parseDouble(deviceValue.getWriteValue()));
                break;
            case 7:
                // string
                valueData = new ValueData<Object>(deviceValue.getWriteValue());
                break;
            case 8:
                // localDate
                valueData = new ValueData<Object>(LocalDate.parse(deviceValue.getWriteValue()));
                break;
            case 9:
                // localTime
                valueData = new ValueData<Object>(LocalTime.parse(deviceValue.getWriteValue()));
                break;
            case 10:
                // localDateTime
                valueData = new ValueData<Object>(LocalDateTime.parse(deviceValue.getWriteValue()));
                break;
            default:
                throw new RuntimeException("java类型错误");
        }
        plcUtils.plcWrite(deviceValue,valueData);
        return Result.success();
    }

    /**
     * 批量写入数据
     */
    @ApiOperation("批量写入数据")
    @PostMapping("/writePLCValueList")
    public Result writePLCValueList(@RequestBody DeviceValue[] deviceValueList) {
        String codes = "";
        Integer successCount = 0;
        String deviceCode = null;
        for(DeviceValue deviceValue:deviceValueList){
            try{
                deviceCode = deviceValue.getDeviceCode();
                //只用于读取的字段不发
                if(deviceValue.getType()==0){
                    continue;
                }
                if(deviceValue.getWriteValue()==null||deviceValue.getWriteValue().equals("")){
                    continue;
                }else{
                    successCount++;
                }
                ValueData<Object> valueData ;
                switch (deviceValue.getJavaType()){
                    case 0:
                        // boolean
                        if("true".equals(deviceValue.getWriteValue())){
                            valueData = new ValueData<Object>(true);
                        }else if("false".equals(deviceValue.getWriteValue())){
                            valueData = new ValueData<Object>(false);
                        }else{
                            throw new RuntimeException("传入值有误");
                        }
                        break;
                    case 1:
                        // byte
                        valueData = new ValueData<Object>(deviceValue.getWriteValue().getBytes()[0]);
                        break;
                    case 2:
                        // integer
                        valueData = new ValueData<Object>(Integer.parseInt(deviceValue.getWriteValue()));
                        break;
                    case 3:
                        // short
                        valueData = new ValueData<Object>(Short.parseShort(deviceValue.getWriteValue()));
                        break;
                    case 4:
                        // long
                        valueData = new ValueData<Object>(Long.parseLong(deviceValue.getWriteValue()));
                        break;
                    case 5:
                        // float
                        valueData = new ValueData<Object>(Float.parseFloat(deviceValue.getWriteValue()));
                        break;
                    case 6:
                        // double
                        valueData = new ValueData<Object>(Double.parseDouble(deviceValue.getWriteValue()));
                        break;
                    case 7:
                        // string
                        valueData = new ValueData<Object>(deviceValue.getWriteValue());
                        break;
                    case 8:
                        // localDate
                        valueData = new ValueData<Object>(LocalDate.parse(deviceValue.getWriteValue()));
                        break;
                    case 9:
                        // localTime
                        valueData = new ValueData<Object>(LocalTime.parse(deviceValue.getWriteValue()));
                        break;
                    case 10:
                        // localDateTime
                        valueData = new ValueData<Object>(LocalDateTime.parse(deviceValue.getWriteValue()));
                        break;
                    default:
                        throw new RuntimeException("java类型错误");
                }
                plcUtils.plcWrite(deviceValue,valueData);
            }catch (Exception e){
                log.error("向设备: "+deviceValue.getDeviceCode()+"-"+deviceValue.getDeviceName()+"的点位: "+deviceValue.getName()+"写入值: "+deviceValue.getWriteValue()+"失败！");
                codes += "向点位: "+deviceValue.getCode()+"-"+deviceValue.getName()+"写入值: "+deviceValue.getWriteValue()+"失败;";
                e.printStackTrace();
            }
        }

        readPLCValueList(deviceCode);

        if(codes.equals("")){
            if(successCount==0){
                return Result.error("未输入写入值或写入值为空");
            }else{
                return Result.success();
            }
        }else{
            return Result.error(codes);
        }
    }

    /**
     * 从redis中获取写入/读取的值
     * 参数为deviceValue
     * key值为device-value:deviceValue.id+deviceValue.code
     * 返回值为deviceValue
     * @param deviceValue
     * @return
     */
    private  DeviceValue getValueFromRedis(DeviceValue deviceValue) {
        try{
            DeviceValue value = redisCache.getCacheObject("device_value:"+deviceValue.getId()+deviceValue.getCode());
            if(value!=null){
                deviceValue.setReadValue(value.getReadValue());
                deviceValue.setReadTime(value.getReadTime());
                deviceValue.setWriteValue(value.getWriteValue());
                deviceValue.setWriteTime(value.getWriteTime());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return deviceValue;
    }

    /**
     * 导出属性列表
     */
    @ApiOperation("导出属性列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:value:export')")
    @Log(title = "属性", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeviceValueCriteria criteria) {
        List<DeviceValueDto> list = deviceValueService.findList(criteria);
        ExcelUtil<DeviceValueDto> util = new ExcelUtil<DeviceValueDto>(DeviceValueDto.class);
        util.exportExcel(response, list, "属性数据");
    }

    /**
     * 获取属性详细信息
     */
    @ApiOperation("获取属性详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:value:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id) {
        return success(deviceValueService.findById(id));
    }

    /**
     * 新增属性
     */
    @ApiOperation("新增属性")
    @PreAuthorize("@ss.hasPermi('wcs-base:value:add')")
    @Log(title = "属性", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody DeviceValue deviceValue) {
        DeviceInfo deviceInfo = deviceInfoService.findById(deviceValue.getDeviceId());
        if(deviceValue==null){
            return Result.error("找不到设备");
        }else{
            deviceValue.setDeviceCode(deviceInfo.getCode());
            deviceValue.setDeviceName(deviceInfo.getName());
            deviceValue.setIp(deviceInfo.getIp());
            deviceValue.setPort(deviceInfo.getPort());
            deviceValue.setComType(deviceInfo.getComType());
            deviceValue.setS7Type(deviceInfo.getS7Type());
        }

        deviceValue.setCreateTime(DateUtil.getNowDateTimeString());
        deviceValue.setCreateUserId(getUserId());
        deviceValue.setCreateUserName(getUsername());
        deviceValueService.save(deviceValue);
        return toAjax(true);
    }

    /**
     * 修改属性
     */
    @ApiOperation("修改属性")
    @PreAuthorize("@ss.hasPermi('wcs-base:value:edit')")
    @Log(title = "属性", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody DeviceValue deviceValue) {

        DeviceInfo deviceInfo = deviceInfoService.findById(deviceValue.getDeviceId());
        deviceValue.setDeviceCode(deviceInfo.getCode());
        deviceValue.setDeviceName(deviceInfo.getName());
        deviceValue.setComType(deviceInfo.getComType());
        deviceValue.setS7Type(deviceInfo.getS7Type());
        deviceValue.setIp(deviceInfo.getIp());
        deviceValue.setPort(deviceInfo.getPort());

        deviceValue.setUpdateTime(DateUtil.getNowDateTimeString());
        deviceValue.setUpdateUserId(getUserId());
        deviceValue.setUpdateUserName(getUsername());
        return toAjax(deviceValueService.update(deviceValue));
    }

    /**
     * 删除属性
     */
    @ApiOperation("删除属性")
    @PreAuthorize("@ss.hasPermi('wcs-base:value:remove')")
    @Log(title = "属性", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        if(Integer.parseInt(sysConfigService.selectConfigByKey("soft_delete"))==0){
            for(Long id:ids){
                DeviceValue deviceValue = deviceValueService.findById(id);
                if(deviceValue!=null){
                    deviceValue.setIsDelete(1);
                    deviceValueService.update(deviceValue);
                }
            }
            return Result.success();
        }else{
            return toAjax(deviceValueService.deleteDeviceValueByIds(ids));
        }
    }
}
