package com.deer.wcs.task.task;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.TaskTypePriority;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.base.service.TaskPriorityService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @description:创建AGV任务定时器
 * @author:zfj
 * @date:2024/9/3 11:47
 */
@Component("JXGAGVTask")
public class JXGAGVTask {
    //定时检测出口是否存在托盘，存在托盘则生成AGV转运任务

    @Autowired
    private DeviceValueService deviceValueService;
    @Autowired
    private TaskPriorityService taskPriorityService;
    @Autowired
    private TaskInfoService taskInfoService;

    private Object readSsxPlc(String valueCode) {
        return deviceValueService.readValueByCode("JXG_SSX", valueCode);
    }


    public void createJXGAGVTask(){
        //输送线是否请求排除
        Integer outRequest = (Integer) readSsxPlc("outRequest");
        if (outRequest == 1) {
            //创建AGV转运任务
            Condition condition = new Condition(TaskInfo.class);
            condition.createCriteria().andEqualTo("wareCode","JXG_DISPALY")
                    .andEqualTo("type","AGV");
            List<TaskInfo> list = taskInfoService.findByCondition(condition);
            if(list.size()==0){
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setWareCode("JXG_DISPALY");
                taskInfo.setWareName("金星高展示");
                taskInfo.setType("AGV");
                taskInfo.setPalletCode("1234");
                taskInfo.setFromCellCode("C01");
                taskInfo.setToCellCode("R01");
                taskInfo.setCreateTime(DateUtil.getNowDateTimeString());

                TaskTypePriority taskTypePriority = taskPriorityService.findBy("code",taskInfo.getType().toString());
                if(taskTypePriority!=null){
                    taskInfo.setPriority(taskTypePriority.getPriority());
                }else{
                    taskInfo.setPriority(10);
                }
                taskInfoService.save(taskInfo);
            }
        }
    }


    @Autowired
    private CellInfoService cellInfoService;



}
