package com.deer.wcs.base.web;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.TaskDefineService;
import com.deer.wcs.base.service.TaskTypeService;
import com.deer.wcs.base.service.WareInfoService;
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
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 任务类型Controller
 *
 * @author deer
 * @date 2024-08-06
 */
@Api("任务类型")
@RestController
@RequestMapping("/wcs-base/taskType")
public class TaskTypeController extends BaseController {
    @Autowired
    private TaskTypeService taskTypeService;
    @Autowired
    private ISysConfigService sysConfigService;
    @Autowired
    private WareInfoService wareInfoService;
    @Autowired
    private TaskDefineService taskDefineService;

    /**
     * 查询任务类型列表
     */
    @ApiOperation("查询任务类型列表")
//    @PreAuthorize("@ss.hasPermi('wcs-base:taskType:list')")
    @GetMapping("/list")
    public TableDataInfo list(TaskTypeCriteria Criteria) {
        startPage();
        List<TaskTypeDto> list = taskTypeService.findList(Criteria);
        return getDataTable(list);
    }

    /**
     * 导出任务类型列表
     */
    @ApiOperation("导出任务类型列表")
//    @PreAuthorize("@ss.hasPermi('wcs-base:taskType:export')")
    @Log(title = "任务类型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TaskTypeCriteria criteria) {
        List<TaskTypeDto> list = taskTypeService.findList(criteria);
        ExcelUtil<TaskTypeDto> util = new ExcelUtil<TaskTypeDto>(TaskTypeDto.class);
        util.exportExcel(response, list, "任务类型数据");
    }

    /**
     * 获取任务类型详细信息
     */
    @ApiOperation("获取任务类型详细信息")
//    @PreAuthorize("@ss.hasPermi('wcs-base:taskType:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id) {
        return success(taskTypeService.findById(id));
    }

    /**
     * 新增任务类型
     */
    @ApiOperation("新增任务类型")
//    @PreAuthorize("@ss.hasPermi('wcs-base:taskType:add')")
    @Log(title = "任务类型", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody TaskType taskType) {
        if(taskType.getWareCode()==null||"".equals(taskType.getWareCode())){
            return Result.error("未传入仓库编码！");
        }
        if(taskType.getCode()==null||"".equals(taskType.getCode())){
            return Result.error("未传入任务类型编码！");
        }
        if(taskType.getName()==null||"".equals(taskType.getName())){
            return Result.error("未传入任务类型名称！");
        }
        WareInfo wareInfo = wareInfoService.findBy("code",taskType.getWareCode());
        if(wareInfo==null||wareInfo.getIsDelete()==1){
            return Result.error("传入仓库编码有误！");
        }else {
            taskType.setWareName(wareInfo.getName());
        }

        Condition repeatCode = new Condition(TaskType.class);
        repeatCode.createCriteria().andEqualTo("code",taskType.getCode())
                .andEqualTo("wareCode",taskType.getWareCode())
                .andEqualTo("delFlag",0);
        List<TaskType> repeatCodes = taskTypeService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            return error("任务类型编码重复");
        }

        Condition repeatName = new Condition(TaskType.class);
        repeatName.createCriteria().andEqualTo("name",taskType.getName())
                .andEqualTo("wareCode",taskType.getWareCode())
                .andEqualTo("delFlag",0);
        List<TaskType> repeatNames = taskTypeService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            return error("任务类型名称重复");
        }

        taskType.setCreateTime(DateUtil.getNowDateTimeString());
        taskType.setCreateUserId(getUserId());
        taskType.setCreateUserName(getUsername());

        taskTypeService.save(taskType);
        return toAjax(true);
    }

    /**
     * 修改任务类型
     */
    @ApiOperation("修改任务类型")
//    @PreAuthorize("@ss.hasPermi('wcs-base:taskType:edit')")
    @Log(title = "任务类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody TaskType taskType) {
        if(taskType.getWareCode()==null||"".equals(taskType.getWareCode())){
            return Result.error("未传入仓库编码！");
        }
        if(taskType.getCode()==null||"".equals(taskType.getCode())){
            return Result.error("未传入任务类型编码！");
        }
        if(taskType.getName()==null||"".equals(taskType.getName())){
            return Result.error("未传入任务类型名称！");
        }
        WareInfo wareInfo = wareInfoService.findBy("code",taskType.getWareCode());
        if(wareInfo==null||wareInfo.getIsDelete()==1){
            return Result.error("传入仓库编码有误！");
        }else {
            taskType.setWareName(wareInfo.getName());
        }
        if(taskType.getWareCode()==null){
            return Result.error("未传入仓库编码！");
        }

        Condition repeatCode = new Condition(TaskType.class);
        repeatCode.createCriteria().andEqualTo("code",taskType.getCode())
                .andEqualTo("wareCode",taskType.getWareCode())
                .andEqualTo("delFlag",0);
        List<TaskType> repeatCodes = taskTypeService.findByCondition(repeatCode);
        if(repeatCodes.size()>0){
            if(repeatCodes.size()==1 && repeatCodes.get(0).getId().longValue() != taskType.getId().longValue()){
                return error("任务类型编码重复");
            }else if(repeatCodes.size()>1){
                return error("任务类型编码重复");
            }
        }

        Condition repeatName = new Condition(TaskType.class);
        repeatName.createCriteria().andEqualTo("name",taskType.getName())
                .andEqualTo("wareCode",taskType.getWareCode())
                .andEqualTo("delFlag",0);
        List<TaskType> repeatNames = taskTypeService.findByCondition(repeatName);
        if(repeatNames.size()>0){
            if(repeatNames.size()==1 && repeatNames.get(0).getId().longValue() != taskType.getId().longValue()){
                return error("任务类型名称重复");
            }else if(repeatNames.size()>1){
                return error("任务类型名称重复");
            }
        }

        taskType.setUpdateTime(DateUtil.getNowDateTimeString());
        taskType.setUpdateUserId(getUserId());
        taskType.setUpdateUserName(getUsername());

        TaskType oldType = taskTypeService.findById(taskType.getId());
        if(oldType==null){
         return error("原始数据不存在");
        }
        TaskDefineCriteria criteria = new TaskDefineCriteria();
        criteria.setType(oldType.getCode());
        List<TaskDefineDto> taskDefines = taskDefineService.findList(criteria);
        if(!taskDefines.isEmpty()){
            for(TaskDefineDto defineDto:taskDefines){
                defineDto.setType(taskType.getCode());
                taskDefineService.update(defineDto);
            }
        }

        return toAjax(taskTypeService.update(taskType));
    }

    /**
     * 删除任务类型
     */
    @ApiOperation("删除任务类型")
//    @PreAuthorize("@ss.hasPermi('wcs-base:taskType:remove')")
    @Log(title = "任务类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        if (Integer.parseInt(sysConfigService.selectConfigByKey("soft_delete")) == 0) {
            for (Long id : ids) {
                TaskType taskType = taskTypeService.findById(id);
                if (taskType != null) {
                    taskType.setDelFlag(1);
                    taskTypeService.update(taskType);
                }
            }
            return Result.success();
        } else {
            return toAjax(taskTypeService.deleteTaskTypeByIds(ids));
        }
    }
}
