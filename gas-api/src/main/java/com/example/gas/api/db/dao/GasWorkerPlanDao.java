package com.example.gas.api.db.dao;

import com.example.gas.api.db.pojo.GasWorkerPlanEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface GasWorkerPlanDao {
    ArrayList<HashMap> searchGasWorkerPlanInRange(Map param);
    ArrayList<HashMap> searchByPage(Map param);
    long searchCount(Map param);
    HashMap searchById(Integer id);
    int insert(GasWorkerPlanEntity entity);
    int update(GasWorkerPlanEntity entity);
    int deleteById(Integer id);
    int deleteByIds(List<Integer> ids);
    Integer searchId();
    int countByDateAndDeptSub(Map param);
    Integer searchMaximumByDateAndDeptSub(Map param);
    int updateFilePath(Map param);
}
