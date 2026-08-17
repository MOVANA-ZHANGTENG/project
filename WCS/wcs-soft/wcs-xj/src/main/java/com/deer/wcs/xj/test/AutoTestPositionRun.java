package com.deer.wcs.xj.test;

import com.deer.wcs.base.model.*;
import com.deer.wcs.base.service.PalletInfoService;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.base.service.PositionRecordService;
import com.deer.wcs.base.service.impl.CellInfoServiceImpl;
import com.deer.wcs.common.core.redis.RedisCache;
import com.deer.wcs.rcs.service.RcsCarInfoService;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.PalletRecordService;
import com.deer.wcs.task.service.TaskInfoService;
import com.deer.wcs.xj.config.UnitBcrP1Constant;
import com.deer.wcs.xj.device.BcrXjDevice;
import com.deer.wcs.xj.device.PositionXjDevice;
import com.deer.wcs.xj.config.StationConfigManager;
import com.deer.wcs.xj.config.StationPlcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;
import java.util.Map;

@Component("testPositionRun")
public class AutoTestPositionRun {
    private static final Logger log = LoggerFactory.getLogger(AutoTestPositionRun.class);


    @Autowired
    private StationConfigManager stationConfigManager;
    @Autowired
    private PositionInfoService positionInfoService;
    @Autowired
    private PalletInfoService palletInfoService;
    @Autowired
    private PalletRecordService palletRecordService;
    @Autowired
    private RcsCarInfoService rcsCarInfoService;
    @Autowired
    private PositionRecordService positionRecordService;
    @Autowired
    private CellInfoServiceImpl cellInfoService;
    @Autowired
    private TaskInfoService taskInfoService;
    @Autowired
    private RedisCache redisCache;


    // 获取S7PLC的心跳192.168.2.13
    public void heartBeat(){

    }

    // PLC轮询定时任务（@Scheduled）
    @Scheduled(fixedDelay = 500)
    public void plcPollTask() {
        // ✅ 直接拿内存Map，不查数据库！
        Map<String, StationPlcConfig> allStation = stationConfigManager.getP1AllConfig();
        for (StationPlcConfig cfg : allStation.values()) {
            try {
                PositionXjDevice positionXjDevice = null;
                BcrXjDevice bcrXjDevice = null;
                switch (cfg.getStationCode()){
                    // C1区域1口出入库
                    case "unit2":
//                    case "unit8":
                        positionXjDevice = stationConfigManager.getP1StationDevice(cfg.getStationCode());
                        bcrXjDevice = stationConfigManager.getP1BcrDevice(UnitBcrP1Constant.getBcrCode(cfg.getNo()));
                        if(handleReadCode(positionXjDevice)){
                            // 读取条码--校验
                            String scanCode = getReadCode(cfg, bcrXjDevice);
                            // 写入校验结束-写入idResult(1)
                            applyCodeWMSCheck(scanCode,cfg, positionXjDevice);
                        }else{
                            bcrReadSignalClean(positionXjDevice);
                        }
                        break;
                    case "unit4":
//                    case "unit10":
                        positionXjDevice = stationConfigManager.getP1StationDevice(cfg.getStationCode());
                        bcrXjDevice = stationConfigManager.getP1BcrDevice(UnitBcrP1Constant.getBcrCode(cfg.getNo()));
                        if(handleReadCode(positionXjDevice)){
                            // 读取条码--校验条码是否存在数据库（从redis校验）
                            String scanCode = getReadCode(cfg, bcrXjDevice);
                            applyCodeLocalCheck(scanCode,cfg, positionXjDevice);
                        }else{
                            bcrReadSignalClean(positionXjDevice);
                        }
                        break;
                    case "unit5":
//                    case "unit11":
                        positionXjDevice = stationConfigManager.getP1StationDevice(cfg.getStationCode());
                        bcrXjDevice = stationConfigManager.getP1BcrDevice(UnitBcrP1Constant.getBcrCode(cfg.getNo()));
                        // 刷新安全信号
                        refreshSafe(cfg, positionXjDevice);
                        // 生成入库任务
                        checkInOrOutReady(cfg, positionXjDevice, bcrXjDevice);
                        break;
                }

                // 业务处理
            } catch (Exception e) {
                PositionInfo positionInfo = positionInfoService.findByCode(cfg.getWareCode(), cfg.getStationCode());
                positionInfoService.updateMemo(positionInfo, String.format("站台{}读取异常:{}", cfg.getStationCode(), e), 1);
                log.error("站台{}读取异常", cfg.getStationCode(), e);
            }
        }
    }

    /**
     *  检查站台入库/出库就绪信号是否OK
     * @param cfg
     * @param positionXjDevice
     * @param bcrXjDevice
     */
    private void checkInOrOutReady(StationPlcConfig cfg, PositionXjDevice positionXjDevice, BcrXjDevice bcrXjDevice) throws Exception {
        if(!positionXjDevice.verifyValid()){
            CellInfo cellInfo = cellInfoService.findByCode(cfg.getWareCode(),cfg.getSubCode());
            if(cellInfo==null){
                throw new RuntimeException("站台{"+cfg.getStationCode()+"}对应库位"+cfg.getSubCode()+"不存在！");
            }
            if(cellInfo.getDisableState()==0){
                cellInfo.setDisableState(1L);
                cellInfoService.update(cellInfo);
            }
            log.warn("{} 站台校验不通过，跳过本轮业务","unit"+ positionXjDevice.getStationUnitId());
            return;
        }
        try {
            boolean inReady = positionXjDevice.executeWithLock(
                    ()->(boolean) positionXjDevice.bStaInReady.read());
            if(inReady){
                // 将接驳位禁用取消
                CellInfo cellInfo = cellInfoService.findByCode(cfg.getWareCode(),cfg.getSubCode());
                if(cellInfo==null){
                    throw new RuntimeException("站台{"+cfg.getStationCode()+"}对应库位"+cfg.getSubCode()+"不存在！");
                }
                if(cellInfo.getDisableState()==1){
                    cellInfo.setDisableState(0L);
                    cellInfoService.update(cellInfo);
                }

                String scanCode = getReadCode(cfg, bcrXjDevice);
                String palletCode = conventScanCode(scanCode);
                if(palletCode==null||palletCode.isEmpty()){
                    log.warn("条码读值为空");
                    return;
                }
                PalletInfo palletInfo = palletInfoService.findByCode(cfg.getWareCode(),palletCode);
                if(palletInfo==null){
                    log.warn("托盘在wcs中不存在");
                    return;
                }
                palletInfo.setCellCode(cfg.getStationCode());
                palletInfoService.update(palletInfo);
                palletRecordService.record(palletInfo.getCode(),palletInfo.getWareCode(),1,"托盘到达站台"+cfg.getStationCode());

                Condition taskCon = new Condition(TaskInfo.class);
                taskCon.createCriteria().andEqualTo("wareCode",cfg.getWareCode())
                        .andEqualTo("palletCode",palletCode)
                        .andEqualTo("type","move1")
                        .andEqualTo("fromCellCode",cfg.getSubCode());
                List<TaskInfo> existTasks = taskInfoService.findByCondition(taskCon);
                if(existTasks.size()>0){
                    return;
                }

                // 分配入库库位
                int floorZ = 1;
                CellInfo toCell = cellInfoService.allotCellForSxc(cfg.getWareCode(),floorZ,false);
                if(toCell==null){
                    throw new RuntimeException("站台{"+cfg.getStationCode()+"}入库任务库位分配失败！");
                }

                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setWareCode("P1");
                taskInfo.setWareName("P1");
                taskInfo.setType("move1");
                taskInfo.setPalletCode(palletCode);
                taskInfo.setFromCellCode(cfg.getSubCode());
                taskInfo.setToCellCode(toCell.getCode());
                taskInfo.setPriority(10);
                taskInfoService.save(taskInfo);

                toCell.setTaskState(taskInfo.getId());
                cellInfoService.update(toCell);
                log.info("生成入库任务");
            }

            // 出库ready就绪，解放库位
            boolean outReady = positionXjDevice.executeWithLock(
                    ()->(boolean) positionXjDevice.bStaOutReady.read());
            if(outReady){
                CellInfo cellInfo = cellInfoService.findByCode(cfg.getWareCode(),cfg.getSubCode());
                if(cellInfo==null){
                    throw new RuntimeException("站台{"+cfg.getStationCode()+"}对应库位"+cfg.getSubCode()+"不存在！");
                }
                if(cellInfo.getInvenState()==1){
                    cellInfo.setInvenState(0L);
                    cellInfoService.update(cellInfo);
                }
                if(cellInfo.getDisableState()==1){
                    cellInfo.setDisableState(0L);
                    cellInfoService.update(cellInfo);
                }
            }

            // 都不满足---将接驳位库位禁用
            if(!inReady&&!outReady){
                CellInfo cellInfo = cellInfoService.findByCode(cfg.getWareCode(),cfg.getSubCode());
                if(cellInfo==null){
                    throw new RuntimeException("站台{"+cfg.getStationCode()+"}对应库位"+cfg.getSubCode()+"不存在！");
                }
                if(cellInfo.getDisableState()==0){
                    cellInfo.setDisableState(1L);
                    cellInfoService.update(cellInfo);
                }
            }

            // 托盘离开位置将库位货物状态置于0

        } catch (Exception e) {
            log.error("读取站台{}信号异常",cfg.getStationCode(),e);
            throw e;
        }
    }

    /*
        扫码
        1. 读取到plc扫码成功信号
        2. 未读取到plc扫码成功信号
     */
    public boolean handleReadCode(PositionXjDevice device){
        if(!device.verifyValid()){
            log.warn("{} 站台校验不通过，跳过本轮业务","unit"+device.getStationUnitId());
            return false;
        }
        try {
            return device.executeWithLock(() -> (boolean) device.bBcrReadOk.read());
        } catch (Exception e) {
            log.error("读取bBcrReadOk信号异常", e);
            return false;
        }
    }

    /*
        读取条码值并返回
     */
    public String getReadCode(StationPlcConfig cfg, BcrXjDevice bcrXjDevice){
        if (!bcrXjDevice.verifyValid(cfg.getNo())) {
            log.warn("{} bcr校验不通过，跳过本轮业务", "bcr" + bcrXjDevice.getStationUnitId());
            return null;
        }
        try {
            return bcrXjDevice.executeWithLock(() -> (String) bcrXjDevice.scanCode.read());
        } catch (Exception e) {
            log.error("读取Bcr条码信号异常", e);
            return null;
        }
    }

    /*
        申请WMS校验
     */
    public void applyCodeWMSCheck(String scanCode, StationPlcConfig cfg, PositionXjDevice positionXjDevice){
        // 校验
        boolean valid = scanCode!=null&&!scanCode.isEmpty();
        if(valid){
            if(scanCode.length()<4||scanCode.length()>200){
                valid = false;
            }
        }
        String palletCode = null;
        if(valid){
            palletCode = conventScanCode(scanCode);
            if(palletCode==null||palletCode.isEmpty()){
                valid = false;
            }
        }

        String isEmpty = "1";
        String typeCode = "metal";
        String typeName = "plastic";
        if(valid){
            // 申请wms接口请求
        }
        if(valid){
            // wcs添加托盘记录并修改相应状态
            PalletInfo palletInfo = palletInfoService.findByCode(palletCode);
            if(palletInfo==null){
                palletInfo = new PalletInfo();
                palletInfo.setWareCode(cfg.getWareCode());
                palletInfo.setCode(palletCode);
                palletInfo.setName(palletCode);
                palletInfo.setCellCode(cfg.getStationCode());
                palletInfo.setIsEmpty(isEmpty);
                palletInfo.setTypeCode(typeCode);
                palletInfo.setTypeName(typeName);
                palletInfo.setState("0");
                palletInfo.setDirection("IN");
                palletInfoService.save(palletInfo);
            }else{
                palletInfo.setWareCode(cfg.getWareCode());
                palletInfo.setCellCode(cfg.getStationCode());
                palletInfo.setIsEmpty(isEmpty);
                palletInfo.setTypeCode(typeCode);
                palletInfo.setTypeName(typeName);
                palletInfo.setDirection("IN");
                palletInfoService.update(palletInfo);
            }
            palletRecordService.record(palletInfo.getCode(),palletInfo.getWareCode(),1,"托盘到达站台"+cfg.getStationCode());
            log.info("托盘{}到达站台"+cfg.getStationCode(),palletInfo.getCode());
        }

        boolean finalValid = valid;
        positionXjDevice.runWithLock(() -> {
            if (finalValid) {
                positionXjDevice.wResult.write(positionXjDevice.SIGNAL_PASS);
                log.info("站台{}直通信号给出",cfg.getStationCode());
            } else {
                positionXjDevice.wResult.write(positionXjDevice.SIGNAL_ERROR);
                log.info("站台{}异常信号给出",cfg.getStationCode());
            }
            positionXjDevice.wBcrReadDoAck.write(true);
            log.info("站台{}扫码确认信号给出",cfg.getStationCode());
        });
    }

    /*
        wcs本地校验
     */
    public void applyCodeLocalCheck(String scanCode, StationPlcConfig cfg, PositionXjDevice positionXjDevice){
        // 校验
        boolean valid = scanCode!=null && !scanCode.isEmpty();
        if(valid){
            if(scanCode.length()<4||scanCode.length()>200){
                valid = false;
            }
        }
        String direction = null;
        String palletCode =null;
        if(valid){
            palletCode = conventScanCode(scanCode);
            if(palletCode==null||palletCode.isEmpty()){
                valid = false;
            }
        }
        if(valid){
            // 申请wcs本地校验
            PalletInfo palletInfo = palletInfoService.findByCode(palletCode);
            if(palletInfo==null){
                valid = false;
            }else{
                palletInfo.setWareCode(cfg.getWareCode());
                palletInfo.setCellCode(cfg.getStationCode());
                palletInfoService.update(palletInfo);
                palletRecordService.record(palletInfo.getCode(),palletInfo.getWareCode(),1,"托盘到达站台"+cfg.getStationCode());
                log.info("托盘{}到达站台"+cfg.getStationCode(),palletInfo.getCode());
                direction = palletInfo.getDirection();
            }
        }

        boolean finalValid = valid;
        String finalDirection = direction;
        positionXjDevice.runWithLock(() -> {
            if (finalValid) {
                if ("IN".equals(finalDirection) || "OUT".equals(finalDirection)) {
                    positionXjDevice.wResult.write(positionXjDevice.SIGNAL_PASS);
                    log.info("站台{}直通信号给出",cfg.getStationCode());
                } else if ("ROLL".equals(finalDirection)) {
                    positionXjDevice.wResult.write(positionXjDevice.SIGNAL_ROLL);
                    log.info("站台{}流转信号给出",cfg.getStationCode());
                }
            } else {
                positionXjDevice.wResult.write(positionXjDevice.SIGNAL_ERROR);
                log.info("站台{}扫码异常信号给出",cfg.getStationCode());
            }
            positionXjDevice.wBcrReadDoAck.write(true);
            log.info("站台{}扫码确认信号给出",cfg.getStationCode());
        });
    }

    private void bcrReadSignalClean(PositionXjDevice positionXjDevice){
        positionXjDevice.runWithLock(() -> {
            boolean ackFlag = (boolean) positionXjDevice.wBcrReadDoAck.read();
            if (ackFlag) {
                positionXjDevice.wBcrReadDoAck.write(false);
            }
        });
    }

    /**
     *  刷新站台safe信号
     * @param positionXjDevice
     */
    private void refreshSafe(StationPlcConfig cfg, PositionXjDevice positionXjDevice){
        if(!positionXjDevice.verifyValid()){
            log.warn("{} 站台校验不通过，跳过本轮业务","unit"+ positionXjDevice.getStationUnitId());
            return;
        }
        // 查询业务数据【锁外执行】
        boolean hasCar = rcsCarInfoService.hasCar(cfg.getWareCode(), cfg.getSubCode());
        // 锁内只执行写入
        positionXjDevice.runWithLock(() -> positionXjDevice.wSafe.write(!hasCar));
    }

    private String conventScanCode(String scanCode){
        if(scanCode==null){
            log.warn("条码读值为空",scanCode);
            return null;
        }
        String[] parts = scanCode.split("\\*");
        if(parts.length<3){
            log.warn("条码分割长度不足，原始条码：{}",scanCode);
            return null;
        }
        return parts[2];
    }

}
