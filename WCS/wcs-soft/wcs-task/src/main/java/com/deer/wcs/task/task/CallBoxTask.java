package com.deer.wcs.task.task;

import com.deer.wcs.base.model.Handle;
import com.deer.wcs.base.service.HandleService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.model.CallBoxRecord;
import com.deer.wcs.task.model.callBoxLG.CallBoxInfo;
import com.deer.wcs.task.service.CallBoxInfoService;
import com.deer.wcs.task.service.CallBoxRecordService;
import com.deer.wcs.task.utils.HandelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component("CallBoxTask")
public class CallBoxTask {

    @Autowired
    private CallBoxInfoService callBoxInfoService;

    @Autowired
    private CallBoxRecordService callBoxRecordService;


    @Autowired
    private HandleService handleService;

    private static final Logger log = LoggerFactory.getLogger(CallBoxTask.class);


    /**
     * 呼叫盒定时任务
     */
//    @Scheduled(cron = "0/1 * * * * ?")
    public void autoRun() {
        //找到所有未执行呼叫盒任务
        Condition condition = new Condition(CallBoxInfo.class);
        condition.createCriteria().andEqualTo("state",0);
        List<CallBoxRecord> list = callBoxRecordService.findByCondition(condition);

        for (CallBoxRecord record:list){
            runCallBox(record);
            try {
                Thread.sleep(2000);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private void runCallBoxHandle(CallBoxRecord record, Handle handle) {
        try {
            HandelUtil.invokeCallBoxHandleMethod(handle.getClassName(), handle.getMethodName(), record);
            record.setState(1);
            record.setUpdateTime(DateUtil.getNowDateTimeString());
            callBoxRecordService.update(record);
        } catch (NoSuchMethodException e) {
            log.warn("找不到方法"+handle.getClassName()+"."+handle.getMethodName());
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            log.warn("当前代码没有足够的权限访问指定的类、字段、方法或构造器");
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            log.warn("执行方法"+handle.getClassName()+"."+handle.getMethodName()+"异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //查找到对应按钮的handle
    @Transactional
    public void runCallBox(CallBoxRecord record) {

        CallBoxInfo callBoxInfo =callBoxInfoService.findById(record.getCallBoxInfoId());
        if(callBoxInfo==null){
            record.setMemo("未找到呼叫器");
            return;
        }
        Handle handle = handleService.findById(callBoxInfo.getHandId());
        if(handle==null){
            record.setMemo("呼叫盒未绑定执行器");
            return;
        }
        runCallBoxHandle(record,handle);
//        //所有的未执行的呼叫盒任务
//        //找到对应的执行器
//        Condition conditionHandle = new Condition(Handle.class);
//        conditionHandle.createCriteria().andEqualTo("id",callBoxInfo.getHandId());
//        List<Handle> handleList = handleService.findByCondition(conditionHandle);
//        for (Handle handle:handleList){
//            runCallBoxHandle(record,handle);
//        }
    }


}
