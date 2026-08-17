package com.deer.wcs.rcs.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.deer.wcs.common.core.domain.BaseEntity;

/**
 * 提升机对象 rcs_tsj
 * 
 * @author deer
 * @date 2026-05-10
 */
public class RcsTsj
{
    private static final long serialVersionUID = 1L;

    /** 提升机主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 提升机编号 */
    @Excel(name = "提升机编号")
    private String code;

    /** 提升机名称 */
    @Excel(name = "提升机名称")
    private String name;

    /** 设备型号 */
    @Excel(name = "设备型号")
    private String model;

    /** 设备位置 */
    @Excel(name = "设备位置")
    private String cellCode;

    @Excel(name = "接驳位")
    private String dockCellCode;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String wareName;

    /** 创建时间 */
    private String createTime;

    /** 当前状态 空闲 / 运行中 / 故障 / 维护中 / 充电中 */
    /** idle/running/fault/maintenance/charging */
    @Excel(name = "当前状态 空闲 / 运行中 / 故障 / 维护中 / 充电中")
    private String status;

    /** 当前层数 */
    @Excel(name = "当前层数")
    private Integer currentFloor;

    /** 目标层数 */
    @Excel(name = "目标层数")
    private Integer targetFloor;

    /** 运行方向  上行 / 下行 / 静止 */
    @Excel(name = "运行方向  上行 / 下行 / 静止")
    private String moveDirection;

    /** 负载状态 布尔值（空载 / 负载中） */
    @Excel(name = "负载状态 布尔值", readConverterExp = "空=载,/=,负=载中")
    private Boolean loadStatus;

    /** 实时速度 */
    @Excel(name = "实时速度")
    private BigDecimal realTimeSpeed;

    /** 累计运行时长  分钟 */
    @Excel(name = "累计运行时长  分钟")
    private Integer totalOperationTime;

    /** 当前任务ID */
    @Excel(name = "当前任务ID")
    private Long currentTaskId;

    /** 待执行任务列表（JSON 格式） */
    @Excel(name = "待执行任务列表", readConverterExp = "J=SON,格=式")
    private String taskQueue;

    /** 任务类型 */
    @Excel(name = "任务类型")
    private String taskType;

    /** 任务启动时间 */
    @Excel(name = "任务启动时间")
    private String taskStartTime;

    /** 任务完成时间 */
    @Excel(name = "任务完成时间")
    private String taskEndTime;

    /** 关联小车ID */
    @Excel(name = "关联小车ID")
    private Long carId;

    /** 额定载重 kg */
    @Excel(name = "额定载重 kg")
    private BigDecimal ratedLoadCapacity;

    /** 长 × 宽 × 高（mm） */
    @Excel(name = "长 × 宽 × 高", readConverterExp = "m=m")
    private String interiorDimensions;

    /** 电机功率 单位：kW */
    @Excel(name = "电机功率 单位：kW")
    private BigDecimal motorPower;

    /** 充电接口标准 */
    @Excel(name = "充电接口标准")
    private String chargingInterfaceType;

    /** 充电功率 */
    @Excel(name = "充电功率")
    private String chargingPower;

    /** 通信协议 */
    @Excel(name = "通信协议")
    private String communicationProtocol;

    /** 定位精度  定位精度 (mm) */
    @Excel(name = "定位精度  定位精度 (mm)")
    private Integer positioningAccuracy;

    /** 错误识别码 */
    @Excel(name = "错误识别码")
    private String errorCode;

    /** 报警类型枚举 */
    @Excel(name = "报警类型枚举")
    private String alarmType;

    /** 错误信息 */
    @Excel(name = "错误信息")
    private String errorMessage;

    /** 权限楼层范围  (1,3) */
    @Excel(name = "权限楼层范围  (1,3)")
    private String authorizedFloorRange;

    /** plc设备关联编码 */
    @Excel(name = "plc设备关联编码")
    private String deviceCode;

    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCellCode() {
        return cellCode;
    }

    public void setCellCode(String cellCode) {
        this.cellCode = cellCode;
    }

    public String getDockCellCode() {
        return dockCellCode;
    }

    public void setDockCellCode(String dockCellCode) {
        this.dockCellCode = dockCellCode;
    }

    public String getWareCode() {
        return wareCode;
    }

    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }

    public String getWareName() {
        return wareName;
    }

    public void setWareName(String wareName) {
        this.wareName = wareName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(Integer currentFloor) {
        this.currentFloor = currentFloor;
    }

    public Integer getTargetFloor() {
        return targetFloor;
    }

    public void setTargetFloor(Integer targetFloor) {
        this.targetFloor = targetFloor;
    }

    public String getMoveDirection() {
        return moveDirection;
    }

    public void setMoveDirection(String moveDirection) {
        this.moveDirection = moveDirection;
    }

    public Boolean getLoadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(Boolean loadStatus) {
        this.loadStatus = loadStatus;
    }

    public BigDecimal getRealTimeSpeed() {
        return realTimeSpeed;
    }

    public void setRealTimeSpeed(BigDecimal realTimeSpeed) {
        this.realTimeSpeed = realTimeSpeed;
    }

    public Integer getTotalOperationTime() {
        return totalOperationTime;
    }

    public void setTotalOperationTime(Integer totalOperationTime) {
        this.totalOperationTime = totalOperationTime;
    }

    public Long getCurrentTaskId() {
        return currentTaskId;
    }

    public void setCurrentTaskId(Long currentTaskId) {
        this.currentTaskId = currentTaskId;
    }

    public String getTaskQueue() {
        return taskQueue;
    }

    public void setTaskQueue(String taskQueue) {
        this.taskQueue = taskQueue;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public String getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(String taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public BigDecimal getRatedLoadCapacity() {
        return ratedLoadCapacity;
    }

    public void setRatedLoadCapacity(BigDecimal ratedLoadCapacity) {
        this.ratedLoadCapacity = ratedLoadCapacity;
    }

    public String getInteriorDimensions() {
        return interiorDimensions;
    }

    public void setInteriorDimensions(String interiorDimensions) {
        this.interiorDimensions = interiorDimensions;
    }

    public BigDecimal getMotorPower() {
        return motorPower;
    }

    public void setMotorPower(BigDecimal motorPower) {
        this.motorPower = motorPower;
    }

    public String getChargingInterfaceType() {
        return chargingInterfaceType;
    }

    public void setChargingInterfaceType(String chargingInterfaceType) {
        this.chargingInterfaceType = chargingInterfaceType;
    }

    public String getChargingPower() {
        return chargingPower;
    }

    public void setChargingPower(String chargingPower) {
        this.chargingPower = chargingPower;
    }

    public String getCommunicationProtocol() {
        return communicationProtocol;
    }

    public void setCommunicationProtocol(String communicationProtocol) {
        this.communicationProtocol = communicationProtocol;
    }

    public Integer getPositioningAccuracy() {
        return positioningAccuracy;
    }

    public void setPositioningAccuracy(Integer positioningAccuracy) {
        this.positioningAccuracy = positioningAccuracy;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getAuthorizedFloorRange() {
        return authorizedFloorRange;
    }

    public void setAuthorizedFloorRange(String authorizedFloorRange) {
        this.authorizedFloorRange = authorizedFloorRange;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
