package com.deer.wcs.base.web;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.system.service.ISysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * 设备Controller
 *
 * @author deer
 * @date 2024-05-16
 */
@Api("设备")
@RestController
@RequestMapping("/wcs-base/DeviceInfo")
public class DeviceInfoController extends BaseController {
    @Autowired
    private DeviceInfoService deviceInfoService;
    @Autowired
    private DeviceValueService deviceValueService;
    @Autowired
    private ISysConfigService sysConfigService;

    /**
     * 查询设备列表
     */
    @ApiOperation("查询设备列表")
    @GetMapping("/list")
    public TableDataInfo list(DeviceInfoCriteria Criteria) {
        startPage();
        List<DeviceInfoDto> list = deviceInfoService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 根据编码查询设备详情
     */
    @ApiOperation("根据编码查询设备详情")
    @GetMapping("/getDeviceDetail")
    public Result getDeviceDetail(DeviceInfoCriteria Criteria) {
        Condition condition = new Condition(DeviceInfo.class);
        condition.createCriteria().andEqualTo("code", Criteria.getCode());
        List<DeviceInfo> deviceInfos = deviceInfoService.findByCondition(condition);
        if (deviceInfos.size() == 1) {
            return success(deviceInfos.get(0));
        } else {
            return Result.error();
        }
    }

    /**
     * 查询设备列表
     */
    @ApiOperation("查询设备列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:DeviceInfo:list')")
    @GetMapping("/getDevices")
    public Result getDevices() {
        Condition condition = new Condition(DeviceInfo.class);
        condition.createCriteria().andEqualTo("isDelete", 0);
        return Result.success(deviceInfoService.findByCondition(condition));
    }

    /**
     * 导出设备列表
     */
    @ApiOperation("导出设备列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:DeviceInfo:export')")
    @Log(title = "设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeviceInfoCriteria criteria) {
        List<DeviceInfoDto> list = deviceInfoService.findList(criteria);
        ExcelUtil<DeviceInfoDto> util = new ExcelUtil<DeviceInfoDto>(DeviceInfoDto.class);
        util.exportExcel(response, list, "设备数据");
    }

    /**
     * 获取设备详细信息
     */
    @ApiOperation("获取设备详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:DeviceInfo:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id) {
        return success(deviceInfoService.findById(id));
    }

    /**
     * 新增设备
     */
    @ApiOperation("新增设备")
    @PreAuthorize("@ss.hasPermi('wcs-base:DeviceInfo:add')")
    @Log(title = "设备", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody DeviceInfoDto deviceInfo) {
        Condition condition2 = new Condition(DeviceInfo.class);
        condition2.createCriteria().andEqualTo("code", deviceInfo.getCode());
        List<DeviceInfo> list2 = deviceInfoService.findByCondition(condition2);
        if (!list2.isEmpty()) {
            return error("编码重复");
        }

        if (deviceInfo.getId() == null) {
            deviceInfo.setCreateTime(DateUtil.getNowDateTimeString());
            deviceInfo.setCreateUserId(getUserId());
            deviceInfo.setCreateUserName(getUsername());
            deviceInfoService.save(deviceInfo);
        } else {
            Condition condition = new Condition(DeviceValue.class);
            condition.createCriteria().andEqualTo("deviceId", deviceInfo.getId());
            List<DeviceValue> list = deviceValueService.findByCondition(condition);

            deviceInfo.setId(null);
            deviceInfo.setCreateTime(DateUtil.getNowDateTimeString());
            deviceInfo.setUpdateTime(DateUtil.getNowDateTimeString());
            deviceInfo.setCreateUserId(getUserId());
            deviceInfo.setCreateUserName(getUsername());
            deviceInfoService.save(deviceInfo);


            // 获取地址偏移量

            int offset = Optional.ofNullable(deviceInfo.getOffset()).orElse(0);

            for (DeviceValue deviceValue : list) {
                String originalAddress = deviceValue.getAddress(); // 原始点位字符串
                // 计算偏移后的地址
                String newAddress = calcS7OffsetAddress(originalAddress, offset);
                deviceValue.setAddress(newAddress); // 设置偏移后地址

                deviceValue.setId(null);
                deviceValue.setDeviceId(deviceInfo.getId());
                deviceValue.setDeviceCode(deviceInfo.getCode());
                deviceValue.setDeviceName(deviceInfo.getName());
                deviceValue.setComType(deviceInfo.getComType());
                deviceValue.setS7Type(deviceInfo.getS7Type());
                deviceValue.setIp(deviceInfo.getIp());
                deviceValue.setPort(deviceInfo.getPort());
                deviceValueService.save(deviceValue);
            }
        }

        return toAjax(true);
    }

    /**
     * 西门子S7地址增加偏移量
     * 支持格式：DB101.2104.0(简写DBX)、DB19.DBX0.0、DB101.DBW0、DB5.DBD10
     *
     * @param srcAddr 原始地址
     * @param offset  偏移量
     * @return 偏移后地址
     */
    public static String calcS7OffsetAddress(String srcAddr, int offset) {
        if (srcAddr == null || srcAddr.isEmpty() || offset == 0) {
            return srcAddr;
        }

        // 兼容两种写法：DB101.2104.0 等价 DB101.DBX2104.0
        srcAddr = srcAddr.replaceAll("^(DB\\d+)\\.(\\d+)\\.(\\d)$", "$1.DBX$2.$3");

        // 匹配 DBxx.DBXn.m / DBxx.DBWn / DBxx.DBDn / DBxx.DBBn
        // 分组1:DB块  分组2:类型DBX/DBW/DBB/DBD  分组3:起始字节号  分组4:位号(DBX才有)
        String regex = "(DB\\d+)\\.(DBX|DBW|DBB|DBD)(\\d+)(\\.\\d)?";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(srcAddr);
        if (matcher.find()) {
            String dbNo = matcher.group(1);
            String dataType = matcher.group(2);
            int byteNum = Integer.parseInt(matcher.group(3));
            String bitSuffix = matcher.group(4) == null ? "" : matcher.group(4);

            int newByte = byteNum + offset;
            return String.format("%s.%s%d%s", dbNo, dataType, newByte, bitSuffix);
        }
        // 格式不匹配直接返回原值，防止数据异常
        return srcAddr;
    }

    /**
     * 修改设备
     */
    @ApiOperation("修改设备")
    @PreAuthorize("@ss.hasPermi('wcs-base:DeviceInfo:edit')")
    @Log(title = "设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody DeviceInfo deviceInfo) {
        //如果通信协议不是s7,那么s7Type =null
        if (deviceInfo.getComType() != 0) {
            deviceInfo.setS7Type(7);
        }
        deviceInfo.setUpdateTime(DateUtil.getNowDateTimeString());
        deviceInfo.setUpdateUserId(getUserId());
        deviceInfo.setUpdateUserName(getUsername());

        Condition condition = new Condition(DeviceValue.class);
        condition.createCriteria().andEqualTo("deviceId", deviceInfo.getId());
        List<DeviceValue> deviceValues = deviceValueService.findByCondition(condition);
        for (DeviceValue deviceValue : deviceValues) {
            deviceValue.setDeviceCode(deviceInfo.getCode());
            deviceValue.setDeviceName(deviceInfo.getName());
            deviceValue.setComType(deviceInfo.getComType());
            deviceValue.setS7Type(deviceInfo.getS7Type());
            deviceValue.setIp(deviceInfo.getIp());
            deviceValue.setPort(deviceInfo.getPort());
            deviceValueService.update(deviceValue);
        }

        return toAjax(deviceInfoService.update(deviceInfo));
    }

    /**
     * 删除设备
     */
    @ApiOperation("删除设备")
    @PreAuthorize("@ss.hasPermi('wcs-base:DeviceInfo:remove')")
    @Log(title = "设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        if (Integer.parseInt(sysConfigService.selectConfigByKey("soft_delete")) == 0) {
            for (Long id : ids) {
                DeviceInfo deviceInfo = deviceInfoService.findById(id);
                if (deviceInfo != null) {
                    if (deviceInfo.getIsDelete() == 1) {
                        deviceValueService.deleteByDeviceIds(ids);
                        deviceInfoService.deleteDeviceInfoByIds(ids);
                        return success("设备成功删除");
                    }
                    deviceInfo.setIsDelete(1);
                    deviceInfoService.update(deviceInfo);
                }
            }
            return Result.success();
        } else {
            return toAjax(deviceInfoService.deleteDeviceInfoByIds(ids));
        }
    }
}
