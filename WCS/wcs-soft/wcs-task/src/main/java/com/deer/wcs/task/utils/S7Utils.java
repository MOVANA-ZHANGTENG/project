package com.deer.wcs.task.utils;

import com.github.xingshuangs.iot.protocol.s7.enums.EPlcType;
import com.github.xingshuangs.iot.protocol.s7.service.S7PLC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class S7Utils {

    // key = ip:port:s7Type
    private static final ConcurrentHashMap<String, S7PLC> S7_CONNECTION_POOL = new ConcurrentHashMap<>();
    // 每条PLC连接对应的收发锁（防止并发报文混乱）
    private static final ConcurrentHashMap<String, ReentrantLock> PLC_LOCK_MAP = new ConcurrentHashMap<>();

    /**
     * 获取PLC连接对象（DCL双重检查，防止并发重复创建TCP）
     *
     * @param ip     IP地址
     * @param port   端口 默认102
     * @param s7Type PLC型号 0~6
     * @return S7PLC 连接实例；失败返回null
     */
    public S7PLC getS7PLC(String ip, Integer port, Integer s7Type) {
        String key = ip + ":" + port + ":" + s7Type;
        // 第一层快速判断
        S7PLC existPlc = S7_CONNECTION_POOL.get(key);
        if (existPlc != null && existPlc.checkConnected()) {
            return existPlc;
        }

        synchronized (key.intern()) {
            // DCL二次校验
            existPlc = S7_CONNECTION_POOL.get(key);
            if (existPlc != null && existPlc.checkConnected()) {
                return existPlc;
            }

            EPlcType plcType;
            switch (s7Type) {
                case 0:
                    plcType = EPlcType.S200;
                    break;
                case 1:
                    plcType = EPlcType.S200_SMART;
                    break;
                case 2:
                    plcType = EPlcType.S300;
                    break;
                case 3:
                    plcType = EPlcType.S400;
                    break;
                case 4:
                    plcType = EPlcType.S1200;
                    break;
                case 5:
                    plcType = EPlcType.S1500;
                    break;
                case 6:
                    plcType = EPlcType.SINUMERIK_828D;
                    break;
                default:
                    log.error("不支持的PLC类型:{}", s7Type);
                    return null;
            }

            S7PLC s7PLC = new S7PLC(plcType, ip, port);
            s7PLC.setPersistence(true);
            try {
                if (!s7PLC.checkConnected()) {
                    s7PLC.connect();
                }
                S7_CONNECTION_POOL.put(key, s7PLC);
                PLC_LOCK_MAP.computeIfAbsent(key, k -> new ReentrantLock());
                log.info("PLC {}:{} 连接成功，加入连接池 key={}", ip, port, key);
                return s7PLC;
            } catch (Exception e) {
                log.error("PLC {}:{} TCP连接建立失败", ip, port, e);
                return null;
            }
        }
    }

    /**
     * 获取当前PLC对应的收发锁
     */
    public ReentrantLock getPlcLock(String ip, Integer port, Integer s7Type) {
        String key = ip + ":" + port + ":" + s7Type;
        return PLC_LOCK_MAP.get(key);
    }

    /**
     * 主动移除并关闭指定PLC连接
     */
    public void removeConnection(String ip, Integer port, Integer s7Type) {
        String key = ip + ":" + port + ":" + s7Type;
        S7PLC plc = S7_CONNECTION_POOL.remove(key);
        if (plc != null) {
            try {
                plc.close();
                log.warn("主动关闭PLC连接 {}:{}", ip, port);
            } catch (Exception e) {
                log.error("关闭PLC资源异常", e);
            }
        }
        PLC_LOCK_MAP.remove(key);
    }

    /**
     * 清空全部PLC连接（服务重启/切换厂区时调用）
     */
    public void clearAllConnection() {
        log.warn("执行清空全部PLC连接池");
        for (Map.Entry<String, S7PLC> entry : S7_CONNECTION_POOL.entrySet()) {
            try {
                entry.getValue().close();
            } catch (Exception ignored) {
            }
        }
        S7_CONNECTION_POOL.clear();
        PLC_LOCK_MAP.clear();
    }

    /**
     * 定时清理失效连接（30s执行一次）
     */
    @Scheduled(fixedRate = 30000)
    public void cleanInvalidConnection() {
        S7_CONNECTION_POOL.keySet().forEach(key -> {
            S7PLC plc = S7_CONNECTION_POOL.get(key);
            if (plc != null && !plc.checkConnected()) {
                S7_CONNECTION_POOL.remove(key);
                PLC_LOCK_MAP.remove(key);
                try {
                    plc.close();
                    log.warn("定时任务清理失效PLC连接 key={}", key);
                } catch (Exception ex) {
                    log.warn("关闭失效PLC资源异常", ex);
                }
            }
        });
    }
}