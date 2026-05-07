package com.example.gas.patient.wx.api.controller.form;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

@Data
public class SaveInspectionForm {

    @NotNull(message = "巡检计划ID不能为空")
    @Min(value = 1, message = "巡检计划ID无效")
    private Integer workerPlanId;

    @NotNull(message = "有无隐患不能为空")
    private Integer hasDanger;

    private String inspectDesc;

    private String inspectPhoto;

    @NotBlank(message = "巡检结果不能为空")
    private String inspectResult;
}
