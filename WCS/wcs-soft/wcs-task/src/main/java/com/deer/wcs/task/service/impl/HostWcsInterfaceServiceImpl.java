package com.deer.wcs.task.service.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.deer.wcs.base.model.AreaInfo;
import com.deer.wcs.base.model.TaskTypePriority;
import com.deer.wcs.base.model.WareInfo;
import com.deer.wcs.base.model.WmsTaskInfo;
import com.deer.wcs.base.service.AreaInfoService;
import com.deer.wcs.base.service.TaskPriorityService;
import com.deer.wcs.base.service.WareInfoService;
import com.deer.wcs.base.service.WmsTaskInfoService;
import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtil;
import com.deer.wcs.task.dao.HostWcsInterfaceMapper;
import com.deer.wcs.task.model.*;
import com.deer.wcs.task.model.recvInterface.*;
import com.deer.wcs.task.service.HostWcsInterfaceService;
import com.deer.wcs.task.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 * 接口记录Service业务层处理
 *
 * @author deer
 * @date 2024-05-23
 */
@Service
public class HostWcsInterfaceServiceImpl extends AbstractService<HostWcsInterface, Long> implements HostWcsInterfaceService {
    @Autowired
    private HostWcsInterfaceMapper hostWcsInterfaceMapper;
    @Autowired
    private TaskInfoService taskInfoService;
    @Autowired
    private WareInfoService wareInfoService;
    @Autowired
    private AreaInfoService areaInfoService;
    @Autowired
    private WmsTaskInfoService wmsTaskInfoService;
    @Autowired
    private TaskPriorityService taskPriorityService;


    @Override
    public void save(HostWcsInterface model) {
        model.setStartTime(DateUtil.getNowDateTimeString());
        model.setDisplayContent(toPretty(model.getContent()));
        model.setDisplayRecv(toPretty(model.getRecv()));
        super.save(model);
    }

    @Override
    public int update(HostWcsInterface model) {
        model.setDisplayContent(toPretty(model.getContent()));
        model.setDisplayRecv(toPretty(model.getRecv()));
        return super.update(model);
    }

    //报文代码格式化
    /*
        1. 先得到json对象
        2. 对json对象进行格式化操作
     */
    private String toPretty(String json){
        JSONObject object = JSONObject.parseObject(json);
        String str = JSON.toJSONString(object, JSONWriter.Feature.PrettyFormat);
        return str;
    }

    @Override
    public void record(String code, String name, String from, String to, String param, String result) {
        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode(code);
        hostWcsInterface.setInterfaceName(name);
        hostWcsInterface.setSendFrom(from);
        hostWcsInterface.setSendTo(to);
        hostWcsInterface.setContent(param);
        hostWcsInterface.setRecv(result);
        hostWcsInterface.setType(0);
        hostWcsInterface.setDisplayContent(toPretty(hostWcsInterface.getContent()));
        hostWcsInterface.setDisplayRecv(toPretty(hostWcsInterface.getRecv()));
        hostWcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        hostWcsInterface.setEndTime(DateUtil.getNowDateTimeString());
        super.save(hostWcsInterface);
    }


    /**
     * 这个方法，需要根据JOBId来判断是更新还是保存
     * @param jobId
     * @param code
     * @param name
     * @param from
     * @param to
     * @param param
     * @param result
     */
    @Override
    public void record(Long jobId, String code, String name, String from, String to, String param, String result) {
        Condition condition = new Condition(HostWcsInterface.class);
        condition.createCriteria().andEqualTo("jobId",jobId)
                .andEqualTo("interfaceName",name)
                .andEqualTo("sendFrom",from)
                .andEqualTo("sendTo",to);
        condition.orderBy("id").desc();
        List<HostWcsInterface> list = super.findByCondition(condition);
        HostWcsInterface hostWcsInterface =null;
        if(list!=null && list.size()>0){
            hostWcsInterface = list.get(0);
        }
        if(hostWcsInterface==null){
            hostWcsInterface=new HostWcsInterface();
        }
        hostWcsInterface.setCode(code);
        hostWcsInterface.setJobId(jobId);
        hostWcsInterface.setInterfaceName(name);
        hostWcsInterface.setSendFrom(from);
        hostWcsInterface.setSendTo(to);
        hostWcsInterface.setContent(param);
        hostWcsInterface.setRecv(result);
        hostWcsInterface.setType(0);
        hostWcsInterface.setDisplayContent(toPretty(hostWcsInterface.getContent()));
        hostWcsInterface.setDisplayRecv(toPretty(hostWcsInterface.getRecv()));
        hostWcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        hostWcsInterface.setEndTime(DateUtil.getNowDateTimeString());
        if(hostWcsInterface.getId()==null){
            super.save(hostWcsInterface);
        }else {
            super.update(hostWcsInterface);
        }
    }

    @Override
    public void record(String code, String name, String from, String to, String param, String result,String url) {
        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode(code);
        hostWcsInterface.setInterfaceName(name);
        hostWcsInterface.setSendFrom(from);
        hostWcsInterface.setSendTo(to);
        hostWcsInterface.setContent(param);
        hostWcsInterface.setRecv(result);
        hostWcsInterface.setType(0);
        hostWcsInterface.setDisplayContent(toPretty(hostWcsInterface.getContent()));
        hostWcsInterface.setDisplayRecv(toPretty(hostWcsInterface.getRecv()));
        hostWcsInterface.setUrl(url);
        hostWcsInterface.setStartTime(DateUtil.getNowDateTimeString());
        hostWcsInterface.setEndTime(DateUtil.getNowDateTimeString());
        super.save(hostWcsInterface);
    }

    /**
     * 查询接口记录
     *
     * @param id 接口记录主键
     * @return 接口记录
     */
    @Override
    public HostWcsInterface selectHostWcsInterfaceById(Long id) {
        return hostWcsInterfaceMapper.selectHostWcsInterfaceById(id);
    }

    /**
     * 查询接口记录列表
     *
     * @param criteria
     * @return 接口记录
     */
    @Override
    public List<HostWcsInterfaceDto> findList(HostWcsInterfaceCriteria criteria) {
        return hostWcsInterfaceMapper.findList(criteria);
    }

    /**
     * 新增接口记录
     *
     * @param hostWcsInterface 接口记录
     * @return 结果
     */
    @Override
    public int insertHostWcsInterface(HostWcsInterface hostWcsInterface) {
        return hostWcsInterfaceMapper.insertHostWcsInterface(hostWcsInterface);
    }

    /**
     * 修改接口记录
     *
     * @param hostWcsInterface 接口记录
     * @return 结果
     */
    @Override
    public int updateHostWcsInterface(HostWcsInterface hostWcsInterface) {
        return hostWcsInterfaceMapper.updateHostWcsInterface(hostWcsInterface);
    }

    /**
     * 批量删除接口记录
     *
     * @param ids 需要删除的接口记录主键
     * @return 结果
     */
    @Override
    public int deleteHostWcsInterfaceByIds(Long[] ids) {
        return hostWcsInterfaceMapper.deleteHostWcsInterfaceByIds(ids);
    }

    /**
     * 删除接口记录信息
     *
     * @param id 接口记录主键
     * @return 结果
     */
    @Override
    public int deleteHostWcsInterfaceById(Long id) {
        return hostWcsInterfaceMapper.deleteHostWcsInterfaceById(id);
    }

    /**
     * 001  搬运任务  host->wcs
     */
    @Override
    public void moveTask(WmsTaskInfo wmsTaskInfo) {

        AreaInfo areaInfo = areaInfoService.findBy("code", wmsTaskInfo.getAreaCode());
        if (areaInfo != null) {
            wmsTaskInfo.setAreaName(areaInfo.getName());
        }
        /*
            根据任务类型赋予不同的优先级  数字越小，优先级越大
         */
        if (wmsTaskInfo.getPriority() == null) {
            TaskTypePriority typePriority = taskPriorityService.findBy("code",wmsTaskInfo.getType().toString());
            if(typePriority!=null){
                wmsTaskInfo.setPriority(typePriority.getPriority());
            }
        }
        wmsTaskInfoService.save(wmsTaskInfo);
    }

    /**
     * 002  作业取消  host->wcs state=0时取消
     */
    @Override
    public void hostCancelTask(TaskCancel hostTaskCancel) {
            Condition condition = new Condition(TaskInfo.class);
            condition.createCriteria().andEqualTo("wmsTaskNo", hostTaskCancel.getWmsTaskNo())
                    .andEqualTo("palletCode", hostTaskCancel.getPalletCode())
                    .andEqualTo("state", 0);
            List<TaskInfo> taskInfoList = taskInfoService.findByCondition(condition);
            if (taskInfoList.size() != 1) {
                throw new RuntimeException("找不到对应的任务");
            }
            for (TaskInfo taskInfo : taskInfoList) {
                taskInfo.setState(-1);
                taskInfoService.update(taskInfo);
            }

    }


    /**
     * 003  作业取消  wcs->host
     */
    public void wcsCancelTask(TaskCancel taskCancel) {
        if (taskCancel.getReason() == null || taskCancel.getReason().equals("")) {
            taskCancel.setReason("wcs取消任务");
        }
        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode("003");
        hostWcsInterface.setInterfaceName("/wcsCancelTask");
        hostWcsInterface.setSendFrom("wcs");
        hostWcsInterface.setSendTo("Host");
        hostWcsInterface.setContent(JSONObject.toJSONString(taskCancel));
        hostWcsInterface.setType(0);
        save(hostWcsInterface);
    }
    /**
     * 004  托盘移动报告  wcs->Host
     */
    public void palletMoveReport(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        PalletMove palletMove = new PalletMove();
        palletMove.setWmsTaskNo(taskInfo.getWmsTaskNo());
        palletMove.setPalletCode(taskInfo.getPalletCode());
        palletMove.setFromCellCode(taskInfo.getFromCellCode());
        palletMove.setToCellCode(taskInfo.getToCellCode());
        if(palletMove.getReason()==null||palletMove.getReason().equals("")){
            palletMove.setReason("wcs报告托盘位置移动");
        }
        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode("004");
        hostWcsInterface.setInterfaceName("/palletMoveReport");
        hostWcsInterface.setSendFrom("wcs");
        hostWcsInterface.setSendTo("Host");
        hostWcsInterface.setContent(JSONObject.toJSONString(palletMove));
        hostWcsInterface.setType(0);
        save(hostWcsInterface);
    }

    /**
     * 005  目的地申请  wcs->host
     */
    public void targetApply(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        TargetApply targetApply = new TargetApply();
        targetApply.setWmsTaskNo(taskInfo.getWmsTaskNo());
        targetApply.setType(taskInfo.getType());
        targetApply.setPalletCode(taskInfo.getPalletCode());
        targetApply.setPalletType(taskInfo.getPalletType());
        targetApply.setPalletWeight(taskInfo.getPalletWeight());
        targetApply.setPalletHeight(taskInfo.getPalletHeight());
        targetApply.setReason("wcs申请目的地");

        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode("005");
        hostWcsInterface.setInterfaceName("/wcsTargetApply");
        hostWcsInterface.setSendFrom("wcs");
        hostWcsInterface.setSendTo("Host");
        hostWcsInterface.setContent(JSONObject.toJSONString(targetApply));
        hostWcsInterface.setType(0);
        save(hostWcsInterface);
    }

    /**
     * 006  目的地变更  host->wcs
     */
    @Override
    public void changeTarget(HostChangeTarget hostChangeTarget) {
        Condition condition = new Condition(TaskInfo.class);
        condition.createCriteria().andEqualTo("wmsTaskNo", hostChangeTarget.getOldWmsTaskNo())
                .andEqualTo("toCellCode", hostChangeTarget.getOldToCellCode())
                .andNotEqualTo("state", 4);
        List<TaskInfo> taskInfoList = taskInfoService.findByCondition(condition);
        if (taskInfoList.size() != 1) {
            throw new RuntimeException("找不到对应任务");
        }
        for (TaskInfo taskInfo : taskInfoList) {
            taskInfo.setTaskNo(hostChangeTarget.getWmsTaskNo());
            taskInfo.setToCellCode(hostChangeTarget.getToCellCode());
            taskInfoService.update(taskInfo);
        }
    }

    /**
     * 007  搬运任务状态更新  wcs->host
     */
    public void taskInfoStatus(JobInfo jobInfo) {
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        TaskInfoStatus status = new TaskInfoStatus();
        status.setWmsTaskNo(taskInfo.getWmsTaskNo());
        status.setPalletCode(taskInfo.getPalletCode());
        status.setFromCellCode(taskInfo.getFromCellCode());
        status.setToCellCode(taskInfo.getToCellCode());
        status.setStatus(taskInfo.getState());
        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode("007");
        hostWcsInterface.setInterfaceName("/taskInfoStatus");
        hostWcsInterface.setSendFrom("wcs");
        hostWcsInterface.setSendTo("Host");
        hostWcsInterface.setContent(JSONObject.toJSONString(status));
        hostWcsInterface.setType(0);
        save(hostWcsInterface);
    }

    /**
     * 008  拣选/盘点任务完成通知  host->wcs
     */
//    public void wcsCancelTask(TaskCancel taskCancel){
//        if(taskCancel.getReason()==null||taskCancel.getReason().equals("")){
//            taskCancel.setReason("wcs取消任务");
//        }
//        HostWcsInterface hostWcsInterface = new HostWcsInterface();
//        hostWcsInterface.setCode("003");
//        hostWcsInterface.setInterfaceName("/WcsCancelTask");
//        hostWcsInterface.setSendFrom("wcs");
//        hostWcsInterface.setSendTo("Host");
//        hostWcsInterface.setContent(JSONObject.toJSONString(taskCancel));
//        save(hostWcsInterface);
//    }
    /**
     * 009  库存同步  host->wcs
     */
//    public void wcsCancelTask(TaskCancel taskCancel){
//        if(taskCancel.getReason()==null||taskCancel.getReason().equals("")){
//            taskCancel.setReason("wcs取消任务");
//        }
//        HostWcsInterface hostWcsInterface = new HostWcsInterface();
//        hostWcsInterface.setCode("003");
//        hostWcsInterface.setInterfaceName("/WcsCancelTask");
//        hostWcsInterface.setSendFrom("wcs");
//        hostWcsInterface.setSendTo("Host");
//        hostWcsInterface.setContent(JSONObject.toJSONString(taskCancel));
//        save(hostWcsInterface);
//    }

    @Override
    public void saveSendInterface(String funCode, String InterfaceName, String sendFrom, String sendTo, String content) {
        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode(funCode);
        hostWcsInterface.setInterfaceName(InterfaceName);
        hostWcsInterface.setSendFrom(sendFrom);
        hostWcsInterface.setSendTo(sendTo);
        hostWcsInterface.setContent(content);
        hostWcsInterface.setType(0);
        save(hostWcsInterface);
    }

    @Override
    public void saveRecvInterface(String funCode, String InterfaceName, String sendFrom, String sendTo, String content) {
        HostWcsInterface hostWcsInterface = new HostWcsInterface();
        hostWcsInterface.setCode(funCode);
        hostWcsInterface.setInterfaceName(InterfaceName);
        hostWcsInterface.setSendFrom(sendFrom);
        hostWcsInterface.setSendTo(sendTo);
        hostWcsInterface.setContent(content);
        hostWcsInterface.setType(1);
        save(hostWcsInterface);
    }

    @Override
    public String getData(HostWcsInterface wcsInterface) {
        HttpResult result = JSONObject.parseObject(wcsInterface.getRecv(), HttpResult.class);
        return result.getData();
    }
}
