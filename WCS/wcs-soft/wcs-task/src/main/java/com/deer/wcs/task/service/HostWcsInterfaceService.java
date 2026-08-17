package com.deer.wcs.task.service;

import com.deer.wcs.base.model.WmsTaskInfo;
import com.deer.wcs.task.model.*;
import com.deer.wcs.task.model.recvInterface.HostChangeTarget;
import com.deer.wcs.task.model.recvInterface.TaskCancel;
import com.deer.wcs.common.core.service.Service;

import java.util.List;

/**
 * 接口记录Service接口
 * 
 * @author deer
 * @date 2024-05-23
 */
public interface HostWcsInterfaceService   extends Service<HostWcsInterface, Long>
{

    public void record(String code,String name,String from,String to,String param,String result);
    public void record(Long jobId,String code,String name,String from,String to,String param,String result);
    public void record(String code,String name,String from,String to,String param,String result,String url);

    /**
     * 查询接口记录
     *
     * @param id 接口记录主键
     * @return 接口记录
     */
    public HostWcsInterface selectHostWcsInterfaceById(Long id);

    /**
     * 查询接口记录列表
     * 
     * @param criteria
     * @return 接口记录集合
     */
    public List<HostWcsInterfaceDto> findList(HostWcsInterfaceCriteria criteria);

    /**
     * 新增接口记录
     *
     * @param hostWcsInterface 接口记录
     * @return 结果
     */
    public int insertHostWcsInterface(HostWcsInterface hostWcsInterface);

    /**
     * 修改接口记录
     *
     * @param hostWcsInterface 接口记录
     * @return 结果
     */
    public int updateHostWcsInterface(HostWcsInterface hostWcsInterface);

    /**
     * 批量删除接口记录
     * 
     * @param ids 需要删除的接口记录主键集合
     * @return 结果
     */
    public int deleteHostWcsInterfaceByIds(Long[] ids);

    /**
     * 删除接口记录信息
     * 
     * @param id 接口记录主键
     * @return 结果
     */
    public int deleteHostWcsInterfaceById(Long id);

    /**
     *  001  搬运任务  host->wcs
     * @param wmsTaskInfo
     */
    void moveTask(WmsTaskInfo wmsTaskInfo);

    /**
     *  002  作业取消  host->wcs state=0时取消
     * @param hostTaskCancel
     */
    void hostCancelTask(TaskCancel hostTaskCancel);

    /**
     * 003  作业取消  wcs->host
     * @param taskCancel
     */
    public void wcsCancelTask(TaskCancel taskCancel);

    /**
     * 004  托盘移动报告  wcs->Host
     */
    public void palletMoveReport(JobInfo jobInfo);

    /**
     * 005  目的地申请  wcs->host
     */
    public void targetApply(JobInfo jobInfo);

    /**
     * 006  目的地变更
     * @param hostChangeTarget
     */
    void changeTarget(HostChangeTarget hostChangeTarget);
    /**
     * 007  搬运任务状态更新  wcs->host
     */
    public void taskInfoStatus(JobInfo jobInfo);


    /**
     *  保存需要发送的接口数据
     * @param funCode  功能码
     * @param InterfaceName  接口名称  test/admin
     * @param sendFrom 发送方
     * @param sendTo 接收方
     * @param content 报文内容
     */
    public void saveSendInterface(String funCode,String InterfaceName,String sendFrom,String sendTo,String content);

    /**
     * 保存接受的接口信息
     * @param funCode  功能码
     * @param InterfaceName  接口名称  test/admin
     * @param sendFrom 发送方
     * @param sendTo 接收方
     * @param content 报文内容
     */
    public void saveRecvInterface(String funCode,String InterfaceName,String sendFrom,String sendTo,String content);

    /**
     * 从接口结果中获取data字符串
     * @param wcsInterface
     * @return
     */
    public String getData(HostWcsInterface wcsInterface);

}
