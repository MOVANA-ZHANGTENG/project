package com.deer.wcs.jxg.web;

import com.deer.wcs.base.model.DeviceInfo;
import com.deer.wcs.base.service.DeviceInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.rcs.model.RcsCarHandle;
import com.deer.wcs.rcs.model.RcsCarHandleCriteria;
import com.deer.wcs.rcs.model.RcsCarHandleDto;
import com.deer.wcs.rcs.service.RcsCarHandleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * RcsCarHandleController
 * 
 * @author deer
 * @date 2025-10-14
 */
@Api("RcsCarHandle")
@RestController
@RequestMapping("/wcs-jxg/zk2")
public class JxgZk2Controller extends BaseController
{

    @Autowired
    private DeviceValueService deviceValueService;

    @Autowired
    private DeviceInfoService deviceInfoService;


    // 设备编码
    private static final String DEVICE_CODE = "tsj";


    @GetMapping("/tsjZ")
    public Result tsjZ( )
    {
        DeviceInfo deviceInfo = deviceInfoService.findByCode(DEVICE_CODE);
        if(deviceInfo.getIsOnline()<1){
            return error("提升机不在线");
        }

        Object floor =  deviceValueService.readValueByCode(DEVICE_CODE, "current_floor");

        return success(floor);
    }






    /**
     * 这里定义一个接口  请求提升机的层数  返回到前端
     */
}
