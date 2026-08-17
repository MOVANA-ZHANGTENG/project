package com.deer.wcs.task.model;


import com.deer.wcs.common.annotation.Excel;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 路径方法对象 path_handle
 * 
 * @author deer
 * @date 2024-05-10
 */
public class PathHandle
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 任务ID */
    @Excel(name = "任务ID")
    private Long taskId;

    /** 任务编号 */
    @Excel(name = "任务编号")
    private String taskNo;

    /** 步骤ID */
    @Excel(name = "步骤ID")
    private Long jobId;

    /** 路劲ID */
    @Excel(name = "路劲ID")
    private Long pathId;

    /** 方法ID */
    @Excel(name = "方法ID")
    private Long handleId;

    /** 任务类型 */
    @Excel(name = "任务类型")
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

    /** 执行顺序 */
    @Excel(name = "执行顺序")
    private Integer cmdIndex;

    /** 创建时间 */
    private String createTime;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    private Long createUserId;

    /** 创建人姓名 */
    @Excel(name = "创建人姓名")
    private String createUserName;

    /** 更新时间 */
    private String updateTime;

    /** 更新人ID */
    @Excel(name = "更新人ID")
    private Long updateUserId;

    /** 更新人姓名 */
    @Excel(name = "更新人姓名")
    private String updateUserName;

    /** 删除标志 */
    @Excel(name = "删除标志")
    private Integer isDelete;

    /** 版本号 */
    @Excel(name = "版本号")
    private Integer version;

    @Excel(name = "状态")
    private Integer state;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

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
    public void setJobId(Long jobId) 
    {
        this.jobId = jobId;
    }

    public Long getJobId() 
    {
        return jobId;
    }
    public void setPathId(Long pathId) 
    {
        this.pathId = pathId;
    }

    public Long getPathId() 
    {
        return pathId;
    }
    public void setHandleId(Long handleId) 
    {
        this.handleId = handleId;
    }

    public Long getHandleId() 
    {
        return handleId;
    }
    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
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
    public void setCmdIndex(Integer cmdIndex) 
    {
        this.cmdIndex = cmdIndex;
    }

    public Integer getCmdIndex() 
    {
        return cmdIndex;
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

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public void setUpdateUserName(String updateUserName) {
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


}
