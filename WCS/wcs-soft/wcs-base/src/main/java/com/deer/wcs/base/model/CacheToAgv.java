package com.deer.wcs.base.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.deer.wcs.common.core.domain.BaseEntity;

/**
 * AGV缓存对象 cache_to_agv
 * 
 * @author deer
 * @date 2025-02-14
 */
public class CacheToAgv
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** agv编码 */
    @Excel(name = "agv编码")
    private String agvCode;

    /** 产品编码 */
    @Excel(name = "产品编码")
    private String itemCode;

    /** 状态 */
    @Excel(name = "状态")
    private Integer state;

    @Excel(name = "任务id")
    private Long taskId;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setAgvCode(String agvCode) 
    {
        this.agvCode = agvCode;
    }

    public String getAgvCode() 
    {
        return agvCode;
    }
    public void setItemCode(String itemCode) 
    {
        this.itemCode = itemCode;
    }

    public String getItemCode() 
    {
        return itemCode;
    }
    public void setState(Integer state) 
    {
        this.state = state;
    }

    public Integer getState() 
    {
        return state;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("agvCode", getAgvCode())
            .append("itemCode", getItemCode())
            .append("state", getState())
            .append("taskId", getTaskId())
            .toString();
    }
}
