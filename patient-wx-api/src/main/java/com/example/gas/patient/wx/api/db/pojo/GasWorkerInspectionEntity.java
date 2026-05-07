package com.example.gas.patient.wx.api.db.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class GasWorkerInspectionEntity {
    private Integer id;
    private Integer workerPlanId;
    private Integer workerId;
    private Date inspectDate;
    private Integer hasDanger;
    private String inspectDesc;
    private String inspectPhoto;
    private String inspectResult;
}
