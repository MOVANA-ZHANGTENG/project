package com.deer.wcs.xj.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PLC读取校验结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class P4PlcCheckResult {
    // 是否校验通过
    private boolean pass;

    // ==========预期值==========
    private P4PlcTaskExpectDto expect;

    // ==========PLC实际读取值==========
    private String actFromFloor;
    private String actFromLine;
    private String actFromRow;
    private String actToX;
    private String actToY;
    private String actToZ;
    private String actForkType;
    private String actTrayId;

    /**
     * 组装差异日志字符串
     */
    public String buildDiffLog() {
        return String.format(
                "【PLC任务校验失败】\n" +
                        "预期fromFloor=%s,实际=%s\n" +
                        "预期fromLine=%s,实际=%s\n" +
                        "预期fromRow=%s,实际=%s\n" +
                        "预期toX=%s,实际=%s\n" +
                        "预期toY=%s,实际=%s\n" +
                        "预期toZ=%s,实际=%s\n" +
                        "预期forkType=%s,实际=%s\n" +
                        "预期trayId=%s,实际=%s",
                expect.getFromFloor(), actFromFloor,
                expect.getFromLine(), actFromLine,
                expect.getFromRow(), actFromRow,
                expect.getToX(), actToX,
                expect.getToY(), actToY,
                expect.getToZ(), actToZ,
                expect.getForkType(), actForkType,
                expect.getTrayId(), actTrayId
        );
    }
}
