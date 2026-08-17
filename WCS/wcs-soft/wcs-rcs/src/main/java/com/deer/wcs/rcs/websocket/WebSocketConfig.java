package com.deer.wcs.rcs.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类
 * 用于启用WebSocket支持
 * 
 * @author Deer WCS Team
 * @date 2024-10-16
 */
//@Configuration
public class WebSocketConfig {
    
    /**
     * 注入ServerEndpointExporter
     * 这个bean会自动注册使用@ServerEndpoint注解声明的WebSocket endpoint
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}

