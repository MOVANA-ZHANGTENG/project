package com.deer.wcs.task.handle.lg7;


import com.deer.wcs.base.model.CacheToAgv;
import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.model.ProPositionContent;
import com.deer.wcs.base.service.CacheToAgvService;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.base.service.ProPositionContentService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.task.handle.hik.AgvResult;
import com.deer.wcs.task.model.CallBoxRecord;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfo;
import com.deer.wcs.task.service.CallBoxInfoService;
import com.deer.wcs.task.service.CallBoxRecordService;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component("Lg7CallBoxHandle")
public class Lg7CallBoxHandle {

    @Autowired
    private CallBoxRecordService callBoxRecordService;

    @Autowired
    private CallBoxInfoService callBoxInfoService;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private Lg7HikJobHandle lg7HikJobHandle;

    @Autowired
    private AutoService autoService;

    @Autowired
    private PositionInfoService positionInfoService;

    @Autowired
    private ProPositionContentService proPositionContentService;

    @Autowired
    private CacheToAgvService cacheToAgvService;


    //LG7厂阳极室内呼叫器
    public Boolean run(CallBoxRecord record){
        CallBoxInfo callBoxInfo = callBoxInfoService.findById(record.getCallBoxInfoId());
        String wareCode = callBoxInfo.getWareCode();
        //呼叫盒中的位置编码
        String positionCode = callBoxInfo.getPositionCode();
        //位置
        PositionInfo positionInfo = positionInfoService.findByCode(wareCode,positionCode);
        //位置扩展  专门给lg agv项目用的
        ProPositionContent proPositionContent = proPositionContentService.findByCode(wareCode,positionCode);
        //产品
        String itemCode = proPositionContent.getItemCode();
        Integer type = proPositionContent.getType();
        record = callBoxRecordService.findById(record.getId());
        if(record.getState()==1){
            record.setMemo("重复处理");
            callBoxRecordService.update(record);
            return true;
        }

        //type 0-按下按钮 1-控制灯
        if(record.getType()==1){
            record.setMemo("亮灯记录");
            callBoxRecordService.update(record);
            return true;
        }

        Integer btn = record.getBtn();
        //CT下满上空（M50）
        //RP的按钮1是下空上满
        if(btn==1 || btn==4){
            //判断当前呼叫盒的位置类型是什么？
            //2-CT
            if (proPositionContent.getType()==2){

                if(positionInfo.getInvenState()<0.1){
                    record.setMemo("该站台无料，不允许下料");
                    callBoxRecordService.update(record);
                    return false;
                }
                Boolean flag = false;
                //下满上空
                //1-下满 起点呼叫盒位置 终点：缓存站台/RP站台
                //从CT站台下满到RP站台（下满），从RP站台取空到CT站台（上空）
                //  逻辑：人员按下呼叫盒按钮，生成任务，亮灯
                //  AGV从CT站台接满料到RP站台，判断RP站台是否有空料棒，如果有，AGV到达RP站台，
                //  AGV先用空料位接RP站台的空料棒，取走后，AGV用满料位的满料给RP站台上料
                //（路径：CT-RP-RP-CT），如果RP站台没有空料棒，那么直接上料（路径：CT-RP）
                //RP站台 有空料棒的
                ProPositionContent toContent1 = proPositionContentService.findByType(itemCode,3,1L,0);
                if(toContent1==null){
                    //没有找到有空料棒的,去找没有空料棒的RP站台
                    toContent1 = proPositionContentService.findByType(itemCode,3,0L,null);
                    if (toContent1 == null){
                        record.setMemo("未找到当前位置-"+positionCode+"的下满上空任务中下满的目标点");
                        callBoxRecordService.update(record);
                        return false;
                    }
                    flag = true;
                }
                if (!flag){
                    //有空料棒，执行CT-RP-RP-CT
                    saveTask(positionCode,toContent1.getCode(),toContent1.getCode(),positionCode,record.getId());
                }else {
                    //RP站台没有空料棒，只能执行CT站台下满到RP站台
                    saveTask(positionCode,toContent1.getCode(),null,null,record.getId());
                }

            }
            if (proPositionContent.getType()==3){
                //3-RP
                if(positionInfo.getInvenState()<0.1){
                    record.setMemo("该站台无料，不允许下料");
                    callBoxRecordService.update(record);
                    return false;
                }
                Boolean flag = false;
                //1-下空上满
                //  从CT站台下满到RP站台（下满），从RP站台取空到CT站台（上空）
                //逻辑：人员按下呼叫盒按钮，生成任务，亮灯
                //  AGV从CT站台接满料到RP站台，判断RP站台是否有空料棒，如果有，AGV到达RP站台，
                //  AGV先用空料位接RP站台的空料棒，取走后，AGV用满料位的满料给RP站台上料
                //（路径：CT/缓存站台-RP-RP-CT）
                //CT站台有料的/缓存站台有料的
                ProPositionContent toContent1 = proPositionContentService.findByType(itemCode,2,1L,1);
                if(toContent1==null){
                    //ct站台没有找到有料的，去缓存站台找
                    toContent1 = proPositionContentService.findByType(itemCode,1,1L,1);
                    if (toContent1 ==  null){
                        record.setMemo("未找到当前位置-"+positionCode+"的下空上满任务中下空的目标点");
                        callBoxRecordService.update(record);
                        return false;
                    }
                    flag = true;
                }
                if (!flag){
                    //从CT站台取料的 CT-RP-RP-CT
                    saveTask(toContent1.getCode(),positionCode,positionCode,toContent1.getCode(),record.getId());
                }else {
                    //从缓存站台取料的 缓存站台-RP-RP-CT
                    //找到需要上空的
                    ProPositionContent fromContent1 = proPositionContentService.findByType(itemCode,1,0L,null);
                    if(fromContent1==null){
                        record.setMemo("未找到当前位置-"+positionCode+"的下空上满任务中上满的目标点");
                        callBoxRecordService.update(record);
                        return false;

                    }
                    saveTask(toContent1.getCode(),positionCode,positionCode,fromContent1.getCode(),record.getId());
                }
            }
        }

        //机台下料
        if(btn==2){
            if(positionInfo.getInvenState()<0.1){
                record.setMemo("该站台无料，不允许下料");
                callBoxRecordService.update(record);
                return false;
            }
            //0-仓库 1-缓存架 2-CT 3-RP
            if(proPositionContent.getType()==2){
                // 缓存架
                //优先从缓存站台查找
                //原代码
                /*ProPositionContent toContent1 = proPositionContentService.findByType(itemCode,1,0L,null);
                if(toContent1==null){
                    record.setMemo("未找到目标点");
                    callBoxRecordService.update(record);
                    return false;

                }*/
                //更新代码
                ProPositionContent toContent1 = proPositionContentService.findByType(itemCode,1,0L,null);
                if(toContent1!=null){
                    saveTask(positionCode,toContent1.getCode(),null,null,record.getId());
                }else {
                    //缓存站台没有查到，就从RP站台查找 没有空料棒的
                    ProPositionContent toContent2 = proPositionContentService.findByType(itemCode,3,0L,null);
                    if (toContent2 != null){
                        saveTask(positionCode,toContent2.getCode(),null,null,record.getId());
                    }else {
                        //RP站台没有查到 放到agv上
                        saveTask(positionCode,null,null,null,record.getId());
                    }
                }

                // 缓存架
               // ProPositionContent toContent3 = proPositionContentService.findByType(itemCode,3,0L,null);
            }
           // 0-仓库 1-缓存架 2-CT 3-RP
            if(proPositionContent.getType()==3){
                // 查找CT站台，缓存站台不允许放空料棒 只能放到CT站台
                ProPositionContent toContent1 = proPositionContentService.findByType(itemCode,2,0L,null);
                if(toContent1==null){
                    record.setMemo("未找到目标点");
                    callBoxRecordService.update(record);
                    return false;

                }
                saveTask(positionCode,toContent1.getCode(),null,null,record.getId());
                // 缓存架
               // ProPositionContent toContent2 = proPositionContentService.findByType(itemCode,2,0L,null);

            }



        }

        //机台上料
        if(btn==3){
            if(positionInfo.getInvenState()>0.9){
                record.setMemo("该站台有料，不允许上料");
                callBoxRecordService.update(record);
                return false;
            }

            // 0-仓库 1-缓存架 2-CT 3-RP
            // CT是上空托盘
            if(proPositionContent.getType()==2){
                // 空料棒只有RP站台才有，缓存站台不允许放空料棒
                ProPositionContent fromContent1 = proPositionContentService.findByType(itemCode,3,1L,0);

                if(fromContent1==null){
                    record.setMemo("未找到起点");
                    callBoxRecordService.update(record);
                    return false;

                }
                saveTask(fromContent1.getCode(),positionCode,null,null,record.getId());

                // 缓存架
                // ProPositionContent toContent3 = proPositionContentService.findByType(itemCode,3,0L,null);

            }
            //RP是上满托盘
            if(proPositionContent.getType()==3){
                // 缓存架
                //原代码
                /*ProPositionContent fromContent1 = proPositionContentService.findByType(itemCode,1,1L,1);
                if(fromContent1==null){
                    record.setMemo("未找到起点");
                    callBoxRecordService.update(record);
                    return false;

                }*/
                //优先从agv上查找有没有满料
                CacheToAgv cache = cacheToAgvService.findCondition(null, itemCode, null);
                if (cache != null){
                    saveTask(null,positionCode,null,null,record.getId());
                }else {
                    //agv上没有满料的情况
                    //从缓存站台查找
                    ProPositionContent fromContent1 = proPositionContentService.findByType(itemCode,1,1L,1);
                    if(fromContent1==null){
                        //缓存站台上也没有，只有从CT站台上查找
                        fromContent1 = proPositionContentService.findByType(itemCode,2,1L,1);
                        if(fromContent1==null){
                            record.setMemo("未找到起点");
                            callBoxRecordService.update(record);
                            return false;

                        }
                    }
                    saveTask(fromContent1.getCode(),positionCode,null,null,record.getId());
                }

            }

        }

        //下满上空（M53）
//        if(btn==4){
//
//        }

        //进入
        if(btn==5){

            JobInfo fromJob = jobInfoService.findBy("fromCellCode",callBoxInfo.getPositionCode());
            if(fromJob!=null){
                //继续执行任务
                AgvResult agvResult= lg7HikJobHandle.continueTaskByAgvCode(autoService.getReqCode(),fromJob.getId().toString());
                if(agvResult.getCode().equals("0")){
                    record.setMemo("成功执行HIK继续执行任务");
                }else {
                    record.setMemo(agvResult.getMessage());
                }
            }
            JobInfo toJob = jobInfoService.findBy("toCellCode",callBoxInfo.getPositionCode());
            if(toJob!=null){
                AgvResult agvResult= lg7HikJobHandle.continueTaskByAgvCode(autoService.getReqCode(),toJob.getId().toString());
                if(agvResult.getCode().equals("0")){
                    record.setMemo("成功执行HIK继续执行任务");
                }else {
                    record.setMemo(agvResult.getMessage());
                }
            }
        }

        //离开
        if(btn==6){
            JobInfo fromJob = jobInfoService.findBy("fromCellCode",callBoxInfo.getPositionCode());
            if(fromJob!=null){
                AgvResult agvResult= lg7HikJobHandle.continueTaskByAgvCode(autoService.getReqCode(),fromJob.getId().toString());
                if(agvResult.getCode().equals("0")){
                    record.setMemo("成功执行HIK继续执行任务");
                }else {
                    record.setMemo(agvResult.getMessage());
                }
            }
            JobInfo toJob = jobInfoService.findBy("toCellCode",callBoxInfo.getPositionCode());
            if(toJob!=null){
                AgvResult agvResult= lg7HikJobHandle.continueTaskByAgvCode(autoService.getReqCode(),toJob.getId().toString());
                if(agvResult.getCode().equals("0")){
                    record.setMemo("成功执行HIK继续执行任务");
                }else {
                    record.setMemo(agvResult.getMessage());
                }
            }
        }

        record.setState(1);
        callBoxRecordService.update(record);

        return true;
    }


    //LG7仓库呼叫器
    public boolean wareCallBoxRun(CallBoxRecord record){
        CallBoxInfo callBoxInfo = callBoxInfoService.findById(record.getCallBoxInfoId());
        String wareCode = callBoxInfo.getWareCode();
        //呼叫盒中的位置编码
        String positionCode = callBoxInfo.getPositionCode();
        //位置
        PositionInfo positionInfo = positionInfoService.findByCode(wareCode,positionCode);
        //位置扩展  专门给lg agv项目用的
        ProPositionContent proPositionContent = proPositionContentService.findByCode(wareCode,positionCode);
        //产品
        String itemCode = proPositionContent.getItemCode();
        // position_info  inven_state-是否有托盘 0无托盘/1有托盘   pro_position_content表  pallet_state-托盘状态 0空托盘/1满托盘  item_code-产品编码
        Integer btn = record.getBtn();
        //上满
        if (btn == 3){
            positionInfo.setInvenState(1L);
            positionInfoService.update(positionInfo);
            proPositionContent.setPalletState(1);
            proPositionContent.setItemCode(itemCode);
            proPositionContentService.update(proPositionContent);
        }
        //下空
        if (btn == 2){
            positionInfo.setInvenState(0L);
            positionInfoService.update(positionInfo);
            proPositionContent.setPalletState(null);
            //proPositionContent.setItemCode("");
            proPositionContentService.updateNull(proPositionContent);
        }
        //System.out.println("仓库呼叫盒执行器");
        record.setState(1);
        callBoxRecordService.update(record);
        return true;
    }

    @Autowired
    private TaskInfoService taskInfoService;

    /**
     * 生成任务
     * @param from 起点1
     * @param to 终点1
     * @param from2 起点2
     * @param to2 终点2
     * @param callBoxRecordId 呼叫盒记录id
     */
    private void saveTask(String from ,String to,String from2,String to2,Long callBoxRecordId){
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setFromCellCode(from);
        taskInfo.setToCellCode(to);
        if (from2 != null && !from2.equals("")){
            taskInfo.setRemark1(from2);
            taskInfo.setRemark2(to2);
        }
        taskInfo.setWareCode("LG-7-YJ");
        taskInfo.setWareName("LG-7-YJ");
        taskInfo.setType("8");
        //taskInfo.setCallBoxRecordId(callBoxRecordId);
        taskInfo.setCreateTime(DateUtil.getNowDateTimeString());
        taskInfoService.save(taskInfo);
    }
}
