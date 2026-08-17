package com.deer.wcs.xj.handle;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.LineInfo;
import com.deer.wcs.base.model.PalletInfo;
import com.deer.wcs.base.service.*;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import com.deer.wcs.xj.model.P4PlcCheckResult;
import com.deer.wcs.xj.model.P4PlcTaskExpectDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("XjP4CrnHandle")
public class XjP4CrnHandle {

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private DeviceValueService deviceValueService;

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private PositionInfoService positionInfoService;

    @Autowired
    private CellInfoService cellInfoService;

    @Autowired
    private PalletInfoService palletInfoService;


    @Autowired
    private LineInfoService lineInfoService;


    // 箱码入库类型校验（黑箱/纸箱）
    public Boolean inPalletCheck(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        String unit = taskInfo.getFromCellCode();
        Boolean bTypeBigRequeset = (Boolean) deviceValueService.readValueByCode(unit, "bTypeBigRequeset");
        Boolean bTypeSmallRequeset = (Boolean) deviceValueService.readValueByCode(unit, "bTypeSmallRequeset");
        String palletCode = (String) deviceValueService.readValueByCode(unit, "palletCode");

        if (bTypeBigRequeset && bTypeSmallRequeset) {
            jobInfoService.updateMemo(jobInfo, "大===小");
            return false;
        }
        PalletInfo palletInfo = palletInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getPalletCode());
        if (bTypeBigRequeset && palletInfo.getTypeCode().equals("p4_small")) {
            jobInfoService.updateMemo(jobInfo, "大小有误");
            return false;
        }
        if (bTypeSmallRequeset && palletInfo.getTypeCode().equals("p4_big")) {
            jobInfoService.updateMemo(jobInfo, "大小有误");
            return false;
        }
        return true;
    }

    /**
     * 检查是否允许发送miniload指令
     *
     * @param jobInfo
     * @return
     */
    public Boolean canSend(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        // 起点是否存在
        CellInfo from = cellInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getFromCellCode());
        if (from == null) {
            jobInfoService.updateMemo(jobInfo, "起点库位不存在");
            return false;
        }
        // 获取巷道对应的设备
        LineInfo lineInfo = lineInfoService.findByCode(taskInfo.getWareCode(), from.getLineCode());
        String deviceCode = lineInfo.getDeviceCode();
        if (deviceCode.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "巷道" + lineInfo.getCode() + "的设备编码不存在");
            return false;
        }

        /**
         * JobReady	Bool	2.0	602.0 	允许下任务
         * JobBusy	Bool	2.1	602.1 	不允许
         */
        Boolean jobReady = (Boolean) deviceValueService.readValueByCode(deviceCode, "JobReady");
        Boolean JobBusy = (Boolean) deviceValueService.readValueByCode(deviceCode, "JobBusy");

        if (JobBusy) {
            jobInfoService.updateMemo(jobInfo, "设备" + deviceCode + "JobBusy=" + JobBusy);
            return false;
        }

        if (!jobReady) {
            jobInfoService.updateMemo(jobInfo, "设备" + deviceCode + "JobReady=" + jobReady);
            return false;
        }

        // 检测无任务--------清空设备点位信号
        if (!JobBusy) {
            deviceValueService.writeValueByCode(deviceCode, "iFKFromLineWcs", (short) 0);
            deviceValueService.writeValueByCode(deviceCode, "iFKFromRowWcs", (short) 0);
            deviceValueService.writeValueByCode(deviceCode, "iFKFromFloorWcs", (short) 0);
            deviceValueService.writeValueByCode(deviceCode, "iFkToLineWcs", (short) 0);
            deviceValueService.writeValueByCode(deviceCode, "iFKToRowWcs", (short) 0);
            deviceValueService.writeValueByCode(deviceCode, "iFKToFloorWcs", (short) 0);
            deviceValueService.writeValueByCode(deviceCode, "iForkTypeWcs", (short) 0);
            deviceValueService.writeValueByCode(deviceCode, "StrTrayId", "0");
            deviceValueService.writeValueByCode(deviceCode, "JobOrder", false);
        }

        jobInfoService.updateMemo(jobInfo, "【miniLoad】设备" + deviceCode + "允许下任务检测通过");

        return true;

    }

    /**
     * 读取PLC点位并和预期值比对
     *
     * @param deviceCode 设备编码
     * @param expect     预期下发值DTO
     * @return 校验结果（包含所有实际读取值）
     */
    private P4PlcCheckResult checkPlcTaskData(String deviceCode, P4PlcTaskExpectDto expect) {
        P4PlcCheckResult result = new P4PlcCheckResult();
        result.setExpect(expect);

        // 读取所有点位
        String actFromFloor = deviceValueService.readValueByCode(deviceCode, "iFKFromFloorWcs").toString();
        String actFromLine = deviceValueService.readValueByCode(deviceCode, "iFKFromLineWcs").toString();
        String actFromRow = deviceValueService.readValueByCode(deviceCode, "iFKFromRowWcs").toString();
        String actToX = deviceValueService.readValueByCode(deviceCode, "iFkToLineWcs").toString();
        String actToY = deviceValueService.readValueByCode(deviceCode, "iFKToRowWcs").toString();
        String actToZ = deviceValueService.readValueByCode(deviceCode, "iFKToFloorWcs").toString();
        String actForkType = deviceValueService.readValueByCode(deviceCode, "iForkTypeWcs").toString();
        String actTrayId = deviceValueService.readValueByCode(deviceCode, "StrTrayId").toString();

        result.setActFromFloor(actFromFloor);
        result.setActFromLine(actFromLine);
        result.setActFromRow(actFromRow);
        result.setActToX(actToX);
        result.setActToY(actToY);
        result.setActToZ(actToZ);
        result.setActForkType(actForkType);
        result.setActTrayId(actTrayId);

        try {
            boolean checkOk = expect.getFromFloor() == Short.parseShort(actFromFloor)
                    && expect.getFromLine() == Short.parseShort(actFromLine)
                    && expect.getFromRow() == Short.parseShort(actFromRow)
                    && expect.getToX() == Short.parseShort(actToX)
                    && expect.getToY() == Short.parseShort(actToY)
                    && expect.getToZ() == Short.parseShort(actToZ)
                    && expect.getForkType() == Short.parseShort(actForkType)
                    && expect.getTrayId().equals(actTrayId);

            result.setPass(checkOk);
        } catch (NumberFormatException e) {
            // 转换异常直接判定失败
            e.printStackTrace();
            result.setPass(false);
        }
        return result;
    }

    /**
     * 向miniLoad下发入库任务
     *
     * @param jobInfo
     * @return
     */
    public Boolean sendInTask(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }

        // 校验起点位置是否存在
        CellInfo from = cellInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getFromCellCode());
        if (from == null) {
            jobInfoService.updateMemo(jobInfo, "起点库位不存在");
            return false;
        }

        // 获取miniLoad设备编码
        LineInfo lineInfo = lineInfoService.findByCode(taskInfo.getWareCode(), from.getLineCode());
        String deviceCode = lineInfo.getDeviceCode();
        if (deviceCode.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "巷道" + lineInfo.getCode() + "的设备编码不存在");
            return false;
        }

        if (!canSend(jobInfo)) {
            return false;
        }

        /**
         * iFKFromLine	Int	24.0	624.0 	x
         * iFKFromRow	Int	26.0	626.0 	y
         * iFKFromFloor	Int	28.0	628.0 	z
         *
         * iFkToLine	Int	30.0	630.0
         * iFKToRow	    Int	32.0	632.0
         * iFKToFloor	Int	34.0	634.0
         *
         * StrTrayId	String	    44.0	644.0 	任务号
         * JobOrder	    Bool	    1.0	    601.0 	开始任务
         *
         */

//        String fromCellCode = jobInfo.getFromCellCode();
//        if(fromCellCode.isEmpty()){
//            jobInfoService.updateMemo(jobInfo,"起始单元格编码不存在");
//            return false;
//        }
//        CellInfo fromCell = cellInfoService.findByCode(taskInfo.getWareCode(), fromCellCode);
//        if (fromCell == null) {
//            jobInfoService.updateMemo(jobInfo, "起始库位无效：" + fromCellCode);
//            return false;
//        }

        // 校验终点
        String toCellCode = jobInfo.getToCellCode();
        if (toCellCode.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "目标单元格编码不存在");
            return false;
        }
        CellInfo toCell = cellInfoService.findByCode(taskInfo.getWareCode(), toCellCode);
        if (toCell == null) {
            jobInfoService.updateMemo(jobInfo, "目标库位无效：" + toCellCode);
            return false;
        }

        // 校验托盘
        PalletInfo palletInfo = palletInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getPalletCode());
        if (palletInfo == null) {
            jobInfoService.updateMemo(jobInfo, "托盘不存在");
            return false;
        }

        // 获取箱子类型
        Short iForkType = 0;
        if (palletInfo.getTypeCode().equals("p4_small")) {
            iForkType = 1;
        }
        if (palletInfo.getTypeCode().equals("p4_big")) {
            iForkType = 2;
        }
        if (iForkType == 0) {
            jobInfoService.updateMemo(jobInfo, "托盘类型不存在");
            return false;
        }
        // 箱子类型不同
        short fromFloorValue = 2;
        if (iForkType == 1) {
            fromFloorValue = (short) 2;
        }
        if (iForkType == 2) {
            fromFloorValue = (short) 3;
        }
        deviceValueService.writeValueByCode(deviceCode, "iFKFromLineWcs", (short) 0);
        deviceValueService.writeValueByCode(deviceCode, "iFKFromRowWcs", (short) 0);
        deviceValueService.writeValueByCode(deviceCode, "iFKFromFloorWcs", fromFloorValue);
        deviceValueService.writeValueByCode(deviceCode, "iFkToLineWcs", toCell.getX().shortValue());
        deviceValueService.writeValueByCode(deviceCode, "iFKToRowWcs", toCell.getY().shortValue());
        deviceValueService.writeValueByCode(deviceCode, "iFKToFloorWcs", toCell.getZ().shortValue());
        deviceValueService.writeValueByCode(deviceCode, "iForkTypeWcs", iForkType);

        deviceValueService.writeValueByCode(deviceCode, "StrTrayId", jobInfo.getId().toString());
        try {
            Thread.sleep(100);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 组装预期DTO
        P4PlcTaskExpectDto expectDTO = new P4PlcTaskExpectDto();
        expectDTO.setFromLine((short) 0);
        expectDTO.setFromRow((short) 0);
        expectDTO.setFromFloor(fromFloorValue);
        expectDTO.setToX(toCell.getX().shortValue());
        expectDTO.setToY(toCell.getY().shortValue());
        expectDTO.setToZ(toCell.getZ().shortValue());
        expectDTO.setForkType(iForkType);
        expectDTO.setTrayId(jobInfo.getId().toString());

        // 执行校验
        P4PlcCheckResult checkResult = checkPlcTaskData(deviceCode, expectDTO);
        if (!checkResult.isPass()) {
            String errorLog = checkResult.buildDiffLog();
//            log.error(errorLog);
            jobInfoService.updateMemo(jobInfo, "任务信息校验失败:" + errorLog);
            return false;
        }

        deviceValueService.writeValueByCode(deviceCode, "JobOrder", true);
        jobInfoService.updateMemo(jobInfo, "任务发送成功:" + palletInfo.getTypeCode());
        return true;
    }


    /**
     * miniLoad发送出库任务
     *
     * @param jobInfo
     * @return
     */
    public Boolean sendOutTask(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        CellInfo from = cellInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getFromCellCode());
        if (from == null) {
            jobInfoService.updateMemo(jobInfo, "起点库位不存在");
            return false;
        }
        LineInfo lineInfo = lineInfoService.findByCode(taskInfo.getWareCode(), from.getLineCode());
        String deviceCode = lineInfo.getDeviceCode();
        if (deviceCode.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "巷道" + lineInfo.getCode() + "的设备编码不存在");
            return false;
        }
        if (!canSend(jobInfo)) {
            return false;
        }
        /**
         * iFKFromLine	Int	24.0	624.0 	x
         * iFKFromRow	Int	26.0	626.0 	y
         * iFKFromFloor	Int	28.0	628.0 	z
         * iFkToLine	Int	30.0	630.0
         * iFKToRow	Int	32.0	632.0
         * iFKToFloor	Int	34.0	634.0
         * StrTrayId	String	44.0	644.0 	任务号
         * JobOrder	Bool	1.0	601.0 	开始任务
         *
         */

        String fromCellCode = jobInfo.getFromCellCode();
        if (fromCellCode.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "起始单元格编码不存在");
            return false;
        }
        CellInfo fromCell = cellInfoService.findByCode(taskInfo.getWareCode(), fromCellCode);
        if (fromCell == null) {
            jobInfoService.updateMemo(jobInfo, "起始库位无效：" + fromCellCode);
            return false;
        }

//        String toCellCode = jobInfo.getToCellCode();
//        if (toCellCode.isEmpty()) {
//            jobInfoService.updateMemo(jobInfo, "目标单元格编码不存在");
//            return false;
//        }
//        CellInfo toCell = cellInfoService.findByCode(taskInfo.getWareCode(), toCellCode);
//        if (toCell == null) {
//            jobInfoService.updateMemo(jobInfo, "目标库位无效：" + toCellCode);
//            return false;
//        }


        PalletInfo palletInfo = palletInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getPalletCode());
        if (palletInfo == null) {
            jobInfoService.updateMemo(jobInfo, "托盘不存在");
            return false;
        }
        // 判断箱子类型
        Short iForkType = 0;
        if (palletInfo.getTypeCode().equals("p4_small")) {
            iForkType = 1;
        }
        if (palletInfo.getTypeCode().equals("p4_big")) {
            iForkType = 2;
        }
        if (iForkType == 0) {
            jobInfoService.updateMemo(jobInfo, "托盘类型不存在");
            return false;
        }

        deviceValueService.writeValueByCode(deviceCode, "iFKFromLineWcs", fromCell.getX().shortValue());
        deviceValueService.writeValueByCode(deviceCode, "iFKFromRowWcs", fromCell.getY().shortValue());
        deviceValueService.writeValueByCode(deviceCode, "iFKFromFloorWcs", fromCell.getZ().shortValue());
        deviceValueService.writeValueByCode(deviceCode, "iFkToLineWcs", (short) 0);
        deviceValueService.writeValueByCode(deviceCode, "iFKToRowWcs", (short) 0);
        deviceValueService.writeValueByCode(deviceCode, "iFKToFloorWcs", (short) 1);
        deviceValueService.writeValueByCode(deviceCode, "iForkTypeWcs", iForkType);

        deviceValueService.writeValueByCode(deviceCode, "StrTrayId", jobInfo.getId().toString());
        try {
            Thread.sleep(100);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 校验任务写入是否成功
        // 组装预期DTO
        P4PlcTaskExpectDto expectDTO = new P4PlcTaskExpectDto();
        expectDTO.setFromFloor(fromCell.getX().shortValue());
        expectDTO.setFromLine(fromCell.getY().shortValue());
        expectDTO.setFromRow(fromCell.getZ().shortValue());
        expectDTO.setToX((short) 0);
        expectDTO.setToY((short) 0);
        expectDTO.setToZ((short) 1);
        expectDTO.setForkType(iForkType);
        expectDTO.setTrayId(jobInfo.getId().toString());

        // 执行校验
        P4PlcCheckResult checkResult = checkPlcTaskData(deviceCode, expectDTO);
        if (!checkResult.isPass()) {
            String errorLog = checkResult.buildDiffLog();
//            log.error(errorLog);
            jobInfoService.updateMemo(jobInfo, "任务信息校验失败:" + errorLog);
            return false;
        }

        deviceValueService.writeValueByCode(deviceCode, "JobOrder", true);
        jobInfoService.updateMemo(jobInfo, "任务发送成功:" + palletInfo.getTypeCode());
        return true;
    }

    /**
     * 检查miniLoad任务是否完成
     *
     * @param jobInfo
     * @return
     */
    public Boolean canFinish(JobInfo jobInfo) {

        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        CellInfo fromCell = cellInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getFromCellCode());
        if (fromCell == null) {
            jobInfoService.updateMemo(jobInfo, "起点库位不存在");
            return false;
        }

//        CellInfo toCell = cellInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getToCellCode());
//        if (toCell == null) {
//            jobInfoService.updateMemo(jobInfo, "终点库位不存在");
//            return false;
//        }

        LineInfo lineInfo = lineInfoService.findByCode(taskInfo.getWareCode(), fromCell.getLineCode());
        String deviceCode = lineInfo.getDeviceCode();
        if (deviceCode.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "巷道" + lineInfo.getCode() + "的设备编码不存在");
            return false;
        }

        PalletInfo palletInfo = palletInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getPalletCode());
        if (palletInfo == null) {
            jobInfoService.updateMemo(jobInfo, "托盘不存在");
            return false;
        }
        // 判断箱子类型
        Short iForkType = 0;
        if (palletInfo.getTypeCode().equals("p4_small")) {
            iForkType = 1;
        }
        if (palletInfo.getTypeCode().equals("p4_big")) {
            iForkType = 2;
        }

        // 校验任务写入是否成功
        // 组装预期DTO
        P4PlcTaskExpectDto expectDTO = new P4PlcTaskExpectDto();
        expectDTO.setFromFloor(fromCell.getX().shortValue());
        expectDTO.setFromLine(fromCell.getY().shortValue());
        expectDTO.setFromRow(fromCell.getZ().shortValue());
//        expectDTO.setToX(toCell.getX().shortValue());
//        expectDTO.setToY(toCell.getX().shortValue());
//        expectDTO.setToZ(toCell.getX().shortValue());
        expectDTO.setForkType(iForkType);
        expectDTO.setTrayId(jobInfo.getId().toString());

        // 执行校验
        P4PlcCheckResult checkResult = checkPlcTaskData(deviceCode, expectDTO);
        if (!checkResult.isPass()) {
            String errorLog = checkResult.buildDiffLog();
//            log.error(errorLog);
            jobInfoService.updateMemo(jobInfo, "任务信息校验失败:" + errorLog);
            return false;
        }

        Boolean JobBusy = (Boolean) deviceValueService.readValueByCode(deviceCode, "JobBusy");

        if (JobBusy) {
            deviceValueService.writeValueByCode(deviceCode, "iFKFromLineWcs", (short) 0);
            deviceValueService.writeValueByCode(deviceCode, "iFKFromRowWcs", (short) 0);
            deviceValueService.writeValueByCode(deviceCode, "iFKFromFloorWcs", (short) 0);
            deviceValueService.writeValueByCode(deviceCode, "iFkToLineWcs", (short) 0);
            deviceValueService.writeValueByCode(deviceCode, "iFKToRowWcs", (short) 0);
            deviceValueService.writeValueByCode(deviceCode, "iFKToFloorWcs", (short) 0);
            deviceValueService.writeValueByCode(deviceCode, "iForkTypeWcs", (short) 0);

            deviceValueService.writeValueByCode(deviceCode, "StrTrayId", "0");
            deviceValueService.writeValueByCode(deviceCode, "JobOrder", false);
        }

        /**
         * JobComplete
         * JobFromComplete
         * JobToComplete
         */
        Boolean jobComplete = (Boolean) deviceValueService.readValueByCode(deviceCode, "JobComplete");
        Boolean jobFromComplete = (Boolean) deviceValueService.readValueByCode(deviceCode, "JobFromComplete");
        Boolean jobToComplete = (Boolean) deviceValueService.readValueByCode(deviceCode, "JobToComplete");

        if (jobComplete) {
            jobInfoService.updateMemo(jobInfo, "堆垛机任务完成");
            return true;
        }
        if (jobToComplete) {
            jobInfoService.updateMemo(jobInfo, "堆垛机到达终点");
            return false;
        }
        if (jobFromComplete) {
            jobInfoService.updateMemo(jobInfo, "堆垛机到达起点");
            return false;
        }

        jobInfoService.updateMemo(jobInfo, "堆垛机任务未完成");
        return false;

        /**


         JOB FROM COMPLETE(ON)		→

         JOB TO COMPLETE(ON)		→
         JOB COMPLETE(ON)		→

         ←		JOB COMPLETE ACK(ON)


         JOB FROM COMPLETE(OFF)		→
         JOB TO COMPLETE(OFF)		→
         JOB COMPLETE(OFF)		→

         ←		JOB COMPLETE ACK(OFF)

         */
    }

    /**
     * P4堆垛机任务成功回调
     * ---更新托盘位置
     *
     * @param jobInfo
     * @return
     */
    public Boolean finish1(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        CellInfo from = cellInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getFromCellCode());
        if (from == null) {
            jobInfoService.updateMemo(jobInfo, "起点库位不存在");
            return false;
        }
        LineInfo lineInfo = lineInfoService.findByCode(taskInfo.getWareCode(), from.getLineCode());
        String deviceCode = lineInfo.getDeviceCode();
        if (deviceCode.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "巷道" + lineInfo.getCode() + "的设备编码不存在");
            return false;
        }

        PalletInfo palletInfo = palletInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getPalletCode());
        palletInfo.setCellCode(taskInfo.getToCellCode());
        palletInfoService.update(palletInfo);

        deviceValueService.writeValueByCode(deviceCode, "JobCompleteAck", true);
        jobInfoService.updateMemo(jobInfo, "JobCompleteAck写true成功");
        return true;

    }

    /**
     * 成功回调----清空任务完成确认信号
     *
     * @param jobInfo
     * @return
     */
    public Boolean finish2(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if (taskInfo == null) {
            jobInfoService.updateMemo(jobInfo, "任务不存在");
            return false;
        }
        CellInfo from = cellInfoService.findByCode(taskInfo.getWareCode(), taskInfo.getFromCellCode());
        if (from == null) {
            jobInfoService.updateMemo(jobInfo, "起点库位不存在");
            return false;
        }
        LineInfo lineInfo = lineInfoService.findByCode(taskInfo.getWareCode(), from.getLineCode());
        String deviceCode = lineInfo.getDeviceCode();
        if (deviceCode.isEmpty()) {
            jobInfoService.updateMemo(jobInfo, "巷道" + lineInfo.getCode() + "的设备编码不存在");
            return false;
        }

        Boolean jobComplete = (Boolean) deviceValueService.readValueByCode(deviceCode, "JobComplete");
        Boolean jobFromComplete = (Boolean) deviceValueService.readValueByCode(deviceCode, "JobFromComplete");
        Boolean jobToComplete = (Boolean) deviceValueService.readValueByCode(deviceCode, "JobToComplete");

        if (jobComplete) {
            jobInfoService.updateMemo(jobInfo, "jobComplete未清零");
            return false;
        }
        if (jobToComplete) {
            jobInfoService.updateMemo(jobInfo, "jobToComplete未清零");
            return false;
        }
        if (jobFromComplete) {
            jobInfoService.updateMemo(jobInfo, "jobFromComplete未清零");
            return false;
        }

        deviceValueService.writeValueByCode(deviceCode, "JobOrder", false);
        deviceValueService.writeValueByCode(deviceCode, "JobCompleteAck", false);
        jobInfoService.updateMemo(jobInfo, "JobCompleteAck写false成功");
        return true;

    }

}
