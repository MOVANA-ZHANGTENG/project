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
 * 站台日志对象 position_record
 * 
 * @author deer
 * @date 2025-04-02
 */
public class PositionRecord
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** position_id */
    @Excel(name = "position_id")
    private Long positionId;

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    /** 时间 */
    private String createTime;

    /** 类型 */
    @Excel(name = "类型")
    private Integer type;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPositionId(Long positionId) 
    {
        this.positionId = positionId;
    }

    public Long getPositionId() 
    {
        return positionId;
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
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("positionId", getPositionId())
            .append("content", getContent())
            .append("createTime", getCreateTime())
            .append("type", getType())
            .toString();
    }
}
