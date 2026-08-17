package com.deer.wcs.task.task;

import com.deer.wcs.base.model.Handle;
import com.deer.wcs.base.service.HandleService;
import com.deer.wcs.task.model.CodeScanner.CodeScannerInfo;
import com.deer.wcs.task.service.CodeScannerInfoService;
import com.deer.wcs.task.utils.HandelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component("CodeScannerTask")
public class CodeScannerTask {
    private static final Logger log = LoggerFactory.getLogger(CodeScannerTask.class);

    @Resource
    private CodeScannerInfoService smqInfoService;

    @Resource
    private HandleService handleService;

    /**
     * 扫描器定时任务
     */
//    @Scheduled(cron = "0/1 * * * * ?")
    public void autoRun() {
        //找到所有未执行扫码器任务
        Condition condition = new Condition(CodeScannerInfo.class);
        condition.createCriteria().andEqualTo("state",0).andEqualTo("delFlag",0);
        List<CodeScannerInfo> codeScannerInfoList = smqInfoService.findByCondition(condition);
        for (CodeScannerInfo codeScannerInfo : codeScannerInfoList){
            runSmq(codeScannerInfo);
        }
    }

    //查找到对应handle
    @Transactional
    public void runSmq(CodeScannerInfo codeScannerInfo) {

        //所有的未执行的任务
        //找到对应的执行器
        Condition conditionHandle = new Condition(Handle.class);
        conditionHandle.createCriteria().andEqualTo("id", codeScannerInfo.getHandId());
        List<Handle> handleList = handleService.findByCondition(conditionHandle);
        for (Handle handle:handleList){
            runSmqHandle(codeScannerInfo,handle);
        }
    }

    private void runSmqHandle(CodeScannerInfo codeScannerInfo, Handle handle) {
        try {
            HandelUtil.invokeSmqHandleMethod(handle.getClassName(), handle.getMethodName(), codeScannerInfo);
            codeScannerInfo.setState(1);
            smqInfoService.update(codeScannerInfo);
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
}
