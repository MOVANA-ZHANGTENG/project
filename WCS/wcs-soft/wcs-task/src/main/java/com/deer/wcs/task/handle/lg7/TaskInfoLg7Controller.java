package com.deer.wcs.task.handle.lg7;

import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.model.ProPositionContent;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.base.service.ProPositionContentService;
import com.deer.wcs.base.service.TaskPriorityService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.TaskInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * 任务Controller
 * 
 * @author deer
 * @date 2024-04-30
 */
@Api("任务")
@RestController
@Transactional
@RequestMapping("/wcs-task/lg7/TaskInfo")
public class TaskInfoLg7Controller extends BaseController
{
    @Autowired
    private TaskInfoService taskInfoService;
    @Autowired
    private TaskPriorityService taskPriorityService;

    @Autowired
    private ProPositionContentService proPositionContentService;

    @Autowired
    private PositionInfoService positionInfoService;

    /**
     * 新增任务
     */
    @ApiOperation("新增任务")
    @PreAuthorize("@ss.hasPermi('wcs-task:TaskInfo:add')")
    @Log(title = "任务", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@RequestBody TaskInfo taskInfo)
    {
        String fromCellCode = taskInfo.getFromCellCode();
        String toCellCode = taskInfo.getToCellCode();
        if(fromCellCode==null && toCellCode==null){

            return error("起点或者终点为空");
        }
        if(fromCellCode.trim().equals(toCellCode.trim())){
            return error("起点和终点不得相同");
        }
        ProPositionContent content = proPositionContentService.findBy("code",fromCellCode);
        if(content==null){
            return error("起点不存在");
        }
        ProPositionContent toContent = proPositionContentService.findBy("code",toCellCode);
        if(toContent==null){
            return error("终点不存在");
        }
//        if(content.getPalletCode()==null || content.getPalletCode().trim().equals("")){
//            return error("起点无托盘");
//        }
        if(content.getItemCode()==null || content.getItemCode().trim().equals("")){
            return error("起点未配置物料");
        }

        PositionInfo from =positionInfoService.findBy("code",fromCellCode);
        if(from==null){
            return error("起点不存在");
        }

        PositionInfo to =positionInfoService.findBy("code",toCellCode);
        if(to==null){
            return error("终点不存在");
        }

//        if(from.getInvenState()==0L){
//            return error("起点无托盘");
//        }

        if(from.getTaskState()>0.1){
            return error("起点有任务");
        }

        if(to.getTaskState()>0.1){
            return error("终点有任务");
        }

        if(toContent.getItemCode()==null || toContent.getItemCode().trim().equals("")){
            return error("终点未配置物料");
        }

        if(hasTask(from.getCode())){
            return error("起点有任务");
        }
        if(hasTask(to.getCode())){
            return error("终点有任务");
        }
        taskInfo.setWareCode("LG-7-YJ");
        taskInfo.setWareName("LG-7-YJ");
        taskInfo.setPalletCode(content.getPalletCode());
        taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
        taskInfoService.save(taskInfo);
        return toAjax(true);
    }

    private Boolean hasTask(String code){
        Condition condition = new Condition(TaskInfo.class);
        condition.createCriteria().andEqualTo("fromCellCode",code);
        List<TaskInfo> list = taskInfoService.findByCondition(condition);
        if(list.size()>0){
            return true;
        }else {
            return false;
        }
    }


}
