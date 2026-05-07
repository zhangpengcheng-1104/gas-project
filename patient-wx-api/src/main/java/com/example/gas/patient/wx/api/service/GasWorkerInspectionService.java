package com.example.gas.patient.wx.api.service;

import com.example.gas.patient.wx.api.db.pojo.GasWorkerInspectionEntity;
import java.util.HashMap;

public interface GasWorkerInspectionService {
    
    int insert(GasWorkerInspectionEntity entity);
    
    int update(GasWorkerInspectionEntity entity);
    
    HashMap searchByWorkerPlanId(Integer workerPlanId);
    
    HashMap searchById(Integer id);
}
