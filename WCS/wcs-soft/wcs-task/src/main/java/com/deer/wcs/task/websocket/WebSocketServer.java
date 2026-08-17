package com.deer.wcs.task.websocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.DeviceValueService;
import com.deer.wcs.base.service.PalletInfoService;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.common.utils.spring.SpringUtils;
import com.deer.wcs.task.model.ThreeData;
import com.deer.wcs.task.task.JxgSsxTask;
import com.google.api.client.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * websocket 消息处理
 *
 * @author ruoyi
 */

@Component
@Service
@ServerEndpoint("/websocket/message")
public class WebSocketServer {
    /**
     * WebSocketServer 日志控制器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * 默认最多允许同时在线人数100
     */
    public static int socketMaxOnlineCount = 100;

    private static Semaphore socketSemaphore = new Semaphore(socketMaxOnlineCount);

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) throws Exception {
        boolean semaphoreFlag = false;
        // 尝试获取信号量
        semaphoreFlag = SemaphoreUtils.tryAcquire(socketSemaphore);
        if (!semaphoreFlag) {
            // 未获取到信号量
            LOGGER.error("\n 当前在线人数超过限制数- {}", socketMaxOnlineCount);
            WebSocketUsers.sendMessageToUserByText(session, "当前在线人数超过限制数：" + socketMaxOnlineCount);
            session.close();
        } else {
            // 添加用户
            WebSocketUsers.put(session.getId(), session);
            LOGGER.info("\n 建立连接 - {}", session);
            LOGGER.info("\n 当前人数 - {}", WebSocketUsers.getUsers().size());
            //给webSocket发信号
//            ThreeData data = new ThreeData();
//            data.setType("agv");
//            data.setFromNode("00");
//            data.setToNode("C01");
//            String msg = JSONObject.toJSONString(data);
//            WebSocketUsers.sendMessageToUserByText(session, msg);
        }
    }

    /**
     * 连接关闭时处理
     */
    @OnClose
    public void onClose(Session session) {
        LOGGER.info("\n 关闭连接 - {}", session);
        // 移除用户
        WebSocketUsers.remove(session.getId());
        // 获取到信号量则需释放
        SemaphoreUtils.release(socketSemaphore);
    }

    /**
     * 抛出异常时处理
     */
    @OnError
    public void onError(Session session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            // 关闭连接
            session.close();
        }
        String sessionId = session.getId();
        LOGGER.info("\n 连接异常 - {}", sessionId);
        LOGGER.info("\n 异常信息 - {}", exception);
        // 移出用户
        WebSocketUsers.remove(sessionId);
        // 获取到信号量则需释放
        SemaphoreUtils.release(socketSemaphore);
    }

    /**
     * 服务器接收到客户端消息时调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        String msg = message.replace("你", "我").replace("吗", "");
        WebSocketUsers.sendMessageToUserByText(session, msg);
    }

//    class TaskTest {
//        private String type;
//        private String palletCode;
//        private String fromCellCode;
//        private String toCellCode;
//
//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }
//
//        public String getPalletCode() {
//            return palletCode;
//        }
//
//        public void setPalletCode(String palletCode) {
//            this.palletCode = palletCode;
//        }
//
//        public String getFromCellCode() {
//            return fromCellCode;
//        }
//
//        public void setFromCellCode(String fromCellCode) {
//            this.fromCellCode = fromCellCode;
//        }
//
//        public String getToCellCode() {
//            return toCellCode;
//        }
//
//        public void setToCellCode(String toCellCode) {
//            this.toCellCode = toCellCode;
//        }
//    }

//    @Scheduled(fixedRate = 10000)
//    public void sendMessage() {
//        TaskTest taskTest = new TaskTest();
//        taskTest.setType("RGV_MOVE");
//        taskTest.setPalletCode("XL0000009H");
//        taskTest.setFromCellCode("1-2");
//        taskTest.setToCellCode("1-5");
//        String msg = JSON.toJSONString(taskTest);
//        WebSocketUsers.sendMessageToUsersByText(msg);
//    }
}
