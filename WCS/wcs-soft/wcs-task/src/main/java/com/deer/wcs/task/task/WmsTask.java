package com.deer.wcs.task.task;

import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.common.utils.http.HttpUtils;
import com.deer.wcs.system.service.ISysConfigService;
import com.deer.wcs.task.model.HostWcsInterface;
import com.deer.wcs.task.model.result.HaiKangServiceResult;
import com.deer.wcs.task.service.HostWcsInterfaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * @description:
 * @author:zfj
 * @date:2024/5/23 18:45
 */
@Component("WmsTask")
public class WmsTask {

    @Autowired
    private HostWcsInterfaceService hostWcsInterfaceService;
    @Autowired
    private ISysConfigService configService;

    private static final Logger log = LoggerFactory.getLogger(WmsTask.class);


    /**
     * 系统定时扫描接口信息表，用于向host发送请求
     */
    public void scanHostWcsInterface(){

        //从redis中获取最大发送次数
        Integer maxSendCount = Integer.parseInt(configService.selectConfigByKey("maxSendCount"));


        Condition condition = new Condition(HostWcsInterface.class);
        condition.createCriteria().andEqualTo("type",0)
                .andLessThan("sendCount",maxSendCount)
                .andEqualTo("sendStatus",0)
                .andNotEqualTo("sendResult",1);
        List<HostWcsInterface> interfaceList = hostWcsInterfaceService.findByCondition(condition);
        for(HostWcsInterface hostWcsInterface:interfaceList){
            /**
             * 该方法用于向host发送请求
             */
            //获取上位系统的ip地址
            String hostIp = configService.selectConfigByKey(hostWcsInterface.getSendTo());
            sendToHost(hostIp,hostWcsInterface);
        }

    }

    /**
     * 定时发送请求到host
     * @param hostWcsInterface
     * @param ip  (127.0.0.1:80)
     */
    private void sendToHost(String ip,HostWcsInterface hostWcsInterface){
        ip = "http://"+ip+ "/"+hostWcsInterface.getInterfaceName();
        try{
            String result = HttpUtils.dataPost(ip,hostWcsInterface.getContent());
            if(result!=null){
                HaiKangServiceResult httpResult = JSONObject.parseObject(result, HaiKangServiceResult.class);
                //发送成功
                if(httpResult.getCode()==0){
                    log.info(ip+hostWcsInterface.getInterfaceName()+" 接口信息发送成功");
                    hostWcsInterface.setSendCount(hostWcsInterface.getSendCount()+1);
                    hostWcsInterface.setSendStatus(1);
                    hostWcsInterface.setSendResult(1);
                    hostWcsInterface.setRecv(result);
                    hostWcsInterfaceService.update(hostWcsInterface);
                }else{
                    log.info(ip+hostWcsInterface.getInterfaceName()+" 接口信息发送成功,但对方返回异常。详细信息:"+httpResult.getMessage());
                    hostWcsInterface.setSendCount(hostWcsInterface.getSendCount()+1);
                    hostWcsInterface.setSendStatus(1);
                    hostWcsInterface.setSendResult(0);
                    hostWcsInterface.setRecv(result);
                    hostWcsInterfaceService.update(hostWcsInterface);
                }
            }else{
                //对方无响应
                log.error(ip+hostWcsInterface.getInterfaceName()+" 接口信息发送失败");
                hostWcsInterface.setSendCount(hostWcsInterface.getSendCount()+1);
                hostWcsInterface.setSendStatus(0);
                hostWcsInterfaceService.update(hostWcsInterface);
            }

        }catch (Exception e){
            log.error(ip+hostWcsInterface.getInterfaceName()+" 接口信息发送失败");
            hostWcsInterface.setSendCount(hostWcsInterface.getSendCount()+1);
            hostWcsInterface.setSendStatus(0);
            hostWcsInterfaceService.update(hostWcsInterface);
        }

    }

}
