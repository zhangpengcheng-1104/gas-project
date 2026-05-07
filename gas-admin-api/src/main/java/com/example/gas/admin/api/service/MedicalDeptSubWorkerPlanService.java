package com.example.gas.api.service;

import com.example.gas.api.common.PageUtils;
import com.example.gas.api.db.pojo.GasWorkerPlanEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MedicalDeptSubWorkerPlanService {
    ArrayList<HashMap> searchWorkerPlanInRange(Map param);
    PageUtils searchByPage(Map param);
    HashMap searchById(Integer id);
    int insert(GasWorkerPlanEntity entity);
    int update(GasWorkerPlanEntity entity);
    int deleteById(Integer id);
    int deleteByIds(List<Integer> ids);
    int countByDateAndDeptSub(Map param);
    Integer searchMaximumByDateAndDeptSub(Map param);
}
