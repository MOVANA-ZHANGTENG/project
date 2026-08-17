package com.deer.wcs.task.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Id;

/**
 * 用于记录任务执行的每一步历史对象 task_info_list_history
 * 
 * @author deer
 * @date 2024-06-06
 */
public class TaskInfoListHistory
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 任务ID */
    @Excel(name = "任务ID")
    private Long taskId;

    /** 任务号 */
    @Excel(name = "任务号")
    private String taskNo;

    /** wms任务号 */
    @Excel(name = "wms任务号")
    private String wmsTaskNo;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 仓库名称 */
    @Excel(name = "仓库名称")
    private String wareName;

    /** 区域编码 */
    @Excel(name = "区域编码")
    private String areaCode;

    /** 区域名称 */
    @Excel(name = "区域名称")
    private String areaName;

    /** 任务类型 */
    @Excel(name = "任务类型")
    private String type;

    /** 托盘编码 */
    @Excel(name = "托盘编码")
    private String palletCode;

    /** 起点位置 */
    @Excel(name = "起点位置")
    private String fromCellCode;

    /** 终点位置 */
    @Excel(name = "终点位置")
    private String toCellCode;

    /** 创建时间 */
    private String createTime;

    /** 完成时间 */
    @Excel(name = "完成时间")
    private String finishTime;

    /** 任务状态 */
    @Excel(name = "任务状态")
    private Integer state;

    /** 版本号 */
    @Excel(name = "版本号")
    private Long version;

    /** 备注 */
    @Excel(name = "备注")
    private String memo;

    /** 托盘类型 */
    @Excel(name = "托盘类型")
    private String palletType;

    /** 载具高度 */
    @Excel(name = "载具高度")
    private Double palletHeight;

    /** 载具重量 */
    @Excel(name = "载具重量")
    private Double palletWeight;

    /** remark1 */
    @Excel(name = "remark1")
    private String remark1;

    /** remark2 */
    @Excel(name = "remark2")
    private String remark2;

    /** remark3 */
    @Excel(name = "remark3")
    private String remark3;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setTaskId(Long taskId) 
    {
        this.taskId = taskId;
    }

    public Long getTaskId() 
    {
        return taskId;
    }
    public void setTaskNo(String taskNo) 
    {
        this.taskNo = taskNo;
    }

    public String getTaskNo() 
    {
        return taskNo;
    }
    public void setWmsTaskNo(String wmsTaskNo) 
    {
        this.wmsTaskNo = wmsTaskNo;
    }

    public String getWmsTaskNo() 
    {
        return wmsTaskNo;
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
    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }
    public void setPalletCode(String palletCode) 
    {
        this.palletCode = palletCode;
    }

    public String getPalletCode() 
    {
        return palletCode;
    }
    public void setFromCellCode(String fromCellCode) 
    {
        this.fromCellCode = fromCellCode;
    }

    public String getFromCellCode() 
    {
        return fromCellCode;
    }
    public void setToCellCode(String toCellCode) 
    {
        this.toCellCode = toCellCode;
    }

    public String getToCellCode() 
    {
        return toCellCode;
    }
    public void setCreateTime(String createTime) 
    {
        this.createTime = createTime;
    }

    public String getCreateTime() 
    {
        return createTime;
    }
    public void setFinishTime(String finishTime) 
    {
        this.finishTime = finishTime;
    }

    public String getFinishTime() 
    {
        return finishTime;
    }
    public void setState(Integer state) 
    {
        this.state = state;
    }

    public Integer getState() 
    {
        return state;
    }
    public void setVersion(Long version) 
    {
        this.version = version;
    }

    public Long getVersion() 
    {
        return version;
    }
    public void setMemo(String memo) 
    {
        this.memo = memo;
    }

    public String getMemo() 
    {
        return memo;
    }
    public void setPalletType(String palletType) 
    {
        this.palletType = palletType;
    }

    public String getPalletType() 
    {
        return palletType;
    }
    public void setPalletHeight(Double palletHeight) 
    {
        this.palletHeight = palletHeight;
    }

    public Double getPalletHeight() 
    {
        return palletHeight;
    }
    public void setPalletWeight(Double palletWeight) 
    {
        this.palletWeight = palletWeight;
    }

    public Double getPalletWeight() 
    {
        return palletWeight;
    }
    public void setRemark1(String remark1) 
    {
        this.remark1 = remark1;
    }

    public String getRemark1() 
    {
        return remark1;
    }
    public void setRemark2(String remark2) 
    {
        this.remark2 = remark2;
    }

    public String getRemark2() 
    {
        return remark2;
    }
    public void setRemark3(String remark3) 
    {
        this.remark3 = remark3;
    }

    public String getRemark3() 
    {
        return remark3;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("taskId", getTaskId())
            .append("taskNo", getTaskNo())
            .append("wmsTaskNo", getWmsTaskNo())
            .append("wareCode", getWareCode())
            .append("wareName", getWareName())
            .append("areaCode", getAreaCode())
            .append("areaName", getAreaName())
            .append("type", getType())
            .append("palletCode", getPalletCode())
            .append("fromCellCode", getFromCellCode())
            .append("toCellCode", getToCellCode())
            .append("createTime", getCreateTime())
            .append("finishTime", getFinishTime())
            .append("state", getState())
            .append("version", getVersion())
            .append("memo", getMemo())
            .append("palletType", getPalletType())
            .append("palletHeight", getPalletHeight())
            .append("palletWeight", getPalletWeight())
            .append("remark1", getRemark1())
            .append("remark2", getRemark2())
            .append("remark3", getRemark3())
            .toString();
    }
}
