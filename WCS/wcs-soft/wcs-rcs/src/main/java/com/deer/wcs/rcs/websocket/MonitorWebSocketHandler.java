package com.deer.wcs.rcs.websocket;

import com.alibaba.fastjson2.JSONObject;
import com.deer.wcs.common.constant.WebSocketCacheConstants;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.rcs.model.RcsCarInfo;
import com.deer.wcs.rcs.model.RcsCarPath;
import com.deer.wcs.rcs.service.RcsCarInfoService;
import com.deer.wcs.rcs.service.RcsCarPathService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.PreDestroy;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 监控 WebSocket 服务（Redis标志位优化版）
 * 统一推送：1) 小车位置 2) 路径状态
 * 优化策略：仅在Redis标志位存在时查询并推送，减少数据库和网络开销
 * 
 * @author Deer WCS Team
 * @date 2024-10-18
 */
@ServerEndpoint("/websocket/carPosition")
@Component
public class MonitorWebSocketHandler {
    
    private static final Logger log = LoggerFactory.getLogger(MonitorWebSocketHandler.class);
    
    // 会话管理
    private static Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    // 定时推送调度器（每个Session独立）
    private static Map<String, ScheduledFuture<?>> monitorSchedulers = new ConcurrentHashMap<>();
    
    // 全局调度器（共享线程池）
    private static ScheduledExecutorService scheduledExecutor = 
        Executors.newScheduledThreadPool(2, new ThreadFactory() {
            private int counter = 0;
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "WebSocket-Monitor-" + counter++);
            }
        });
    
    // 注入Service
    private static RcsCarInfoService rcsCarInfoService;
    private static RcsCarPathService rcsCarPathService;
    private static RedisCache redisCache;
    
    @Autowired
    public void setRcsCarInfoService(RcsCarInfoService rcsCarInfoService) {
        MonitorWebSocketHandler.rcsCarInfoService = rcsCarInfoService;
    }
    
    @Autowired
    public void setRcsCarPathService(RcsCarPathService rcsCarPathService) {
        MonitorWebSocketHandler.rcsCarPathService = rcsCarPathService;
    }
    
    @Autowired
    public void setRedisCache(RedisCache redisCache) {
        MonitorWebSocketHandler.redisCache = redisCache;
    }
    
    /**
     * WebSocket会话上下文
     */
    @Data
    public static class WebSocketSession {
        private Session session;
        private String userId;
        private String userName;
        private String wareCode;
        private Integer currentFloor;
        private Date connectTime;
        
        // 缓存上次推送的数据hash，用于增量推送
        private String lastCarDataHash;
        private String lastPathDataHash;
    }
    
    @OnOpen
    public void onOpen(Session session) {
        String userId = getUserIdFromSession(session);
        String userName = getUserNameFromSession(session);
        
        WebSocketSession wsSession = new WebSocketSession();
        wsSession.setSession(session);
        wsSession.setUserId(userId);
        wsSession.setUserName(userName);
        wsSession.setConnectTime(new Date());
        
        sessions.put(session.getId(), wsSession);
        
        log.info("✓ WebSocket连接建立: 用户={}, SessionID={}", userName, session.getId());
        log.info("当前在线用户数: {}", sessions.size());
    }
    
    @OnClose
    public void onClose(Session session) {
        String sessionId = session.getId();
        
        // 停止该Session的定时任务
        stopMonitorTask(sessionId);
        
        // 移除会话
        WebSocketSession wsSession = sessions.remove(sessionId);
        if (wsSession != null) {
            log.info("✓ WebSocket连接关闭: 用户={}, SessionID={}", 
                     wsSession.getUserName(), sessionId);
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
            
            switch (type) {
                case "subscribe":
                    // 订阅仓库
                    String wareCode = msg.getString("wareCode");
                    Integer floor = msg.getInteger("floor");
                    wsSession.setWareCode(wareCode);
                    wsSession.setCurrentFloor(floor);
                    log.info("用户 {} 订阅仓库: {}, 楼层: {}", 
                             wsSession.getUserName(), wareCode, floor);
                    
                    // 启动监控任务
                    startMonitorTask(session.getId(), wsSession);
                    break;
                    
                case "changeFloor":
                    // 切换楼层
                    Integer newFloor = msg.getInteger("floor");
                    wsSession.setCurrentFloor(newFloor);
                    wsSession.setLastCarDataHash(null);  // 清空缓存
                    wsSession.setLastPathDataHash(null);
                    log.info("用户 {} 切换楼层: {}", wsSession.getUserName(), newFloor);
                    
                    // 重启监控任务
                    stopMonitorTask(session.getId());
                    startMonitorTask(session.getId(), wsSession);
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
     * 启动监控任务（每1秒检查Redis标志位并推送）
     */
    private void startMonitorTask(String sessionId, WebSocketSession wsSession) {
        ScheduledFuture<?> future = scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                if (!wsSession.getSession().isOpen()) {
                    stopMonitorTask(sessionId);
                    return;
                }
                
                // 检查并推送监控数据
                checkAndPushMonitorData(wsSession);
                
            } catch (Exception e) {
                log.error("监控任务执行失败: sessionId={}", sessionId, e);
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        monitorSchedulers.put(sessionId, future);
        
        log.info("✓ 启动监控任务: sessionId={}, 仓库={}, 楼层={}", 
                 sessionId, wsSession.getWareCode(), wsSession.getCurrentFloor());
    }
    
    /**
     * 停止监控任务
     */
    private void stopMonitorTask(String sessionId) {
        ScheduledFuture<?> future = monitorSchedulers.remove(sessionId);
        if (future != null) {
            future.cancel(false);
            log.info("🛑 停止监控任务: sessionId={}", sessionId);
        }
    }
    
    /**
     * 检查Redis标志位并推送监控数据
     */
    private void checkAndPushMonitorData(WebSocketSession wsSession) {
        String wareCode = wsSession.getWareCode();
        Integer floor = wsSession.getCurrentFloor();
        
        if (wareCode == null || floor == null) {
            return;
        }
        
        // 1. 检查小车位置更新标志
//        String carFlagKey = WebSocketCacheConstants.buildCarUpdateFlagKey(wareCode, floor);
//        boolean carNeedUpdate = redisCache.hasKey(carFlagKey);
//
//        log.debug("检查小车更新标志: key={}, exists={}", carFlagKey, carNeedUpdate);
//
//        if (carNeedUpdate) {
//            pushCarPositions(wsSession);
//            // 推送完成后删除标志位
//            redisCache.deleteObject(carFlagKey);
//            log.info("✓ 小车位置已推送并清除标志: {}", carFlagKey);
//        }
        
        // 2. 检查路径状态更新标志
        String pathFlagKey = WebSocketCacheConstants.buildPathUpdateFlagKey(wareCode, floor);
        boolean pathNeedUpdate = redisCache.hasKey(pathFlagKey);
        
        if (pathNeedUpdate) {
            pushPathStatus(wsSession);
            // 推送完成后删除标志位
            redisCache.deleteObject(pathFlagKey);
            log.debug("✓ 路径状态已推送并清除标志: {}", pathFlagKey);
        }
        
        // 注意：如果两个标志位都不存在，本次不查询数据库，不推送，节省资源
    }
    
    /**
     * 推送小车位置
     */
    private void pushCarPositions(WebSocketSession wsSession) {
        if (rcsCarInfoService == null) {
            return;
        }
        
        String wareCode = wsSession.getWareCode();
        Integer floor = wsSession.getCurrentFloor();
        
        try {
            // 查询该仓库楼层的所有小车
            Condition condition = new Condition(RcsCarInfo.class);
            condition.createCriteria()
                .andEqualTo("wareCode", wareCode)
                .andEqualTo("z", floor);  // 按楼层过滤
            
            List<RcsCarInfo> cars = rcsCarInfoService.findByCondition(condition);
            
            if (cars == null || cars.isEmpty()) {
                log.debug("该仓库楼层暂无小车: wareCode={}, floor={}", wareCode, floor);
                return;
            }
            
            // 计算数据Hash，判断是否有变化（增量推送优化）
            String currentHash = calculateDataHash(cars);
            if (currentHash.equals(wsSession.getLastCarDataHash())) {
                log.debug("小车位置数据无变化，跳过推送");
                return;
            }
            
            // 批量推送小车位置
            int pushCount = 0;
            for (RcsCarInfo car : cars) {
                JSONObject message = buildCarPositionMessage(car);
                wsSession.getSession().getBasicRemote().sendText(message.toJSONString());
                pushCount++;
            }
            
            // 更新缓存Hash
            wsSession.setLastCarDataHash(currentHash);
            
            log.info("✓ 推送小车位置: {} 辆, 仓库={}, 楼层={}", 
                     pushCount, wareCode, floor);
            
        } catch (Exception e) {
            log.error("推送小车位置失败", e);
        }
    }
    
    /**
     * 推送路径状态
     */
    private void pushPathStatus(WebSocketSession wsSession) {
        if (rcsCarPathService == null) {
            return;
        }
        
        String wareCode = wsSession.getWareCode();
        Integer floor = wsSession.getCurrentFloor();
        
        try {
            // 查询该仓库楼层的路径状态
            Condition condition = new Condition(RcsCarPath.class);
            condition.createCriteria()
                .andEqualTo("wareCode", wareCode)
                .andEqualTo("z", floor);
            
            List<RcsCarPath> paths = rcsCarPathService.findByCondition(condition);
            
            if (paths == null) {
                paths = new ArrayList<>();
            }
            
            // 计算数据Hash，判断是否有变化
            String currentHash = calculateDataHash(paths);
            if (currentHash.equals(wsSession.getLastPathDataHash())) {
                log.debug("路径状态数据无变化，跳过推送");
                return;
            }
            
            // 构建推送消息
            JSONObject message = new JSONObject();
            message.put("type", "pathStatus");
            message.put("data", paths);
            message.put("timestamp", System.currentTimeMillis());
            
            // 推送
            wsSession.getSession().getBasicRemote().sendText(message.toJSONString());
            
            // 更新缓存Hash
            wsSession.setLastPathDataHash(currentHash);
            
            log.info("✓ 推送路径状态: {} 条, 仓库={}, 楼层={}", 
                     paths.size(), wareCode, floor);
            
        } catch (Exception e) {
            log.error("推送路径状态失败", e);
        }
    }
    
    /**
     * 计算数据Hash（用于增量推送判断）
     */
    private String calculateDataHash(List<?> data) {
        if (data == null || data.isEmpty()) {
            return "EMPTY";
        }
        
        // 简单Hash：数据个数 + 第一个和最后一个对象的hashCode
        int hash = data.size();
        hash = 31 * hash + data.get(0).hashCode();
        if (data.size() > 1) {
            hash = 31 * hash + data.get(data.size() - 1).hashCode();
        }
        
        return String.valueOf(hash);
    }
    
    /**
     * 构建小车位置消息
     */
    private static JSONObject buildCarPositionMessage(RcsCarInfo carInfo) {
        JSONObject message = new JSONObject();
        message.put("type", "carPosition");
        message.put("carCode", carInfo.getCode());
        message.put("carName", carInfo.getName());
        // 坐标信息（原始坐标）
        message.put("x", carInfo.getCurrentX());
        message.put("y", carInfo.getCurrentY());
        message.put("z", carInfo.getCurrentZ());
        // 库位信息（计算后的位置）
        message.put("fromCellCode", carInfo.getFromCellCode());
        message.put("toCellCode", carInfo.getToCellCode());
        message.put("positionRatio", carInfo.getPositionRatio());
        // 其他状态信息
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
     * 获取用户ID（从Session参数）
     */
    private String getUserIdFromSession(Session session) {
        try {
            Map<String, List<String>> params = session.getRequestParameterMap();
            if (params.containsKey("userId")) {
                return params.get("userId").get(0);
            }
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
     * 外部调用：立即推送单个小车位置（保留兼容性）
     * 用于紧急事件驱动场景
     */
    public static void pushCarPosition(RcsCarInfo carInfo) {
        log.info("📡 开始推送小车位置: carCode={}, wareCode={}", carInfo.getCode(), carInfo.getWareCode());
        
        if (sessions.isEmpty()) {
            log.warn("⚠️ 没有活跃的WebSocket连接，无法推送");
            return;
        }
        
        log.info("当前活跃WebSocket连接数: {}", sessions.size());
        
        String wareCode = carInfo.getWareCode();
        if (wareCode == null || wareCode.trim().isEmpty()) {
            log.warn("⚠️ 小车 {} 的wareCode为空，无法推送", carInfo.getCode());
            return;
        }
        
        // 打印所有会话的订阅信息
        for (WebSocketSession ws : sessions.values()) {
            log.info("会话信息: 用户={}, 订阅仓库={}, 楼层={}, 连接状态={}", 
                    ws.getUserName(), 
                    ws.getWareCode(), 
                    ws.getCurrentFloor(),
                    ws.getSession().isOpen());
        }
        
        JSONObject message = buildCarPositionMessage(carInfo);
        String msg = message.toJSONString();
        
        log.info("推送消息内容: {}", msg);
        
        int pushCount = 0;
        for (WebSocketSession ws : sessions.values()) {
            if (wareCode.equals(ws.getWareCode()) && ws.getSession().isOpen()) {
                try {
                    ws.getSession().getBasicRemote().sendText(msg);
                    pushCount++;
                    log.info("✓ 成功推送给用户: {}", ws.getUserName());
                } catch (IOException e) {
                    log.error("❌ 推送失败: 用户={}, 错误={}", ws.getUserName(), e.getMessage());
                }
            } else {
                if (!wareCode.equals(ws.getWareCode())) {
                    log.debug("跳过推送: 用户={}, 原因=仓库不匹配 (小车仓库={}, 订阅仓库={})", 
                            ws.getUserName(), wareCode, ws.getWareCode());
                } else if (!ws.getSession().isOpen()) {
                    log.debug("跳过推送: 用户={}, 原因=连接已关闭", ws.getUserName());
                }
            }
        }
        
        if (pushCount > 0) {
            log.info("✅ 事件驱动推送完成: 小车={}, 推送给{}个用户", carInfo.getCode(), pushCount);
        } else {
            log.warn("⚠️ 没有用户接收到推送: 小车={}, wareCode={}", carInfo.getCode(), wareCode);
        }
    }
    
    /**
     * 获取在线用户数
     */
    public static int getOnlineUserCount() {
        return sessions.size();
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
     * 应用关闭时清理资源（修复线程泄漏）
     */
    @PreDestroy
    public void shutdown() {
        log.info("🔄 WebSocket服务正在关闭，清理资源...");
        
        try {
            // 1. 停止所有Session的定时任务
            for (String sessionId : new ArrayList<>(monitorSchedulers.keySet())) {
                stopMonitorTask(sessionId);
            }
            
            // 2. 关闭所有WebSocket连接
            for (WebSocketSession wsSession : new ArrayList<>(sessions.values())) {
                try {
                    if (wsSession.getSession().isOpen()) {
                        wsSession.getSession().close(new CloseReason(
                            CloseReason.CloseCodes.GOING_AWAY, 
                            "服务器正在关闭"
                        ));
                    }
                } catch (Exception e) {
                    log.error("关闭WebSocket连接失败: sessionId={}", 
                             wsSession.getSession().getId(), e);
                }
            }
            
            // 3. 优雅关闭线程池
            scheduledExecutor.shutdown();
            try {
                // 等待30秒让任务完成
                if (!scheduledExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                    // 超时后强制关闭
                    log.warn("定时任务未在30秒内完成，强制关闭");
                    scheduledExecutor.shutdownNow();
                    
                    // 再等待5秒
                    if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                        log.error("线程池无法完全关闭");
                    }
                }
            } catch (InterruptedException e) {
                log.error("等待线程池关闭时被中断", e);
                scheduledExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            
            // 4. 清空集合
            sessions.clear();
            monitorSchedulers.clear();
            
            log.info("✅ WebSocket服务资源清理完成");
            
        } catch (Exception e) {
            log.error("❌ WebSocket服务资源清理失败", e);
        }
    }
}

