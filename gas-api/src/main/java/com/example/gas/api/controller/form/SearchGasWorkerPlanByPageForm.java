package com.example.gas.api.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SearchGasWorkerPlanByPageForm {
    @NotNull(message = "page不能为空")
    @Min(value = 1, message = "page不能小于1")
    private Integer page;

    @NotNull(message = "length不能为空")
    @Min(value = 1, message = "length不能小于1")
    private Integer length;

    private String workerName;
    private Integer deptId;
    private String startDate;
    private String endDate;
}
