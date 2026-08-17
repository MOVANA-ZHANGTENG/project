package com.deer.wcs.task.handle;


import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.LineInfo;
import com.deer.wcs.base.model.PalletInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.LineInfoService;
import com.deer.wcs.base.service.PalletInfoService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.PalletRecordService;
import com.deer.wcs.task.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;
import java.util.Random;

@Component("JobCellHandle")
public class JobCellHandle {

    @Autowired
    private CellInfoService cellInfoService;


    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private TaskInfoService taskInfoService;
    @Autowired
    private PalletInfoService palletInfoService;
    @Autowired
    private PalletRecordService palletRecordService;

    public Boolean getCellCode(JobInfo jobInfo){

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String toCellCode =taskInfo.getToCellCode();
        //出库
        if(toCellCode!=null && toCellCode.equals("0-0-2")){
            return true;
        }
        //移库
        if(toCellCode!=null ){
            CellInfo cellInfo = cellInfoService.findBy("code",toCellCode);
            if(cellInfo!=null && cellInfo.getInvenState()<1 && cellInfo.getTaskState()<1){
                return true;
            }
        }
        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria().andEqualTo("wareCode",taskInfo.getWareCode())
                .andEqualTo("invenState",0)
                .andEqualTo("taskState",0)
                .andEqualTo("disableState",0);

        //查出所有剩余库位
        List<CellInfo> list = cellInfoService.findByCondition(condition);  // 1-1-1 （0）  1-1-2 （1） 1-1-3 （2）
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
    public Boolean taskFromCell(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        jobInfo.setFromCellCode(taskInfo.getFromCellCode());
        jobInfoService.update(jobInfo);
        return true;
    }
    /**
     * 任务起点作为当前起点
     */
    public Boolean taskToJobFrom(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        jobInfo.setFromCellCode(taskInfo.getToCellCode());
        jobInfoService.update(jobInfo);
        return true;
    }
    /**
     * 任务起点作为当前终点
     */
    public Boolean taskFromJobTo(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        jobInfo.setToCellCode(taskInfo.getFromCellCode());
        jobInfoService.update(jobInfo);
        return true;
    }

    /**
     * 任务终点作为当前起点
     */
    public Boolean taskToCell(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        jobInfo.setToCellCode(taskInfo.getToCellCode());
        jobInfoService.update(jobInfo);
        return true;
    }

    /**
     * 上一步目标货位作为当前起点
     */
    public Boolean lastToCell(JobInfo jobInfo){
        Integer lastJobIndex = jobInfo.getJobIndex()-1;
        JobInfo lastJobInfo = jobInfoService.findByIndex(jobInfo.getTaskId(),lastJobIndex);
        if(lastJobInfo.getToCellCode()==null){
            jobInfoService.updateMemo(jobInfo,"上一步未获取到目标货位");
            return false;
        }
        jobInfo.setFromCellCode(lastJobInfo.getToCellCode());
        jobInfoService.update(jobInfo);
        return true;
    }

    @Autowired
    private LineInfoService lineInfoService;

    /**
     * 获取出库接驳位置
     */
    public Boolean getOutJbCell(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        CellInfo  cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),taskInfo.getFromCellCode());
        if(cellInfo==null){
            jobInfoService.updateMemo(jobInfo,"找不到任务号"+jobInfo.getTaskId()+"起点货位"+jobInfo.getFromCellCode());
            return false;
        }
        LineInfo lineInfo = lineInfoService.findBy("code",cellInfo.getLineCode());
        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria()
                .andEqualTo("lineCode",lineInfo.getCode())
                .andEqualTo("type",2);
        List<CellInfo> list = cellInfoService.findByCondition(condition);
        if(list.size()==0){
            jobInfoService.updateMemo(jobInfo, lineInfo.getCode()+"没有出库接驳位置");
            return false;
        }
        jobInfo.setToCellCode(list.get(0).getCode());
        jobInfoService.update(jobInfo);
        return true;
    }

    /**
     * 获取入库接驳位置
     */
    public Boolean getInJbCell(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        CellInfo  cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),taskInfo.getToCellCode());
        if(cellInfo==null){
            jobInfoService.updateMemo(jobInfo,"获取入库接驳位置时无目标货位"+taskInfo.getToCellCode());
            return false;
        }
        LineInfo lineInfo = lineInfoService.findBy("code",cellInfo.getLineCode());
        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria()
                .andEqualTo("lineCode",lineInfo.getCode())
                .andEqualTo("type",1);
        List<CellInfo> list = cellInfoService.findByCondition(condition);
        if(list.size()==0){
            jobInfoService.updateMemo(jobInfo, lineInfo.getCode()+"没有入库接驳位置");
            return false;
        }
        jobInfo.setToCellCode(list.get(0).getCode());
        jobInfoService.update(jobInfo);
        return true;
    }

    /**
     * 判断起点货位是否有任务
     */
    public Boolean fromCellNoTask(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String cellCode = jobInfo.getFromCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到起点货位");
            return false;
        }
        CellInfo  cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),cellCode.trim());
        if(cellInfo==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job起点库位无效"+ taskInfo.getWareCode()+" "+cellCode);
            return false;
        }
        if(!taskState0(cellInfo,jobInfo)){
            jobInfoService.updateMemo(jobInfo,cellCode+"：起点货位被其他任务占用 "+cellInfo.getTaskState());
            return false;
        }
        cellInfo.setTaskState(jobInfo.getId());
        cellInfoService.update(cellInfo);
        jobInfoService.updateMemo(jobInfo,"起点库位无任务监测通过");
        return true;
    }

    /**
     * 判断起点货位是否有任务
     */
    public Boolean fromLineNoTask(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String cellCode = jobInfo.getFromCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"fromLineNoTask 【执行器逻辑错误】，当前job还未获取到起点货位");
            return false;
        }
        CellInfo  cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),cellCode.trim());
        if(cellInfo==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job起点库位无效");
            return false;
        }
        LineInfo lineInfo = lineInfoService.findBy("code",cellInfo.getLineCode());

        if(!taskState0(lineInfo,jobInfo)){
            jobInfoService.updateMemo(jobInfo,cellCode+"：起点巷道被其他任务占用 "+lineInfo.getTaskState());
            return false;
        }
        lineInfo.setTaskState(jobInfo.getId());
        lineInfoService.update(lineInfo);
        jobInfoService.updateMemo(jobInfo,"起点巷道无任务监测通过");
        return true;
    }

    /**
     * 判断起点货位是否有任务
     */
    public Boolean toLineNoTask(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String cellCode = jobInfo.getToCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"fromLineNoTask 【执行器逻辑错误】，当前job还未获取到起点货位");
            return false;
        }
        CellInfo  cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),cellCode.trim());
        if(cellInfo==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job目标库位无效");
            return false;
        }
        LineInfo lineInfo = lineInfoService.findBy("code",cellInfo.getLineCode());
        if(!taskState0(lineInfo,jobInfo)){
            jobInfoService.updateMemo(jobInfo,cellCode+"：目标巷道被其他任务占用 "+lineInfo.getTaskState());
            return false;
        }
        lineInfo.setTaskState(jobInfo.getId());
        lineInfoService.update(lineInfo);
        jobInfoService.updateMemo(jobInfo,"目标巷道无任务监测通过");
        return true;
    }

      /* * 判断起点货位是否有任务
     */
    public Boolean toCellNoTask(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String cellCode = jobInfo.getToCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到目标货位");
            return false;
        }
        CellInfo  cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),cellCode.trim());
        if(cellInfo==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job目标库位无效"+cellCode);
            return false;
        }

        if(!taskState0(cellInfo,jobInfo)){
            jobInfoService.updateMemo(jobInfo,cellCode+"：目标货位被其他任务占用 "+cellInfo.getTaskState());
            return false;
        }
        cellInfo.setTaskState(jobInfo.getId());
        cellInfoService.update(cellInfo);
        jobInfoService.updateMemo(jobInfo,"目标库位无任务监测通过");
        return true;
    }

    /* * 判断起点货位是否有任务
     */
    public Boolean toCellInvenState1(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String cellCode = jobInfo.getToCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到目标货位");
            return false;
        }
        CellInfo  cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),cellCode);
        cellInfo.setInvenState(1L);
        cellInfo.setInTime(DateUtil.getNowDateTimeString());
        cellInfoService.update(cellInfo);
        return true;
    }

    /* *
     */
    public Boolean fromCellInvenState0(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String cellCode = jobInfo.getFromCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到起点货位");
            return false;
        }
        CellInfo  cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),cellCode);
        cellInfo.setInvenState(0L);
        cellInfoService.update(cellInfo);
        return true;
    }

    /* * 目标库位设置任务状态为0
     */
    public Boolean toCellTaskState0(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String cellCode = jobInfo.getToCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到目标货位");
            return false;
        }
        CellInfo  cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),cellCode);
        if(cellInfo.getTaskState()<0.1){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，cellInfo 的任务状态==0，无需置0，请检查配置");
            return true;
        }

        if(!cellInfo.getTaskState().equals(jobInfo.getId()) && !cellInfo.getTaskState().equals(jobInfo.getTaskId())){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，cellInfo 的任务状态=="+cellInfo.getTaskState()+"，非当前任务，请检查配置");
            return false;
        }
        cellInfo.setTaskState(0L);
        cellInfoService.update(cellInfo);
        return true;
    }

    /* * 起点库位设置任务状态为0
     */
    public Boolean fromCellTaskState0(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String cellCode = jobInfo.getFromCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到起点货位");
            return false;
        }
        CellInfo  cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),cellCode);

        if(cellInfo.getTaskState()<0.1){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，cellInfo 的任务状态==0，无需置0，请检查配置");
            return true;
        }

        if(!cellInfo.getTaskState().equals(jobInfo.getId()) && !cellInfo.getTaskState().equals(jobInfo.getTaskId())){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，cellInfo 的任务状态=="+cellInfo.getTaskState()+"，非当前任务，请检查配置");
            return false;
        }

        cellInfo.setTaskState(0L);
        cellInfoService.update(cellInfo);
        return true;
    }
    /* * 目标库位设置任务状态为0
     */
    public Boolean toLineTaskState0(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String cellCode = jobInfo.getToCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到目标货位");
            return false;
        }
        CellInfo  cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),cellCode);
        LineInfo lineInfo = lineInfoService.findBy("code",cellInfo.getLineCode());
        if(cellInfo.getTaskState()<0.1){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，cellInfo 的任务状态==0，无需置0，请检查配置");
            return true;
        }

        if(!lineInfo.getTaskState().equals(jobInfo.getId()) && !lineInfo.getTaskState().equals(jobInfo.getTaskId())){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，lineInfo 的任务状态=="+lineInfo.getTaskState()+"，非当前任务，请检查配置");
            return false;
        }
        lineInfo.setTaskState(0L);
        lineInfoService.update(lineInfo);
        return true;
    }

    /* * 起点库位设置任务状态为0
     */
    public Boolean fromLineTaskState0(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String cellCode = jobInfo.getFromCellCode();
        if(cellCode==null){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，当前job还未获取到起点货位");
            return false;
        }
        CellInfo  cellInfo = cellInfoService.findByCode(taskInfo.getWareCode(),cellCode);
        LineInfo lineInfo = lineInfoService.findBy("code",cellInfo.getLineCode());
        if(lineInfo.getTaskState()<0.1){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，lineInfo 的任务状态==0，无需置0，请检查配置");
            return true;
        }

        if(!lineInfo.getTaskState().equals(jobInfo.getId()) && !lineInfo.getTaskState().equals(jobInfo.getTaskId())){
            jobInfoService.updateMemo(jobInfo,"【执行器逻辑错误】，lineInfo 的任务状态=="+lineInfo.getTaskState()+"，非当前任务，请检查配置");
            return false;
        }
        lineInfo.setTaskState(0L);
        lineInfoService.update(lineInfo);
        return true;
    }

    /**
     *  更新托盘位置
     *  托盘位置更新为任务终点
     *  -------------
     */
    public Boolean updatePalletLocation(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String palletCode = taskInfo.getPalletCode();
        if(palletCode==null||palletCode.isEmpty()){
            jobInfoService.updateMemo(jobInfo,"该托盘搬运任务未记录托盘号");
            return true;
        }
        PalletInfo palletInfo = palletInfoService.findByCode(taskInfo.getWareCode(),taskInfo.getPalletCode());
        if(palletInfo==null){
            jobInfoService.updateMemo(jobInfo,"托盘"+palletCode+"在wcs系统中未记录");
            return false;
        }
        palletInfo.setCellCode(taskInfo.getToCellCode());
        palletInfoService.update(palletInfo);
        palletRecordService.record(palletCode,taskInfo.getWareCode(),1,"托盘"+palletCode+"入库，存放库位"+taskInfo.getToCellCode());
        return true;
    }



    private  Boolean taskState0(CellInfo cellInfo,JobInfo jobInfo){
        Long taskState=cellInfo.getTaskState();
        if(taskState.equals(jobInfo.getId())){
            return true;
        }
        if(taskState.equals(jobInfo.getTaskId())){
            return true;
        }
        if(taskState<0.1){
            return true;
        }
        return false;
    }
    private  Boolean taskState0(LineInfo lineInfo,JobInfo jobInfo){
        Long taskState=lineInfo.getTaskState();
        if(taskState.equals(jobInfo.getId())){
            return true;
        }
        if(taskState.equals(jobInfo.getTaskId())){
            return true;
        }
        if(taskState<0.1){
            return true;
        }
        return false;
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
