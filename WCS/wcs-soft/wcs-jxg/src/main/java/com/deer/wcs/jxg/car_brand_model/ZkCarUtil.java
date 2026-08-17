package com.deer.wcs.jxg.car_brand_model;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.rcs.model.RcsCarInfo;
import com.deer.wcs.rcs.service.RcsCarInfoService;
import com.deer.wcs.task.model.DeviceTaskResult;
import com.deer.wcs.task.service.DeviceTaskResultService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.deer.wcs.jxg.car_brand_model.ZkMessageType.*;

@Component("zkCarUtil")
public class ZkCarUtil {

    @Autowired
    private RcsCarInfoService rcsCarInfoService;

    @Autowired
    private CellInfoService cellInfoService;

    @Autowired
    private DeviceTaskResultService deviceTaskResultService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ZkCarRedisUtil zkCarRedisUtil;

    private RcsTcpServer server;
    
    private ScheduledExecutorService syncScheduler;

    private static final Logger log = LoggerFactory.getLogger(ZkCarUtil.class);
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // 内存Map：存储每个小车最后处理时间（用于频率限制）
    private final Map<Integer, Long> lastProcessTimeMap = new java.util.concurrent.ConcurrentHashMap<>();
    
    // 状态数据处理间隔（毫秒）- 每个小车1秒处理一次
    private static final long STATE_PROCESS_INTERVAL_MS = 1000;

    /**
     * Spring容器启动后自动执行
     */
    @PostConstruct
    public void autoRun(){
        List<RcsCarInfo> list = rcsCarInfoService.findAll();
        for(RcsCarInfo carInfo : list){
            carInfo.setIsConnected(0);
            rcsCarInfoService.update(carInfo);
        }
        log.info("ZkCarUtil 开始自动启动...");
        // 初始化服务器
        server = new RcsTcpServer(852);
        // 设置RcsCarInfoService，用于查询robotId
        server.setRcsCarInfoService(rcsCarInfoService);
        // 启动服务
        run();

    }

    /**
     * 同步发送消息（用于发送请求并等待响应）
     */
    public String sendMessageSync(String ip, ZkDeviceMessage deviceMessage) {
        try {
            String response = server.sendMessageSync(ip, deviceMessage, 5000);
            log.info("同步发送消息成功，响应: {}", response);
            return response;
        } catch (Exception ex) {
            log.error("同步发送消息失败", ex);
            return null;
        }
    }
    
    /**
     * 异步发送消息（用于发送响应消息，不等待回复）
     */
    public void sendMessageAsync(String ip, ZkDeviceMessage deviceMessage) {
        try {
            String messageJson = ZkDeviceMessage.ZkMessageUtil.toJson(deviceMessage);
            server.sendMessageAsync(ip, messageJson);
            log.info("异步发送消息成功: {}", messageJson);
        } catch (Exception ex) {
            log.error("异步发送消息失败", ex);
        }
    }

    private void run() {

        // 设置连接回调
        server.setOnClientConnected(clientInfo -> {
            log.info("🎉 客户端连接: " + clientInfo);
            RcsCarInfo carInfo = rcsCarInfoService.findBy("ip", clientInfo.getRemoteAddress());
            if(carInfo!=null){
                carInfo.setIsConnected(1);
                rcsCarInfoService.update(carInfo);
            }

        });

        // 设置断开回调
        server.setOnClientDisconnected((clientInfo, reason) -> {
            long connectionDuration = System.currentTimeMillis() - clientInfo.getConnectTime();
            
            // 如果是短时连接（小于30秒），可能是客户端定时重连，给出警告提示
            if (connectionDuration < 30000) {
                log.warn("⚠️ 客户端短时断开: " + clientInfo + ", 原因: " + reason + 
                        " [连接时长仅{}ms，建议检查客户端是否有定时重连机制]", connectionDuration);
                // 短时断开暂不更新数据库连接状态，因为客户端通常会立即重连
                // 避免频繁更新数据库和触发不必要的业务逻辑
                return;
            }
            
            log.info("❌ 客户端断开: " + clientInfo + ", 原因: " + reason);
            RcsCarInfo carInfo = rcsCarInfoService.findBy("ip", clientInfo.getRemoteAddress());
            if(carInfo!=null){
                carInfo.setIsConnected(0);
                rcsCarInfoService.update(carInfo);
            }

        });



        // 设置消息接收回调
        server.setOnClientMessageReceived((clientInfo, message) -> {
           // log.info("📨 收到消息 [" + clientInfo.getClientId() + "]: " + message);
            try {
                String ip = clientInfo.getRemoteAddress();
                ZkDeviceMessage zkDeviceMessage = ZkDeviceMessage.ZkMessageUtil.fromJson(message);
                
                // 检查消息是否为空
                if (zkDeviceMessage == null) {
                    log.error("消息解析失败，zkDeviceMessage 为 null");
                    return;
                }
                
                // 处理消息
                handleMessage(ip, zkDeviceMessage);
            } catch (Exception e) {
                log.error("处理客户端消息异常: " + e.getMessage(), e);
            }
        });

        // 在单独线程中启动服务器
        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

    }

    private   void handleMessage(String ip,ZkDeviceMessage zkDeviceMessage ) {
        switch (zkDeviceMessage.getMsgType()) {
//            case CMD_RES_MSG:
//                // 处理指令响应消息
//              //  handleCmdResponse(zkDeviceMessage, carInfo);
//                break;
            //事件上报
            case EVENT_SEND_MSG:
                // 处理事件响应消息
                handleEvent(ip,zkDeviceMessage );
                break;
            //遥测数据上报
            case STATE_MSG:
                // 处理状态消息
                handleStateUpdate(ip,zkDeviceMessage);
                break;
            //心跳消息（客户端发送的心跳）
            case PING_MSG:
                // 根据协议规范，心跳消息无需应答，只需更新心跳时间即可
                // 服务器端的RcsTcpServer已经在收到任何消息时自动更新lastHeartbeatTime
                log.debug("收到客户端心跳: IP={}", ip);
                break;

            default:
                log.warn("未知消息类型: " + zkDeviceMessage.getMsgType());
        }
    }
    private void handleEvent(String ip,ZkDeviceMessage zkDeviceMessage ) {
        Object bodyObj = zkDeviceMessage.getRequest().getBody();
        if (!(bodyObj instanceof Map)) {
            log.error("消息体类型错误，期望Map但实际类型: {}", bodyObj != null ? bodyObj.getClass().getName() : "null");
            return;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> bodyData = (Map<String, Object>) bodyObj;
        String eventType = (String) bodyData.get("eventType");

        if("INIT".equals(eventType)){
            // 处理初始化事件
            handleInitEvent(ip,zkDeviceMessage);
        }
        else if("TASK_PHASE".equals(eventType)){
            // 处理任务阶段事件
            handleTaskPhaseEvent(ip,zkDeviceMessage);
        }


        log.info("处理事件响应消息: " + zkDeviceMessage);
    }

    /**
     * 处理初始化事件
     * 小车上报初始化位置（qrCode）
     */
    private void handleInitEvent(String ip,ZkDeviceMessage zkDeviceMessage) {
        try {
            // 1、解析消息
            Integer robotId = zkDeviceMessage.getRobotId();
            Object bodyObj = zkDeviceMessage.getRequest().getBody();
            if (!(bodyObj instanceof Map)) {
                log.error("消息体类型错误，期望Map但实际类型: {}", bodyObj != null ? bodyObj.getClass().getName() : "null");
                sendErrorResponse(ip, zkDeviceMessage, "消息格式错误");
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> bodyData = (Map<String, Object>) bodyObj;
            String qrCode = (String) bodyData.get("qrCode");
            Long requestId = zkDeviceMessage.getRequest().getHeader().getRequestId();

            log.info("收到小车初始化事件，robotId: {}, qrCode: {}", robotId, qrCode);

            // 2、查询小车信息，如果不存在则创建
            RcsCarInfo carInfo = rcsCarInfoService.findBy("code", robotId.toString());
            
            if (carInfo == null) {
                log.info("未找到小车信息，创建新小车记录，robotId: {}", robotId);
                carInfo = createNewCar(robotId, qrCode);
                if (carInfo == null) {
                    log.error("创建小车失败，robotId: {}", robotId);
                    sendErrorResponse(ip,zkDeviceMessage, "创建小车失败");
                    return;
                }
            }
            CellInfo cellInfo = new CellInfo();
            // 3、根据qrCode（subCode）查找库位并更新小车位置
            if (qrCode != null && !qrCode.isEmpty()) {
                cellInfo = findCellBySubCode(qrCode, carInfo.getWareCode());
                if (cellInfo != null) {
                    carInfo.setFromCellCode(cellInfo.getCode()); // 设置当前位置为库位编码
                    carInfo.setToCellCode(cellInfo.getCode()); // 设置当前位置为库位编码
                    carInfo.setZ(cellInfo.getZ()); // 设置当前位置为库位编码
                    rcsCarInfoService.update(carInfo);
                    log.info("更新小车位置成功，小车ID: {}, qrCode: {}, 库位: {}", 
                            carInfo.getId(), qrCode, cellInfo.getCode());
                } else {
                    log.warn("未找到对应的库位，qrCode: {}", qrCode);
                }
            }
            if(carInfo.getIp()==null || carInfo.getIp().trim().equals("")){
                carInfo.setIp(ip);
                rcsCarInfoService.update(carInfo);
            }


            // 4、先发送事件响应消息（EventResponseMsg）
            sendSuccessResponse(ip, zkDeviceMessage);

            /**
             * "msgType":"InstructionRequestMsg",
             *  "robotId":500001,
             *  "request":{
             *  "header":{
             *  "requestId":112236,
             *  "version":"2.0.0",
             *  "extParam":"1556"
             *  },
             *  "body":{
             *  "operationType":"DATA",
             *  "operationCode":"LOCATION",
             *  "location":{
             *  "x":11002,
             *  "y":12567,
             *  "z":1
             *  },
             *  "devicePoints":[
             *  {
             *  "x":11002,
             *  "y":12567,
             *  "z":0
             *  },
             *  {
             *  "x":11002,
             *  "y":12567,
             *  "z":0
             *  },
             *  {
             *  "x":11002,
             *  "y":12567,
             *  "z":0
             *  }
             *  ]
             *  }
             *  }
             */
            // 5、发送位置指令消息（InstructionRequestMsg）


            /**
             * 除了发送小车位置，还应该发送提升机位置 通过 devicePoints
             * 提升机去cell_info表查询 type=5
             * 请你先查看小车的通信协议
             */

            Map<String, Object> location = new HashMap<>();
            // 如果cellInfo不为null且有坐标信息，则使用cellInfo的坐标
            if (cellInfo != null && cellInfo.getSubX() != null && cellInfo.getSubY() != null) {
                location.put("x", cellInfo.getSubX());
                location.put("y", cellInfo.getSubY());
                location.put("z", cellInfo.getSubZ() != null ? cellInfo.getSubZ() : 0);
            } else {
                // 如果cellInfo为null，使用默认值或小车当前位置
                location.put("x", 0);
                location.put("y", 0);
                location.put("z", 0);
            }

            // 查询提升机位置（type=5）并构建 devicePoints 数组
            List<Map<String, Object>> devicePoints = queryElevatorPositions(carInfo.getWareCode());

            bodyData = new HashMap<>();
            bodyData.put("operationType", "DATA");
            bodyData.put("operationCode", "LOCATION");
            bodyData.put("location", location);
            // 添加提升机位置到 devicePoints
            if (devicePoints != null && !devicePoints.isEmpty()) {
                bodyData.put("devicePoints", devicePoints);
                log.info("添加提升机位置到devicePoints，数量: {}", devicePoints.size());
            } else {
                log.warn("未找到提升机位置（type=5），wareCode: {}", carInfo.getWareCode());
            }
            
            requestId = System.currentTimeMillis();
            ZkDeviceMessage zkMessage = ZkDeviceMessage.ZkMessageUtil.createRequest(
                    "InstructionRequestMsg",
                    Integer.parseInt(carInfo.getCode()) ,
                    requestId,
                    "2.0.0",
                    bodyData
            );

            /**
             *
             */


            sendMessageAsync(ip,zkMessage);

        } catch (Exception e) {
            log.error("处理初始化事件失败", e);
            sendErrorResponse(ip,zkDeviceMessage, "处理初始化事件失败: " + e.getMessage());
        }
    }

    /**
     * 处理任务阶段事件
     * 小车上报任务执行阶段变化
     */
    private void handleTaskPhaseEvent(String ip,ZkDeviceMessage zkDeviceMessage) {
        try {
            // 1、解析消息
            Integer robotId = zkDeviceMessage.getRobotId();
            Object bodyObj = zkDeviceMessage.getRequest().getBody();
            if (!(bodyObj instanceof Map)) {
                log.error("消息体类型错误，期望Map但实际类型: {}", bodyObj != null ? bodyObj.getClass().getName() : "null");
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> bodyData = (Map<String, Object>) bodyObj;
            Integer taskId = (Integer) bodyData.get("taskId"); //服务端调度指令下发的taskId
            Integer taskState = (Integer) bodyData.get("taskState");//任务中：0; 完成：1; 异常：2
            Integer pathState = (Integer) bodyData.get("pathState");//路径中：0; 完成：1
            Integer statusCode = (Integer) bodyData.get("statusCode");//正常：0； 异常 非零
            System.out.println("✅ 收到任务阶段事件 [" + robotId + "]: " + robotId + " taskId: " + taskId + " taskState: " + taskState + " pathState: " + pathState + " statusCode: " + statusCode);


            // 2、查询小车信息
            RcsCarInfo carInfo = rcsCarInfoService.findBy("code", robotId.toString());
//
//            if (carInfo == null) {
//                log.error("未找到小车信息，robotId: {}", robotId);
//                sendErrorResponse(ip,zkDeviceMessage, "未找到小车信息");
//                return;
//            }
//
//            // 3、根据任务状态更新小车状态
//            if (taskState != null) {
//                if (taskState == 0) {
//                    // 任务中
//                    log.info("小车任务执行中，robotId: {}, taskId: {}", robotId, taskId);
//                    carInfo.setTaskState(1L); // 设置为执行中
//                } else if (taskState == 1) {
//                    // 任务完成
//                    log.info("小车任务完成，robotId: {}, taskId: {}", robotId, taskId);
//                    carInfo.setTaskState(0L); // 设置为空闲
//                } else if (taskState == 2) {
//                    // 任务异常
//                    log.error("小车任务异常，robotId: {}, taskId: {}, statusCode: {}", robotId, taskId, statusCode);
//                    carInfo.setTaskState(-2L); // 设置为故障
//                }
//            }

            // 4、更新小车状态到数据库
            rcsCarInfoService.update(carInfo);

            // 5、保存任务上报数据到 device_task_result 表
            saveTaskResult(carInfo, taskId, taskState, pathState, statusCode, bodyData);

            // 6、发送成功响应
            sendSuccessResponse(ip,zkDeviceMessage);

        } catch (Exception e) {
            log.error("处理任务阶段事件失败", e);
            sendErrorResponse(ip,zkDeviceMessage, "处理任务阶段事件失败: " + e.getMessage());
        }
    }

    /**
     * 处理状态数据事件
     * 小车定期上报状态信息（电量、速度、位置等）
     */
    private void handleStateDataEvent(String ip,ZkDeviceMessage zkDeviceMessage) {
        try {
            // 1、解析消息
            Integer robotId = zkDeviceMessage.getRobotId();
            Object bodyObj = zkDeviceMessage.getRequest().getBody();
            if (!(bodyObj instanceof Map)) {
                log.error("消息体类型错误，期望Map但实际类型: {}", bodyObj != null ? bodyObj.getClass().getName() : "null");
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> bodyData = (Map<String, Object>) bodyObj;
            
            // 提取状态数据
            Object batteryObj = bodyData.get("battery");
            Object speedObj = bodyData.get("speed");
            String qrCode = (String) bodyData.get("qrCode");
            String statusStr = (String) bodyData.get("status");

            log.info("收到状态数据事件，robotId: {}, battery: {}, speed: {}, qrCode: {}, status: {}", 
                    robotId, batteryObj, speedObj, qrCode, statusStr);

            // 2、查询小车信息
            RcsCarInfo carInfo = rcsCarInfoService.findBy("code", robotId);

            if (carInfo == null) {
                log.info("未找到小车信息，创建新小车记录，robotId: {}", robotId);
                carInfo = createNewCar(robotId, qrCode);
                if (carInfo == null) {
                    log.error("创建小车失败，robotId: {}", robotId);
                    return;
                }
            }

            // 3、更新小车状态数据
            if (batteryObj != null) {
                Integer battery = batteryObj instanceof Integer ? (Integer) batteryObj : 
                                 Integer.parseInt(batteryObj.toString());
                carInfo.setBatteryLevel(battery);
            }

            if (speedObj != null) {
                java.math.BigDecimal speed;
                if (speedObj instanceof java.math.BigDecimal) {
                    speed = (java.math.BigDecimal) speedObj;
                } else if (speedObj instanceof Integer) {
                    speed = new java.math.BigDecimal((Integer) speedObj);
                } else {
                    speed = new java.math.BigDecimal(speedObj.toString());
                }
                carInfo.setSpeed(speed);
            }

            if (qrCode != null && !qrCode.isEmpty()) {
                CellInfo cellInfo = findCellBySubCode(qrCode, carInfo.getWareCode());
                if (cellInfo != null) {
                    carInfo.setFromCellCode(cellInfo.getCode());
                } else {
                    log.warn("未找到对应的库位，qrCode: {}", qrCode);
                }
            }

            if (statusStr != null) {
                // 根据状态字符串设置小车状态
                // 例如: IDLE(空闲), RUNNING(运行中), CHARGING(充电中), ERROR(故障)
                if ("IDLE".equals(statusStr)) {
                    carInfo.setTaskState(0L); // 空闲
                } else if ("RUNNING".equals(statusStr)) {
                    // 保持当前任务状态
                } else if ("CHARGING".equals(statusStr)) {
                    // 充电中
                    carInfo.setTaskState(-1L); // 充电
                } else if ("ERROR".equals(statusStr)) {
                    // 故障
                    carInfo.setTaskState(-2L); // 故障
                }
            }

            // 4、更新数据库
            rcsCarInfoService.update(carInfo);

            // 5、发送成功响应
             sendSuccessResponse(ip,zkDeviceMessage);

        } catch (Exception e) {
            log.error("处理状态数据事件失败", e);
            sendErrorResponse(ip,zkDeviceMessage, "处理状态数据事件失败: " + e.getMessage());
        }
    }

    /**
     * 发送成功响应给小车
     */
    private void sendSuccessResponse(String ip, ZkDeviceMessage requestMessage) {
        try {
            Long requestId = requestMessage.getRequest().getHeader().getRequestId();
            Integer robotId = requestMessage.getRobotId();
            
            ZkDeviceMessage response = ZkDeviceMessage.ZkMessageUtil.createSuccessResponse(
                    "EventResponseMsg",
                    robotId,
                    requestId
            );

            String messageJson = ZkDeviceMessage.ZkMessageUtil.toJson(response);
            log.info("发送成功响应: {}", messageJson);
            
            // 异步发送响应到小车（响应消息不需要等待回复）
            sendMessageAsync(ip, response);

        } catch (Exception e) {
            log.error("发送成功响应失败", e);
        }
    }

    /**
     * 发送错误响应给小车
     */
    private void sendErrorResponse(String ip, ZkDeviceMessage requestMessage, String errorMsg) {
        try {
            Long requestId = requestMessage.getRequest().getHeader().getRequestId();
            Integer robotId = requestMessage.getRobotId();
            
            ZkDeviceMessage response = ZkDeviceMessage.ZkMessageUtil.createErrorResponse(
                    "EventResponseMsg",
                    robotId,
                    requestId,
                    500,
                    errorMsg
            );

            String messageJson = ZkDeviceMessage.ZkMessageUtil.toJson(response);
            log.error("发送错误响应: {}", messageJson);
            
            // 异步发送响应到小车（响应消息不需要等待回复）
            sendMessageAsync(ip, response);

        } catch (Exception e) {
            log.error("发送错误响应失败", e);
        }
    }

    /**
     * 创建新的小车记录
     * @param robotId 机器人ID（设备ID）
     * @param qrCode 当前位置的qrCode
     * @return 创建的小车信息，失败返回 null
     */
    private RcsCarInfo createNewCar(Integer robotId, String qrCode) {
        try {
            RcsCarInfo carInfo = new RcsCarInfo();

            carInfo.setCode(robotId.toString()); // 生成小车编码
            carInfo.setName("智库小车-" + robotId); // 生成小车名称
            
            // 根据 qrCode 查找库位，获取仓库信息
            if (qrCode != null && !qrCode.isEmpty()) {
                CellInfo cellInfo = findCellBySubCode(qrCode, null);
                if (cellInfo != null) {
                    carInfo.setWareCode(cellInfo.getWareCode());
                    carInfo.setFromCellCode(cellInfo.getCode());
                    carInfo.setToCellCode(cellInfo.getCode());
                }
            }
            
            // 设置默认状态
            carInfo.setTaskState(0L); // 空闲
            carInfo.setDisableState(0L); // 未禁用
            carInfo.setIsConnected(1); // 已连接
            carInfo.setBatteryLevel(100); // 默认电量100%
            carInfo.setIsCharge(0); // 未充电
            
            // 设置创建时间
            carInfo.setCreateTime(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new java.util.Date()));
            carInfo.setCreateUserName("系统自动创建");
            
            // 保存到数据库
            rcsCarInfoService.save(carInfo);
            
            log.info("成功创建新小车记录，robotId: {}, 小车ID: {}, 编码: {}", 
                    robotId, carInfo.getId(), carInfo.getCode());
            
            return carInfo;
            
        } catch (Exception e) {
            log.error("创建小车记录失败，robotId: {}", robotId, e);
            return null;
        }
    }

    /**
     * 根据 subCode（qrCode）查找库位信息
     * @param subCode 下位编码（qrCode）
     * @param wareCode 仓库编码
     * @return 库位信息，未找到返回 null
     */
    private CellInfo findCellBySubCode(String subCode, String wareCode) {
        try {
            Condition condition = new Condition(CellInfo.class);
            Condition.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("subCode", subCode);
            
            // 如果提供了仓库编码，则作为条件
            if (wareCode != null && !wareCode.isEmpty()) {
                criteria.andEqualTo("wareCode", wareCode);
            }
            
            List<CellInfo> cellList = cellInfoService.findByCondition(condition);
            
            if (cellList != null && !cellList.isEmpty()) {
                return cellList.get(0);
            } else {
                log.warn("未找到库位信息，subCode: {}, wareCode: {}", subCode, wareCode);
                return null;
            }
        } catch (Exception e) {
            log.error("查询库位信息失败，subCode: {}, wareCode: {}", subCode, wareCode, e);
            return null;
        }
    }

    /**
     * 查询提升机位置（type=5）并构建 devicePoints 数组
     * @param wareCode 仓库编码
     * @return 提升机位置列表，每个元素包含 x, y, z 坐标
     */
    private List<Map<String, Object>> queryElevatorPositions(String wareCode) {
        try {
            if (wareCode == null || wareCode.isEmpty()) {
                log.warn("仓库编码为空，无法查询提升机位置");
                return new ArrayList<>();
            }
            
            Condition condition = new Condition(CellInfo.class);
            Condition.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("wareCode", wareCode);
            criteria.andEqualTo("type", 5); // 提升机类型为5
            criteria.andEqualTo("isDelete", 0); // 未删除
            
            List<CellInfo> elevatorList = cellInfoService.findByCondition(condition);
            
            if (elevatorList == null || elevatorList.isEmpty()) {
                log.debug("未找到提升机位置，wareCode: {}, type: 5", wareCode);
                return new ArrayList<>();
            }
            
            List<Map<String, Object>> devicePoints = new ArrayList<>();
            for (CellInfo elevator : elevatorList) {
                // 只添加有坐标信息的提升机
                if (elevator.getSubX() != null && elevator.getSubY() != null) {
                    Map<String, Object> point = new HashMap<>();
                    point.put("x", elevator.getSubX());
                    point.put("y", elevator.getSubY());
                    point.put("z", elevator.getSubZ() != null ? elevator.getSubZ() : 0);
                    devicePoints.add(point);
                    log.debug("添加提升机位置: code={}, x={}, y={}, z={}", 
                            elevator.getCode(), elevator.getSubX(), elevator.getSubY(), elevator.getSubZ());
                } else {
                    log.warn("提升机位置坐标不完整，code: {}, subX: {}, subY: {}", 
                            elevator.getCode(), elevator.getSubX(), elevator.getSubY());
                }
            }
            
            log.info("查询提升机位置完成，wareCode: {}, 数量: {}", wareCode, devicePoints.size());
            return devicePoints;
            
        } catch (Exception e) {
            log.error("查询提升机位置失败，wareCode: {}", wareCode, e);
            return new ArrayList<>();
        }
    }

    /**
     * 保存任务上报数据到 device_task_result 表
     * @param carInfo 小车信息
     * @param taskId 任务ID
     * @param taskState 任务状态（任务中：0; 完成：1; 异常：2）
     * @param pathState 路径状态（路径中：0; 完成：1）
     * @param statusCode 状态码（正常：0； 异常 非零）
     * @param bodyData 完整的上报数据
     */
    private void saveTaskResult(RcsCarInfo carInfo, Integer taskId, Integer taskState, Integer pathState, Integer statusCode, Map<String, Object> bodyData) {
        try {
            // 创建设备任务回传对象
            DeviceTaskResult taskResult = new DeviceTaskResult();
            
            // 设置任务编码
            String taskCode = taskId != null ? String.valueOf(taskId) : "";
            taskResult.setTaskCode(taskCode);

            taskResult.setState(0); // 任务中，设置为初始化
            
            // 设置当前位置（这里可以从小车当前位置获取）
            //taskResult.setNode(carInfo.getFromCellCode());
            
            // 设置类型（根据 taskState 和 statusCode 确定）
            String type = getTaskStateDescription(taskState, statusCode);
            taskResult.setType(type);
            
            // 设置设备名（小车编码或设备ID）
            taskResult.setFromDevice(carInfo.getCode() != null ? carInfo.getCode() : String.valueOf(carInfo.getDeviceId()));
            
            // 将完整的上报数据转为 JSON 字符串，放到 data 字段
            String dataJson = objectMapper.writeValueAsString(bodyData);
            taskResult.setData(dataJson);
            
            // 保存到数据库
            deviceTaskResultService.save(taskResult);
            
            log.info("任务上报数据已保存到 device_task_result 表，taskCode: {}, taskState: {}, pathState: {}, statusCode: {}, data: {}", 
                    taskCode, taskState, pathState, statusCode, dataJson);
            
        } catch (Exception e) {
            log.error("保存任务上报数据到 device_task_result 表失败", e);
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 处理状态消息
     * 小车主动上报状态信息（比事件响应消息更频繁的状态上报）
     * 优化：将高频状态数据存储到Redis中，避免频繁操作数据库
     * 优化：对比上一次状态数据，只有在数据发生变化时才进行处理和存储
     * 优化：频率限制，每个小车每1秒才处理一次数据，避免高频上报造成系统压力
     * 
     * 根据MD文档协议规范，处理StateRequestMsg中的遥测数据字段
     */
    private void handleStateUpdate(String ip,ZkDeviceMessage zkDeviceMessage) {
        try {
            // 1、解析消息
            Integer robotId = zkDeviceMessage.getRobotId();
            
            // 2、频率限制：每个小车1秒才处理一次，直接return
            long currentTime = System.currentTimeMillis();
            Long lastProcessTime = lastProcessTimeMap.get(robotId);
            
            if (lastProcessTime != null) {
                long timeSinceLastProcess = currentTime - lastProcessTime;
                if (timeSinceLastProcess < STATE_PROCESS_INTERVAL_MS) {
                    // 距离上次处理不足1秒，直接跳过
                    log.debug("小车状态上报频率过高，跳过处理，robotId: {}, 距上次处理: {}ms", 
                            robotId, timeSinceLastProcess);
                    return;
                }
            }
            
            // 更新最后处理时间
            lastProcessTimeMap.put(robotId, currentTime);
            
            Object bodyObj = zkDeviceMessage.getRequest().getBody();
            if (!(bodyObj instanceof Map)) {
                log.error("消息体类型错误，期望Map但实际类型: {}", bodyObj != null ? bodyObj.getClass().getName() : "null");
                return;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> bodyData = (Map<String, Object>) bodyObj;
            
            log.debug("处理状态消息，robotId: {}", robotId);

            // 3、查询小车信息
            RcsCarInfo carInfo = rcsCarInfoService.findBy("code", robotId.toString());
            if (carInfo == null) {
                log.warn("未找到小车信息，robotId: {}", robotId);
                return;
            }

            // 4、构建新的状态数据对象（使用 ZkCarRedisUtil 工具类）
            Map<String, Object> newStateData = zkCarRedisUtil.buildCarStateData(
                robotId, 
                carInfo.getCode(), 
                carInfo.getId()
            );
            
            // 根据MD文档协议规范提取字段
            
            // 4、提取位置信息（单位：微米，0.001米）
            Object xObj = bodyData.get("x");
            if (xObj != null) {
                int x = xObj instanceof Integer ? (Integer) xObj : Integer.parseInt(xObj.toString());
                newStateData.put("x", x);
            }
            
            Object yObj = bodyData.get("y");
            if (yObj != null) {
                int y = yObj instanceof Integer ? (Integer) yObj : Integer.parseInt(yObj.toString());
                newStateData.put("y", y);
            }
            
            Object zObj = bodyData.get("z");
            if (zObj != null) {
                int z = zObj instanceof Integer ? (Integer) zObj : Integer.parseInt(zObj.toString());
                newStateData.put("z", z);
            }
            
            // 5、提取角度信息（单位：0.01度）
//            Object angleObj = bodyData.get("angle");
//            if (angleObj != null) {
//                int angle = angleObj instanceof Integer ? (Integer) angleObj : Integer.parseInt(angleObj.toString());
//                newStateData.put("angle", angle);
//            }
            
            // 6、提取速度信息（单位：0.001米/秒）
//            Object speedObj = bodyData.get("speed");
//            if (speedObj != null) {
//                int speed = speedObj instanceof Integer ? (Integer) speedObj : Integer.parseInt(speedObj.toString());
//                newStateData.put("speed", speed);
//            }
            
            // 7、提取电量信息（单位：百分比，0-100）
            Object powerPercentObj = bodyData.get("powerPercent");
            if (powerPercentObj != null) {
                int powerPercent = powerPercentObj instanceof Integer ? (Integer) powerPercentObj : Integer.parseInt(powerPercentObj.toString());
                newStateData.put("powerPercent", powerPercent);
            }
            
            // 8、提取电池电压（单位：0.001V）
//            Object batteryVoltageObj = bodyData.get("batteryVoltage");
//            if (batteryVoltageObj != null) {
//                int batteryVoltage = batteryVoltageObj instanceof Integer ? (Integer) batteryVoltageObj : Integer.parseInt(batteryVoltageObj.toString());
//                newStateData.put("batteryVoltage", batteryVoltage);
//            }
            
            // 9、提取电池电流（单位：0.001A）
//            Object batteryElectricObj = bodyData.get("batteryElectric");
//            if (batteryElectricObj != null) {
//                int batteryElectric = batteryElectricObj instanceof Integer ? (Integer) batteryElectricObj : Integer.parseInt(batteryElectricObj.toString());
//                newStateData.put("batteryElectric", batteryElectric);
//            }
            
            // 10、提取电池温度（单位：0.01℃）
//            Object batteryTemperatureObj = bodyData.get("batteryTemperature");
//            if (batteryTemperatureObj != null) {
//                int batteryTemperature = batteryTemperatureObj instanceof Integer ? (Integer) batteryTemperatureObj : Integer.parseInt(batteryTemperatureObj.toString());
//                newStateData.put("batteryTemperature", batteryTemperature);
//            }
            
            // 11、提取充电电流（单位：0.001A）
//            Object chargingCurrentObj = bodyData.get("chargingCurrent");
//            if (chargingCurrentObj != null) {
//                int chargingCurrent = chargingCurrentObj instanceof Integer ? (Integer) chargingCurrentObj : Integer.parseInt(chargingCurrentObj.toString());
//                newStateData.put("chargingCurrent", chargingCurrent);
//            }
            
            // 12、提取负载状态（0空载/1负载）
            Object loadStateObj = bodyData.get("loadState");
            if (loadStateObj != null) {
                int loadState = loadStateObj instanceof Integer ? (Integer) loadStateObj : Integer.parseInt(loadStateObj.toString());
                newStateData.put("loadState", loadState);
            }
            
            // 13、提取货架编码
//            Object shelfCodeObj = bodyData.get("shelfCode");
//            if (shelfCodeObj != null) {
//                String shelfCode = shelfCodeObj.toString();
//                newStateData.put("shelfCode", shelfCode);
//            }
            
            // 14、提取货架角度（单位：0.01度）
//            Object shelfAngleObj = bodyData.get("shelfAngle");
//            if (shelfAngleObj != null) {
//                int shelfAngle = shelfAngleObj instanceof Integer ? (Integer) shelfAngleObj : Integer.parseInt(shelfAngleObj.toString());
//                newStateData.put("shelfAngle", shelfAngle);
//            }
            
            // 15、提取路径状态（0执行中/1完成）
            Object pathStateObj = bodyData.get("pathState");
            if (pathStateObj != null) {
                int pathState = pathStateObj instanceof Integer ? (Integer) pathStateObj : Integer.parseInt(pathStateObj.toString());
                newStateData.put("pathState", pathState);
            }
            
            // 16、提取任务状态（0接收/1完成/2失败）
            Object taskStateObj = bodyData.get("taskState");
            if (taskStateObj != null) {
                int taskState = taskStateObj instanceof Integer ? (Integer) taskStateObj : Integer.parseInt(taskStateObj.toString());
                newStateData.put("taskState", taskState);
            }
            
            // 17、提取当前任务ID
            Object taskIdObj = bodyData.get("taskId");
            if (taskIdObj != null) {
                long taskId = taskIdObj instanceof Long ? (Long) taskIdObj : Long.parseLong(taskIdObj.toString());
                newStateData.put("taskId", taskId);
            }
            
            // 18、提取手自动状态（0手动/1自动）
            Object workingModeObj = bodyData.get("workingMode");
            if (workingModeObj != null) {
                int workingMode = workingModeObj instanceof Integer ? (Integer) workingModeObj : Integer.parseInt(workingModeObj.toString());
                newStateData.put("workingMode", workingMode);
            }
            
            // 19、提取异常码集合
//            Object errorCodesObj = bodyData.get("errorCodes");
//            if (errorCodesObj != null) {
//                newStateData.put("errorCodes", errorCodesObj);
//            }
            
            // 20、提取机构组件状态
//            Object mechanismObj = bodyData.get("mechanism");
//            if (mechanismObj != null) {
//                newStateData.put("mechanism", mechanismObj);
//            }
            
            // 21、提取统计数据
//            Object statusSumObj = bodyData.get("statusSum");
//            if (statusSumObj != null) {
//                newStateData.put("statusSum", statusSumObj);
//            }
            
//            Object statusTimeObj = bodyData.get("StatusTime");
//            if (statusTimeObj != null) {
//                newStateData.put("statusTime", statusTimeObj);
//            }
            
            // 22、提取最后任务信息
//            Object lastTaskDataObj = bodyData.get("lastTaskData");
//            if (lastTaskDataObj != null) {
//                newStateData.put("lastTaskData", lastTaskDataObj);
//            }
            
            // 23、提取时间戳
//            Object robotTimeObj = bodyData.get("robotTime");
//            if (robotTimeObj != null) {
//                long robotTime = robotTimeObj instanceof Long ? (Long) robotTimeObj : Long.parseLong(robotTimeObj.toString());
//                newStateData.put("robotTime", robotTime);
//            }

            // 24、是否在充电
            Object isChargingObj = bodyData.get("chargingCurrent");
            if (isChargingObj != null) {
                int isCharging = isChargingObj instanceof Integer ? (Integer) isChargingObj : Integer.parseInt(isChargingObj.toString());
                newStateData.put("isCharging", isCharging);
            }
            
            // 5、从Redis获取上一次的状态数据（使用 ZkCarRedisUtil 工具类）
            Map<String, Object> oldStateData = zkCarRedisUtil.getCarState(robotId);


            zkCarRedisUtil.saveCarState(robotId, newStateData);

            // 6、对比状态数据是否有变化
            boolean hasChanged = isStateChanged(oldStateData, newStateData);
            
            if (!hasChanged) {
                log.debug("小车状态无变化，跳过处理，robotId: {}", robotId);
                return;
            }
            


            // 记录变化的字段
            String changedFields = getChangedFields(oldStateData, newStateData);
            log.info("小车状态已更新到Redis，robotId: {}, 变化字段: {}", robotId, changedFields);

            // 7、根据变化字段更新数据库状态
            updateCarStateInDatabase(robotId, newStateData, changedFields);

            // 8、状态消息通常不需要响应（如果需要响应，可以取消注释下面的代码）
            // sendSuccessResponse(ip, zkDeviceMessage);

        } catch (Exception e) {
            log.error("处理状态消息失败，robotId: {}", zkDeviceMessage.getRobotId(), e);
            // 状态消息处理失败不影响主流程，不发送错误响应
        }
    }
    
    /**
     * 判断状态数据是否发生变化（对比关键字段）
     * @param oldState 旧状态数据
     * @param newState 新状态数据
     * @return true-有变化，false-无变化
     */
    private boolean isStateChanged(Map<String, Object> oldState, Map<String, Object> newState) {
        // 如果旧状态为空，认为是首次上报，有变化
        if (oldState == null || oldState.isEmpty()) {
            return true;
        }
        
        // 对比关键字段
        String[] keyFields = {"battery", "speed", "qrCode", "status", "isCharging", "cellCode","x","y","z","loadState"};
        
        for (String field : keyFields) {
            Object oldValue = oldState.get(field);
            Object newValue = newState.get(field);
            
            // 两个值都为空，继续下一个字段
            if (oldValue == null && newValue == null) {
                continue;
            }
            
            // 一个为空一个不为空，有变化
            if (oldValue == null || newValue == null) {
                return true;
            }
            
            // 对比值是否相等
            if (!oldValue.equals(newValue)) {
                return true;
            }
        }
        
        // 所有关键字段都没有变化
        return false;
    }
    
    /**
     * 获取变化的字段列表（用于日志记录）
     * @param oldState 旧状态数据
     * @param newState 新状态数据
     * @return 变化字段的描述字符串
     */
    private String getChangedFields(Map<String, Object> oldState, Map<String, Object> newState) {
        if (oldState == null || oldState.isEmpty()) {
            return "首次上报";
        }
        
        java.util.List<String> changedFields = new java.util.ArrayList<>();
        String[] keyFields = {"battery", "speed", "qrCode", "status", "isCharging", "cellCode", "loadState"};
        
        for (String field : keyFields) {
            Object oldValue = oldState.get(field);
            Object newValue = newState.get(field);
            
            if (oldValue == null && newValue == null) {
                continue;
            }
            
            if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
                changedFields.add(field + "(" + oldValue + "->" + newValue + ")");
            }
        }
        
        return changedFields.isEmpty() ? "无" : String.join(", ", changedFields);
    }
    
    /**
     * 从Redis获取小车实时状态（使用 ZkCarRedisUtil 工具类）
     * @param robotId 机器人ID
     * @return 状态数据，如果不存在则返回null
     */
    public Map<String, Object> getCarStateFromRedis(Integer robotId) {
        return zkCarRedisUtil.getCarState(robotId);
    }
    
    /**
     * 从Redis获取所有小车的实时状态（使用 ZkCarRedisUtil 工具类）
     * @return 所有小车的状态数据
     */
    public List<Map<String, Object>> getAllCarStatesFromRedis() {
        return zkCarRedisUtil.getAllCarStates();
    }

    /**
     * 根据任务状态和状态码获取任务状态描述
     * @param taskState 任务状态（任务中：0; 完成：1; 异常：2）
     * @param statusCode 状态码（正常：0； 异常 非零）
     * @return 任务状态描述
     */
    private String getTaskStateDescription(Integer taskState, Integer statusCode) {
        if (taskState == null) {
            return "UNKNOWN";
        }
        
        switch (taskState) {
            case 0:
                return "EXECUTING"; // 任务执行中
            case 1:
                return "COMPLETED"; // 任务完成
            case 2:
                return "EXCEPTION_" + (statusCode != null ? statusCode : "UNKNOWN"); // 任务异常，包含状态码
            default:
                return "UNKNOWN_STATE_" + taskState;
        }
    }

    /**
     * 根据变化字段更新数据库状态（使用专用遥测数据更新方法，并推送 WebSocket）
     * @param robotId 机器人ID
     * @param newStateData 新状态数据
     * @param changedFields 变化字段描述
     */
    private void updateCarStateInDatabase(Integer robotId, Map<String, Object> newStateData, String changedFields) {
        try {
            // 1、查询小车信息
            RcsCarInfo carInfo = rcsCarInfoService.findBy("code", robotId.toString());
            if (carInfo == null) {
                log.warn("更新数据库失败，未找到小车信息，robotId: {}", robotId);
                return;
            }
            
            // 2、创建遥测数据更新对象（只包含ID和需要更新的字段）
            RcsCarInfo telemetryUpdate = new RcsCarInfo();
            telemetryUpdate.setId(carInfo.getId());
            telemetryUpdate.setCode(carInfo.getCode());
            
            boolean needUpdate = false;
            
            // 3、更新坐标信息（x, y, z）
            if (newStateData.containsKey("x")) {
                Integer x = (Integer) newStateData.get("x");
                if (x != null && !x.equals(carInfo.getCurrentX())) {
                    telemetryUpdate.setCurrentX(x);
                    needUpdate = true;
                }
            }
            
            if (newStateData.containsKey("y")) {
                Integer y = (Integer) newStateData.get("y");
                if (y != null && !y.equals(carInfo.getCurrentY())) {
                    telemetryUpdate.setCurrentY(y);
                    needUpdate = true;
                }
            }
            
            if (newStateData.containsKey("z")) {
                Integer z = (Integer) newStateData.get("z");
                if (z != null && !z.equals(carInfo.getCurrentZ())) {
                    telemetryUpdate.setCurrentZ(z);
                    needUpdate = true;
                }
            }

            /**
             * 如果位置信息发生变动，则需要根据xyz,计算当前位置的cellCode，
             * 如果找不到cellCode，则需要找到在哪两个cellCode之间，
             * 并根据当前位置的xyz，计算出当前位置在这两个cellCode之间的比例，
             * 这样 就有了fromCellCode,toCellCode，和比例，更新cellInfo的位置信息（需要单独写sql去更新这三个值）
             * 然后需要通过websocket发送到前端，这样前端就能实时查看小车位置
             */
            
            // 如果位置信息发生变动，计算库位信息
            if (telemetryUpdate.getCurrentX() != null || telemetryUpdate.getCurrentY() != null || telemetryUpdate.getCurrentZ() != null) {
                Integer currentX = telemetryUpdate.getCurrentX() != null ? telemetryUpdate.getCurrentX() : carInfo.getCurrentX();
                Integer currentY = telemetryUpdate.getCurrentY() != null ? telemetryUpdate.getCurrentY() : carInfo.getCurrentY();
                Integer currentZ = telemetryUpdate.getCurrentZ() != null ? telemetryUpdate.getCurrentZ() : carInfo.getCurrentZ();
                
                if (currentX != null && currentY != null && currentZ != null) {
                    // 计算当前位置的库位信息
                    CellPositionResult cellPosition = calculateCellPosition(currentX, currentY, currentZ, carInfo.getWareCode());
                    
                    if (cellPosition != null) {
                        // 更新库位信息
                        if (cellPosition.getFromCellCode() != null && !cellPosition.getFromCellCode().equals(carInfo.getFromCellCode())) {
                            telemetryUpdate.setFromCellCode(cellPosition.getFromCellCode());
                            needUpdate = true;
                        }
                        
                        if (cellPosition.getToCellCode() != null && !cellPosition.getToCellCode().equals(carInfo.getToCellCode())) {
                            telemetryUpdate.setToCellCode(cellPosition.getToCellCode());
                            needUpdate = true;
                        }
                        
                        if (cellPosition.getPositionRatio() != null && 
                            (carInfo.getPositionRatio() == null || 
                             cellPosition.getPositionRatio().compareTo(carInfo.getPositionRatio()) != 0)) {
                            telemetryUpdate.setPositionRatio(cellPosition.getPositionRatio());
                            needUpdate = true;
                        }
                        
                        log.debug("小车位置已计算: robotId={}, from={}, to={}, ratio={}", 
                                robotId, cellPosition.getFromCellCode(), cellPosition.getToCellCode(), 
                                cellPosition.getPositionRatio());
                    } else {
                        log.warn("无法计算小车位置的库位信息: robotId={}, x={}, y={}, z={}", 
                                robotId, currentX, currentY, currentZ);
                    }
                }
            }

            // 4、更新电量信息（powerPercent -> batteryLevel）
            if (newStateData.containsKey("powerPercent")) {
                Integer powerPercent = (Integer) newStateData.get("powerPercent");
                if (powerPercent != null && !powerPercent.equals(carInfo.getBatteryLevel())) {
                    telemetryUpdate.setBatteryLevel(powerPercent);
                    needUpdate = true;
                }
            }
            
            // 5、更新充电状态（isCharging -> isCharge，需要转换为字符串 "0" 或 "1"）
            if (newStateData.containsKey("isCharging")) {
                Integer isChargingObj = (Integer) newStateData.get("isCharging");
                if (isChargingObj != null) {
                    Integer isCharge;
                    if(isChargingObj!=null && isChargingObj>0){
                        isCharge=1;
                    }else{
                        isCharge=0;
                    }
                    if (!isCharge.equals(carInfo.getIsCharge())) {
                        telemetryUpdate.setIsCharge(isCharge);
                        needUpdate = true;
                    }
                }
            }
            
            // 6、更新速度信息（speed）
            if (newStateData.containsKey("speed")) {
                Object speedObj = newStateData.get("speed");
                if (speedObj != null) {
                    java.math.BigDecimal speed;
                    if (speedObj instanceof Integer) {
                        // 速度单位是 0.001米/秒，转换为 mm/s
                        speed = new java.math.BigDecimal((Integer) speedObj).divide(new java.math.BigDecimal(1000), 3, java.math.BigDecimal.ROUND_HALF_UP);
                    } else if (speedObj instanceof java.math.BigDecimal) {
                        speed = (java.math.BigDecimal) speedObj;
                    } else {
                        speed = new java.math.BigDecimal(speedObj.toString());
                    }
                    
                    if (carInfo.getSpeed() == null || speed.compareTo(carInfo.getSpeed()) != 0) {
                        telemetryUpdate.setSpeed(speed);
                        needUpdate = true;
                    }
                }
            }
            
            // 7、更新负载状态（loadState：0-空载/1-负载）
            if (newStateData.containsKey("loadState")) {
                Integer loadState = (Integer) newStateData.get("loadState");
                if (loadState != null && !loadState.equals(carInfo.getLoadState())) {
                    telemetryUpdate.setLoadState(loadState);
                    needUpdate = true;
                    log.info("小车负载状态发生变化: robotId={}, 旧值={}, 新值={}", 
                            robotId, carInfo.getLoadState(), loadState);
                }
            }
            
            // 8、如果有字段变化，执行遥测数据更新（包含 WebSocket 推送）
            if (needUpdate) {
                telemetryUpdate.setLastUpdateTime(new java.util.Date());
                
                // 使用专用的遥测数据更新方法（不触发业务逻辑，自动推送 WebSocket）
                rcsCarInfoService.updateCarTelemetryData(telemetryUpdate);
                log.debug("遥测数据已更新并推送，robotId: {}, 变化字段: {}", robotId, changedFields);
            } else {
                log.debug("遥测数据无变化，跳过更新，robotId: {}", robotId);
            }
            
        } catch (Exception e) {
            log.error("更新遥测数据失败，robotId: {}", robotId, e);
        }
    }

    /**
     * 根据xyz坐标计算当前位置的库位信息
     * 
     * @param currentX 当前X坐标(mm)
     * @param currentY 当前Y坐标(mm)
     * @param currentZ 当前Z坐标(楼层)
     * @param wareCode 仓库编码
     * @return 库位位置结果(fromCellCode, toCellCode, positionRatio)
     */
    private CellPositionResult calculateCellPosition(Integer currentX, Integer currentY, Integer currentZ, String wareCode) {
        try {
            if (wareCode == null || wareCode.isEmpty()) {
                log.warn("仓库编码为空，无法计算库位位置");
                return null;
            }
            
            // 1、首先尝试精确匹配 - 根据subX, subY, subZ查找库位
            CellInfo exactCell = findCellByCoordinates(currentX, currentY, currentZ, wareCode);
            
            if (exactCell != null) {
                // 找到精确匹配的库位，小车就在该库位上
                log.debug("找到精确匹配的库位: code={}, subX={}, subY={}, subZ={}", 
                        exactCell.getCode(), exactCell.getSubX(), exactCell.getSubY(), exactCell.getSubZ());
                
                CellPositionResult result = new CellPositionResult();
                result.setFromCellCode(exactCell.getCode());
                result.setToCellCode(exactCell.getCode());
                result.setPositionRatio(java.math.BigDecimal.ZERO);
                return result;
            }
            
            // 2、未找到精确匹配，查找最近的两个库位
            CellPair nearestPair = findNearestCellPair(currentX, currentY, currentZ, wareCode);
            
            if (nearestPair != null) {
                // 找到两个最近的库位，计算小车在两个库位之间的比例
                java.math.BigDecimal ratio = calculatePositionRatio(
                        currentX, currentY, 
                        nearestPair.getFromCell(), 
                        nearestPair.getToCell()
                );
                
                log.debug("小车在两个库位之间: from={}, to={}, ratio={}", 
                        nearestPair.getFromCell().getCode(), 
                        nearestPair.getToCell().getCode(), 
                        ratio);
                
                CellPositionResult result = new CellPositionResult();
                result.setFromCellCode(nearestPair.getFromCell().getCode());
                result.setToCellCode(nearestPair.getToCell().getCode());
                result.setPositionRatio(ratio);
                return result;
            }
            
            // 3、都找不到，返回null
            log.warn("未找到匹配的库位: x={}, y={}, z={}, wareCode={}", 
                    currentX, currentY, currentZ, wareCode);
            return null;
            
        } catch (Exception e) {
            log.error("计算库位位置失败: x={}, y={}, z={}, wareCode={}", 
                    currentX, currentY, currentZ, wareCode, e);
            return null;
        }
    }
    
    /**
     * 根据坐标精确查找库位
     * 
     * @param x X坐标(mm)
     * @param y Y坐标(mm)
     * @param z Z坐标(楼层)
     * @param wareCode 仓库编码
     * @return 匹配的库位，未找到返回null
     */
    private CellInfo findCellByCoordinates(Integer x, Integer y, Integer z, String wareCode) {
        try {
            // 允许一定的坐标误差(比如±100mm)
            final int TOLERANCE = 100;
            
            Condition condition = new Condition(CellInfo.class);
            Condition.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("wareCode", wareCode);
            
            // 如果CellInfo有z字段，则按z过滤
            if (z != null) {
                criteria.andEqualTo("z", z);
            }
            
            List<CellInfo> cellList = cellInfoService.findByCondition(condition);
            
            if (cellList == null || cellList.isEmpty()) {
                return null;
            }
            
            // 遍历查找坐标匹配的库位(允许误差)
            for (CellInfo cell : cellList) {
                if (cell.getSubX() != null && cell.getSubY() != null) {
                    int deltaX = Math.abs(cell.getSubX() - x);
                    int deltaY = Math.abs(cell.getSubY() - y);
                    
                    // 如果坐标误差在容差范围内，认为是匹配的
                    if (deltaX <= TOLERANCE && deltaY <= TOLERANCE) {
                        return cell;
                    }
                }
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("根据坐标查找库位失败", e);
            return null;
        }
    }
    
    /**
     * 查找最近的两个库位(用于计算小车在两个库位之间的位置)
     * 
     * @param x 当前X坐标(mm)
     * @param y 当前Y坐标(mm)
     * @param z 当前Z坐标(楼层)
     * @param wareCode 仓库编码
     * @return 最近的两个库位对，未找到返回null
     */
    private CellPair findNearestCellPair(Integer x, Integer y, Integer z, String wareCode) {
        try {
            Condition condition = new Condition(CellInfo.class);
            Condition.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("wareCode", wareCode);
            
            // 如果有z字段，则按z过滤
            if (z != null) {
                criteria.andEqualTo("z", z);
            }
            
            List<CellInfo> cellList = cellInfoService.findByCondition(condition);
            
            if (cellList == null || cellList.size() < 2) {
                return null;
            }
            
            // 过滤掉没有坐标信息的库位
            List<CellInfo> validCells = new ArrayList<>();
            for (CellInfo cell : cellList) {
                if (cell.getSubX() != null && cell.getSubY() != null) {
                    validCells.add(cell);
                }
            }
            
            if (validCells.size() < 2) {
                return null;
            }
            
            // 计算每个库位到当前位置的距离，找出最近的两个
            validCells.sort((c1, c2) -> {
                double dist1 = calculateDistance(x, y, c1.getSubX(), c1.getSubY());
                double dist2 = calculateDistance(x, y, c2.getSubX(), c2.getSubY());
                return Double.compare(dist1, dist2);
            });
            
            // 取最近的两个库位
            CellInfo nearestCell = validCells.get(0);
            CellInfo secondNearestCell = validCells.get(1);
            
            // 判断哪个是起点，哪个是终点(根据Y坐标或X坐标的大小关系)
            // 这里假设：较小的Y坐标是起点，较大的Y坐标是终点
            CellInfo fromCell, toCell;
            if (nearestCell.getSubY() <= secondNearestCell.getSubY()) {
                fromCell = nearestCell;
                toCell = secondNearestCell;
            } else {
                fromCell = secondNearestCell;
                toCell = nearestCell;
            }
            
            CellPair pair = new CellPair();
            pair.setFromCell(fromCell);
            pair.setToCell(toCell);
            return pair;
            
        } catch (Exception e) {
            log.error("查找最近的库位对失败", e);
            return null;
        }
    }
    
    /**
     * 计算两点之间的欧氏距离
     */
    private double calculateDistance(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * 计算小车在两个库位之间的位置比例
     * 
     * @param currentX 当前X坐标
     * @param currentY 当前Y坐标
     * @param fromCell 起始库位
     * @param toCell 目标库位
     * @return 位置比例(0-1之间)
     */
    private java.math.BigDecimal calculatePositionRatio(Integer currentX, Integer currentY, 
                                                        CellInfo fromCell, CellInfo toCell) {
        try {
            // 计算起点到终点的总距离
            double totalDistance = calculateDistance(
                    fromCell.getSubX(), fromCell.getSubY(), 
                    toCell.getSubX(), toCell.getSubY()
            );
            
            if (totalDistance == 0) {
                // 两个库位坐标相同，返回0
                return java.math.BigDecimal.ZERO;
            }
            
            // 计算起点到当前位置的距离
            double currentDistance = calculateDistance(
                    fromCell.getSubX(), fromCell.getSubY(), 
                    currentX, currentY
            );
            
            // 计算比例
            double ratio = currentDistance / totalDistance;
            
            // 限制在0-1之间
            if (ratio < 0) {
                ratio = 0;
            } else if (ratio > 1) {
                ratio = 1;
            }
            
            return new java.math.BigDecimal(ratio).setScale(3, java.math.BigDecimal.ROUND_HALF_UP);
            
        } catch (Exception e) {
            log.error("计算位置比例失败", e);
            return java.math.BigDecimal.ZERO;
        }
    }
    
    /**
     * 库位位置结果
     */
    private static class CellPositionResult {
        private String fromCellCode;
        private String toCellCode;
        private java.math.BigDecimal positionRatio;
        
        public String getFromCellCode() {
            return fromCellCode;
        }
        
        public void setFromCellCode(String fromCellCode) {
            this.fromCellCode = fromCellCode;
        }
        
        public String getToCellCode() {
            return toCellCode;
        }
        
        public void setToCellCode(String toCellCode) {
            this.toCellCode = toCellCode;
        }
        
        public java.math.BigDecimal getPositionRatio() {
            return positionRatio;
        }
        
        public void setPositionRatio(java.math.BigDecimal positionRatio) {
            this.positionRatio = positionRatio;
        }
    }
    
    /**
     * 库位对
     */
    private static class CellPair {
        private CellInfo fromCell;
        private CellInfo toCell;
        
        public CellInfo getFromCell() {
            return fromCell;
        }
        
        public void setFromCell(CellInfo fromCell) {
            this.fromCell = fromCell;
        }
        
        public CellInfo getToCell() {
            return toCell;
        }
        
        public void setToCell(CellInfo toCell) {
            this.toCell = toCell;
        }
    }

}