package com.deer.wcs.task.web;

import com.deer.wcs.base.model.PathInfo;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.base.service.PathInfoService;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.system.service.AutoService;
import com.deer.wcs.task.service.HostWcsInterfaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:AGV调试流程 1. 创建一个AGV任务接口发送给小车
 * 2. 小车上报AGV状态，到达起点
 * 3. 写入上货指令
 * 4. 接口通知小车继续任务
 * 5. 小车到达终点
 * 6. 任务结束
 * @author:zfj
 * @date:2024/7/16 21:52
 */
@Component("HaiKangSsxService")
public class HaiKangSsxService {

    private static final Logger log = LoggerFactory.getLogger(HaiKangSsxService.class);

    @Autowired
    private HostWcsInterfaceService hostWcsInterfaceService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private DeviceValueService deviceValueService;
    @Autowired
    private AutoService autoService;
    @Autowired
    private PathInfoService pathInfoService;

    private Object readPlc(String valueCode) {
        return deviceValueService.readValueByCode("JXG_SSX", valueCode);
    }

    private Object write(String valueCode, Object object) {
        return deviceValueService.writeValueByCode("JXG_SSX", valueCode, object);
    }

    private Object readSsxPlc(String valueCode) {
        return deviceValueService.readValueByCode("JXG_SSX", valueCode);
    }

    private Object writeSsx(String valueCode, Object object) {
        return deviceValueService.writeValueByCode("JXG_SSX", valueCode, object);
    }

    //输送线请求排出
    public boolean ssxRequestOut(PathInfo pathInfo) {
        Integer outRequest = (Integer) readPlc("outRequest");
        if(outRequest!=1){
            return false;
        }
        if (outRequest == 1) {
            log.info("输送线请求排出");
            pathInfo.setMemo("输送线请求排出");
            pathInfoService.update(pathInfo);
            return true;
        }
        return false;
    }

    //通知输送线Agv到达出口
    public boolean noticeAgvReachOut(PathInfo pathInfo) {
        Integer agvReachOut = 1;
        write("agvReachOut",agvReachOut);
        log.info("通知输送线Agv到达出口");
        pathInfo.setMemo("通知输送线Agv到达出口");
        pathInfoService.update(pathInfo);
        return true;
    }




}
