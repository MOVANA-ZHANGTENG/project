package com.deer.wcs.task.model.recvInterface;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author:zfj
 * @date:2024/5/28 11:01
 */
@Data
@NoArgsConstructor
@ToString
public class TargetApply {
    @NotBlank(message = "任务号不可为空")
    private String wmsTaskNo;
    @NotBlank(message = "任务类型不可为空")
    private String type;
    @NotBlank(message = "托盘号不可为空")
    private String palletCode;
    @NotBlank(message = "托盘类型不可为空")
    private String palletType;
    @NotNull(message = "托盘重量不可为空")
    private Double palletWeight;
    @NotNull(message = "托盘高度不可为空")
    private Double palletHeight;
    //    @NotBlank(message = "原因不可为空")
    private String reason;
}
