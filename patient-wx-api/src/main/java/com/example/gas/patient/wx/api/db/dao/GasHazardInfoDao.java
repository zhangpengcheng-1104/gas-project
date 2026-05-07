package com.example.gas.patient.wx.api.db.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface GasHazardInfoDao {
    
    HashMap searchById(Integer id);
    
    int update(Map param);
    
    List<HashMap> searchByAssigneeId(Map param);
    
    long searchCountByAssigneeId(Map param);
}
