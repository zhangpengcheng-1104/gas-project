package com.example.gas.api.controller.form;

import lombok.Data;

@Data
public class SearchWorkerPlanInRangeForm {
    private String startDate;
    private String endDate;
    private Integer deptSubId;
    private Integer deptId;
}
