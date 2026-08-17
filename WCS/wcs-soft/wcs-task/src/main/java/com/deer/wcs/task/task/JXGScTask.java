package com.deer.wcs.task.task;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.TaskTypePriority;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.base.service.TaskPriorityService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.ISysConfigService;
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
@Component("JXGScTask")
public class JXGScTask {
    //定时检测出口是否存在托盘，存在托盘则生成AGV转运任务

    @Autowired
    private DeviceValueService deviceValueService;
    @Autowired
    private TaskPriorityService taskPriorityService;
    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private ISysConfigService configService;

    private static String wareCode = "JXG_DISPALY";

    //查询接驳位的状态
    private Object readSsxScPlc(String valueCode) {
        return deviceValueService.readValueByCode("JXG_SSX_SC", valueCode);
    }
    public Object readPlc( String valueCode){
        return deviceValueService.readValueByCode("sc",valueCode);
    };

    private static String mode = "";



    public void createScInTask(){

        //入库口是否有托盘
        short inHas = (short) readSsxScPlc("001");
        //入库口是否允许取货
        short canIn = (short) readSsxScPlc("005");
        Short Job_Ready=(Short) readPlc( "Job_Ready");
        //入库口托盘编码
        String palletCode = (String) readSsxScPlc("003");

        Short Comm_State_On=(Short) readPlc( "Comm_State_On");
        Short Comm_State_Off=(Short) readPlc( "Comm_State_Off");
        Short EQP_State=(Short) readPlc( "EQP_State");
        Short EQP_Operation_Mode=(Short) readPlc( "EQP_Operation_Mode");
        if(Comm_State_On==0 || Comm_State_Off==1){
            return;
        }
        //1-手动 2-自动
        if(EQP_Operation_Mode==1){
            return;
        }

        //10010
        if(EQP_State!=2){
            return;
        }

        if( Job_Ready==0){
            return;
        }
        if (inHas == 1 && canIn==1 && Job_Ready ==1) {
            //SC入库任务
            Condition condition = new Condition(TaskInfo.class);
            condition.createCriteria().andEqualTo("wareCode",wareCode)
                    .andEqualTo("type",3);
            List<TaskInfo> list = taskInfoService.findByCondition(condition);
            if(list.size()==0){
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setWareCode(wareCode);
                taskInfo.setWareName("金星高展示");
                taskInfo.setType(String.valueOf(3));
                taskInfo.setPalletCode(palletCode);
                taskInfo.setFromCellCode("0-0-1");
                taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
                TaskTypePriority taskTypePriority = taskPriorityService.findBy("code",taskInfo.getType().toString());
                if(taskTypePriority!=null){
                    taskInfo.setPriority(taskTypePriority.getPriority());
                }else{
                    taskInfo.setPriority(10);
                }
                if(mode.equals("out")){
                    return;
                }
                mode = "in";
                System.out.println("in");
                taskInfoService.save(taskInfo);
            }
        }
        mode = "";
    }

    @Autowired
    private CellInfoService cellInfoService;


    public void createScOutTask(){
        short outHas = (short) readSsxScPlc("002");
        //输送线是否允许放货
        Short canFangHuo = (Short) readSsxScPlc("004");
        if(outHas==1 ){
            return;
        }

        if(canFangHuo==0){
            return;
        }

        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria().andEqualTo("isDelete",0)
                .andEqualTo("invenState",1)
                .andEqualTo("taskState",0)
                .andEqualTo("disableState",0)
                .andEqualTo("wareCode",wareCode)
                .andEqualTo("ab","A");
        List<CellInfo> list = cellInfoService.findByCondition(condition);

        Integer maxMun = null;

        String outTaskMaxMun = configService.selectConfigByKey("outTaskMaxMun");
        try {
            maxMun = Integer.valueOf(outTaskMaxMun);
        }catch (Exception ex){

        }
        if(maxMun==null || maxMun>99) {
            maxMun = 20;
        }

        if(list.size()<maxMun){
            return;
        }
        Condition condition2 = new Condition(TaskInfo.class);
        condition2.createCriteria().andEqualTo("wareCode",wareCode)
                .andEqualTo("type",3);
        List<TaskInfo> tasks = taskInfoService.findByCondition(condition2);
        if(tasks.size()==0 && outHas==0){
            CellInfo cellInfo =cellInfoService.findFirstInCell(wareCode);
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setWareCode(wareCode);
            taskInfo.setWareName("金星高展示");
            taskInfo.setType(String.valueOf(3));
            taskInfo.setPalletCode("123");
            taskInfo.setFromCellCode(cellInfo.getCode());
            taskInfo.setToCellCode("0-0-2");
            taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
            TaskTypePriority taskTypePriority = taskPriorityService.findBy("code",taskInfo.getType().toString());
            if(taskTypePriority!=null){
                taskInfo.setPriority(taskTypePriority.getPriority());
            }else{
                taskInfo.setPriority(10);
            }
            if(mode.equals("in")){
                return;
            }
            mode = "out";
            System.out.println("out");
            taskInfoService.save(taskInfo);
        }
        mode = "";
    }

}
