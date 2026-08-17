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
 * 库位日志记录对象 cell_record
 * 
 * @author deer
 * @date 2025-11-04
 */
public class CellRecord
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 库位ID */
    @Excel(name = "库位ID")
    private Long cellId;

    /** 库位编码 */
    @Excel(name = "库位编码")
    private String cellCode;

    /** 仓库编码 */
    @Excel(name = "仓库编码")
    private String wareCode;

    /** 日志内容 */
    @Excel(name = "日志内容")
    private String content;

    /** 创建时间 */
    private String createTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCellId(Long cellId) 
    {
        this.cellId = cellId;
    }

    public Long getCellId() 
    {
        return cellId;
    }
    public void setCellCode(String cellCode) 
    {
        this.cellCode = cellCode;
    }

    public String getCellCode() 
    {
        return cellCode;
    }
    public void setWareCode(String wareCode) 
    {
        this.wareCode = wareCode;
    }

    public String getWareCode() 
    {
        return wareCode;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    public void setCreateTime(String createTime) 
    {
        this.createTime = createTime;
    }

    public String getCreateTime() 
    {
        return createTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("cellId", getCellId())
            .append("cellCode", getCellCode())
            .append("wareCode", getWareCode())
            .append("content", getContent())
            .append("createTime", getCreateTime())
            .toString();
    }
}
