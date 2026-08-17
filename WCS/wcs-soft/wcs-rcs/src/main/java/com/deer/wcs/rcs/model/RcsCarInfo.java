package com.deer.wcs.rcs.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.deer.wcs.common.core.domain.BaseEntity;

/**
 * 四向车/AGV对象 rcs_car_info
 * 
 * @author deer
 * @date 2025-10-14
 */
public class RcsCarInfo
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 设备ID */
    @Excel(name = "设备ID")
    private Long deviceId;
    private Integer z;

    /** 编码 */
    @Excel(name = "编码")
    private String code;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    private String wareCode;

    /** 类型ID */
    @Excel(name = "类型ID")
    private Long rcsCarTypeId;

    /** 禁用状态 */
    @Excel(name = "禁用状态")
    private Long disableState;

    /** 任务状态 */
    @Excel(name = "任务状态")
    private Long taskState;

    /** 电量 */
    @Excel(name = "电量")
    private Integer batteryLevel;

    /** 是否充电 */
    @Excel(name = "是否充电")
    private Integer isCharge;

    private Integer isConnected;

    /** 创建时间 */
    private String createTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long createUserId;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createUserName;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long updateUserId;

    /** 更新人 */
    @Excel(name = "更新人")
    private String updateUserName;

    /** 更新时间 */
    private String updateTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long version;

    /** 当前X坐标(mm，下位地图坐标) */
    @Excel(name = "当前X坐标")
    private Integer currentX;

    /** 当前Y坐标(mm，下位地图坐标) */
    @Excel(name = "当前Y坐标")
    private Integer currentY;

    /** 当前Z坐标(mm，下位地图坐标) */
    @Excel(name = "当前Z坐标")
    private Integer currentZ;

    /** 起始库位编码 */
    @Excel(name = "起始库位")
    private String fromCellCode;

    /** 目标库位编码 */
    @Excel(name = "目标库位")
    private String toCellCode;

    /** 位置插值系数(0-1) */
    @Excel(name = "位置系数")
    private java.math.BigDecimal positionRatio;

    /** 移动方向 */
    @Excel(name = "移动方向")
    private Integer moveDirection;

    /** 当前速度(mm/s) */
    @Excel(name = "当前速度")
    private java.math.BigDecimal speed;

    /** 位置最后更新时间 */
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date lastUpdateTime;

    private String ip;

     /** 端口 */
    @Excel(name = "端口")
    private Integer port;

    /** 负载状态（0-空载/1-负载） */
    @Excel(name = "负载状态")
    private Integer loadState;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public String getWareCode() {
        return wareCode;
    }

    public void setWareCode(String wareCode) {
        this.wareCode = wareCode;
    }

    public Integer getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(Integer isConnected) {
        this.isConnected = isConnected;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDeviceId(Long deviceId) 
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() 
    {
        return deviceId;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setRcsCarTypeId(Long rcsCarTypeId) 
    {
        this.rcsCarTypeId = rcsCarTypeId;
    }

    public Long getRcsCarTypeId() 
    {
        return rcsCarTypeId;
    }

    public Long getDisableState() {
        return disableState;
    }

    public void setDisableState(Long disableState) {
        this.disableState = disableState;
    }

    public Long getTaskState() {
        return taskState;
    }

    public void setTaskState(Long taskState) {
        this.taskState = taskState;
    }

    public void setBatteryLevel(Integer batteryLevel)
    {
        this.batteryLevel = batteryLevel;
    }

    public Integer getBatteryLevel() 
    {
        return batteryLevel;
    }
    public void setIsCharge(Integer isCharge)
    {
        this.isCharge = isCharge;
    }

    public Integer getIsCharge()
    {
        return isCharge;
    }
    public void setCreateTime(String createTime) 
    {
        this.createTime = createTime;
    }

    public String getCreateTime() 
    {
        return createTime;
    }
    public void setCreateUserId(Long createUserId) 
    {
        this.createUserId = createUserId;
    }

    public Long getCreateUserId() 
    {
        return createUserId;
    }
    public void setCreateUserName(String createUserName) 
    {
        this.createUserName = createUserName;
    }

    public String getCreateUserName() 
    {
        return createUserName;
    }
    public void setUpdateUserId(Long updateUserId) 
    {
        this.updateUserId = updateUserId;
    }

    public Long getUpdateUserId() 
    {
        return updateUserId;
    }
    public void setUpdateUserName(String updateUserName) 
    {
        this.updateUserName = updateUserName;
    }

    public String getUpdateUserName() 
    {
        return updateUserName;
    }
    public void setUpdateTime(String updateTime) 
    {
        this.updateTime = updateTime;
    }

    public String getUpdateTime() 
    {
        return updateTime;
    }
    public void setVersion(Long version) 
    {
        this.version = version;
    }

    public Long getVersion() 
    {
        return version;
    }

    public Integer getCurrentX() {
        return currentX;
    }

    public void setCurrentX(Integer currentX) {
        this.currentX = currentX;
    }

    public Integer getCurrentY() {
        return currentY;
    }

    public void setCurrentY(Integer currentY) {
        this.currentY = currentY;
    }

    public Integer getCurrentZ() {
        return currentZ;
    }

    public void setCurrentZ(Integer currentZ) {
        this.currentZ = currentZ;
    }

    public String getFromCellCode() {
        return fromCellCode;
    }

    public void setFromCellCode(String fromCellCode) {
        this.fromCellCode = fromCellCode;
    }

    public String getToCellCode() {
        return toCellCode;
    }

    public void setToCellCode(String toCellCode) {
        this.toCellCode = toCellCode;
    }

    public java.math.BigDecimal getPositionRatio() {
        return positionRatio;
    }

    public void setPositionRatio(java.math.BigDecimal positionRatio) {
        this.positionRatio = positionRatio;
    }

    public Integer getMoveDirection() {
        return moveDirection;
    }

    public void setMoveDirection(Integer moveDirection) {
        this.moveDirection = moveDirection;
    }

    public java.math.BigDecimal getSpeed() {
        return speed;
    }

    public void setSpeed(java.math.BigDecimal speed) {
        this.speed = speed;
    }

    public java.util.Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(java.util.Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Integer getLoadState() {
        return loadState;
    }

    public void setLoadState(Integer loadState) {
        this.loadState = loadState;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deviceId", getDeviceId())
            .append("code", getCode())
            .append("name", getName())
            .append("rcsCarTypeId", getRcsCarTypeId())
            .append("disableState", getDisableState())
            .append("taskState", getTaskState())
            .append("batteryLevel", getBatteryLevel())
            .append("isCharge", getIsCharge())
            .append("createTime", getCreateTime())
            .append("createUserId", getCreateUserId())
            .append("createUserName", getCreateUserName())
            .append("updateUserId", getUpdateUserId())
            .append("updateUserName", getUpdateUserName())
            .append("updateTime", getUpdateTime())
            .append("version", getVersion())
            .toString();
    }
}
