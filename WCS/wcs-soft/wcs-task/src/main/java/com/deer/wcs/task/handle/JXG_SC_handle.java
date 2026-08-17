package com.deer.wcs.task.handle;


import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.system.service.BillRecordService;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.model.ThreeData;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import com.deer.wcs.task.task.WcsTask;
import com.deer.wcs.task.websocket.WebSocketUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component("JXG_SC_handle")
public class JXG_SC_handle {

    @Autowired
    private DeviceValueService deviceValueService;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private BillRecordService billRecordService;


    @Value("${isTest}")
    private Boolean isTest;


    public Object readPlc( String valueCode){
        return deviceValueService.readValueByCode("sc",valueCode);
    };

    private Object write( String valueCode,Object object){
        return deviceValueService.writeValueByCode("sc",valueCode,object);
    };



    /**
     * 是否可以下任务
     */
    public    Boolean canTask(JobInfo jobInfo){
        if(isTest){
            return true;
        }
        String msg;
        Short Comm_State_On=(Short) readPlc( "Comm_State_On");
        Short Comm_State_Off=(Short) readPlc( "Comm_State_Off");
        Short Job_Ready=(Short) readPlc( "Job_Ready");
        Short EQP_State=(Short) readPlc( "EQP_State");
        Short EQP_Operation_Mode=(Short) readPlc( "EQP_Operation_Mode");
        if(Comm_State_On==0 || Comm_State_Off==1){
            msg="【PLC】当前心跳状态异常";
            jobInfoService.updateMemo(jobInfo,msg);
            return false;
        }
        //1-手动 2-自动
        if(EQP_Operation_Mode==1){
            msg= "【PLC】当前堆垛机时是手动状态 EQP_Operation_Mode==1";
            jobInfoService.updateMemo(jobInfo,msg);
            return false;
        }

        //10010
        if(EQP_State!=2){
            msg= "【PLC】EQP_State不为2";
            jobInfoService.updateMemo(jobInfo,msg);
            return false;
        }

        if( Job_Ready==0){
            msg= "【PLC】SC正忙，Job_Ready未准备好";
            jobInfoService.updateMemo(jobInfo,msg);
            return false;
        }

        billRecordService.createTaskRecord(jobInfo.getTaskId(),"第"+jobInfo.getJobIndex()+"步：堆垛机执行条件检测通过");
        return true;
    }


    private void sendMsgToWebSocket(ThreeData data){
        WebSocketUsers.sendMessageToUsersByText(JSONObject.toJSONString(data));
    }


    /**
     * 下任务
     * @param jobInfo
     * @return
     */
    public   Boolean  sendTask(JobInfo jobInfo){
        if(isTest){
            return true;
        }
        if(!canTask(jobInfo)){
            throw new ServiceException();
        }
        Short fromX=getCellX(jobInfo.getFromCellCode());
        Short fromY=getCellY(jobInfo.getFromCellCode());
        Short fromZ=getCellZ(jobInfo.getFromCellCode());
        Short toX=getCellX(jobInfo.getToCellCode());
        Short toY=getCellY(jobInfo.getToCellCode());
        Short toZ=getCellZ(jobInfo.getToCellCode());
        String palletCode=jobInfo.getPalletCode();
        if(palletCode==null || palletCode.trim().equals("")){
            palletCode="noRead";
        }
        //下发任务
        write("HOST_FROM_X",fromX);
        write("HOST_FROM_Y",fromY);
        write("HOST_FROM_Z",fromZ);
        write("HOST_TO_X",toX);
        write("HOST_TO_Y",toY);
        write("HOST_TO_Z",toZ);
        write("Host_Pallet_ID",palletCode);
        try {
            Thread.sleep(1000);
        }catch (Exception ex){

        }
        write("Job_Order",(short)1);
        Job_Order_false(  jobInfo);
        billRecordService.createTaskRecord(jobInfo.getTaskId(),"第"+jobInfo.getJobIndex()+"步：发送任务参数成功");


        ThreeData data = new ThreeData();
        data.setType("scTask");
        data.setFromNode(jobInfo.getFromCellCode());
        data.setToNode(jobInfo.getToCellCode());
        sendMsgToWebSocket(data);
        return true;
    }

    public void getScWz(){
        Short TASK_STATE =(Short)readPlc("TASK_STATE");
    }


    /**
     * 复位job_order
     */
    private   void Job_Order_false(JobInfo jobInfo){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<String> task = () -> {
            Boolean aaa =true;
            while (aaa){
                Short TASK_STATE =(Short)readPlc("TASK_STATE");
                if(TASK_STATE==1){
                    write("Job_Order",(short)0);
                    jobInfoService.updateMemo(jobInfo,"Job_Order："+0);
                }
                Short Job_Order =(Short)readPlc("Job_Order");
                if(Job_Order==0){

                    aaa=false;
                }
            }
            return "Task completed!";
        };
        Future<String> future = executor.submit(task);
        //System.out.println(future.get()); // 获取并打印结果
        executor.shutdown();
    }

    /**
     * 检测任务是否完成
     * @param jobInfo
     * @return
     */
    public   Boolean canFinish(JobInfo jobInfo){
        if(isTest){
            return true;
        }
        isTrouble(jobInfo);
        Short TASK_STATE =(Short)readPlc("TASK_STATE");
        String msg=null;
        //1-开始执行 2-到达取料位置 4-取料完成 5-到达终点 6放料完成
        switch (TASK_STATE){
            case 1: msg="开始执行";break;
            case 2: msg="到达取料位置";break;
            case 4: msg="取料完成";break;
            case 5: msg="到达终点";break;
            case 6: msg="6放料完成";break;
        }
        if(msg!=null){
            jobInfoService.updateMemo(jobInfo,msg);
        }
        Short Job_Complete =(Short)readPlc("Job_Complete");
        if(Job_Complete!=1){
            return false;
        }
        jobInfoService.updateMemo(jobInfo,"堆垛机收到完成信号");
        return true;
    }

    @Autowired
    private TaskInfoService taskInfoService;

    private Boolean isTrouble (JobInfo jobInfo){
        if(isTest){
            return true;
        }
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        // 2-等待 4-异常
        Short EQP_State =(Short)readPlc("EQP_State");
        Short EQP_Trouble_Code =(Short)readPlc("EQP_Trouble_Code");
        Short Job_Order_Initialize =(Short)readPlc("Job_Order_Initialize");
        //重复入库  重新找目标点
        if(EQP_State ==4 && EQP_Trouble_Code == 33 && Job_Order_Initialize==1){
            write("Job_Order",(short)0);

            jobInfoService.updateMemo(jobInfo,"检测到重复入库信号");
            String oldToCellCode = jobInfo.getToCellCode();
            CellInfo oldToCell = cellInfoService.findBy("code",oldToCellCode);
            oldToCell.setInvenState(1L);
            oldToCell.setTaskState(0L);
            cellInfoService.update(oldToCell);


            String cellCode = getNewCellCode(jobInfo);
            jobInfo.setToCellCode(cellCode);
            taskInfo.setToCellCode(cellCode);
            taskInfoService.update(taskInfo);

            jobInfoService.updateMemo(jobInfo,"更新了新的入库库位："+cellCode);

            CellInfo newToCell = cellInfoService.findBy("code",cellCode);
            newToCell.setTaskState(jobInfo.getId());
            cellInfoService.update(newToCell);


            write("Trouble_Reset",(short)1);

            jobInfoService.updateMemo(jobInfo,"Trouble_Reset："+1);
            write("Job_Order_Initialize_Confirm",(short)1);
            jobInfoService.updateMemo(jobInfo,"Job_Order_Initialize_Confirm："+1);

            Trouble_Reset_false(jobInfo);


        }
        //空出库  重新找起点
        if(EQP_State ==4 && EQP_Trouble_Code == 34 && Job_Order_Initialize==1){
            write("Job_Order",(short)0);
            jobInfoService.updateMemo(jobInfo,"检测到空出库：");
            String oldFromCellCode = jobInfo.getFromCellCode();
            CellInfo oldFromCell = cellInfoService.findBy("code",oldFromCellCode);
            oldFromCell.setInvenState(0L);
            oldFromCell.setTaskState(0L);
            cellInfoService.update(oldFromCell);

            String cellCode = getNewOutCellCode(jobInfo);
            CellInfo newFromCell = cellInfoService.findBy("code",cellCode);
            newFromCell.setTaskState(jobInfo.getId());
            cellInfoService.update(newFromCell);
            jobInfo.setFromCellCode(cellCode);
            taskInfo.setFromCellCode(cellCode);
            jobInfoService.update(jobInfo);
            taskInfoService.update(taskInfo);

            write("Trouble_Reset",(short)1);

            jobInfoService.updateMemo(jobInfo,"Trouble_Reset："+1);
            write("Job_Order_Initialize_Confirm",(short)1);
            jobInfoService.updateMemo(jobInfo,"Job_Order_Initialize_Confirm："+1);

            Trouble_Reset_false(jobInfo);

        }
        return true;
    }

    private void reSendTask(JobInfo jobInfo){
        jobInfo = jobInfoService.findById(jobInfo.getId());
        Short fromX=getCellX(jobInfo.getFromCellCode());
        Short fromY=getCellY(jobInfo.getFromCellCode());
        Short fromZ=getCellZ(jobInfo.getFromCellCode());
        Short toX=getCellX(jobInfo.getToCellCode());
        Short toY=getCellY(jobInfo.getToCellCode());
        Short toZ=getCellZ(jobInfo.getToCellCode());
        String palletCode=jobInfo.getPalletCode();
        write("HOST_FROM_X",fromX);
        write("HOST_FROM_Y",fromY);
        write("HOST_FROM_Z",fromZ);
        write("HOST_TO_X",toX);
        write("HOST_TO_Y",toY);
        write("HOST_TO_Z",toZ);
        write("Host_Pallet_ID",palletCode);

        jobInfoService.updateMemo(jobInfo,"发送任务参数");

        try {
            Thread.sleep(1000);
        }catch (Exception ex){

        }

        write("Job_Order",(short)1);
        jobInfoService.updateMemo(jobInfo,"Job_Order："+1);

        Job_Order_false(jobInfo);
    }

    private   void Trouble_Reset_false(JobInfo jobInfo){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<String> task = () -> {
            while (true){
                Boolean EQP_Trouble_RequestReset =(Boolean)readPlc("EQP_Trouble_RequestReset");
                if(EQP_Trouble_RequestReset){

                    write("Trouble_Reset",(short)0);
                    jobInfoService.updateMemo(jobInfo,"Trouble_Reset："+0);
                    write("Job_Order_Initialize_Confirm",(short)0);
                    jobInfoService.updateMemo(jobInfo,"Job_Order_Initialize_Confirm："+0);
                    reSendTask(jobInfo);
                }
                Short Trouble_Reset =(Short)readPlc("Trouble_Reset");
                if(Trouble_Reset==0){
                    break;
                }
            }
            return "Task completed!";
        };
        Future<String> future = executor.submit(task);
        //System.out.println(future.get()); // 获取并打印结果
        executor.shutdown();
    }

    @Autowired
    private CellInfoService cellInfoService;

    public String getNewCellCode(JobInfo jobInfo){

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());

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
            return  list.get(randomNum).getCode();
        }else {
            return  null;
        }
    }

    public String getNewOutCellCode(JobInfo jobInfo){

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());

        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria().andEqualTo("wareCode",taskInfo.getWareCode())
                .andEqualTo("invenState",1)
                .andEqualTo("taskState",0)
                .andEqualTo("disableState",0);

        //查出所有剩余库位
        List<CellInfo> list = cellInfoService.findByCondition(condition);  // 1-1-1 （0）  1-1-2 （1） 1-1-3 （2）
        // 30   29  randomNum（0，29）  18
        //随机
        int randomNum = new Random().nextInt(list.size()-1);
        if(list.size()>0){
            return  list.get(randomNum).getCode();
        }else {
            return  null;
        }
    }




    /**
     * 任务完成后回调
     * @param jobInfo
     * @return
     */
    public    Boolean finish(JobInfo jobInfo){
        if(isTest){
            return true;
        }
        //Job_Complete_Confirm
        write("Job_Complete_Confirm",(short)1);
        write("HOST_FROM_X",(short)0);
        write("HOST_FROM_Y",(short)0);
        write("HOST_FROM_Z",(short)0);
        write("HOST_TO_X",(short)0);
        write("HOST_TO_Y",(short)0);
        write("HOST_TO_Z",(short)0);
        write("Job_Order",(short)0);
        write("Host_Pallet_ID","");
        jobInfoService.updateMemo(jobInfo,"回复堆垛机完成信号确认");
        Job_Complete_Confirm_false();
        return true;
    }

    private   void Job_Complete_Confirm_false(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<String> task = () -> {
            while (true){
                Short Job_Complete =(Short)readPlc("Job_Complete");
                if(Job_Complete==0){
                    write("Job_Complete_Confirm",(short)0);
                    write("Job_Order",(short)0);
                }
                Short Job_Complete_Confirm =(Short)readPlc("Job_Complete_Confirm");
                if(Job_Complete_Confirm==0){
                    break;
                }
            }
            return "Task completed!";
        };
        Future<String> future = executor.submit(task);
        //System.out.println(future.get()); // 获取并打印结果
        executor.shutdown();
    }



    private Object readSsx( String valueCode){
        return deviceValueService.readValueByCode("JXG_SSX_SC",valueCode);
    };

    private Object writeSsx( String valueCode,Object object){
        return deviceValueService.writeValueByCode("JXG_SSX_SC",valueCode,object);
    };


    public    Boolean SSX(JobInfo jobInfo){
        if(isTest){
            return true;
        }
        String fromCellCode = jobInfo.getFromCellCode();
        String toCellCode = jobInfo.getToCellCode();
        if(fromCellCode!=null && fromCellCode.equals("0-0-1")){
            Short has = (Short) readSsx("001");
            //入库口是否允许取货
            short canIn = (short) readSsx("005");
            if( has==0){
                jobInfoService.updateMemo(jobInfo,"输送线入库口无货");
                return false;
            }
            if( canIn==0){
                jobInfoService.updateMemo(jobInfo,"输送线入库口不允许取货");
                return false;
            }
            return  true;
        }
        if(toCellCode!=null && toCellCode.equals("0-0-2")){
            Short has = (Short) readSsx("002");
            //输送线是否允许放货
            Short canFangHuo = (Short) readSsx("004");
            if(has==1){
                jobInfoService.updateMemo(jobInfo,"输送线出库口有货");
                return false;
            }

            if(canFangHuo==0){
                jobInfoService.updateMemo(jobInfo,"输送线出库口不允许放货");
                return false;
            }
            return  true;
        }
        return true;
    }

    private static Short getCellX(String cellCode){
        return Short.parseShort(cellCode.split("-")[0]);
    }
    private static Short getCellY(String cellCode){
        return Short.parseShort(cellCode.split("-")[1]);
    }
    private static Short getCellZ(String cellCode){
        return Short.parseShort(cellCode.split("-")[2]);
    }





}
