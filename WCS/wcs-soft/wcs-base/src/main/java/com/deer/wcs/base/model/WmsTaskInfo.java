package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * wms任务对象 wms_task_info
 * 
 * @author deer
 * @date 2024-05-10
 */
@NoArgsConstructor
@ToString
public class WmsTaskInfo
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 任务编号 */
    @Excel(name = "任务编号")
    @NotBlank(message = "任务编号taskId不可为空")
    private Long taskId;

    private String taskNo;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    @NotNull(message = "仓库编码wareCode不可为空")
    private String wareCode;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String wareName;

    /** 区域编码 */
    @Excel(name = "区域编码")
   // @NotNull(message = "区域编码areaCode不可为空")
    private String areaCode;

    /** 区域名称 */
    @Excel(name = "区域名称")
    private String areaName;

    /** 任务类型 */
    @Excel(name = "任务类型")
    @NotNull(message = "任务类型type不可为空")
    private String type;

    /** 托盘编码 */
    @Excel(name = "托盘编码")
    @NotNull(message = "托盘编码palletCode不可为空")
    private String palletCode;

    /** 托盘类型 */
    @Excel(name = "托盘类型")
    private String palletType;

    /** 载具高度 */
    @Excel(name = "载具高度")
    private Double palletHeight;

    /** 载具重量 */
    @Excel(name = "载具重量")
    private Double palletWeight;

    /** 起点位置 */
    @Excel(name = "起点位置")
    @NotNull(message = "起点位置fromCellCode不可为空")
    private String fromCellCode;

    /** 终点位置 */
    @Excel(name = "终点位置")
    @NotNull(message = "终点位置toCellCode不可为空")
    private String toCellCode;

    /** 创建时间 */
    private String createTime;

    /** 0-初始化 1-已生成wcs任务 2-任务运行中  3-任务异常 4-任务完成 -1-任务取消 */
    @Excel(name = "0-初始化 1-已生成wcs任务 2-任务运行中  3-任务异常 4-任务完成 -1-任务取消")
    private Integer state;

    /** $column.columnComment */
    @Excel(name = "版本号", readConverterExp = "$column.readConverterExp()")
    private Integer version;

    /** 优先级 */
    @Excel(name = "优先级")
    private Integer priority;

    /** 备注1 */
    @Excel(name = "备注1")
    private Integer remark1;

    /** 备注2 */
    @Excel(name = "备注2")
    private Integer remark2;

    /** 备注3 */
    @Excel(name = "备注3")
    private Integer remark3;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getRemark1() {
        return remark1;
    }

    public void setRemark1(Integer remark1) {
        this.remark1 = remark1;
    }

    public Integer getRemark2() {
        return remark2;
    }

    public void setRemark2(Integer remark2) {
        this.remark2 = remark2;
    }

    public Integer getRemark3() {
        return remark3;
    }

    public void setRemark3(Integer remark3) {
        this.remark3 = remark3;
    }
}
