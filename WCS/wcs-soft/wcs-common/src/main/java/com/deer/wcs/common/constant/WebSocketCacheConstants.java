package com.deer.wcs.common.constant;

/**
 * WebSocket推送 Redis缓存Key常量
 * 
 * @author Deer WCS Team
 * @date 2024-10-18
 */
public class WebSocketCacheConstants {
    
    /**
     * 小车位置更新标志 Key
     * 格式：websocket:car:update:{wareCode}:{floor}
     */
    public static final String CAR_UPDATE_FLAG = "websocket:car:update:%s:%s";
    
    /**
     * 路径状态更新标志 Key
     * 格式：websocket:path:update:{wareCode}:{floor}
     */
    public static final String PATH_UPDATE_FLAG = "websocket:path:update:%s:%s";
    
    /**
     * 小车位置数据缓存 Key
     * 格式：websocket:car:data:{wareCode}:{floor}
     */
    public static final String CAR_DATA_CACHE = "websocket:car:data:%s:%s";
    
    /**
     * 路径状态数据缓存 Key
     * 格式：websocket:path:data:{wareCode}:{floor}
     */
    public static final String PATH_DATA_CACHE = "websocket:path:data:%s:%s";
    
    /**
     * 标志位过期时间（秒）
     * 设置为5秒，超时自动清除，防止Redis堆积
     */
    public static final long FLAG_EXPIRE_SECONDS = 5L;
    
    /**
     * 数据缓存过期时间（秒）
     * 设置为10秒，避免推送过期数据
     */
    public static final long DATA_CACHE_EXPIRE_SECONDS = 10L;
    
    /**
     * 构建小车更新标志Key
     * 
     * @param wareCode 仓库编码
     * @param floor 楼层
     * @return Redis Key
     */
    public static String buildCarUpdateFlagKey(String wareCode, Integer floor) {
        return String.format(CAR_UPDATE_FLAG, wareCode, floor);
    }
    
    /**
     * 构建路径更新标志Key
     * 
     * @param wareCode 仓库编码
     * @param floor 楼层
     * @return Redis Key
     */
    public static String buildPathUpdateFlagKey(String wareCode, Integer floor) {
        return String.format(PATH_UPDATE_FLAG, wareCode, floor);
    }
    
    /**
     * 构建小车数据缓存Key
     * 
     * @param wareCode 仓库编码
     * @param floor 楼层
     * @return Redis Key
     */
    public static String buildCarDataCacheKey(String wareCode, Integer floor) {
        return String.format(CAR_DATA_CACHE, wareCode, floor);
    }
    
    /**
     * 构建路径数据缓存Key
     * 
     * @param wareCode 仓库编码
     * @param floor 楼层
     * @return Redis Key
     */
    public static String buildPathDataCacheKey(String wareCode, Integer floor) {
        return String.format(PATH_DATA_CACHE, wareCode, floor);
    }
}

