package com.deer.wcs.xj.test;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.CellInfoCriteria;
import com.deer.wcs.base.model.CellInfoDto;
import com.deer.wcs.base.model.TaskTypePriority;
import com.deer.wcs.base.service.TaskPriorityService;
import com.deer.wcs.base.service.impl.CellInfoServiceImpl;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.rcs.model.RcsCarInfo;
import com.deer.wcs.rcs.service.RcsCarInfoService;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.TaskInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component("testCarRun")
public class AutoTestRun {
    private final Logger log = LoggerFactory.getLogger(AutoTestRun.class);

    private final TaskInfoService taskInfoService;
    private final RcsCarInfoService rcsCarInfoService;
    private final TaskPriorityService taskPriorityService;
    private final CellInfoServiceImpl cellInfoService;
    private final RedisCache redisCache;

    public AutoTestRun(TaskInfoService taskInfoService, RcsCarInfoService rcsCarInfoService, TaskPriorityService taskPriorityService, CellInfoServiceImpl cellInfoService, RedisCache redisCache) {
        this.taskInfoService = taskInfoService;
        this.rcsCarInfoService = rcsCarInfoService;
        this.taskPriorityService = taskPriorityService;
        this.cellInfoService = cellInfoService;
        this.redisCache = redisCache;
    }

    public void run() {
        List<TaskInfo> taskInfos = taskInfoService.findAll();
        if(taskInfos.size() > 1) {
            return;
        }
        Integer autoRunCarNo = redisCache.getCacheObject("AutoRunCarNo");
        String carCode = "5042";
        if(autoRunCarNo== null||autoRunCarNo ==0){
            autoRunCarNo = 1;
        }else if(autoRunCarNo == 1){
            autoRunCarNo++;
            carCode="5043";
        }else if(autoRunCarNo == 2){
            autoRunCarNo++;
            carCode="5044";
        }else if(autoRunCarNo == 3){
            autoRunCarNo++;
            carCode="5045";
        }else{
            autoRunCarNo = 0;
            carCode="5046";
        }
        RcsCarInfo carInfo = rcsCarInfoService.findBy("code",carCode);
        if(carInfo == null||carInfo.getDisableState()==1L) {
            return;
        }
        redisCache.setCacheObject("AutoRunCarNo",autoRunCarNo);

        if(carInfo.getIsCharge()==1||carInfo.getTaskState()!=0){
            // 充电/有任务的车不生成任务
            return;
        }

        // 执行测试任务
        TaskInfo taskInfo = new TaskInfo();

        // 随机分配库位（空闲/无任务）
        taskInfo.setWareCode("P1");
        taskInfo.setWareName("P1");
        taskInfo.setFromCellCode(carInfo.getFromCellCode());
        taskInfo.setType("car_move1");

        CellInfoCriteria criteria = new CellInfoCriteria();
        criteria.setWareCode("P1");
        criteria.setDisableState(0L);
        criteria.setTaskState(0L);
        criteria.setType(0);
        if(carInfo.getCurrentZ()==1){
            criteria.setZ(2);
        }else if(carInfo.getCurrentZ()==2){
            criteria.setZ(3);
        }else if(carInfo.getCurrentZ()==3){
            criteria.setZ(1);
        }

        List<CellInfoDto> cellInfos = cellInfoService.findList(criteria);
        if(cellInfos.size()==0){
            return;
        }
        Random random = new Random();
        while (true) {
            CellInfoDto toCell = cellInfos.get(random.nextInt(cellInfos.size()));
            if(toCell.getDisableState()==1){
                continue;
            }
            if(toCell.getTaskState()>0){
                continue;
            }
            if(!toCell.getCode().equals(carInfo.getFromCellCode())){
                String toCellCode = toCell.getCode();
                taskInfo.setToCellCode(toCellCode);
                break;
            }
        }

        taskInfo.setRcsCarId(carInfo.getId());
        taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
        TaskTypePriority taskTypePriority = taskPriorityService.findBy("code",taskInfo.getType().toString());
        if(taskTypePriority!=null){
            taskInfo.setPriority(taskTypePriority.getPriority());
        }else{
            taskInfo.setPriority(10);
        }
        if(taskInfo.getFromCellCode()!=null && taskInfo.getToCellCode()!=null && taskInfo.getFromCellCode().equals(taskInfo.getToCellCode())){
            log.error("任务起始库位和目标库位不得一致");
            return;
        }
        taskInfoService.save(taskInfo);

    }

}
