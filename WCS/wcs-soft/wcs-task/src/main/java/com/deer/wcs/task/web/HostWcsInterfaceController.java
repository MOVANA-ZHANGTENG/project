package com.deer.wcs.task.web;

import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.base.model.WareInfo;
import com.deer.wcs.base.model.WmsTaskInfo;
import com.deer.wcs.base.service.WareInfoService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.task.model.HostWcsInterface;
import com.deer.wcs.task.model.HostWcsInterfaceCriteria;
import com.deer.wcs.task.model.HostWcsInterfaceDto;
import com.deer.wcs.task.model.recvInterface.HostChangeTarget;
import com.deer.wcs.task.model.recvInterface.TaskCancel;
import com.deer.wcs.task.service.HostWcsInterfaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * 接口记录Controller
 *
 * @author deer
 * @date 2024-05-23
 */
@Api("接口记录")
@RestController
@RequestMapping("/wcs-base/interface")
public class HostWcsInterfaceController extends BaseController {
    @Autowired
    private HostWcsInterfaceService hostWcsInterfaceService;

    /**
     * 查询接口记录列表
     */
    @ApiOperation("查询接口记录列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:interface:list')")
    @GetMapping("/list")
    public TableDataInfo list(HostWcsInterfaceCriteria Criteria) {
        startPage();
        List<HostWcsInterfaceDto> list = hostWcsInterfaceService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出接口记录列表
     */
    @ApiOperation("导出接口记录列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:interface:export')")
    @Log(title = "接口记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HostWcsInterfaceCriteria criteria) {
        List<HostWcsInterfaceDto> list = hostWcsInterfaceService.findList(criteria);
        ExcelUtil<HostWcsInterfaceDto> util = new ExcelUtil<HostWcsInterfaceDto>(HostWcsInterfaceDto.class);
        util.exportExcel(response, list, "接口记录数据");
    }

    /**
     * 获取接口记录详细信息
     */
    @ApiOperation("获取接口记录详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:interface:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id) {
        return success(hostWcsInterfaceService.findById(id));
    }

    /**
     * 新增接口记录
     */
    @ApiOperation("新增接口记录")
    @PreAuthorize("@ss.hasPermi('wcs-base:interface:add')")
    @Log(title = "接口记录", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody HostWcsInterface hostWcsInterface) {

        hostWcsInterfaceService.save(hostWcsInterface);
        return toAjax(true);
    }

    /**
     * 修改接口记录
     */
    @ApiOperation("修改接口记录")
    @PreAuthorize("@ss.hasPermi('wcs-base:interface:edit')")
    @Log(title = "接口记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody HostWcsInterface hostWcsInterface) {


        return toAjax(hostWcsInterfaceService.update(hostWcsInterface));
    }

    /**
     * 删除接口记录
     */
    @ApiOperation("删除接口记录")
    @PreAuthorize("@ss.hasPermi('wcs-base:interface:remove')")
    @Log(title = "接口记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        return toAjax(hostWcsInterfaceService.deleteHostWcsInterfaceByIds(ids));
    }

    @Autowired
    private WareInfoService wareInfoService;


    /**
     * 001  搬运任务  host->wcs
     */
    @ApiOperation("搬运任务")
    @PostMapping("/moveTask")
    @Synchronized
    public Result moveTask(@RequestBody @Valid WmsTaskInfo wmsTaskInfo) {
        boolean recvOk = true;
        WareInfo wareInfo = wareInfoService.findBy("code", wmsTaskInfo.getWareCode());
        if(wareInfo==null){
           return error("找不到对应仓库");
        }
        if (wareInfo != null) {
            wmsTaskInfo.setWareName(wareInfo.getName());
        }
        try{
            hostWcsInterfaceService.moveTask(wmsTaskInfo);
            return Result.success();
        }catch (Exception e){
            recvOk = false;
            e.printStackTrace();
            return Result.error();
        }finally {
            saveRecvInterface("001","/wcs-base/interface/moveTask",
                    JSONObject.toJSONString(wmsTaskInfo),recvOk);
        }
    }

    /**
     * 002  作业取消  host->wcs
     */
    @ApiOperation("作业取消")
    @Log(title = "作业取消", businessType = BusinessType.INSERT)
    @PostMapping("/hostCancelTask")
    @Synchronized
    public Result hostCancelTask(@RequestBody @Valid TaskCancel hostTaskCancel) {
        boolean recvOk = true;
        try{
            hostWcsInterfaceService.hostCancelTask(hostTaskCancel);
            return Result.success();
        }catch (Exception e){
            recvOk = false;
            e.printStackTrace();
            return Result.error();
        }finally {
            saveRecvInterface("002","/wcs-base/interface/hostCancelTask",
                    JSONObject.toJSONString(hostTaskCancel),recvOk);
        }
    }

    /**
     * 006  目的地变更  host->wcs
     */
    @ApiOperation("目的地变更")
    @Log(title = "目的地变更", businessType = BusinessType.INSERT)
    @PostMapping("/changeTarget")
    @Synchronized
    public Result changeTarget(@RequestBody @Valid HostChangeTarget hostChangeTarget) {
        boolean recvOk = true;
        try{
            hostWcsInterfaceService.changeTarget(hostChangeTarget);
            return Result.success();
        }catch (Exception e){
            recvOk = false;
            e.printStackTrace();
            return Result.error();
        }finally {
            saveRecvInterface("006","/wcs-base/interface/changeTarget",
                    JSONObject.toJSONString(hostChangeTarget),recvOk);
        }
    }

    /**
     * 008  拣选/盘点任务完成通知  host->wcs
     */
    @ApiOperation("拣选/盘点任务完成通知")
    @Log(title = "拣选/盘点任务完成通知", businessType = BusinessType.INSERT)
    @PostMapping("/taskCompletedNotice")
    @Synchronized
    public Result taskCompletedNotice(@RequestBody WmsTaskInfo wmsTaskInfo) {
        boolean recvOk = true;
        try{

            return Result.success();
        }catch (Exception e){
            recvOk = false;
            e.printStackTrace();
            return Result.error();
        }finally {
            saveRecvInterface("008","/wcs-base/interface/taskCompletedNotice",
                    JSONObject.toJSONString(wmsTaskInfo),recvOk);
        }
    }

    /**
     * 009  库存同步  host->wcs
     */
    @ApiOperation("库存同步")
    @Log(title = "库存同步", businessType = BusinessType.INSERT)
    @PostMapping("/InventorySync")
    @Synchronized
    public Result InventorySync(@RequestBody WmsTaskInfo wmsTaskInfo) {
        boolean recvOk = true;
        try{

            return Result.success();
        }catch (Exception e){
            recvOk = false;
            e.printStackTrace();
            return Result.error();
        }finally {
            saveRecvInterface("009","/wcs-base/interface/InventorySync",
                    JSONObject.toJSONString(wmsTaskInfo),recvOk);
        }
    }

    private void saveRecvInterface(String code,String name,String content,boolean recvOk){
        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode(code);
        hostWcsInterface.setInterfaceName(name);
        hostWcsInterface.setSendFrom("Host");
        hostWcsInterface.setSendTo("Wcs");
        hostWcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        hostWcsInterface.setContent(content);
        if(recvOk){
            hostWcsInterface.setRecv(JSONObject.toJSONString(Result.success()));
        }else {
            hostWcsInterface.setRecv(JSONObject.toJSONString(Result.error()));
        }
        hostWcsInterface.setType(1);
        hostWcsInterfaceService.save(hostWcsInterface);
    }

}
