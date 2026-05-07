package com.example.gas.patient.wx.api.db.dao;

import com.example.gas.patient.wx.api.db.pojo.GasWorkerInspectionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface GasWorkerInspectionDao {
    
    int insert(GasWorkerInspectionEntity entity);
    
    int update(GasWorkerInspectionEntity entity);
    
    HashMap searchByWorkerPlanId(@Param("workerPlanId") Integer workerPlanId);
    
    HashMap searchById(@Param("id") Integer id);
}
