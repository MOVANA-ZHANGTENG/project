package com.deer.wcs.base.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 任务执行器对象 task_handle
 * 
 * @author deer
 * @date 2024-04-28
 */
public class TaskHandle
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 任务定义ID */
    @Excel(name = "任务定义ID")
    private Long taskDefineId;

    /** 执行器ID */
    @Excel(name = "执行器ID")
    private Long handleId;

    /** 类型 0-cmd_pre  1-cmd 2-success_re 3-success */
    @Excel(name = "类型 0-cmd_pre  1-cmd 2-success_re 3-success 4-delete")
    private Integer type;

    /** 类名 */
    @Excel(name = "类名")
    private String className;

    /** 方法名 */
    @Excel(name = "方法名")
    private String methodName;

    /** 编码 */
    @Excel(name = "编码")
    private String code;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 排序 */
    @Excel(name = "排序")
    private Integer cmdIndex;

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

    /** 删除标志 */
    @Excel(name = "删除标志")
    private Integer isDelete;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

   

    public Long DefineId() {
        return taskDefineId;
    }

    public void setTaskDefineId(Long taskDefineId) {
        this.taskDefineId = taskDefineId;
    }

    

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getClassName() 
    {
        return className;
    }
    public void setMethodName(String methodName) 
    {
        this.methodName = methodName;
    }

    public String getMethodName() 
    {
        return methodName;
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

    public Integer getCmdIndex() {
        return cmdIndex;
    }

    public void setCmdIndex(Integer cmdIndex) {
        this.cmdIndex = cmdIndex;
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
    public void setIsDelete(Integer isDelete) 
    {
        this.isDelete = isDelete;
    }

    public Long getTaskDefineId() {
        return taskDefineId;
    }

    public Long getHandleId() {
        return handleId;
    }

    public void setHandleId(Long handleId) {
        this.handleId = handleId;
    }

    public Integer getTaskIndex() {
        return cmdIndex;
    }

    public Integer getIsDelete()
    {
        return isDelete;
    }
    public void setVersion(Integer version) 
    {
        this.version = version;
    }

    public Integer getVersion() 
    {
        return version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("taskDefineId", DefineId())
            .append("handleId", getHandleId())
            .append("type", getType())
            .append("className", getClassName())
            .append("methodName", getMethodName())
            .append("code", getCode())
            .append("name", getName())
            .append("cmdIndex", getCmdIndex())
            .append("createTime", getCreateTime())
            .append("createUserId", getCreateUserId())
            .append("createUserName", getCreateUserName())
            .append("updateTime", getUpdateTime())
            .append("updateUserId", getUpdateUserId())
            .append("updateUserName", getUpdateUserName())
            .append("isDelete", getIsDelete())
            .append("version", getVersion())
            .toString();
    }
}
