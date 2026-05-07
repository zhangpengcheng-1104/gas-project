package com.example.gas.api.db.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class GasWorkerPlanEntity {
    private Integer id;
    private Integer workerId;
    private Integer deptSubId;
    private Date date;
    private Long userId;
    private Integer maximum;
    private String description;
}
