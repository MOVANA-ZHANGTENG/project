package com.deer.wcs.xj.handle;


import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.PalletInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.LineInfoService;
import com.deer.wcs.base.service.PalletInfoService;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

/**
 *  希捷P4库位执行器
 */
@Component("XjP4CellHandle")
public class XjP4CellHandle {

    @Autowired
    private CellInfoService cellInfoService;

    @Autowired
    private LineInfoService lineInfoService;

    @Autowired
    private PalletInfoService palletInfoService;

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private JobInfoService jobInfoService;


    public Boolean allotInCell(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        if(taskInfo==null){
            jobInfoService.updateMemo(jobInfo,"任务不存在");
            return false;
        }

        PalletInfo palletInfo = palletInfoService.findByCode(taskInfo.getWareCode(),taskInfo.getPalletCode());
        if(palletInfo==null){
            jobInfoService.updateMemo(jobInfo,"托盘不存在");
            return false;
        }
        CellInfo from  = cellInfoService.findByCode(taskInfo.getWareCode(),taskInfo.getFromCellCode());
        if(from==null){
            jobInfoService.updateMemo(jobInfo,"起点库位不存在");
            return false;
        }
        Condition condition = new Condition(CellInfo.class);
        condition.createCriteria()
                .andEqualTo("disableState",0L)
                .andEqualTo("type",0)
                .andEqualTo("taskState",0L)
                .andEqualTo("invenState",0L)
                .andEqualTo("palletType",palletInfo.getTypeCode())
                .andEqualTo("lineCode",from.getLineCode());

        List<CellInfo> cellInfos = cellInfoService.findByCondition(condition);
        if(cellInfos.isEmpty()){
            jobInfoService.updateMemo(jobInfo,"起点库位"+from.getCode()+"无可用库位");
            return false;
        }
        CellInfo to = cellInfos.get(0);
        jobInfo.setToCellCode(to.getCode());
        taskInfo.setToCellCode(to.getCode());
        jobInfoService.update(jobInfo);
        taskInfoService.update(taskInfo);
        to.setTaskState(taskInfo.getId());
        cellInfoService.update(to);
        return true;

    }


}
