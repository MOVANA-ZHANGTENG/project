package com.deer.wcs.rcs.websocket;

import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.rcs.model.RcsCarInfo;
import com.deer.wcs.rcs.service.RcsCarInfoService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 小车位置WebSocket服务（已弃用，请使用 MonitorWebSocketHandler）
 * 按Session管理连接，支持多用户独立订阅
 * 
 * @author Deer WCS Team
 * @date 2024-10-16
 * @deprecated 已被 MonitorWebSocketHandler 替代，使用Redis标志位优化
 */
@Deprecated
// @ServerEndpoint("/websocket/carPosition")  // 已禁用，避免与新版本冲突
// @Component  // 已禁用
public class CarPositionWebSocket {
    
    private static final Logger log = LoggerFactory.getLogger(CarPositionWebSocket.class);
    
    // 存储会话信息：SessionID -> 会话上下文
    private static Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    // 注入Service（静态变量需要通过setter注入）
    private static RcsCarInfoService rcsCarInfoService;
    
    @Autowired
    public void setRcsCarInfoService(RcsCarInfoService rcsCarInfoService) {
        CarPositionWebSocket.rcsCarInfoService = rcsCarInfoService;
    }
    
    /**
     * WebSocket会话上下文
     */
    @Data
    public static class WebSocketSession {
        private Session session;
        private String userId;        // 用户ID
        private String userName;      // 用户名称
        private String wareCode;      // 关注的仓库编码
        private Integer currentFloor; // 当前楼层
        private Date connectTime;     // 连接时间
    }
    
    @OnOpen
    public void onOpen(Session session) {
        // 从请求参数获取用户信息
        String userId = getUserIdFromSession(session);
        String userName = getUserNameFromSession(session);
        
        WebSocketSession wsSession = new WebSocketSession();
        wsSession.setSession(session);
        wsSession.setUserId(userId);
        wsSession.setUserName(userName);
        wsSession.setConnectTime(new Date());
        
        sessions.put(session.getId(), wsSession);
        
        log.info("小车位置WebSocket连接建立: 用户={}, SessionID={}", userName, session.getId());
        log.info("当前在线用户数: {}", sessions.size());
    }
    
    @OnClose
    public void onClose(Session session) {
        WebSocketSession wsSession = sessions.remove(session.getId());
        if (wsSession != null) {
            log.info("小车位置WebSocket连接关闭: 用户={}, SessionID={}", 
                     wsSession.getUserName(), session.getId());
            log.info("当前在线用户数: {}", sessions.size());
        }
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            JSONObject msg = JSONObject.parseObject(message);
            String type = msg.getString("type");
            
            WebSocketSession wsSession = sessions.get(session.getId());
            if (wsSession == null) {
                log.warn("Session不存在: {}", session.getId());
                return;
            }
            
            // 处理客户端消息
            switch (type) {
                case "subscribe":
                    // 订阅仓库
                    String wareCode = msg.getString("wareCode");
                    Integer floor = msg.getInteger("floor");
                    wsSession.setWareCode(wareCode);
                    wsSession.setCurrentFloor(floor);
                    log.info("用户 {} 订阅仓库: {}, 楼层: {}", 
                             wsSession.getUserName(), wareCode, floor);
                    
                    // 立即推送当前楼层的所有小车位置
                    pushInitialCarPositions(session, wareCode, floor);
                    break;
                    
                case "changeFloor":
                    // 切换楼层
                    Integer newFloor = msg.getInteger("floor");
                    wsSession.setCurrentFloor(newFloor);
                    log.info("用户 {} 切换楼层: {}", wsSession.getUserName(), newFloor);
                    
                    // 推送新楼层的小车位置
                    pushInitialCarPositions(session, wsSession.getWareCode(), newFloor);
                    break;
                    
                case "heartbeat":
                    // 心跳保活
                    sendHeartbeatResponse(session);
                    break;
                    
                default:
                    log.warn("未知消息类型: {}", type);
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败: {}", e.getMessage(), e);
            sendErrorMessage(session, "MESSAGE_PARSE_ERROR", "消息处理失败: " + e.getMessage());
        }
    }
    
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket错误: SessionID={}, Error={}", 
                  session.getId(), error.getMessage());
    }
    
    /**
     * 推送小车位置更新到前端（按仓库和楼层过滤）
     * 
     * @param carInfo 小车信息
     */
    public static void pushCarPosition(RcsCarInfo carInfo) {
        if (sessions.isEmpty()) {
            return;
        }
        
        String wareCode = carInfo.getWareCode();
        if (wareCode == null) {
            log.warn("小车 {} 没有仓库编码", carInfo.getCode());
            return;
        }
        
        // 构建消息
        JSONObject message = buildCarPositionMessage(carInfo);
        String msg = message.toJSONString();
        
        // 推送给所有订阅了该仓库的用户
        int pushCount = 0;
        for (WebSocketSession ws : sessions.values()) {
            if (wareCode.equals(ws.getWareCode()) && ws.getSession().isOpen()) {
                try {
                    // 可选：进一步按楼层过滤
                    // Integer carFloor = getFloorFromZ(carInfo.getCurrentZ());
                    // if (ws.getCurrentFloor() != null && !ws.getCurrentFloor().equals(carFloor)) {
                    //     continue;
                    // }
                    
                    ws.getSession().getBasicRemote().sendText(msg);
                    pushCount++;
                    log.debug("推送小车位置到用户: {}", ws.getUserName());
                } catch (IOException e) {
                    log.error("推送失败: 用户={}, Error={}", ws.getUserName(), e.getMessage());
                }
            }
        }
        
        if (pushCount > 0) {
            log.debug("小车 {} 位置更新推送完成，推送给 {} 个用户", carInfo.getCode(), pushCount);
        }
    }
    
    /**
     * 推送初始小车位置（用户订阅时）
     */
    private void pushInitialCarPositions(Session session, String wareCode, Integer floor) {
        if (rcsCarInfoService == null) {
            log.error("RcsCarInfoService未注入");
            return;
        }
        
        try {
            // 查询该仓库的所有小车（可选：按楼层过滤）
            // List<RcsCarInfo> cars = rcsCarInfoService.findByWareCode(wareCode);
            // 这里简化处理，查询所有小车
            List<RcsCarInfo> cars = rcsCarInfoService.findAll();
            
            int pushCount = 0;
            for (RcsCarInfo car : cars) {
                // 过滤：只推送有位置数据的小车
                if (car.getCurrentX() != null && car.getCurrentY() != null) {
                    JSONObject message = buildCarPositionMessage(car);
                    session.getBasicRemote().sendText(message.toJSONString());
                    pushCount++;
                }
            }
            
            log.info("推送初始小车位置: {} 辆", pushCount);
        } catch (Exception e) {
            log.error("推送初始小车位置失败", e);
        }
    }
    
    /**
     * 构建小车位置消息
     */
    private static JSONObject buildCarPositionMessage(RcsCarInfo carInfo) {
        JSONObject message = new JSONObject();
        message.put("type", "carPosition");
        message.put("carCode", carInfo.getCode());
        message.put("carName", carInfo.getName());
        message.put("x", carInfo.getCurrentX());
        message.put("y", carInfo.getCurrentY());
        message.put("z", carInfo.getCurrentZ());
        message.put("fromCellCode", carInfo.getFromCellCode());
        message.put("toCellCode", carInfo.getToCellCode());
        message.put("positionRatio", carInfo.getPositionRatio());
        message.put("direction", carInfo.getMoveDirection());
        message.put("speed", carInfo.getSpeed());
        message.put("batteryLevel", carInfo.getBatteryLevel());
        message.put("taskState", carInfo.getTaskState());
        message.put("isConnected", carInfo.getIsConnected());
        message.put("isCharge", carInfo.getIsCharge());  // ✨ 充电状态
        message.put("loadState", carInfo.getLoadState());  // ✨ 负载状态
        message.put("timestamp", System.currentTimeMillis());
        return message;
    }
    
    /**
     * 心跳响应
     */
    private void sendHeartbeatResponse(Session session) {
        JSONObject response = new JSONObject();
        response.put("type", "heartbeat");
        response.put("timestamp", System.currentTimeMillis());
        
        try {
            session.getBasicRemote().sendText(response.toJSONString());
            log.debug("发送心跳响应");
        } catch (IOException e) {
            log.error("发送心跳响应失败", e);
        }
    }
    
    /**
     * 发送错误消息
     */
    private void sendErrorMessage(Session session, String code, String message) {
        JSONObject error = new JSONObject();
        error.put("type", "error");
        error.put("code", code);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        
        try {
            session.getBasicRemote().sendText(error.toJSONString());
        } catch (IOException e) {
            log.error("发送错误消息失败", e);
        }
    }
    
    /**
     * 获取用户ID（从Session参数或JWT Token）
     */
    private String getUserIdFromSession(Session session) {
        try {
            Map<String, List<String>> params = session.getRequestParameterMap();
            if (params.containsKey("userId")) {
                return params.get("userId").get(0);
            }
            
            // 方案2：从Token解析（需要引入JWT工具类）
            // if (params.containsKey("token")) {
            //     String token = params.get("token").get(0);
            //     return JwtUtils.getUserId(token);
            // }
        } catch (Exception e) {
            log.warn("获取用户ID失败", e);
        }
        
        return "anonymous_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * 获取用户名
     */
    private String getUserNameFromSession(Session session) {
        try {
            Map<String, List<String>> params = session.getRequestParameterMap();
            if (params.containsKey("userName")) {
                return params.get("userName").get(0);
            }
        } catch (Exception e) {
            log.warn("获取用户名失败", e);
        }
        
        return "匿名用户";
    }
    
    /**
     * 获取在线用户列表
     */
    public static List<Map<String, Object>> getOnlineUsers() {
        return sessions.values().stream()
            .map(ws -> {
                Map<String, Object> info = new HashMap<>();
                info.put("userId", ws.getUserId());
                info.put("userName", ws.getUserName());
                info.put("wareCode", ws.getWareCode());
                info.put("floor", ws.getCurrentFloor());
                info.put("connectTime", ws.getConnectTime());
                info.put("sessionId", ws.getSession().getId());
                return info;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 获取在线用户数
     */
    public static int getOnlineUserCount() {
        return sessions.size();
    }
}

