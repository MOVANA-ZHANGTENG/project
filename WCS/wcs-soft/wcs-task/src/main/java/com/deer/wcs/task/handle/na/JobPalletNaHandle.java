package com.deer.wcs.task.handle.na;


import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.ProPositionContent;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.ProPositionContentService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.handle.hik.Hik_LG_NA_YANG_JobHandle;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.SaoMaSuccess;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.SaoMaSuccessService;
import com.deer.wcs.task.service.TaskInfoHistoryService;
import com.deer.wcs.task.service.TaskInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;

@Component("JobPalletNaHandle")
public class JobPalletNaHandle {

    private static final Logger log = LoggerFactory.getLogger(JobPalletNaHandle.class);

    @Autowired
    private ProPositionContentService proPositionContentService;


    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private TaskInfoService taskInfoService;

    public Boolean movePallet(JobInfo jobInfo){
        TaskInfo taskInfo  =taskInfoService.findById(jobInfo.getTaskId());
        String palletCode = taskInfo.getPalletCode();
        String to = jobInfo.getToCellCode();
        ProPositionContent content = proPositionContentService.findByCode(taskInfo.getWareCode(),to);
        content.setPalletCode(palletCode);
        proPositionContentService.update(content);
        return true;
    }

    @Autowired
    private SaoMaSuccessService saoMaSuccessService;

    @Autowired
    private TaskInfoHistoryService taskInfoHistoryService;

    //任务结束之后需要统计一下失败率
    public Boolean statisticsFail(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String remark1 = taskInfo.getRemark1();
        if (remark1 == null || "".equals(remark1)){
            //检查是否扫码过的
            log.info(jobInfo.getId()+"没有经过扫码,不需要统计扫码率");
            return true;
        }
        String classTime = null;
        String beginTime=null;
        String endTime = null;
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        if (hour >=8 && hour<20){
            classTime = "1";
            //白班
            beginTime = "08:00:00";
            endTime = "20:00:00";
        }else {
            classTime="2";
            //夜班
            beginTime = "20:00:00";
            endTime = "08:00:00";
        }
        SaoMaSuccess saoMaSuccess2 = saoMaSuccessService.findByTypeAndTime(classTime, 2, beginTime, endTime,hour);
        SaoMaSuccess saoMaSuccess1 = saoMaSuccessService.findByTypeAndTime(classTime, 1, beginTime, endTime,hour);
        Integer number1 = 0;
        Integer number2 = 0;
        if (saoMaSuccess1 !=null){
            number1 = saoMaSuccess1.getClassNumber();
        }
        if (saoMaSuccess2 != null){
            number2 = saoMaSuccess2.getClassNumber();
        }
        log.info(jobInfo.getId()+"扫码失败,开始记录PLC扫码失败的类型数量");
        //查询一下当前班次的总任务数
//        Integer taskNumber = taskInfoHistoryService.findByTimeAllTask(beginTime,endTime);
//        taskNumber = taskNumber+1;
        //扫码失败记录数量和失败率
        SaoMaSuccess saoMaSuccess3 = saoMaSuccessService.findByTypeAndTime(classTime,3, beginTime,endTime,hour);
        if (saoMaSuccess3 == null){
            saoMaSuccess3 = new SaoMaSuccess();
            saoMaSuccess3.setType(3);
            saoMaSuccess3.setClassNumber(0);
            saoMaSuccess3.setClassTime(classTime);
            Integer classNumber = saoMaSuccess3.getClassNumber();
            //classNumber=classNumber+1;
            double percentage =0;
            if ((number1+number2+classNumber) ==0){

            }else {
                percentage= ((double)classNumber / (number1+number2+classNumber)) * 100;
            }

            if (percentage >100){
                percentage = 100;
            }

            // 格式化保留两位小数
            String result = String.format("%.2f%%", percentage);
            //失败率和这个班次的总任务数对比
            saoMaSuccess3.setClassSuccess(result);
            saoMaSuccess3.setCreateTime(DateUtil.getNowDateTimeString());
            saoMaSuccess3.setTaskNumber(1);
            saoMaSuccessService.save(saoMaSuccess3);
            log.info(jobInfo.getId()+"当天第一次扫码失败,开始记录PLC扫码失败的类型数量："+1+",失败率："+result);
        }else {
            //有这个数据了
            Integer classNumber = saoMaSuccess3.getClassNumber();
            double percentage = 0;
            //classNumber=classNumber+1;
            if ((number1+number2+classNumber) ==0){

            }else {
                percentage= ((double)classNumber / (number1+number2+classNumber)) * 100;
            }

            if (percentage >100){
                percentage = 100;
            }
            // 格式化保留两位小数
            String result = String.format("%.2f%%", percentage);
            saoMaSuccess3.setClassNumber(classNumber);
            saoMaSuccess3.setClassSuccess(result);

            //计算失败的任务数,在之前扫码的时候就已经计算过了
            int taskNumber =saoMaSuccess3.getTaskNumber();
            //查询历史的总任务数量
            Integer byTimeAllTask = taskInfoHistoryService.findByTimeAllTask(beginTime, endTime,classTime,hour);
            //加上现在当前的任务数量
            Integer taskAll = byTimeAllTask+1;
            double percentage2 = ((double) taskNumber / taskAll) * 100;
            if (percentage2 >100){
                percentage2 = 100;
            }
            // 格式化保留两位小数
            String result2 = String.format("%.2f%%", percentage2);
            //saoMaSuccess3.setTaskNumber(taskNumber);
            saoMaSuccess3.setTaskSuccess(result2);
            saoMaSuccess3.setTaskNumberAll(taskAll+"");
            log.info(jobInfo.getId()+"任务结束最后一次扫码，开始记录扫码失败的任务数量："+taskNumber+",总任务数："+taskAll+",比例是："+result2);

            saoMaSuccessService.update(saoMaSuccess3);
            log.info(jobInfo.getId()+"扫码失败,开始记录PLC扫码失败的类型数量："+classNumber+",失败率："+result+",总任务数量"+taskAll+",classTime="+classTime+",beginTime="+beginTime+",hour="+hour);
        }
        return true;
    }
}
