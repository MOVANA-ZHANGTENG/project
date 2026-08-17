package com.deer.wcs.task.web;

import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.service.JobInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author:zfj
 * @date:2024/5/10 11:37
 */

@Component("Aaa")
public class Aaa {
    @Autowired
    private JobInfoService jobInfoService;

    public boolean cmdNoException(JobInfo jobInfo){
        return true;
    }
    public boolean cmdHasException(JobInfo jobInfo){
        jobInfo.setMemo("未回滚");
        jobInfo.setState(1);
        jobInfoService.update(jobInfo);
        throw new RuntimeException("执行job异常");
    }
    public boolean successNoException(JobInfo jobInfo){
        return true;
    }
    public boolean successHasException(JobInfo jobInfo){
        throw new RuntimeException("job成功回调异常");
    }

}
