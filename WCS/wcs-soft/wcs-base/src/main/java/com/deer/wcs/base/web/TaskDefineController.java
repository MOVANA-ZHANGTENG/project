package com.deer.wcs.base.web;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.TaskDefineService;
import com.deer.wcs.base.service.TaskHandleService;
import com.deer.wcs.base.service.TaskPriorityService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.domain.model.SysDictData;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.StringUtils;
import com.deer.wcs.common.utils.bean.BeanUtils;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.system.service.ISysDictTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务定义Controller
 *
 * @author deer
 * @date 2024-04-28
 */
@Api("任务定义")
@RestController
@RequestMapping("/wcs-base/TaskDefine")
public class TaskDefineController extends BaseController {
    @Autowired
    private TaskDefineService taskDefineService;
    @Autowired
    private TaskHandleService taskHandleService;
    @Autowired
    private ISysDictTypeService dictTypeService;
    @Autowired
    private TaskPriorityService taskPriorityService;

    /**
     * 查询任务优先级列表
     */
    @ApiOperation("查询任务优先级列表")
    @GetMapping("/taskPriority")
    public Result getTaskPriority(){
        List<SysDictData> data = dictTypeService.selectDictDataByType("task_type");
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<SysDictData>();
        }
        List<TaskTypePriority> list = taskDefineService.getTaskPriorityList();
        List<String> taskCodes = new ArrayList<>();
        for(TaskTypePriority taskTypePriority :list){
           taskCodes.add(taskTypePriority.getCode());
        }
        if(data.size()>0){
            for(SysDictData dictData:data){
                if(!taskCodes.contains(dictData.getDictValue())){
                    TaskTypePriority taskTypePriority = new TaskTypePriority();
                    taskTypePriority.setCode(dictData.getDictValue());
                    taskTypePriority.setName(dictData.getDictLabel());
                    taskPriorityService.save(taskTypePriority);
                }
            }
        }
        list = taskDefineService.getTaskPriorityList();
        return Result.success(list);
    }

    /**
     * 修改任务优先级
     */
    @ApiOperation("查询任务优先级列表")
    @RequestMapping("/setPriority")
    public Result setPriority(@RequestBody TaskTypePriority[] data){
        for (TaskTypePriority priority:data){
            taskPriorityService.update(priority);
        }
        return Result.success();
    }


    /**
     * 查询任务定义列表
     */
    @ApiOperation("查询任务定义列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskDefine:list')")
    @GetMapping("/list")
    public TableDataInfo list(TaskDefineCriteria Criteria) {
        startPage();
        List<TaskDefineDto> list = taskDefineService.findList(Criteria);
        for(TaskDefineDto taskDefineDto:list){
            Condition condition = new Condition(TaskHandle.class);
            condition.createCriteria().andEqualTo("taskDefineId",taskDefineDto.getId());
            List<TaskHandle> taskHandles = taskHandleService.findByCondition(condition);
            List<TaskHandle> cmdPreList = taskHandles.stream().filter(s->s.getType()==1).collect(Collectors.toList());
            List<TaskHandle> cmdList = taskHandles.stream().filter(s->s.getType()==2).collect(Collectors.toList());
            List<TaskHandle> successPreList = taskHandles.stream().filter(s->s.getType()==3).collect(Collectors.toList());
            List<TaskHandle> successList = taskHandles.stream().filter(s->s.getType()==4).collect(Collectors.toList());
            List<TaskHandle> deleteList = taskHandles.stream().filter(s->s.getType()==5).collect(Collectors.toList());
            taskDefineDto.setCmdPreList(cmdPreList);
            taskDefineDto.setCmdList(cmdList);
            taskDefineDto.setSuccessPreList(successPreList);
            taskDefineDto.setSuccessList(successList);
            taskDefineDto.setDeleteList(deleteList);
        }
        return getDataTable(list);
    }

    /**
     * 导出任务定义列表
     */
    @ApiOperation("导出任务定义列表")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskDefine:export')")
    @Log(title = "任务定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TaskDefineCriteria criteria) {
        List<TaskDefineDto> list = taskDefineService.findList(criteria);
        ExcelUtil<TaskDefineDto> util = new ExcelUtil<TaskDefineDto>(TaskDefineDto.class);
        util.exportExcel(response, list, "任务定义数据");
    }

    /**
     * 获取任务定义详细信息
     */
    @ApiOperation("获取任务定义详细信息")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskDefine:query')")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id) {
        return success(taskDefineService.findById(id));
    }

    /**
     * 获取任务定义详细信息（包含所有处理器列表）
     */
    @ApiOperation("获取任务定义详细信息（包含所有处理器列表）")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskDefine:query')")
    @GetMapping(value = "/detail/{id}")
    public Result getDetailWithHandles(@PathVariable("id") Long id) {
        TaskDefine taskDefine =  taskDefineService.findById(id);
        if (taskDefine == null) {
            return Result.error("任务定义不存在");
        }
        TaskDefineDto taskDefineDto = new TaskDefineDto();
        BeanUtils.copyProperties(taskDefine,taskDefineDto);

        
        // 查询关联的处理器列表
        Condition condition = new Condition(TaskHandle.class);
        condition.createCriteria().andEqualTo("taskDefineId", taskDefineDto.getId());
        List<TaskHandle> taskHandles = taskHandleService.findByCondition(condition);
        
        // 按类型分类处理器
        List<TaskHandle> cmdPreList = taskHandles.stream().filter(s -> s.getType() == 1).collect(Collectors.toList());
        List<TaskHandle> cmdList = taskHandles.stream().filter(s -> s.getType() == 2).collect(Collectors.toList());
        List<TaskHandle> successPreList = taskHandles.stream().filter(s -> s.getType() == 3).collect(Collectors.toList());
        List<TaskHandle> successList = taskHandles.stream().filter(s -> s.getType() == 4).collect(Collectors.toList());
        List<TaskHandle> deleteList = taskHandles.stream().filter(s -> s.getType() == 5).collect(Collectors.toList());

        // 设置到DTO中
        taskDefineDto.setCmdPreList(cmdPreList);
        taskDefineDto.setCmdList(cmdList);
        taskDefineDto.setSuccessPreList(successPreList);
        taskDefineDto.setSuccessList(successList);
        taskDefineDto.setDeleteList(deleteList);

        return success(taskDefineDto);
    }

    /**
     * 新增任务定义
     */
    @ApiOperation("新增任务定义")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskDefine:add')")
    @Log(title = "任务定义", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody TaskDefineDto taskDefineDto) {

        taskDefineService.save(taskDefineDto);
        saveTaskHandles(taskDefineDto);
        return toAjax(true);
    }

    /**
     * 更新任务定义的连线关系（更新lastId）- 兼容旧接口
     */
    @ApiOperation("更新任务定义的连线关系")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskDefine:edit')")
    @Log(title = "更新任务连线", businessType = BusinessType.UPDATE)
    @PutMapping("/updateLink")
    public Result updateLink(@RequestBody TaskDefine taskDefine) {
        if (taskDefine.getId() == null) {
            return Result.error("任务定义ID不能为空");
        }
        
        // 只更新 lastId 字段
        TaskDefine existingTask = taskDefineService.findById(taskDefine.getId());
        if (existingTask == null) {
            return Result.error("任务定义不存在");
        }
        
        existingTask.setLastId(taskDefine.getLastId());
        int result = taskDefineService.update(existingTask);
        
        return toAjax(result > 0);
    }

    /**
     * 更新任务定义连线（支持判断步骤）
     */
    @ApiOperation("更新任务定义连线（支持判断步骤）")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskDefine:edit')")
    @Log(title = "更新任务定义连线", businessType = BusinessType.UPDATE)
    @PutMapping("/link")
    public Result updateTaskDefineLink(@RequestBody UpdateLinkRequest request) {
        if (request.getFromStepId() == null || request.getToStepId() == null) {
            return Result.error("起始步骤和目标步骤不能为空");
        }
        
        try {
            int result = taskDefineService.updateTaskDefineLink(
                request.getFromStepId(), 
                request.getToStepId(), 
                request.getBranchType()
            );
            return toAjax(result > 0);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 验证判断步骤的连线完整性
     */
    @ApiOperation("验证判断步骤的连线完整性")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskDefine:query')")
    @GetMapping("/validate-links")
    public Result validateLinks(String wareCode, String taskType) {
        try {
            taskDefineService.validateJudgeStepLinks(wareCode, taskType);
            return Result.success("验证通过");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除任务定义连线
     */
    @ApiOperation("删除任务定义连线")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskDefine:edit')")
    @Log(title = "删除任务定义连线", businessType = BusinessType.UPDATE)
    @DeleteMapping("/link/{stepId}")
    public Result deleteTaskDefineLink(@PathVariable Long stepId) {
        if (stepId == null) {
            return Result.error("步骤ID不能为空");
        }
        
        try {
            int result = taskDefineService.deleteTaskDefineLink(stepId);
            return toAjax(result > 0);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新步骤位置
     */
    @ApiOperation("更新步骤位置")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskDefine:edit')")
    @PutMapping("/position")
    public Result updateStepPosition(@RequestBody UpdatePositionRequest request) {
        if (request.getStepId() == null) {
            return Result.error("步骤ID不能为空");
        }
        
        try {
            int result = taskDefineService.updateStepPosition(
                request.getStepId(),
                request.getPositionX(),
                request.getPositionY()
            );
            return toAjax(result > 0);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新连线请求DTO
     */
    public static class UpdateLinkRequest {
        /** 起始步骤ID */
        private Long fromStepId;
        
        /** 目标步骤ID */
        private Long toStepId;
        
        /** 分支类型（success/fail/null） */
        private String branchType;

        public Long getFromStepId() {
            return fromStepId;
        }

        public void setFromStepId(Long fromStepId) {
            this.fromStepId = fromStepId;
        }

        public Long getToStepId() {
            return toStepId;
        }

        public void setToStepId(Long toStepId) {
            this.toStepId = toStepId;
        }

        public String getBranchType() {
            return branchType;
        }

        public void setBranchType(String branchType) {
            this.branchType = branchType;
        }
    }

    /**
     * 更新位置请求DTO
     */
    public static class UpdatePositionRequest {
        /** 步骤ID */
        private Long stepId;
        
        /** X坐标 */
        private Double positionX;
        
        /** Y坐标 */
        private Double positionY;

        public Long getStepId() {
            return stepId;
        }

        public void setStepId(Long stepId) {
            this.stepId = stepId;
        }

        public Double getPositionX() {
            return positionX;
        }

        public void setPositionX(Double positionX) {
            this.positionX = positionX;
        }

        public Double getPositionY() {
            return positionY;
        }

        public void setPositionY(Double positionY) {
            this.positionY = positionY;
        }
    }

    /**
     * 修改任务定义
     */
    @ApiOperation("修改任务定义")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskDefine:edit')")
    @Log(title = "任务定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@RequestBody TaskDefineDto taskDefineDto) {
        for(TaskHandle taskHandle:taskDefineDto.getCmdPreList()){
            if(taskHandle.getHandleId()!=null){
                taskHandle.setId(taskHandle.getHandleId());
            }
        }
        for(TaskHandle taskHandle:taskDefineDto.getCmdList()){
            if(taskHandle.getHandleId()!=null){
                taskHandle.setId(taskHandle.getHandleId());
            }
        }
        for(TaskHandle taskHandle:taskDefineDto.getSuccessPreList()){
            if(taskHandle.getHandleId()!=null){
                taskHandle.setId(taskHandle.getHandleId());
            }
        }
        for(TaskHandle taskHandle:taskDefineDto.getSuccessList()){
            if(taskHandle.getHandleId()!=null){
                taskHandle.setId(taskHandle.getHandleId());
            }
        }

        for(TaskHandle taskHandle:taskDefineDto.getDeleteList()){
            if(taskHandle.getHandleId()!=null){
                taskHandle.setId(taskHandle.getHandleId());
            }
        }
        saveTaskHandles(taskDefineDto);
        return toAjax(taskDefineService.update(taskDefineDto));
    }

    /**
     * 删除任务定义
     */
    @ApiOperation("删除任务定义")
    @PreAuthorize("@ss.hasPermi('wcs-base:TaskDefine:remove')")
    @Log(title = "任务定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        for (Long id :ids){
            deleteTaskHandles(id);
        }
        return toAjax(taskDefineService.deleteTaskDefineByIds(ids));
    }

    /**
     * 保存任务处理器
     */
    private void saveTaskHandles(TaskDefineDto taskDefineDto){
        if(taskDefineDto.getId()!=null){
            //更新
            deleteTaskHandles(taskDefineDto.getId());
        }
        saveTaskHandlesByCategory(taskDefineDto,taskDefineDto.getCmdPreList(),1);
        saveTaskHandlesByCategory(taskDefineDto,taskDefineDto.getCmdList(),2);
        saveTaskHandlesByCategory(taskDefineDto,taskDefineDto.getSuccessPreList(),3);
        saveTaskHandlesByCategory(taskDefineDto,taskDefineDto.getSuccessList(),4);
        saveTaskHandlesByCategory(taskDefineDto,taskDefineDto.getDeleteList(),5);
    }
    /**
     * 删除原对应任务的处理器
     */
    private void deleteTaskHandles(Long taskDefineId){
        taskHandleService.deleteByTaskDefineId(taskDefineId);
    }
    /**
     * 分类别保存taskHandlers
     */
    private void saveTaskHandlesByCategory(TaskDefineDto taskDefineDto,List<TaskHandle> list,Integer type){
        if(list==null){
            return;
        }
        int index = 0;
        for(TaskHandle taskHandle:list){
            if(taskHandle.getHandleId()==null){
                taskHandle.setHandleId(taskHandle.getId());
            }
            taskHandle.setId(null);
            taskHandle.setTaskDefineId(taskDefineDto.getId());
            taskHandle.setType(type);
            taskHandle.setCmdIndex(index++);
            taskHandle.setCreateUserId(getUserId());
            taskHandle.setCreateUserName(getUsername());
            taskHandleService.save(taskHandle);
        }
    }
}
