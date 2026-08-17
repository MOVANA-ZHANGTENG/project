package com.deer.wcs.xj.device;

import com.deer.wcs.base.model.S7Item;
import com.deer.wcs.xj.config.StationPlcConfig;
import com.github.xingshuangs.iot.protocol.s7.service.S7PLC;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Slf4j
public class BcrXjDevice {
    private final S7PLC s7PLC;
    private final int stationUnitId;
    private final String dbReadBase;
    private final String dbWriteBase;
    // 当前PLC连接全局收发锁（同IP共用一把锁）
    private final ReentrantLock plcCommLock;

    public final S7Item no;
    public final S7Item scanCode;

    /**
     * 私有构造，统一由静态工厂创建
     */
    private BcrXjDevice(S7PLC plc, ReentrantLock commLock, StationPlcConfig config) {
        this.s7PLC = plc;
        this.plcCommLock = commLock;
        this.stationUnitId = config.getNo();
        this.dbReadBase = config.getReadBaseAddr();
        this.dbWriteBase = config.getWriteBaseAddr();

        // 站台偏移计算
        int stationOffset = (stationUnitId - 1) * 258;
        no = new S7Item(dbReadBase + (stationOffset + 2102), S7Item.INT, plc);
        scanCode = new S7Item(dbReadBase + (stationOffset + 2104), S7Item.STRING, plc);
    }

    /**
     * 静态工厂 —— 唯一创建入口
     */
    public static BcrXjDevice create(S7PLC plc, ReentrantLock commLock, StationPlcConfig config) {
        if (plc == null) {
            log.error("S7PLC 对象为空，无法创建设备");
            throw new IllegalArgumentException("S7PLC cannot be null");
        }
        if (config == null) {
            log.error("StationPlcConfig 配置为空");
            throw new IllegalArgumentException("StationPlcConfig cannot be null");
        }
        if (commLock == null) {
            log.error("PLC通信锁不能为空");
            throw new IllegalArgumentException("commLock cannot be null");
        }
        return new BcrXjDevice(plc, commLock, config);
    }//===================== 对外封装方法 =====================

    /**
     * 【业务推荐使用】带锁读取站台编号并校验是否匹配配置
     * 对应你之前那段业务校验逻辑
     * @return true = PLC内部站台编号和本地配置一致
     */
    public boolean verifyValid(short unitNo) {
        plcCommLock.lock();
        try {
            Short plcNo = (Short)no.read();
            if (plcNo == null) {
                log.warn("站台 {} 读取站台编号返回空", stationUnitId);
                return false;
            }
            boolean match = plcNo == unitNo;
            if (!match) {
                log.warn("读取站台{}与站台{}校验不匹配！", plcNo, unitNo);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("读取站台编号通信异常 stationUnitId={}", stationUnitId, e);
            return false;
        } finally {
            plcCommLock.unlock();
        }
    }

    /**
     * 获取通信锁，供上层轮询任务自定义读写使用
     */
    public ReentrantLock getCommLock() {
        return plcCommLock;
    }

    public int getStationUnitId() {
        return stationUnitId;
    }

    // 有返回值（读信号）
    public <T> T executeWithLock(Supplier<T> action) throws Exception {
        plcCommLock.lock();
        try {
            return action.get();
        } finally {
            plcCommLock.unlock();
        }
    }

    // 无返回值（写信号专用！重点新增）
    public void runWithLock(Runnable action) {
        plcCommLock.lock();
        try {
            action.run();
        } catch (Exception e) {
            log.error("PLC读写信号异常", e);
        } finally {
            plcCommLock.unlock();
        }
    }
}