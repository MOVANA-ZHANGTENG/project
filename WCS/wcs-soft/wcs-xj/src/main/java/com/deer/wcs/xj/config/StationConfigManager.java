package com.deer.wcs.xj.config;

import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.task.utils.S7Utils;
import com.deer.wcs.xj.device.BcrXjDevice;
import com.deer.wcs.xj.device.PositionXjDevice;
import com.github.xingshuangs.iot.protocol.s7.service.S7PLC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class StationConfigManager {
    private volatile Map<String, StationPlcConfig> stationP1ConfigMap = Collections.emptyMap();
    private volatile Map<String, PositionXjDevice> stationP1PositionMap = Collections.emptyMap();
    private volatile Map<String, BcrXjDevice> stationP1BcrMap = Collections.emptyMap();


    private volatile Map<String, StationPlcConfig> stationP4ConfigMap = Collections.emptyMap();
    private volatile Map<String, PositionXjDevice> stationP4PositionMap = Collections.emptyMap();
    private volatile Map<String, BcrXjDevice> stationP4BcrMap = Collections.emptyMap();

    private final ReentrantLock refreshLock = new ReentrantLock();

    @Autowired
    private PositionInfoService positionInfoService;
    @Autowired
    private S7Utils s7Utils;

    @PostConstruct
    public void initLoad() {
        reloadP1All();
        log.info("P1站台&PLC设备初始化完成，总数：{}", stationP1ConfigMap.size());

//        reloadP4All();
//        log.info("P4站台&PLC设备初始化完成，总数：{}", stationP4ConfigMap.size());
    }

    private void reloadP1All() {
        List<PositionInfo> dbList = positionInfoService.findByWareCode("P1");
        Map<String, StationPlcConfig> tempConfigMap = new HashMap<>(dbList.size());
        Map<String, PositionXjDevice> tempPositionMap = new HashMap<>(dbList.size());

        for (PositionInfo info : dbList) {
            StationPlcConfig cfg = new StationPlcConfig();
            cfg.setWareCode(info.getWareCode());
            // 站台编码
            cfg.setStationCode(info.getCode());
            // 上位编码--对应的库位编码
            cfg.setSubCode(info.getName());
            cfg.setNo(Short.parseShort(info.getCode().substring(4)));
            cfg.setType(info.getType());
            cfg.setIp(info.getPlcIp());
            cfg.setReadBaseAddr("DB101.");
            cfg.setReadByteOffset(info.getAddress1());
            cfg.setWriteBaseAddr("DB100.");
            cfg.setWriteByteOffset(info.getAddress2());
            tempConfigMap.put(cfg.getStationCode(), cfg);

            // 获取当前区域共用PLC连接（同IP复用一条TCP）
            S7PLC sharedPlc = s7Utils.getS7PLC(cfg.getIp(),102,5);
            if (sharedPlc == null) {
                log.error("站台 {} 获取PLC连接失败，ip={}，跳过创建设备", cfg.getStationCode(), cfg.getIp());
                continue;
            }
            ReentrantLock lock = s7Utils.getPlcLock(cfg.getIp(),102,5);
            PositionXjDevice device = PositionXjDevice.create(sharedPlc,lock, cfg);
            tempPositionMap.put(cfg.getStationCode(), device);
        }
        stationP1ConfigMap = Collections.unmodifiableMap(tempConfigMap);
        stationP1PositionMap = Collections.unmodifiableMap(tempPositionMap);

        // 30个bcr
        String ip = "192.168.2.13";
        Map<String, BcrXjDevice> tempBcrMap = new HashMap<>(dbList.size());
        for(short i=1;i<=30;i++){
            S7PLC sharedPlc = s7Utils.getS7PLC(ip,102,5);
            if (sharedPlc == null) {
                log.error("bcr {} 获取PLC连接失败，ip={}，跳过创建设备",i, ip);
                continue;
            }
            ReentrantLock lock = s7Utils.getPlcLock(ip,102,5);
            StationPlcConfig config = new StationPlcConfig();
            config.setNo(i);
            config.setReadBaseAddr("DB101.");
            config.setWriteBaseAddr("DB100.");
            BcrXjDevice device = BcrXjDevice.create(sharedPlc,lock,config);
            tempBcrMap.put("bcr"+i, device);
        }

        stationP1BcrMap = Collections.unmodifiableMap(tempBcrMap);
    }

    private void reloadP4All() {
        List<PositionInfo> dbList = positionInfoService.findByWareCode("P4");
        Map<String, StationPlcConfig> tempConfigMap = new HashMap<>(dbList.size());
        Map<String, PositionXjDevice> tempPositionMap = new HashMap<>(dbList.size());

        for (PositionInfo info : dbList) {
            StationPlcConfig cfg = new StationPlcConfig();
            cfg.setWareCode(info.getWareCode());
            // 站台编码
            cfg.setStationCode(info.getCode());
            // 上位编码--对应的库位编码
            cfg.setSubCode(info.getName());
            cfg.setNo(Short.parseShort(info.getCode().substring(4)));
            cfg.setType(info.getType());
            cfg.setIp(info.getPlcIp());
            cfg.setReadBaseAddr("DB101.");
            cfg.setReadByteOffset(info.getAddress1());
            cfg.setWriteBaseAddr("DB100.");
            cfg.setWriteByteOffset(info.getAddress2());
            tempConfigMap.put(cfg.getStationCode(), cfg);

            // 获取当前区域共用PLC连接（同IP复用一条TCP）
            S7PLC sharedPlc = s7Utils.getS7PLC(cfg.getIp(),102,5);
            if (sharedPlc == null) {
                log.error("站台 {} 获取PLC连接失败，ip={}，跳过创建设备", cfg.getStationCode(), cfg.getIp());
                continue;
            }
            ReentrantLock lock = s7Utils.getPlcLock(cfg.getIp(),102,5);
            PositionXjDevice device = PositionXjDevice.create(sharedPlc,lock, cfg);
            tempPositionMap.put(cfg.getStationCode(), device);
        }
        stationP4ConfigMap = Collections.unmodifiableMap(tempConfigMap);
        stationP4PositionMap = Collections.unmodifiableMap(tempPositionMap);

        // 30个bcr
        String ip = "192.168.2.13";
        Map<String, BcrXjDevice> tempBcrMap = new HashMap<>(dbList.size());
        for(short i=1;i<=30;i++){
            S7PLC sharedPlc = s7Utils.getS7PLC(ip,102,5);
            if (sharedPlc == null) {
                log.error("bcr {} 获取PLC连接失败，ip={}，跳过创建设备",i, ip);
                continue;
            }
            ReentrantLock lock = s7Utils.getPlcLock(ip,102,5);
            StationPlcConfig config = new StationPlcConfig();
            config.setNo(i);
            config.setReadBaseAddr("DB101.");
            config.setWriteBaseAddr("DB100.");
            BcrXjDevice device = BcrXjDevice.create(sharedPlc,lock,config);
            tempBcrMap.put("bcr"+i, device);
        }

        stationP4BcrMap = Collections.unmodifiableMap(tempBcrMap);
    }

    public boolean refreshConfig() {
        if (!refreshLock.tryLock()) {
            log.warn("配置正在刷新，请勿重复调用");
            return false;
        }
        try {
            reloadP1All();
//            reloadP4All();
            log.info("站台配置刷新成功");
            return true;
        } catch (Exception e) {
            log.error("刷新配置异常", e);
            return false;
        } finally {
            refreshLock.unlock();
        }
    }

    public StationPlcConfig getP1Config(String stationCode) {
        return stationP1ConfigMap.get(stationCode);
    }

    public Map<String,StationPlcConfig> getP1AllConfig(){
        return stationP1ConfigMap;
    }

    public PositionXjDevice getP1StationDevice(String stationCode) {
        return stationP1PositionMap.get(stationCode);
    }

    public Map<String, PositionXjDevice> getP1AllPositionDevice(){
        return stationP1PositionMap;
    }

    public BcrXjDevice getP1BcrDevice(String bcrCode){ return stationP1BcrMap.get(bcrCode); }

    public Map<String, BcrXjDevice> getP1AllBcrDevice(){ return stationP1BcrMap; }

    public StationPlcConfig getP4Config(String stationCode) {
        return stationP4ConfigMap.get(stationCode);
    }

    public Map<String,StationPlcConfig> getP4AllConfig(){
        return stationP4ConfigMap;
    }

    public PositionXjDevice getP4StationDevice(String stationCode) {
        return stationP4PositionMap.get(stationCode);
    }

    public Map<String, PositionXjDevice> getP4AllPositionDevice(){
        return stationP4PositionMap;
    }

    public BcrXjDevice getP4BcrDevice(String bcrCode){ return stationP4BcrMap.get(bcrCode); }

    public Map<String, BcrXjDevice> getP4AllBcrDevice(){ return stationP4BcrMap; }
}