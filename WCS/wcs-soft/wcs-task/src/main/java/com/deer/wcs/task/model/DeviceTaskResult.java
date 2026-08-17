package com.deer.wcs.task.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.deer.wcs.common.annotation.Excel;
import com.deer.wcs.common.core.domain.BaseEntity;

/**
 * 设备任务回传对象 device_task_result
 * 
 * @author deer
 * @date 2024-11-22
 */
public class DeviceTaskResult
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 设备任务编码 */
    @Excel(name = "设备任务编码")
    private String taskCode;

    /** 状态 0-初始化 1-已经处理 */
    @Excel(name = "状态 0-初始化 1-已经处理")
    private Integer state;

    /** 当前位置 */
    @Excel(name = "当前位置")
    private String node;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 创建时间 */
    private String createTime;

    /** 备注数据 */
    @Excel(name = "备注数据")
    private String data;

    /** 设备名 */
    @Excel(name = "设备名")
    private String fromDevice;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setTaskCode(String taskCode) 
    {
        this.taskCode = taskCode;
    }

    public String getTaskCode() 
    {
        return taskCode;
    }
    public void setState(Integer state)
    {
        this.state = state;
    }

    public Integer getState()
    {
        return state;
    }
    public void setNode(String node) 
    {
        this.node = node;
    }

    public String getNode() 
    {
        return node;
    }
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setCreateTime(String createTime) 
    {
        this.createTime = createTime;
    }

    public String getCreateTime() 
    {
        return createTime;
    }
    public void setData(String data) 
    {
        this.data = data;
    }

    public String getData() 
    {
        return data;
    }
    public void setFromDevice(String fromDevice) 
    {
        this.fromDevice = fromDevice;
    }

    public String getFromDevice() 
    {
        return fromDevice;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("taskCode", getTaskCode())
            .append("state", getState())
            .append("node", getNode())
            .append("type", getType())
            .append("createTime", getCreateTime())
            .append("data", getData())
            .append("fromDevice", getFromDevice())
            .toString();
    }
}
