package com.deer.wcs.task.model.recvInterface;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @description:
 * @author:zfj
 * @date:2024/5/27 10:28
 */
@Data
@NoArgsConstructor
@ToString
public class TaskCancel {
    @NotBlank(message = "任务号不可为空")
    private String wmsTaskNo;
    @NotBlank(message = "托盘号不可为空")
    private String palletCode;
//    @NotBlank(message = "原因不可为空")
    private String reason;
}
