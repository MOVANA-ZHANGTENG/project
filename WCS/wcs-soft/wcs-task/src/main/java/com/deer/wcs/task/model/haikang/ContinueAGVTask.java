package com.deer.wcs.task.model.haikang;

import lombok.Data;

/**
 * @description:
 * @author:zfj
 * @date:2024/7/18 10:14
 */
@Data
public class ContinueAGVTask {
    private String  reqCode;
    private String  reqTime;
    private String  clientCode;
    private String  tokenCode;
    private String  wbCode;
    private String  podCode;
    private String  agvCode;
    private String  taskCode;
    private String  taskSeq;
    private PositionPath  nextPositionCode;
}
