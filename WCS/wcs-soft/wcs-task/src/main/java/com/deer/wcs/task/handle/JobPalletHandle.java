package com.deer.wcs.task.handle;


import com.deer.wcs.base.model.PalletInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.PalletInfoService;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("JobPalletHandle")
public class JobPalletHandle {
    @Autowired
    private PalletInfoService palletInfoService;

    @Autowired
    private JobInfoService jobInfoService;

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private PositionInfoService positionInfoService;

    @Autowired
    private CellInfoService cellInfoService;

    public Boolean selectEmptyPallet(JobInfo jobInfo){
        TaskInfo taskInfo = taskInfoService.findById(jobInfo.getTaskId());
        String wareCode = taskInfo.getWareCode();
        PalletInfo palletInfo = palletInfoService.getAEmptyPallet(wareCode);
        if(palletInfo==null){
            jobInfoService.updateMemo(jobInfo,"没有空托盘");
            return true;
        }
        jobInfo.setPalletCode(palletInfo.getCode());
        jobInfo.setFromCellCode(palletInfo.getCellCode());
        jobInfoService.update(jobInfo);
        jobInfoService.updateMemo(jobInfo,"获取空托盘成功，托盘号："+palletInfo.getCode()+"，库位号："+palletInfo.getCellCode());
        return false;
    }


}
