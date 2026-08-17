package com.deer.wcs.task.model.recvInterface;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @description:
 * @author:zfj
 * @date:2024/5/27 10:54
 */
@Data
@NoArgsConstructor
@ToString
public class HostChangeTarget {
    @NotBlank(message = "任务号不可为空")
    private String wmsTaskNo;
    @NotBlank(message = "原任务号不可为空")
    private String oldWmsTaskNo;
    @NotBlank(message = "新目的地位置不可为空")
    private String toCellCode;
    @NotBlank(message = "原目的地位置不可为空")
    private String oldToCellCode;
    //    @NotBlank(message = "原因不可为空")
    private String reason;
}
