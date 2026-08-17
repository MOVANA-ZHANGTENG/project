package com.deer.wcs.task.model.recvInterface;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @description:
 * @author:zfj
 * @date:2024/5/28 10:54
 */
@Data
@NoArgsConstructor
@ToString
public class PalletMove {
    @NotBlank(message = "任务号不可为空")
    private String wmsTaskNo;
    @NotBlank(message = "托盘号不可为空")
    private String palletCode;
    @NotBlank(message = "起始位置不可为空")
    private String fromCellCode;
    @NotBlank(message = "目的地位置不可为空")
    private String toCellCode;
    //    @NotBlank(message = "原因不可为空")
    private String reason;
}
