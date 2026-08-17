package com.deer.wcs.task.model;


import com.deer.wcs.common.annotation.Excel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 任务对象 task_info
 *
 * @author deer
 * @date 2024-04-30
 */
@Data
@NoArgsConstructor
@ToString
public class TaskInfo {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 任务号
     */
    @Excel(name = "任务号")
    private String taskNo;

    /**
     * WMS任务号
     */
    @Excel(name = "WMS任务号")
    private String wmsTaskNo;

    /**
     * 仓库编码
     */
    @Excel(name = "仓库编码")
    private String wareCode;

    /**
     * 仓库名称
     */
    @Excel(name = "仓库名称")
    private String wareName;

    /**
     * 货区编码
     */
    @Excel(name = "货区编码")
    private String areaCode;

    /**
     * 货区名称
     */
    @Excel(name = "货区名称")
    private String areaName;

    /**
     * 类型
     */
    @Excel(name = "类型")
    private String type;

    /**
     * 托盘号
     */
    @Excel(name = "托盘号")
    private String palletCode;

    /**
     * 托盘类型
     */
    @Excel(name = "托盘类型")
    private String palletType;

    /**
     * 载具高度
     */
    @Excel(name = "载具高度")
    private Double palletHeight;

    /**
     * 载具重量
     */
    @Excel(name = "载具重量")
    private Double palletWeight;

    /**
     * 起始位置
     */
    @Excel(name = "起始位置")
    private String fromCellCode;

    /**
     * 目标位置
     */
    @Excel(name = "目标位置")
    private String toCellCode;

    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 创建时间
     */
    private String finishTime;

    /**
     * 状态
     */
    @Excel(name = "状态")
    private Integer state;

    @Excel(name = "备注")
    private String memo;

    /**
     * 版本号
     */
    @Excel(name = "版本号")
    private Integer version;

    /** 备注1 */
    @Excel(name = "备注1")
    private String remark1;

    /** 备注2 */
    @Excel(name = "备注2")
    //用于加工线的型号名称
    private String remark2;

    /** 备注3 */
    @Excel(name = "备注3")
    //用于加工线是否报警记录
    private String remark3;

    /** 备注3 */
    @Excel(name = "优先级")
    private Integer priority;

    /** RCS车辆ID */
    @Excel(name = "RCS车辆ID")
    private Long rcsCarId;

    private Long subTaskId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(Long subTaskId) {
        this.subTaskId = subTaskId;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }

    public String getWmsTaskNo() {
        return wmsTaskNo;
    }

    public void setWmsTaskNo(String wmsTaskNo) {
        this.wmsTaskNo = wmsTaskNo;
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

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getPalletCode() {
        return palletCode;
    }

    public void setPalletCode(String palletCode) {
        this.palletCode = palletCode;
    }

    public String getPalletType() {
        return palletType;
    }

    public void setPalletType(String palletType) {
        this.palletType = palletType;
    }

    public Double getPalletHeight() {
        return palletHeight;
    }

    public void setPalletHeight(Double palletHeight) {
        this.palletHeight = palletHeight;
    }

    public Double getPalletWeight() {
        return palletWeight;
    }

    public void setPalletWeight(Double palletWeight) {
        this.palletWeight = palletWeight;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public String getRemark3() {
        return remark3;
    }

    public void setRemark3(String remark3) {
        this.remark3 = remark3;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getRcsCarId() {
        return rcsCarId;
    }

    public void setRcsCarId(Long rcsCarId) {
        this.rcsCarId = rcsCarId;
    }
}
