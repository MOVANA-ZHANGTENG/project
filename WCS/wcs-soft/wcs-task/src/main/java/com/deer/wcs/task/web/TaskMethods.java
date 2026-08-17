package com.deer.wcs.task.web;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.PathInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.PathInfoService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @description:
 * @author:zfj
 * @date:2024/6/4 10:19
 */
@Component("TaskMethods")
public class TaskMethods {

    private static final Logger log = LoggerFactory.getLogger(TaskMethods.class);

    @Autowired
    private JobInfoService jobInfoService;
    @Autowired
    private TaskInfoService taskInfoService;
    @Autowired
    private PathInfoService pathInfoService;
    @Autowired
    private CellInfoService cellInfoService;

    /**
     * 上一步完成
     * @param jobInfo
     * @return
     */
    public boolean lastFinish(JobInfo jobInfo){
        Integer lastJobIndex = jobInfo.getJobIndex()-1;
        JobInfo lastJobInfo = jobInfoService.findByIndex(jobInfo.getTaskId(),lastJobIndex);
        if(lastJobInfo==null){
            jobInfoService.updateMemo(jobInfo,"未获取到上一步");
            return false;
        }
        if (lastJobInfo.getState()<4){
            jobInfoService.updateMemo(jobInfo,"等待上一步完成");
            return false;
        }

        return true;
    }

    /**
     * 检查是否所有的path全部完成了
     * @param jobInfo
     * @return
     */
    public boolean checkAllPathFinish(JobInfo jobInfo){
        Condition condition = new Condition(PathInfo.class);
        condition.createCriteria().andEqualTo("jobId",jobInfo.getId());
        List<PathInfo> pathInfoList = pathInfoService.findByCondition(condition);
        for(PathInfo pathInfo:pathInfoList){
            if(pathInfo.getState()!=2){
                return false;
            }
        }
        return true;
    }

    /**
     * 执行器：任务完成
     * @param jobInfo
     */
    @Transactional
    public boolean finishTask(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        taskInfo.setState(2);
        taskInfo.setMemo("任务执行完毕");
        taskInfo.setFinishTime(DateUtil.getNowDateTimeString());
        taskInfoService.update(taskInfo);
        taskInfoService.recordHistory(taskInfo.getId());
        return true;
    }

    /**
     * 终点货位是否禁用
     */
    public boolean isToCellDisabled(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getToCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"终点货位"+pathInfo.getToCellCode());
            return false;
        }
        if(cellInfo.getDisableState()==0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 终点货位无货
     */
    public boolean isToCellInvenState0(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getToCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"终点货位"+pathInfo.getToCellCode());
            return false;
        }
        if(cellInfo.getInvenState()==0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 终点货位有货
     */
    public boolean isToCellInvenState1(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getToCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"终点货位"+pathInfo.getToCellCode());
            return false;
        }
        if(cellInfo.getInvenState()==1){
            return true;
        }else {
            return false;
        }
    }

    /**
     *  终点货位没有任务
     */
    public boolean isToCellTask0(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getToCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"终点货位"+pathInfo.getToCellCode());
            return false;
        }
        if(cellInfo.getTaskState()==0){
            return true;
        }else {
            return false;
        }
    }

    /**
     *  终点货位有任务
     */
    public boolean isToCellTask(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getToCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"终点货位"+pathInfo.getToCellCode());
            return false;
        }
        if(cellInfo.getTaskState()!=0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 起始货位是否禁用
     */
    public boolean isFromCellDisabled(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getFromCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"起始货位"+pathInfo.getFromCellCode());
            return false;
        }
        if(cellInfo.getDisableState()==0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 起始货位无货
     */
    public boolean isFromCellInvenState0(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getFromCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"起始货位"+pathInfo.getFromCellCode());
            return false;
        }
        if(cellInfo.getInvenState()==0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 起始货位有货
     */
    public boolean isFromCellInvenState1(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getFromCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"起始货位"+pathInfo.getFromCellCode());
            return false;
        }
        if(cellInfo.getInvenState()==1){
            return true;
        }else {
            return false;
        }
    }

    /**
     *  起始货位没有任务
     */
    public boolean isFromCellTask0(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getFromCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"起始货位"+pathInfo.getFromCellCode());
            return false;
        }
        if(cellInfo.getTaskState()==0){
            return true;
        }else {
            return false;
        }
    }

    /**
     *  起始货位有任务
     */
    public boolean isFromCellTask(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getFromCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"起始货位"+pathInfo.getFromCellCode());
            return false;
        }
        if(cellInfo.getTaskState()!=0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 将目标位置库存状态置为1
     */
    public boolean setTargetCellInvenState1(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getToCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"终点货位"+pathInfo.getFromCellCode());
            return false;
        }
        cellInfo.setInvenState(1L);
        cellInfoService.update(cellInfo);
        return true;
    }

    /**
     * 将目标位置任务状态清空
     */
    public boolean setTargetCellTaskState0(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getToCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"终点货位"+pathInfo.getFromCellCode());
            return false;
        }
        cellInfo.setTaskState(0l);
        cellInfoService.update(cellInfo);
        return true;
    }

    /**
     * 将起始位置库存状态置0
     */
    public boolean setFromCellInvenState0(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getFromCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"终点货位"+pathInfo.getFromCellCode());
            return false;
        }
        cellInfo.setInvenState(0L);
        cellInfoService.update(cellInfo);
        return true;
    }

    /**
     * 将起始位置任务状态清空
     */
    public boolean setFromCellTaskState0(PathInfo pathInfo){
        CellInfo cellInfo = cellInfoService.findBy("cellCode",pathInfo.getFromCellCode());
        if(cellInfo==null){
            log.error("找不到任务号"+pathInfo.getTaskId()+"终点货位"+pathInfo.getFromCellCode());
            return false;
        }
        cellInfo.setTaskState(0l);
        cellInfoService.update(cellInfo);
        return true;
    }


    /**
     * 向速锐四项车下发任务
     */
    public boolean sendTaskToSSC(PathInfo pathInfo){
        return true;
    }

    /**
     * 检测四项车·任务状态执行
     */
    public boolean checkSSCTaskState(PathInfo pathInfo){
        return true;
    }


}
