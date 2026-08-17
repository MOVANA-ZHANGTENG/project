package com.deer.wcs.xj.device;

import com.deer.wcs.base.model.S7Item;
import com.deer.wcs.xj.config.StationPlcConfig;
import com.github.xingshuangs.iot.protocol.s7.service.S7PLC;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Slf4j
public class PositionXjDevice {
    public static final short SIGNAL_ROLL   = (short) 1;
    public static final short SIGNAL_PASS   = (short) 2;
    public static final short SIGNAL_ERROR  = (short) 99;

    private final S7PLC s7PLC;
    private final int stationUnitId;
    private final String dbReadBase;
    private final String dbWriteBase;
    // 当前PLC连接全局收发锁（同IP共用一把锁）
    private final ReentrantLock plcCommLock;

    //===================== 全局读信号 =====================
    public final S7Item comCheck;
    public final S7Item comOn;
    public final S7Item comOff;
    public final S7Item timeSetAck;
    public final S7Item resetAck;
    public final S7Item bool0;
    public final S7Item bool1;
    public final S7Item bool2;

    //===================== 站台读信号 =====================
    public final S7Item no;
    public final S7Item alarmCode;
    public final S7Item bAutoMode;
    public final S7Item bAlarmMode;
    public final S7Item bTypeBigRequest;
    public final S7Item bTypeSmallRequest;
    public final S7Item bTrayDet;
    public final S7Item bRunInBusy;
    public final S7Item bRunOutBusy;
    public final S7Item bFull;
    public final S7Item bBcrReadOk;
    public final S7Item bBcrReadNg;
    public final S7Item bStaInReady;
    public final S7Item bStaOutReady;
    public final S7Item bResetACK;
    public final S7Item bReReadACK;
    public final S7Item bStopAck;
    public final S7Item bRunAck;

    //===================== 全局写信号 =====================
    public final S7Item wComCheck;
    public final S7Item wTimeSet;
    public final S7Item wSpare;
    public final S7Item wSpare1;
    public final S7Item wSpare2;
    public final S7Item wSpare3;
    public final S7Item wSpare4;
    public final S7Item wSpare5;
    public final S7Item wYear;
    public final S7Item wMonth;
    public final S7Item wDay;
    public final S7Item wHour;
    public final S7Item wMin;
    public final S7Item wSec;

    //===================== 站台写信号 =====================
    public final S7Item wReset;
    public final S7Item wStopCmd;
    public final S7Item wRunCmd;
    public final S7Item wReRead;
    public final S7Item wBcrReadDoAck;
    public final S7Item wBcrReadNgAck;
    public final S7Item wTypeBigRequest;
    public final S7Item wTypeSmallRequest;
    public final S7Item wSafe;
    public final S7Item wResult;

    /**
     * 私有构造，统一由静态工厂创建
     */
    private PositionXjDevice(S7PLC plc, ReentrantLock commLock, StationPlcConfig config) {
        this.s7PLC = plc;
        this.plcCommLock = commLock;
        this.stationUnitId = config.getNo();
        this.dbReadBase = config.getReadBaseAddr();
        this.dbWriteBase = config.getWriteBaseAddr();

        // 全局读信号
        comCheck    = new S7Item(dbReadBase + "0.0", S7Item.BOOL, plc);
        comOn       = new S7Item(dbReadBase + "0.1", S7Item.BOOL, plc);
        comOff      = new S7Item(dbReadBase + "0.2", S7Item.BOOL, plc);
        timeSetAck  = new S7Item(dbReadBase + "0.3", S7Item.BOOL, plc);
        resetAck    = new S7Item(dbReadBase + "0.4", S7Item.BOOL, plc);
        bool0       = new S7Item(dbReadBase + "0.5", S7Item.BOOL, plc);
        bool1       = new S7Item(dbReadBase + "0.6", S7Item.BOOL, plc);
        bool2       = new S7Item(dbReadBase + "0.7", S7Item.BOOL, plc);

        // 全局写信号
        wComCheck   = new S7Item(dbWriteBase + "0.0", S7Item.BOOL, plc);
        wTimeSet    = new S7Item(dbWriteBase + "0.1", S7Item.BOOL, plc);
        wSpare      = new S7Item(dbWriteBase + "0.2", S7Item.BOOL, plc);
        wSpare1     = new S7Item(dbWriteBase + "0.3", S7Item.BOOL, plc);
        wSpare2     = new S7Item(dbWriteBase + "0.4", S7Item.BOOL, plc);
        wSpare3     = new S7Item(dbWriteBase + "0.5", S7Item.BOOL, plc);
        wSpare4     = new S7Item(dbWriteBase + "0.6", S7Item.BOOL, plc);
        wSpare5     = new S7Item(dbWriteBase + "0.7", S7Item.BOOL, plc);
        wYear       = new S7Item(dbWriteBase + "2", S7Item.INT, plc);
        wMonth      = new S7Item(dbWriteBase + "4", S7Item.INT, plc);
        wDay        = new S7Item(dbWriteBase + "6", S7Item.INT, plc);
        wHour       = new S7Item(dbWriteBase + "8", S7Item.INT, plc);
        wMin        = new S7Item(dbWriteBase + "10", S7Item.INT, plc);
        wSec        = new S7Item(dbWriteBase + "12", S7Item.INT, plc);

        // 站台偏移计算
        int stationOffset = (stationUnitId - 1) * 6;
        no                 = new S7Item(dbReadBase + (stationOffset + 2), S7Item.INT, plc);
        alarmCode          = new S7Item(dbReadBase + (stationOffset + 4), S7Item.INT, plc);
        bAutoMode          = new S7Item(dbReadBase + (stationOffset + 6) + ".0", S7Item.BOOL, plc);
        bAlarmMode         = new S7Item(dbReadBase + (stationOffset + 6) + ".1", S7Item.BOOL, plc);
        bTypeBigRequest    = new S7Item(dbReadBase + (stationOffset + 6) + ".2", S7Item.BOOL, plc);
        bTypeSmallRequest  = new S7Item(dbReadBase + (stationOffset + 6) + ".3", S7Item.BOOL, plc);
        bTrayDet           = new S7Item(dbReadBase + (stationOffset + 6) + ".4", S7Item.BOOL, plc);
        bRunInBusy         = new S7Item(dbReadBase + (stationOffset + 6) + ".5", S7Item.BOOL, plc);
        bRunOutBusy        = new S7Item(dbReadBase + (stationOffset + 6) + ".6", S7Item.BOOL, plc);
        bFull              = new S7Item(dbReadBase + (stationOffset + 6) + ".7", S7Item.BOOL, plc);
        bBcrReadOk         = new S7Item(dbReadBase + (stationOffset + 7) + ".0", S7Item.BOOL, plc);
        bBcrReadNg         = new S7Item(dbReadBase + (stationOffset + 7) + ".1", S7Item.BOOL, plc);
        bStaInReady        = new S7Item(dbReadBase + (stationOffset + 7) + ".2", S7Item.BOOL, plc);
        bStaOutReady       = new S7Item(dbReadBase + (stationOffset + 7) + ".3", S7Item.BOOL, plc);
        bResetACK          = new S7Item(dbReadBase + (stationOffset + 7) + ".4", S7Item.BOOL, plc);
        bReReadACK         = new S7Item(dbReadBase + (stationOffset + 7) + ".5", S7Item.BOOL, plc);
        bStopAck           = new S7Item(dbReadBase + (stationOffset + 7) + ".6", S7Item.BOOL, plc);
        bRunAck            = new S7Item(dbReadBase + (stationOffset + 7) + ".7", S7Item.BOOL, plc);

        // 站台写信号偏移
        int writeOffset = (stationUnitId - 1) * 4;
        wReset              = new S7Item(dbWriteBase + (writeOffset + 14) + ".0", S7Item.BOOL, plc);
        wStopCmd            = new S7Item(dbWriteBase + (writeOffset + 14) + ".1", S7Item.BOOL, plc);
        wRunCmd             = new S7Item(dbWriteBase + (writeOffset + 14) + ".2", S7Item.BOOL, plc);
        wReRead             = new S7Item(dbWriteBase + (writeOffset + 14) + ".3", S7Item.BOOL, plc);
        wBcrReadDoAck       = new S7Item(dbWriteBase + (writeOffset + 14) + ".4", S7Item.BOOL, plc);
        wBcrReadNgAck       = new S7Item(dbWriteBase + (writeOffset + 14) + ".5", S7Item.BOOL, plc);
        wTypeBigRequest     = new S7Item(dbWriteBase + (writeOffset + 14) + ".6", S7Item.BOOL, plc);
        wTypeSmallRequest   = new S7Item(dbWriteBase + (writeOffset + 14) + ".7", S7Item.BOOL, plc);
        wSafe               = new S7Item(dbWriteBase + (writeOffset + 15) + ".0", S7Item.BOOL, plc);
        wResult             = new S7Item(dbWriteBase + (writeOffset + 16), S7Item.INT, plc);
    }

    /**
     * 静态工厂 —— 唯一创建入口
     */
    public static PositionXjDevice create(S7PLC plc, ReentrantLock commLock, StationPlcConfig config) {
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
        return new PositionXjDevice(plc, commLock, config);
    }

    //===================== 对外封装方法 =====================

    /**
     * 【业务推荐使用】带锁读取站台编号并校验是否匹配配置
     * 对应你之前那段业务校验逻辑
     * @return true = PLC内部站台编号和本地配置一致
     */
    public boolean verifyValid() {
        plcCommLock.lock();
        try {
            Short plcNo = (Short)no.read();
            if (plcNo == null) {
                log.warn("站台 {} 读取站台编号返回空", stationUnitId);
                return false;
            }
            boolean match = plcNo == stationUnitId;
            if (!match) {
                log.warn("站台校验不匹配！配置No:{}, PLC读取No:{}", stationUnitId, plcNo);
                return false;
            }
            boolean autoMode = (boolean) bAutoMode.read();
            if(!autoMode){
                log.warn("站台{unit"+stationUnitId+"}处于手动模式！");
                return false;
            }
            boolean alarmMode = (boolean) bAlarmMode.read();
            short alarmNo = (short) alarmCode.read();
            if(alarmMode){
                log.warn("站台{unit"+stationUnitId+"}正在报警！");
                log.warn("站台{unit"+stationUnitId+"}正在报警！报警代码【"+alarmNo+"】");
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