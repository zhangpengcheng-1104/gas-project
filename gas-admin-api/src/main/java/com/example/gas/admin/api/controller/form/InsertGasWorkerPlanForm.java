package com.example.gas.api.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class InsertGasWorkerPlanForm {
    @NotNull(message = "deptSubId不能为空")
    @Min(value = 1, message = "deptSubId不能小于1")
    private Integer deptSubId;

    @NotNull(message = "workerId不能为空")
    @Min(value = 1, message = "workerId不能小于1")
    private Integer workerId;

    @NotNull(message = "date不能为空")
    private String date;

    private Long userId;
    private Integer maximum;
    private String description;
}
