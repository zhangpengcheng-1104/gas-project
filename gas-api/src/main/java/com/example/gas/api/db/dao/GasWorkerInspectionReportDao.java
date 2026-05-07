package com.example.gas.api.db.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface GasWorkerInspectionReportDao {
    
    List<HashMap> searchInspectionReportList(Map param);
    
    HashMap searchInspectionReportDetail(@Param("planId") Integer planId);
}
