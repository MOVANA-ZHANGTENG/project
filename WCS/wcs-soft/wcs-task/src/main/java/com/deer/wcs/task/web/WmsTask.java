package com.deer.wcs.task.web;

import com.alibaba.fastjson2.JSON;
import com.deer.wcs.base.model.WmsTaskInfo;
import com.deer.wcs.base.service.WmsTaskInfoService;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.model.HostWcsInterface;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.HostWcsInterfaceService;
import com.deer.wcs.task.service.TaskInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("用于接收WCS的任务下发接口")
@RestController
@RequestMapping("/wcs-api/WmsTask")
public class WmsTask {

    private static final Logger log = LoggerFactory.getLogger(WmsTask.class);

    @Autowired
    private WmsTaskInfoService wmsTaskInfoService;
    @Autowired
    private HostWcsInterfaceService hostWcsInterfaceService;
    @Autowired
    private TaskInfoService taskInfoService;

    /**
     *  接收WMS下发的任务请求
     */
    @ApiOperation("接收WMS下发的任务请求")
    @PostMapping("sendTask")
    public Result sendTask(@RequestBody WmsTaskInfo wmsTaskInfo){

        Result result = new Result();
        if(wmsTaskInfo.getWareCode()==null||"".equals(wmsTaskInfo.getWareCode())){
            result = Result.error("未传入仓库编码");
        }
        if(wmsTaskInfo.getType()==null||"".equals(wmsTaskInfo.getType())){
            result = Result.error("未传入任务类型");
        }
        if(wmsTaskInfo.getPalletCode()==null||"".equals(wmsTaskInfo.getPalletCode())){
            result = Result.error("未传入托盘编号");
        }
//        wmsTaskInfo.setState(1);
//        wmsTaskInfoService.save(wmsTaskInfo);
        result = Result.success("wms任务接受成功");

        String content = JSON.toJSONString(wmsTaskInfo);
        String recv = JSON.toJSONString(result);

        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode("/wcs-api/WmsTask/sendTask");
        hostWcsInterface.setInterfaceName("接收WMS下发的任务请求");
        hostWcsInterface.setSendFrom("WMS");
        hostWcsInterface.setSendTo("WCS");
        hostWcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        hostWcsInterface.setContent(content);
        hostWcsInterface.setRecv(recv);
        hostWcsInterface.setType(HostWcsInterface.TYPE_RECV);
        hostWcsInterfaceService.save(hostWcsInterface);

        TaskInfo taskInfo = new TaskInfo();
      //  taskInfo.setWmsTaskNo(wmsTaskInfo.getTaskId().toString());
        taskInfo.setWareCode(wmsTaskInfo.getWareCode());
        taskInfo.setWareName(wmsTaskInfo.getWareName());
        taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
        taskInfo.setPalletCode(wmsTaskInfo.getPalletCode());
        taskInfo.setPriority(10);
        taskInfo.setState(0);
        taskInfo.setFromCellCode(wmsTaskInfo.getFromCellCode());
        taskInfo.setType(wmsTaskInfo.getType());
        taskInfo.setToCellCode(wmsTaskInfo.getToCellCode());
        taskInfoService.save(taskInfo);

        return result;
    }

    /**
     *  WMS取消任务
     */
    @ApiOperation("WMS取消任务")
    @PostMapping("cancelTask")
    public Result cancelTask(@RequestBody WmsTaskInfo wmsTaskInfo){

        Result result = new Result();

        if(wmsTaskInfo.getTaskNo()==null||"".equals(wmsTaskInfo.getTaskNo())){
            result = Result.error("未传入任务编号");
        }

        // 取消wms任务逻辑

        result = Result.success("wms任务取消成功");

        String content = JSON.toJSONString(wmsTaskInfo);
        String recv = JSON.toJSONString(result);

        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode("/wcs-api/WmsTask/sendTask");
        hostWcsInterface.setInterfaceName("接收WMS下发的任务请求");
        hostWcsInterface.setSendFrom("WMS");
        hostWcsInterface.setSendTo("WCS");
        hostWcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        hostWcsInterface.setContent(content);
        hostWcsInterface.setRecv(recv);
        hostWcsInterface.setType(1);
        hostWcsInterfaceService.save(hostWcsInterface);

        return result;
    }

}
