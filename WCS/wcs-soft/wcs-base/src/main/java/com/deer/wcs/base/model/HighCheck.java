package com.deer.wcs.base.model;

import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * 高度检测对象 hight_check
 *
 * @author deer
 * @date 2026-01-24
 */
public class HighCheck
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 起点巷道 */
    @Excel(name = "起点巷道")
    private String fromCellCode;

    /** 任务编号 */
    @Excel(name = "任务编号")
    private String taskNo;

    /** 高度检测 */
    @Excel(name = "高度检测")
    private Boolean highCheck;

    /** 托盘编码 */
    @Excel(name = "托盘编码")
    private String palletCode;

    @Excel(name = "处理状态")
    private String handleState;

    @Excel(name = "创建时间")
    private String createTime;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setWareCode(String wareCode)
    {
        this.wareCode = wareCode;
    }

    public String getWareCode()
    {
        return wareCode;
    }
    public void setFromCellCode(String fromCellCode)
    {
        this.fromCellCode = fromCellCode;
    }

    public String getFromCellCode()
    {
        return fromCellCode;
    }
    public void setTaskNo(String taskNo)
    {
        this.taskNo = taskNo;
    }

    public String getTaskNo()
    {
        return taskNo;
    }
    public void setHighCheck(Boolean highCheck)
    {
        this.highCheck = highCheck;
    }

    public Boolean getHighCheck()
    {
        return highCheck;
    }
    public void setPalletCode(String palletCode)
    {
        this.palletCode = palletCode;
    }

    public String getPalletCode()
    {
        return palletCode;
    }

    public String getHandleState() {
        return handleState;
    }

    public void setHandleState(String handleState) {
        this.handleState = handleState;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("wareCode", getWareCode())
                .append("fromCellCode", getFromCellCode())
                .append("taskNo", getTaskNo())
                .append("highCheck", getHighCheck())
                .append("palletCode", getPalletCode())
                .toString();
    }
}
