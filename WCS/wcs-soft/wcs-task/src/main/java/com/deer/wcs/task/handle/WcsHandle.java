package com.deer.wcs.task.handle;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.base.model.HighCheck;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.http.HttpUtils;
import com.deer.wcs.system.service.ISysConfigService;
import com.deer.wcs.task.model.BoxTargetApply;
import com.deer.wcs.task.model.HostWcsInterface;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.model.recvInterface.TaskInfoStatus;
import com.deer.wcs.task.model.result.HaiKangServiceResult;
import com.deer.wcs.task.service.HostWcsInterfaceService;
import com.deer.wcs.task.service.TaskInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("WcsHandle")
public class WcsHandle {

    private static final Logger log = LoggerFactory.getLogger(WcsHandle.class);

    // Redis key prefix for box target apply
    private static final String REDIS_KEY_BOX_TARGET_APPLY_PREFIX = "WCS:BOX_TARGET_APPLY:";
    private static final String REDIS_KEY_BOX_HIGH_CHECK_SUFFIX = "WCS:HIGH_CHECK:";
    // Time interval of 10 minutes in milliseconds
    private static final long TIME_INTERVAL_10_MINUTES = 600000;
    // Time unit minutes
    private static final java.util.concurrent.TimeUnit TIME_UNIT_MINUTES = java.util.concurrent.TimeUnit.MINUTES;

    @Autowired
    private TaskInfoService taskInfoService;
    @Autowired
    private HostWcsInterfaceService interfaceService;
    @Autowired
    private ISysConfigService configService;
    @Autowired
    private RedisCache redisCache;

    // 任务已创建
    public boolean reportWcsStatus0(TaskInfo taskInfo) {
        if(!ifReportStatus(taskInfo)){
            return false;
        }

        TaskInfoStatus status = new TaskInfoStatus();
        status.setWmsTaskNo(taskInfo.getWmsTaskNo());
        status.setPalletCode(taskInfo.getPalletCode());
        status.setFromCellCode(taskInfo.getFromCellCode());
        status.setToCellCode(taskInfo.getToCellCode());
        status.setStatus(0);
        status.setMemo("WCS任务已创建");

        HostWcsInterface wcsInterface = saveInterfaceRecord(status);


        String hostIp = configService.selectConfigByKey(wcsInterface.getSendTo());

        return sendToHost(hostIp, wcsInterface);
    }

    // 任务执行中
    public boolean reportWcsStatus1(TaskInfo taskInfo) {
        if(!ifReportStatus(taskInfo)){
            return false;
        }

        TaskInfoStatus status = new TaskInfoStatus();
        status.setWmsTaskNo(taskInfo.getWmsTaskNo());
        status.setPalletCode(taskInfo.getPalletCode());
        status.setFromCellCode(taskInfo.getFromCellCode());
        status.setToCellCode(taskInfo.getToCellCode());
        status.setStatus(1);
        status.setMemo("WCS任务执行中");

        HostWcsInterface wcsInterface = saveInterfaceRecord(status);

        String hostIp = configService.selectConfigByKey(wcsInterface.getSendTo());

        return sendToHost(hostIp, wcsInterface);
    }

    // 任务已完成
    public boolean reportWcsStatus2(TaskInfo taskInfo) {
        if(!ifReportStatus(taskInfo)){
            return false;
        }

        TaskInfoStatus status = new TaskInfoStatus();
        status.setWmsTaskNo(taskInfo.getWmsTaskNo());
        status.setPalletCode(taskInfo.getPalletCode());
        status.setFromCellCode(taskInfo.getFromCellCode());
        status.setToCellCode(taskInfo.getToCellCode());
        status.setStatus(2);
        status.setMemo("WCS任务已完成");

        HostWcsInterface wcsInterface = saveInterfaceRecord(status);

        String hostIp = configService.selectConfigByKey(wcsInterface.getSendTo());

        return sendToHost(hostIp, wcsInterface);
    }

    public boolean reportWcsStatus3(JobInfo jobInfo, String memo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());

        if(!ifReportStatus(taskInfo)){
            return false;
        }

        TaskInfoStatus status = new TaskInfoStatus();
        status.setWmsTaskNo(taskInfo.getWmsTaskNo());
        status.setPalletCode(taskInfo.getPalletCode());
        status.setFromCellCode(taskInfo.getFromCellCode());
        status.setToCellCode(taskInfo.getToCellCode());
        status.setStatus(3);
        status.setMemo(memo);

        HostWcsInterface wcsInterface = saveInterfaceRecord(status);

        String hostIp = configService.selectConfigByKey(wcsInterface.getSendTo());

        return sendToHost(hostIp, wcsInterface);
    }

    private HostWcsInterface saveInterfaceRecord(TaskInfoStatus status) {
        HostWcsInterface wcsInterface = new HostWcsInterface();
        wcsInterface.setCode("reportWcsStatus");
        wcsInterface.setUrl("wms/fromWcs/reportWcsStatus");
        wcsInterface.setInterfaceName("WCS上报任务状态");
        wcsInterface.setContent(JSON.toJSONString(status));
        saveInterfaceRecord(wcsInterface);
        return wcsInterface;
    }

    private boolean ifReportStatus(TaskInfo taskInfo) {
        String ifReportStatus =  configService.selectConfigByKey("ifReportStatus");
        return ifReportStatus.equalsIgnoreCase("true");
    }

    /**
     * 发送目的地请求
     *
     * @param boxTargetApply 目的地请求信息
     * @return 是否发送成功
     */
    public boolean sendBoxTargetApply(BoxTargetApply boxTargetApply) {
        // 检查同一个托盘是否在10分钟内已经执行过该操作
        String palletCode = boxTargetApply.getPalletCode();
        if (palletCode == null || palletCode.isEmpty()) {
            log.warn("托盘号为空，无法进行重复执行限制检查");
            return false;
        }
        String redisKey = REDIS_KEY_BOX_TARGET_APPLY_PREFIX + palletCode;
        // 检查Redis中是否存在该托盘的执行记录
        String lastExecuteTimeStr = redisCache.getCacheObject(redisKey);
        if (lastExecuteTimeStr != null) {
            // 计算时间差
            long lastExecuteTime = Long.parseLong(lastExecuteTimeStr);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastExecuteTime < TIME_INTERVAL_10_MINUTES) {
                log.info("托盘[{}]在10分钟内已经执行过目的地请求，本次请求将被忽略", palletCode);
                return false;
            }
        }


        HostWcsInterface wcsInterface = new HostWcsInterface();
        wcsInterface.setCode("sendBoxTargetApply");
        wcsInterface.setUrl("wms/fromWcs/targetApply");
        wcsInterface.setInterfaceName("WCS发送目的地请求");
        wcsInterface.setSendFrom("WCS");
        wcsInterface.setSendTo("WMS");
        wcsInterface.setType(HostWcsInterface.TYPE_SEND);
        wcsInterface.setContent(JSON.toJSONString(boxTargetApply));
        wcsInterface.setSendCount(0);

        String hostIp = configService.selectConfigByKey(wcsInterface.getSendTo());

        boolean result = sendToHost(hostIp, wcsInterface);
        if (result) {
            // 更新Redis中的执行时间
            redisCache.setCacheObject(redisKey, String.valueOf(System.currentTimeMillis()), 10, TIME_UNIT_MINUTES);

        }
        return result;
    }

     /**
     * 发送高检查结果
     *
     * @param highCheck 高检查请求信息
     * @return 是否发送成功
     */
    public boolean sendHighCheck(HighCheck highCheck) {
        HostWcsInterface wcsInterface = new HostWcsInterface();
        wcsInterface.setCode("sendHighCheck");
        wcsInterface.setUrl("wms/fromWcs/highCheck");
        wcsInterface.setInterfaceName("WCS发送高检查结果");
        wcsInterface.setSendFrom("WCS");
        wcsInterface.setSendTo("WMS");
        wcsInterface.setType(HostWcsInterface.TYPE_SEND);
        wcsInterface.setContent(JSON.toJSONString(highCheck));
        wcsInterface.setSendCount(0);

        String hostIp = configService.selectConfigByKey(wcsInterface.getSendTo());

        boolean result = sendToHost(hostIp, wcsInterface);
        return result;
    }



    /**
     * 保存接口记录
     *
     * @param wcsInterface 接口信息
     */
    private void saveInterfaceRecord(HostWcsInterface wcsInterface) {
        wcsInterface.setSendFrom("WCS");
        wcsInterface.setSendTo("WMS");
        wcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        wcsInterface.setType(HostWcsInterface.TYPE_SEND);
        wcsInterface.setSendStatus(HostWcsInterface.SEND_STATUS_NOT_SEND);
        interfaceService.save(wcsInterface);
    }

    /**
     * 发送信息到主机
     *
     * @param ip               主机IP
     * @param hostWcsInterface 接口信息
     */
    public boolean sendToHost(String ip, HostWcsInterface hostWcsInterface) {
        ip = "http://" + ip + "/" + hostWcsInterface.getUrl();
        boolean res = false;
        try {
            String result = HttpUtils.dataPost(ip, hostWcsInterface.getContent());
            if (result != null) {
                HaiKangServiceResult httpResult = JSONObject.parseObject(result, HaiKangServiceResult.class);
                //发送成功
                if (httpResult.getCode() == 200) {
                    log.info(ip + hostWcsInterface.getInterfaceName() + " 接口信息发送成功");
                    hostWcsInterface.setSendCount(hostWcsInterface.getSendCount() == null ? 0 : hostWcsInterface.getSendCount() + 1);
                    hostWcsInterface.setSendStatus(HostWcsInterface.SEND_STATUS_SEND);
                    hostWcsInterface.setSendResult(HostWcsInterface.SEND_RESULT_SEND_SUCCESS);
                    hostWcsInterface.setRecv(result);
                    hostWcsInterface.setEndTime(DateUtil.getNowDateTimeString());
                    res = true;
                } else {
                    log.info(ip + hostWcsInterface.getInterfaceName() + " 接口信息发送成功,但对方返回异常。详细信息:" + httpResult.getMessage());
                    hostWcsInterface.setSendCount(hostWcsInterface.getSendCount() == null ? 0 : hostWcsInterface.getSendCount() + 1);
                    hostWcsInterface.setSendStatus(HostWcsInterface.SEND_STATUS_SEND);
                    hostWcsInterface.setSendResult(HostWcsInterface.SEND_RESULT_SEND_ERROR);
                    hostWcsInterface.setRecv(result);
                }
            } else {
                //对方无响应
                log.error(ip + hostWcsInterface.getInterfaceName() + " 接口信息发送失败");
                hostWcsInterface.setSendCount(hostWcsInterface.getSendCount() == null ? 0 : hostWcsInterface.getSendCount() + 1);
                hostWcsInterface.setSendStatus(HostWcsInterface.SEND_RESULT_SEND_ERROR);
            }

        } catch (Exception e) {
            log.error(ip + hostWcsInterface.getInterfaceName() + " 接口信息发送失败");
            hostWcsInterface.setSendCount(hostWcsInterface.getSendCount() == null ? 0 : hostWcsInterface.getSendCount() + 1);
            hostWcsInterface.setSendStatus(HostWcsInterface.SEND_RESULT_SEND_ERROR);
        } finally {
            hostWcsInterface.setStartTime(DateUtil.getNowDateTimeString());
            if (hostWcsInterface.getId() == null) {
                interfaceService.save(hostWcsInterface);
            } else {
                interfaceService.update(hostWcsInterface);
            }
        }

        return res;
    }

}
