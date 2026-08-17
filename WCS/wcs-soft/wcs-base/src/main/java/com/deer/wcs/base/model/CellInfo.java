package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 库位对象 cell_info
 * 
 * @author deer
 * @date 2024-04-28
 */
public class CellInfo
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 编码 */
    @Excel(name = "编码")
    private String code;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 上位编码 */
    @Excel(name = "上位编码")
    private String hostCode;

    /** 下位编码 */
    @Excel(name = "下位编码")
    private String subCode;

    /** 巷道编码 */
    @Excel(name = "巷道编码")
    private String lineCode;

    /** 巷道名称 */
    @Excel(name = "巷道名称")
    private String lineName;

    /** X */
    @Excel(name = "X")
    private Integer x;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String wareName;

    /** Y */
    @Excel(name = "Y")
    private Integer y;

    /** Z */
    @Excel(name = "Z")
    private Integer z;

    /** 下位地图X坐标(mm) */
    @Excel(name = "下位地图X坐标")
    private Integer subX;

    //** 下位地图Y坐标(mm) *//*
    @Excel(name = "下位地图Y坐标")
    private Integer subY;

    //** 下位地图Z坐标(mm) *//*
    @Excel(name = "下位地图Z坐标")
    private Integer subZ;

    /** 库区编码 */
    @Excel(name = "库区编码")
    private String areaCode;

    /** 库区名称 */
    @Excel(name = "库区名称")
    private String areaName;

    /** 前置货位 */
    @Excel(name = "前置货位")
    private String preCode;

    /**
     * 伸位  1，2，3 数字越小越接近巷道
     * */
    @Excel(name = "伸位")
    private Integer priority;

    /** 库存状态 */
    @Excel(name = "库存状态")
    private Long invenState;

    /** 任务状态 */
    @Excel(name = "任务状态")
    private Long taskState;

    /** 禁用状态 */
    @Excel(name = "禁用状态")
    private Long disableState;

    /** 创建时间 */
    private String createTime;

    /** 创建人 */
    @Excel(name = "创建人")
    private Long createUserId;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createUserName;

    /** 更新时间 */
    private String updateTime;

    /** 更新人 */
    @Excel(name = "更新人")
    private Long updateUserId;

    /** 更新人 */
    @Excel(name = "更新人")
    private String updateUserName;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    /** 删除标志 */
    @Excel(name = "删除标志")
    private Integer isDelete;

    private String ab;
    private String abc;

    /** 最新入库时间 */
    @Excel(name = "最新入库时间")
    private String inTime;

    private Integer type;

//    private Integer isMove;

    private Integer isError;

    /** 托盘类型 */
    private String palletType;

    /** 当前记录内容（用于去重） */
    private String recordContent;


    public String getPalletType() {
        return palletType;
    }

    public void setPalletType(String palletType) {
        this.palletType = palletType;
    }

    public Integer getIsError() {
        return isError;
    }

    public void setIsError(Integer isError) {
        this.isError = isError;
    }

//    public Integer getIsMove() {
//        return isMove;
//    }
//    public void setIsMove(Integer isMove) {
//        this.isMove  = isMove;
//    }

    public String getRecordContent() {
        return recordContent;
    }

    public void setRecordContent(String recordContent) {
        this.recordContent = recordContent;
    }


    public void setType(Integer type) {
        this.type = type;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getAbc() {
        return abc;
    }

    public void setAbc(String abc) {
        this.abc = abc;
    }

    public String getAb() {
        return ab;
    }

    public void setAb(String ab) {
        this.ab = ab;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
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
    public void setHostCode(String hostCode) 
    {
        this.hostCode = hostCode;
    }

    public String getHostCode() 
    {
        return hostCode;
    }
    public void setSubCode(String subCode) 
    {
        this.subCode = subCode;
    }

    public String getSubCode() 
    {
        return subCode;
    }
    public void setLineCode(String lineCode) 
    {
        this.lineCode = lineCode;
    }

    public String getLineCode() 
    {
        return lineCode;
    }
    public void setLineName(String lineName) 
    {
        this.lineName = lineName;
    }

    public String getLineName() 
    {
        return lineName;
    }
    public void setWareCode(String wareCode) 
    {
        this.wareCode = wareCode;
    }

    public String getWareCode() 
    {
        return wareCode;
    }
    public void setWareName(String wareName) 
    {
        this.wareName = wareName;
    }

    public String getWareName() 
    {
        return wareName;
    }
    public void setAreaCode(String areaCode) 
    {
        this.areaCode = areaCode;
    }

    public String getAreaCode() 
    {
        return areaCode;
    }
    public void setAreaName(String areaName) 
    {
        this.areaName = areaName;
    }

    public String getAreaName() 
    {
        return areaName;
    }
    public void setPreCode(String preCode) 
    {
        this.preCode = preCode;
    }

    public String getPreCode() 
    {
        return preCode;
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
    public void setUpdateTime(String updateTime) 
    {
        this.updateTime = updateTime;
    }

    public String getUpdateTime() 
    {
        return updateTime;
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
    public void setVersion(Integer version) 
    {
        this.version = version;
    }

    public Integer getVersion() 
    {
        return version;
    }
    public void setIsDelete(Integer isDelete) 
    {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() 
    {
        return isDelete;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

   /* public Integer getSubX() {
        return subX;
    }

    public void setSubX(Integer subX) {
        this.subX = subX;
    }

    public Integer getSubY() {
        return subY;
    }

    public void setSubY(Integer subY) {
        this.subY = subY;
    }

    public Integer getSubZ() {
        return subZ;
    }

    public void setSubZ(Integer subZ) {
        this.subZ = subZ;
    }*/

    public Integer getType() {
        return type;
    }

    public Long getInvenState() {
        return invenState;
    }

    public void setInvenState(Long invenState) {
        this.invenState = invenState;
    }

    public Long getTaskState() {
        return taskState;
    }

    public void setTaskState(Long taskState) {
        this.taskState = taskState;
    }

    public Long getDisableState() {
        return disableState;
    }

    public void setDisableState(Long disableState) {
        this.disableState = disableState;
    }

    public Integer getSubX() {
        return subX;
    }

    public void setSubX(Integer subX) {
        this.subX = subX;
    }

    public Integer getSubY() {
        return subY;
    }

    public void setSubY(Integer subY) {
        this.subY = subY;
    }

    public Integer getSubZ() {
        return subZ;
    }

    public void setSubZ(Integer subZ) {
        this.subZ = subZ;
    }
}
