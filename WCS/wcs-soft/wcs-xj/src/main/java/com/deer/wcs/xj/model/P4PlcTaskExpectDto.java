package com.deer.wcs.xj.model;

import lombok.Data;

/**
 * WCS下发PLC任务预期值DTO
 */
@Data
public class P4PlcTaskExpectDto {// 来源楼层
    private Short fromFloor;
    // 来源巷道
    private short fromLine;
    // 来源排
    private short fromRow;

    // 目标X
    private short toX;
    // 目标Y
    private short toY;
    // 目标Z
    private short toZ;

    // 叉取类型
    private short forkType;
    // 托盘编号
    private String trayId;
}
