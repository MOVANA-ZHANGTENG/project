package com.deer.wcs.task.handle;


import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;
import java.util.Random;

@Component("JobPositionHandle")
public class JobPositionHandle {

    @Autowired
    private PositionInfoService positionInfoService;


    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private TaskInfoService taskInfoService;

    public Boolean getCellCode(JobInfo jobInfo){

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String toCellCode =taskInfo.getToCellCode();
        //出库
        if(toCellCode!=null && toCellCode.equals("0-0-2")){
            return true;
        }
        //移库
        if(toCellCode!=null ){
            PositionInfo positionInfo = positionInfoService.findBy("code",toCellCode);
            if(positionInfo!=null && positionInfo.getInvenState()<1 && positionInfo.getTaskState()<1){
                return true;
            }
        }
        Condition condition = new Condition(PositionInfo.class);
        condition.createCriteria().andEqualTo("wareCode",taskInfo.getWareCode())
                .andEqualTo("invenState",0)
                .andEqualTo("taskState",0)
                .andEqualTo("disableState",0);

        //查出所有剩余库位
        List<PositionInfo> list = positionInfoService.findByCondition(condition);  // 1-1-1 （0）  1-1-2 （1） 1-1-3 （2）
        // 30   29  randomNum（0，29）  18
        //随机
        int randomNum = new Random().nextInt(list.size()-1);
        if(list.size()>0){
            taskInfo.setToCellCode(list.get(randomNum).getCode());   //1-7-1
            taskInfoService.update(taskInfo);
            return true;
        }else {
            jobInfoService.updateMemo(jobInfo,"无可用库位");
            return false;
        }
    }


    /**
     * 任务起点作为当前起点
     */
    public Boolean taskFromPosition(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String fromPositionCode = taskInfo.getFromCellCode();
        Condition condition = new Condition(PositionInfo.class);
        condition.createCriteria().andEqualTo("wareCode",taskInfo.getWareCode())
                .andEqualTo("code",fromPositionCode);
        List<PositionInfo> list = positionInfoService.findByCondition(condition);
        if(list.size()==0){
            jobInfoService.updateMemo(jobInfo,"任务起点位置无效："+fromPositionCode);
            return false;
        }
        jobInfo.setFromCellCode(taskInfo.getFromCellCode());
        jobInfoService.update(jobInfo);
        return true;
    }

    /**
     * 任务终点作为当前起点
     */
    public    Boolean taskToPosition(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String toPositionCode = taskInfo.getToCellCode();
        Condition condition = new Condition(PositionInfo.class);
        condition.createCriteria().andEqualTo("wareCode",taskInfo.getWareCode())
                .andEqualTo("code",toPositionCode);
        List<PositionInfo> list = positionInfoService.findByCondition(condition);
        if(list.size()==0){
            jobInfoService.updateMemo(jobInfo,"任务目标位置无效："+toPositionCode);
            return false;
        }
        jobInfo.setToCellCode(taskInfo.getToCellCode());
        jobInfoService.update(jobInfo);
        return true;
    }

    /**
     * 上一步目标货位作为当前起点
     */
    public Boolean lastToPosition(JobInfo jobInfo){
        Integer lastJobIndex = jobInfo.getJobIndex()-1;
        JobInfo lastJobInfo = jobInfoService.findByIndex(jobInfo.getTaskId(),lastJobIndex);
        if(lastJobInfo.getToCellCode()==null){
            jobInfoService.updateMemo(jobInfo,"上一步未获取到目标货位");
            return false;
        }
        jobInfo.setToCellCode(lastJobInfo.getToCellCode());
        jobInfoService.update(jobInfo);
        return true;
    }
    /**
     * 判断起点货位是否有任务
     */
    public Boolean fromPositionNoTask(JobInfo jobInfo){
        String cellCode = jobInfo.getFromCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到起点货位");
            return false;
        }
        PositionInfo  positionInfo = positionInfoService.findBy("code",cellCode);
        if(!taskState0(positionInfo,jobInfo)){
            jobInfoService.updateMemo(jobInfo,cellCode+"：起点货位被其他任务占用 "+positionInfo.getTaskState());
            return false;
        }
        positionInfo.setTaskState(jobInfo.getId());
        positionInfoService.update(positionInfo);
        return true;
    }

      /* * 判断终点站台是否有任务
     */
    public Boolean toPositionNoTask(JobInfo jobInfo){
        String cellCode = jobInfo.getToCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到目标货位");
            return false;
        }
        PositionInfo  positionInfo = positionInfoService.findBy("code",cellCode);
        if(!taskState0(positionInfo,jobInfo)){
            jobInfoService.updateMemo(jobInfo,cellCode+"：目标货位被其他任务占用 "+positionInfo.getTaskState());
            return false;
        }
        positionInfo.setTaskState(jobInfo.getId());
        positionInfoService.update(positionInfo);
        return true;
    }

    /* * 判断起点货位是否有任务
     */
    public Boolean toPositionInvenState1(JobInfo jobInfo){
        String cellCode = jobInfo.getToCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到目标货位");
            return false;
        }
        PositionInfo  positionInfo = positionInfoService.findBy("code",cellCode);
        positionInfo.setInvenState(1L);
     //   positionInfo.setInTime(DateUtil.getNowDateTimeString());
        positionInfoService.update(positionInfo);
        return true;
    }
    /* * 判断起点货位是否有任务
     */
    public Boolean fromPositionInvenState0(JobInfo jobInfo){
        String cellCode = jobInfo.getFromCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到起点货位");
            return false;
        }
        PositionInfo  positionInfo = positionInfoService.findBy("code",cellCode);
        positionInfo.setInvenState(0L);
        positionInfoService.update(positionInfo);
        return true;
    }

    /* * 目标库位设置任务状态为0
     */
    public Boolean toPositionTaskState0(JobInfo jobInfo){
        String cellCode = jobInfo.getToCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到目标货位");
            return false;
        }
        PositionInfo  positionInfo = positionInfoService.findBy("code",cellCode);
        positionInfo.setTaskState(0L);
        positionInfoService.update(positionInfo);
        return true;
    }

    /* * 起点库位设置任务状态为0
     */
    public Boolean fromPositionTaskState0(JobInfo jobInfo){
        String cellCode = jobInfo.getFromCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到起点货位");
            return false;
        }
        PositionInfo  positionInfo = positionInfoService.findBy("code",cellCode);
        positionInfo.setTaskState(0L);
        positionInfoService.update(positionInfo);
        return true;
    }



    private  Boolean taskState0(PositionInfo positionInfo,JobInfo jobInfo){
        Long taskState=positionInfo.getTaskState();
        if(taskState>0 && !taskState.equals(jobInfo.getTaskId()) && !taskState.equals(jobInfo.getTaskId())){
            JobInfo jobInfo1 = jobInfoService.findById(taskState);
            TaskInfo taskInfo = taskInfoService.findById(taskState);
            if(jobInfo1==null && taskInfo==null){
                return true;
            }
            return false;
        }else
        {
            return true;
        }
    }







    private static Integer getCellCol(String cellCode){
        return Integer.parseInt(cellCode.split("-")[0]);
    }
    private static Integer getCellRow(String cellCode){
        return Integer.parseInt(cellCode.split("-")[1]);
    }
    private static Integer getCellFloor(String cellCode){
        return Integer.parseInt(cellCode.split("-")[2]);
    }





}
