package com.deer.wcs.task.handle.na;

import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.model.ProPositionContent;
import com.deer.wcs.base.model.TaskTypePriority;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.base.service.ProPositionContentService;
import com.deer.wcs.base.service.TaskPriorityService;
import com.deer.wcs.common.annotation.Log;
import com.deer.wcs.common.core.controller.BaseController;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.page.TableDataInfo;
import com.deer.wcs.common.enums.BusinessType;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.poi.ExcelUtil;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.model.TaskInfoCriteria;
import com.deer.wcs.task.model.TaskInfoDto;
import com.deer.wcs.task.service.TaskInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Condition;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
@RequestMapping("/wcs-task/na/TaskInfo")
public class TaskInfoNAController extends BaseController
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
        //当是指定站台的时候 不需要下料点，直接查到所有的指定的下料点上，根据状态查找选择下料点
        String fromCellCode = taskInfo.getFromCellCode();
        String toCellCode = taskInfo.getToCellCode();
        if (fromCellCode != null && !fromCellCode.equals("")){
            if (fromCellCode.equals("ADC001") || fromCellCode.equals("ADC002") || fromCellCode.equals("ADC003") || fromCellCode.equals("ADC004")
            || fromCellCode.equals("ADC005") || fromCellCode.equals("ADC006") || fromCellCode.equals("SBR001") || fromCellCode.equals("SBR002")
            ){
                //去找到室外的站台余料站台，固定是STA001-002  STA015
                List<String> codes = new ArrayList<>();
                codes.add("STA001");
                codes.add("STA002");
                codes.add("STA015");
                ProPositionContent byKong = proPositionContentService.findByKong(codes);
                if (byKong == null){
                    //没有找到空闲的回流站台
                    return error("没有找到空闲的回流站台");
                }
                toCellCode = byKong.getCode();
                taskInfo.setToCellCode(toCellCode);
            }
        }
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

        PositionInfo from =positionInfoService.findBy("code",fromCellCode);
        if(from==null){
            return error("起点不存在");
        }

        PositionInfo to =positionInfoService.findBy("code",toCellCode);
        if(to==null){
            return error("终点不存在");
        }

        if(from.getInvenState()==0L){
            return error("起点无托盘");
        }

        if(from.getTaskState()>0.1){
            return error("起点有任务");
        }

        if(to.getTaskState()>0.1){
            return error("终点有任务");
        }

        if(hasTask(from.getCode())){
            return error("起点有任务");
        }
        if(hasTask(to.getCode())){
            return error("终点有任务");
        }

        //代表是点对点任务，不扫码
        taskInfo.setRemark2("1");
        if(taskInfo.getWareCode().equals("LG-NA")){
            taskInfo.setType("8");
        }
        if(taskInfo.getWareCode().equals("LG-NA-YANG")){
            taskInfo.setType("9");
        }
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
