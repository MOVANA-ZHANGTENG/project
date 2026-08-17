package com.deer.wcs.jxg.car_brand_model;

import com.deer.wcs.rcs.service.RcsCarInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RcsTcpServer {
    private ServerSocket serverSocket;
    private final int port;
    private volatile boolean isRunning = false;
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private final ScheduledExecutorService heartbeatExecutor = Executors.newScheduledThreadPool(1);
    private final ExecutorService clientExecutor = Executors.newCachedThreadPool();
    private final ExecutorService callbackExecutor = Executors.newCachedThreadPool();

    // 回调接口
    private Consumer<ClientInfo> onClientConnected;
    private BiConsumer<ClientInfo, String> onClientDisconnected;
    private BiConsumer<ClientInfo, String> onClientMessageReceived;

    // 心跳配置（根据协议规范：客户端超过20秒无消息则断开）
    private static final long HEARTBEAT_INTERVAL = 10 * 1000; // 10秒检测一次
    private static final long HEARTBEAT_TIMEOUT = 20 * 1000; // 20秒超时（协议要求）
    private static final long SERVER_WRITE_IDLE = 3 * 1000; // 服务器写空闲3秒后发送心跳

    public RcsTcpServer(int port) {
        this.port = port;
    }

    // 设置连接回调
    public void setOnClientConnected(Consumer<ClientInfo> callback) {
        this.onClientConnected = callback;
    }

    // 设置断开回调
    public void setOnClientDisconnected(BiConsumer<ClientInfo, String> callback) {
        this.onClientDisconnected = callback;
    }

    // 设置消息接收回调
    public void setOnClientMessageReceived(BiConsumer<ClientInfo, String> callback) {
        this.onClientMessageReceived = callback;
    }

    // 客户端信息类
    public static class ClientInfo {
        private final String clientId;
        private final String remoteAddress;
        private final int remotePort;
        private final long connectTime;

        public ClientInfo(String clientId, String remoteAddress, int remotePort) {

            this.clientId = clientId;
            this.remoteAddress = remoteAddress;
            this.remotePort = remotePort;
            this.connectTime = System.currentTimeMillis();
        }

        public String getClientId() { return clientId; }
        public String getRemoteAddress() { return remoteAddress; }
        public int getRemotePort() { return remotePort; }
        public long getConnectTime() { return connectTime; }
        public long getConnectionDuration() { return System.currentTimeMillis() - connectTime; }

        @Override
        public String toString() {
            return String.format("ClientInfo{id='%s', address=%s:%d, connected=%dms}",
                    clientId, remoteAddress, remotePort, getConnectionDuration());
        }
    }

    private RcsCarInfoService rcsCarInfoService;
    
    // 设置RcsCarInfoService
    public void setRcsCarInfoService(RcsCarInfoService rcsCarInfoService) {
        this.rcsCarInfoService = rcsCarInfoService;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        isRunning = true;
        System.out.println("TCP服务器启动在端口: " + port);

        // 启动心跳检测
        startHeartbeatCheck();

        // 接受客户端连接
        while (isRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                String clientId = generateClientId(clientSocket);
                String ip = clientSocket.getInetAddress().getHostAddress();
                
                // 检查同IP是否已存在连接，如果存在则关闭旧连接
                ClientHandler existingClient = clients.get(clientId);
                if (existingClient != null) {
                    System.out.println("检测到同IP重复连接，关闭旧连接: " + clientId);
                    existingClient.close("同IP新连接建立");
                    // close()方法内部会自动从clients中移除
                }
                
                System.out.println("客户端连接: " + clientId);
                
                ClientInfo clientInfo = new ClientInfo(
                        clientId,
                        ip,
                        clientSocket.getPort()
                );

                // 触发连接回调
                if (onClientConnected != null) {
                    callbackExecutor.execute(() -> {
                        try {
                            onClientConnected.accept(clientInfo);
                        } catch (Exception e) {
                            System.err.println("连接回调执行异常: " + e.getMessage());
                        }
                    });
                }

                ClientHandler clientHandler = new ClientHandler(clientInfo, clientSocket);
                clients.put(clientId, clientHandler);
                clientExecutor.execute(clientHandler);

            } catch (SocketException e) {
                if (isRunning) {
                    System.err.println("接受客户端连接异常: " + e.getMessage());
                }
            }
        }
    }

    private void startHeartbeatCheck() {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            try {
                long currentTime = System.currentTimeMillis();
                List<String> timeoutClients = new ArrayList<>();

                // 只在有客户端连接时输出摘要日志，避免日志过多
                if (!clients.isEmpty()) {
                    System.out.println("🔍 心跳检测 - 当前连接客户端数: " + clients.size());
                }
                
                for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
                    ClientHandler client = entry.getValue();
                    long timeSinceLastHeartbeat = currentTime - client.getLastHeartbeatTime();
                    long timeSinceLastWrite = currentTime - client.getLastWriteTime();
                    
                    // 1. 检查客户端是否超时（超过20秒未收到消息）
                    if (timeSinceLastHeartbeat > HEARTBEAT_TIMEOUT) {
                        System.out.println("❌ 客户端心跳超时: " + client.getClientInfo().getClientId() + 
                                ", 距上次心跳: " + timeSinceLastHeartbeat + "ms");
                        timeoutClients.add(entry.getKey());
                        client.close("心跳超时");
                    } 
                    // 2. 检查服务器是否需要发送心跳（写空闲超过3秒）
                    else if (timeSinceLastWrite > SERVER_WRITE_IDLE) {
                        // 服务器写空闲超过3秒，发送心跳
                        try {
                            client.sendHeartbeat();
                            System.out.println("💓 发送心跳给客户端: " + client.getClientInfo().getClientId() + 
                                    " (写空闲: " + timeSinceLastWrite + "ms)");
                        } catch (Exception e) {
                            System.err.println("发送心跳失败: " + client.getClientInfo().getClientId() + ", " + e.getMessage());
                        }
                    } 
                    // 3. 警告：接近超时
                    else if (timeSinceLastHeartbeat > HEARTBEAT_TIMEOUT * 0.8) {
                        System.out.println("⚠️ 客户端心跳接近超时: " + client.getClientInfo().getClientId() + 
                                ", 距上次心跳: " + timeSinceLastHeartbeat + "ms (超时阈值: " + HEARTBEAT_TIMEOUT + "ms)");
                    }
                }

                // 移除超时客户端
                for (String clientId : timeoutClients) {
                    clients.remove(clientId);
                }
            } catch (Exception e) {
                System.err.println("心跳检测异常: " + e.getMessage());
                e.printStackTrace();
            }

        }, HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * 向指定客户端发送消息并等待响应（同步）
     */
    public String sendMessageSync(String clientId, ZkDeviceMessage deviceMessage, long timeoutMs)
            throws ClientNotFoundException, TimeoutException, IOException {
        ClientHandler client = clients.get(clientId);
        if (client == null) {
            throw new ClientNotFoundException("客户端未找到: " + clientId);
        }
        return client.sendMessageSync(deviceMessage, timeoutMs);
    }

    /**
     * 向指定客户端发送消息（异步）
     */
    public void sendMessageAsync(String clientId, String message)
            throws ClientNotFoundException, IOException {
        ClientHandler client = clients.get(clientId);
        if (client == null) {
            throw new ClientNotFoundException("客户端未找到: " + clientId);
        }
        client.sendMessageAsync(message);
    }

    /**
     * 广播消息给所有客户端
     */
    public void broadcastMessage(String message) {
        for (ClientHandler client : clients.values()) {
            try {
                client.sendMessageAsync(message);
            } catch (IOException e) {
                System.err.println("广播消息失败: " + client.getClientInfo().getClientId() + ", " + e.getMessage());
            }
        }
    }

    /**
     * 获取所有连接的客户端ID
     */
    public Set<String> getConnectedClients() {
        return new HashSet<>(clients.keySet());
    }

    /**
     * 获取客户端信息
     */
    public ClientInfo getClientInfo(String clientId) {
        ClientHandler client = clients.get(clientId);
        return client != null ? client.getClientInfo() : null;
    }

    private String generateClientId(Socket socket) {
        // 只使用IP地址作为客户端ID，确保同一IP只有一个连接
        return socket.getInetAddress().getHostAddress();
    }



    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("关闭服务器异常: " + e.getMessage());
        }

        // 关闭所有客户端连接
        for (ClientHandler client : clients.values()) {
            client.close("服务器关闭");
        }
        clients.clear();

        // 关闭线程池
        heartbeatExecutor.shutdown();
        clientExecutor.shutdown();
        callbackExecutor.shutdown();

        System.out.println("TCP服务器已停止");
    }

    // 客户端处理器
    private class ClientHandler implements Runnable {
        private final ClientInfo clientInfo;
        private final Socket socket;
        private final BufferedReader reader;
        private final PrintWriter writer;
        private volatile long lastHeartbeatTime;  // 最后一次收到客户端消息的时间
        private volatile long lastWriteTime;      // 最后一次向客户端发送消息的时间
        private final Map<Long, ResponseFuture> pendingRequests = new ConcurrentHashMap<>();
        private final AtomicLong requestIdGenerator = new AtomicLong(0);
        private volatile boolean connected = true;
        private Integer robotId;  // 机器人ID，从数据库查询获取

        public ClientHandler(ClientInfo clientInfo, Socket socket) throws IOException {
            this.clientInfo = clientInfo;
            this.socket = socket;
            this.socket.setKeepAlive(true);
            this.socket.setSoTimeout(0);
            // 设置SO_LINGER为0，关闭时立即返回，不等待数据发送完成
            this.socket.setSoLinger(true, 0);

            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            long currentTime = System.currentTimeMillis();
            this.lastHeartbeatTime = currentTime;
            this.lastWriteTime = currentTime;
            
            // 根据IP地址查询robotId
            this.robotId = getRobotIdByIp(clientInfo.getRemoteAddress());
            if (this.robotId != null) {
                System.out.println("✅ 客户端连接初始化成功: IP=" + clientInfo.getRemoteAddress() + ", robotId=" + this.robotId);
            } else {
                System.out.println("⚠️ 客户端连接初始化: IP=" + clientInfo.getRemoteAddress() + ", 未找到对应的robotId");
            }
        }
        
        /**
         * 根据IP地址查询robotId
         */
        private Integer getRobotIdByIp(String ip) {
            if (rcsCarInfoService != null) {
                try {
                    com.deer.wcs.rcs.model.RcsCarInfo carInfo = rcsCarInfoService.findBy("ip", ip);
                    if (carInfo != null) {
                        return Integer.parseInt(carInfo.getId().toString());
                    }
                } catch (Exception e) {
                    System.err.println("查询robotId失败: IP=" + ip + ", 错误: " + e.getMessage());
                }
            }
            return 0; // 默认返回0
        }

        // 消息缓冲区，用于处理包含换行符的JSON消息
        private final StringBuilder messageBuffer = new StringBuilder();
        
        @Override
        public void run() {
            try {
                char[] buffer = new char[1024];
                int bytesRead;
                
                // 读取原始字符流，而不是按行读取
                while (connected && (bytesRead = reader.read(buffer)) != -1) {
                    lastHeartbeatTime = System.currentTimeMillis();
                    
                    // 将读取到的数据添加到消息缓冲区
                    messageBuffer.append(buffer, 0, bytesRead);
                    
                    // 尝试从缓冲区中解析完整的JSON消息
                    parseMessagesFromBuffer();
                }
                
                // 读取到 null，说明客户端关闭了连接
                System.out.println("⚠️ 客户端关闭了连接流: " + clientInfo.getClientId() + 
                        ", 连接时长: " + (System.currentTimeMillis() - clientInfo.getConnectTime()) + "ms");
                
            } catch (SocketTimeoutException e) {
                System.out.println("客户端读取超时: " + clientInfo.getClientId());
                close("读取超时");
            } catch (IOException e) {
                if (connected) {
                    System.err.println("客户端连接异常: " + clientInfo.getClientId() + ", " + e.getMessage());
                    close("IO异常: " + e.getMessage());
                }
            } finally {
                if (connected) {
                    close("连接关闭");
                }
            }
        }

        /**
         * 从消息缓冲区中解析完整的JSON消息
         * 支持处理包含换行符的JSON消息，正确识别嵌套结构
         */
        private void parseMessagesFromBuffer() {
            String bufferContent = messageBuffer.toString();
            int startIndex = 0;
            int braceCount = 0;
            boolean inString = false;
            boolean escaped = false;
            int bufferLength = bufferContent.length();
            
            for (int i = 0; i < bufferLength; i++) {
                char c = bufferContent.charAt(i);
                
                // 处理字符串中的转义字符
                if (escaped) {
                    escaped = false;
                    continue;
                }
                
                // 处理字符串边界
                if (c == '"') {
                    inString = !inString;
                    continue;
                }
                
                // 处理转义字符
                if (c == '\\' && inString) {
                    escaped = true;
                    continue;
                }
                
                // 只在非字符串环境中处理括号
                if (!inString) {
                    if (c == '{') {
                        braceCount++;
                        // 记录第一个左括号的位置
                        if (braceCount == 1) {
                            startIndex = i;
                        }
                    } else if (c == '}') {
                        braceCount--;
                        
                        // 找到完整的JSON对象
                        if (braceCount == 0) {
                            // 提取完整的JSON消息
                            String jsonMessage = bufferContent.substring(startIndex, i + 1).trim();
                            
                            // 只处理非空消息
                            if (!jsonMessage.isEmpty()) {
                                handleReceivedMessage(jsonMessage);
                            }
                            
                            // 清除已处理的消息
                            messageBuffer.delete(0, i + 1);
                            
                            // 重新开始处理剩余内容
                            parseMessagesFromBuffer();
                            return;
                        }
                    }
                }
            }
        }
        
        private void handleReceivedMessage(String message) {
            try {
                // 数据验证：检查是否为空或只包含空白字符
                if (message == null || message.trim().isEmpty()) {
                    System.err.println("收到空消息，已忽略");
                    return;
                }
                
                // 数据验证：检查是否包含非打印字符（可能是二进制数据）
                if (containsInvalidCharacters(message)) {
                    System.err.println("收到包含非法字符的消息，已忽略");
                    System.err.println("消息前100字符: " + getDisplayableString(message, 100));
                    return;
                }
                
                // 数据验证：简单检查是否像 JSON（以 { 或 [ 开始）
                String trimmed = message.trim();
                if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
                    System.err.println("收到的消息不是有效的 JSON 格式，已忽略");
                    System.err.println("消息内容: " + message.substring(0, Math.min(200, message.length())));
                    return;
                }
                
                // JSON 反序列化
                ZkDeviceMessage deviceMessage = ZkDeviceMessage.ZkMessageUtil.fromJson(message);
                
                // 检查消息类型
                if (deviceMessage.getMsgType().equals(ZkMessageType.CMD_RES_MSG)) {
                    handleResponse(deviceMessage, message);
                    return;
                }
                
                // 普通消息 - 客户端主动上报
                // 这种消息触发 onClientMessageReceived 回调
             //   System.out.println("收到来自 " + clientInfo.getClientId() + " 的消息: " + message);
                
                // 触发消息接收回调
                if (onClientMessageReceived != null) {
                    final String msg = message;
                    callbackExecutor.execute(() -> {
                        try {
                            onClientMessageReceived.accept(clientInfo, msg);
                        } catch (Exception e) {
                            System.err.println("消息接收回调执行异常: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }
                
            } catch (Exception e) {
                System.err.println("处理消息异常: " + e.getMessage());
                System.err.println("原始消息: " + message.substring(0, Math.min(200, message.length())));
                e.printStackTrace();
            }
        }
        
        /**
         * 检查字符串是否包含非法控制字符
         */
        private boolean containsInvalidCharacters(String str) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                // 允许的控制字符：\t(9), \n(10), \r(13)
                // 其他小于32的控制字符都认为是非法的
                if (c < 32 && c != 9 && c != 10 && c != 13) {
                    return true;
                }
                // 检查是否是其他非法的 Unicode 控制字符
                if (Character.isISOControl(c) && c != '\t' && c != '\n' && c != '\r') {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * 获取可显示的字符串（用于日志输出）
         */
        private String getDisplayableString(String str, int maxLength) {
            if (str == null) return "null";
            
            StringBuilder result = new StringBuilder();
            int length = Math.min(str.length(), maxLength);
            
            for (int i = 0; i < length; i++) {
                char c = str.charAt(i);
                if (c >= 32 && c < 127) {
                    result.append(c);
                } else if (c == '\t') {
                    result.append("\\t");
                } else if (c == '\n') {
                    result.append("\\n");
                } else if (c == '\r') {
                    result.append("\\r");
                } else {
                    result.append(String.format("\\u%04x", (int) c));
                }
            }
            
            if (str.length() > maxLength) {
                result.append("...");
            }
            
            return result.toString();
        }

        /**
         * 处理客户端对同步请求的响应
         * 响应格式: RESPONSE:requestId:responseData
         */
        private void handleResponse(ZkDeviceMessage deviceMessage,String msg) {
            try {
                Long requestId = deviceMessage.getResponse().getHeader().getResponseId();
                // 找到对应的等待Future并完成它
                ResponseFuture future = pendingRequests.remove(requestId);
                if (future != null) {
                    future.complete(msg);
                    System.out.println("✅ 收到同步响应 [" + requestId + "]: " + msg);
                } else {
                    System.err.println("⚠️ 收到未知请求ID的响应: " + requestId);
                }

            } catch (Exception e) {
                System.err.println("处理响应异常: " + e.getMessage());
            }
        }

        public String sendMessageSync(ZkDeviceMessage deviceMessage, long timeoutMs) throws IOException, TimeoutException {
            // 验证消息类型：只有请求消息才能使用同步发送
            if (deviceMessage.getRequest() == null) {
                throw new IOException("sendMessageSync 只能用于发送请求消息（request），响应消息请使用 sendMessageAsync");
            }
            
            if (deviceMessage.getRequest().getHeader() == null) {
                throw new IOException("请求消息的 header 不能为空");
            }

            Long requestId = deviceMessage.getRequest().getHeader().getRequestId();
            if (requestId == null  ) {
                throw new IOException("请求消息的 requestId 不能为空");
            }
            
            ResponseFuture future = new ResponseFuture();
            pendingRequests.put(requestId, future);
            String message = ZkDeviceMessage.ZkMessageUtil.toJson(deviceMessage);
            
            try {
                writer.println(message);
                lastWriteTime = System.currentTimeMillis(); // 更新写入时间
                return future.get(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                pendingRequests.remove(requestId);
                throw new TimeoutException("请求超时: " + requestId);
            } catch (InterruptedException e) {
                pendingRequests.remove(requestId);
                Thread.currentThread().interrupt();
                throw new IOException("请求被中断", e);
            } catch (Exception e) {
                pendingRequests.remove(requestId);
                throw new IOException("发送消息失败", e);
            }
        }

        public void sendMessageAsync(String message) throws IOException {
            try {
                writer.println(message);
                lastWriteTime = System.currentTimeMillis(); // 更新写入时间
            } catch (Exception e) {
                throw new IOException("发送异步消息失败", e);
            }
        }
        
        /**
         * 发送心跳消息到客户端
         * 根据协议规范，心跳msgType为PingRequestMsg，无需应答
         */
        public void sendHeartbeat() throws IOException {
            try {
                // 构造心跳消息
                ZkDeviceMessage heartbeatMsg = new ZkDeviceMessage();
                heartbeatMsg.setMsgType(ZkMessageType.PING_MSG);
                
                // 使用初始化时查询到的robotId
                heartbeatMsg.setRobotId(this.robotId != null ? this.robotId : 0);
                
                ZkDeviceMessage.ZkRequest request = new ZkDeviceMessage.ZkRequest();
                ZkDeviceMessage.ZkRequestHeader header = new ZkDeviceMessage.ZkRequestHeader();
                header.setRequestId(requestIdGenerator.incrementAndGet());
                header.setVersion("2.0.0");
                request.setHeader(header);
                request.setBody(new java.util.HashMap<>()); // 心跳消息body为空
                heartbeatMsg.setRequest(request);
                
                String heartbeatJson = ZkDeviceMessage.ZkMessageUtil.toJson(heartbeatMsg);
                writer.println(heartbeatJson);
                lastWriteTime = System.currentTimeMillis(); // 更新写入时间
            } catch (Exception e) {
                throw new IOException("发送心跳失败", e);
            }
        }

        public long getLastHeartbeatTime() {
            return lastHeartbeatTime;
        }
        
        public long getLastWriteTime() {
            return lastWriteTime;
        }

        public ClientInfo getClientInfo() {
            return clientInfo;
        }

        public String getClientId() {
            return clientInfo.getClientId();
        }

        public void close(String reason) {
            if (!connected) return;

            connected = false;
            clients.remove(clientInfo.getClientId());
            
            // 判断是否为短时连接（可能是客户端定时重连）
            long connectionDuration = System.currentTimeMillis() - clientInfo.getConnectTime();
            boolean isShortConnection = connectionDuration < 30000; // 小于30秒认为是短连接
            
            if (isShortConnection) {
                System.out.println("⚠️ 检测到短时连接断开: " + clientInfo.getClientId() + 
                        ", 连接时长: " + connectionDuration + "ms, 原因: " + reason + 
                        " [建议检查客户端是否有定时重连机制]");
            }

            // 触发断开连接回调
            if (onClientDisconnected != null) {
                callbackExecutor.execute(() -> {
                    try {
                        onClientDisconnected.accept(clientInfo, reason);
                    } catch (Exception e) {
                        System.err.println("断开连接回调执行异常: " + e.getMessage());
                    }
                });
            }

            // 完成所有等待的请求
            for (ResponseFuture future : pendingRequests.values()) {
                future.completeExceptionally(new IOException("连接已关闭: " + reason));
            }
            pendingRequests.clear();

            // 异步关闭资源，避免阻塞
            clientExecutor.execute(() -> {
                try {
                    // 先shutdown socket的输出，通知对方连接即将关闭
                    if (socket != null && !socket.isClosed()) {
                        try {
                            socket.shutdownOutput();
                        } catch (IOException e) {
                            // 忽略shutdown异常
                        }
                    }
                    
                    // 关闭流和socket
                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (Exception e) {
                            // 忽略关闭异常
                        }
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            // 忽略关闭异常
                        }
                    }
                    if (socket != null && !socket.isClosed()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // 忽略关闭异常
                        }
                    }
                } catch (Exception e) {
                    System.err.println("关闭客户端连接异常: " + e.getMessage());
                }
            });

            System.out.println("客户端断开连接: " + clientInfo.getClientId() + ", 原因: " + reason);
        }
    }

    // 响应Future类
    private static class ResponseFuture {
        private final CompletableFuture<String> future = new CompletableFuture<>();

        public String get(long timeout, TimeUnit unit) throws TimeoutException, InterruptedException {
            try {
                return future.get(timeout, unit);
            } catch (ExecutionException e) {
                throw new RuntimeException(e.getCause());
            } catch (java.util.concurrent.TimeoutException e) {
                throw new TimeoutException(e.getMessage());
            }
        }

        public void complete(String response) {
            future.complete(response);
        }

        public void completeExceptionally(Throwable ex) {
            future.completeExceptionally(ex);
        }
    }

    // 自定义异常
    public static class ClientNotFoundException extends Exception {
        public ClientNotFoundException(String message) {
            super(message);
        }
    }

    public static void main(String[] args) throws Exception {
        RcsTcpServer server = new RcsTcpServer(852);

        // 设置连接回调
        server.setOnClientConnected(clientInfo -> {
            System.out.println("🎉 客户端连接: " + clientInfo);
            // 这里可以记录日志、初始化会话等
        });

        // 设置断开回调
        server.setOnClientDisconnected((clientInfo, reason) -> {
            System.out.println("❌ 客户端断开: " + clientInfo + ", 原因: " + reason);
            // 这里可以清理资源、记录断开原因等
        });

        // 设置消息接收回调
        server.setOnClientMessageReceived((clientInfo, message) -> {
            System.out.println("📨 收到消息 [" + clientInfo.getClientId() + "]: " + message);
            // 这里可以处理业务逻辑
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

        // 等待服务器启动
        Thread.sleep(2000);

        // 模拟运行一段时间后关闭
        Thread.sleep(300000); // 运行5分钟
        server.stop();
    }
}