package com.deer.wcs.task.task;

import com.deer.wcs.base.model.CellInfo;
import com.deer.wcs.base.model.LineInfo;
import com.deer.wcs.base.model.PositionInfo;
import com.deer.wcs.base.service.CellInfoService;
import com.deer.wcs.base.service.LineInfoService;
import com.deer.wcs.base.service.PositionInfoService;
import com.deer.wcs.task.model.JobInfo;
import com.deer.wcs.task.model.TaskInfo;
import com.deer.wcs.task.service.JobInfoService;
import com.deer.wcs.task.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.net.UnknownHostException;
import java.util.List;

@Service
public class CellState {
    @Autowired
    private CellInfoService cellInfoService;

    @Autowired
    private LineInfoService lineInfoService;

    @Autowired
    private TaskInfoService taskInfoService;

    @Autowired
    private PositionInfoService positionInfoService;

    @Autowired
    private JobInfoService jobInfoService;


    /***
     * 将无任务的货位  任务状态设置为0
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void setCellTaskState() throws UnknownHostException {

        try {

            Condition condition = new Condition(CellInfo.class);
            condition.createCriteria().andGreaterThan("taskState",0);
            List<CellInfo> list = cellInfoService.findByCondition(condition);

            for (CellInfo cellInfo:list  ) {
                cellInfo=cellInfoService.findById(cellInfo.getId());
                TaskInfo taskInfo = taskInfoService.findById(cellInfo.getTaskState());
                JobInfo jobInfo = jobInfoService.findById(cellInfo.getTaskState());
                if(taskInfo==null && jobInfo==null){
                    cellInfo.setTaskState(0L);
                    cellInfoService.update(cellInfo);
                }
            }

            Condition condition2 = new Condition(LineInfo.class);
            condition2.createCriteria().andGreaterThan("taskState",0);
            List<LineInfo> list2 = lineInfoService.findByCondition(condition);
            for (LineInfo lineInfo:list2  ) {
                lineInfo=lineInfoService.findById(lineInfo.getId());
                TaskInfo taskInfo = taskInfoService.findById(lineInfo.getTaskState());
                JobInfo jobInfo = jobInfoService.findById(lineInfo.getTaskState());
                if(taskInfo==null && jobInfo==null){
                    lineInfo.setTaskState(0L);
                    lineInfoService.update(lineInfo);
                }
            }

            Condition condition3 = new Condition(PositionInfo.class);
            condition3.createCriteria().andGreaterThan("taskState",0);
            List<PositionInfo> list3 = positionInfoService.findByCondition(condition3);

            for (PositionInfo position:list3  ) {
                position=positionInfoService.findById(position.getId());
                TaskInfo taskInfo = taskInfoService.findById(position.getTaskState());
                JobInfo jobInfo = jobInfoService.findById(position.getTaskState());
                if(taskInfo==null && jobInfo==null){
                    position.setTaskState(0L);
                    positionInfoService.update(position);
                }
            }
//            Condition condition4 = new Condition(RcsTsj.class);
//            condition4.createCriteria().andGreaterThan("taskState",0);
//            List<PositionInfo> list4 = positionInfoService.findByCondition(condition4);
//
//            for (PositionInfo position:list3  ) {
//                position=positionInfoService.findById(position.getId());
//                TaskInfo taskInfo = taskInfoService.findById(position.getTaskState());
//                JobInfo jobInfo = jobInfoService.findById(position.getTaskState());
//                if(taskInfo==null && jobInfo==null){
//                    position.setTaskState(0L);
//                    positionInfoService.update(position);
//                }
//            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
