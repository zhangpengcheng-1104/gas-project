package com.example.gas.api.service;

import com.example.gas.api.common.PageUtils;
import com.example.gas.api.db.pojo.MisDeptEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface MisDeptService {
    public ArrayList<HashMap> searchAll();
    public PageUtils searchDeptByPage(Map<String, Object> params);
    public MisDeptEntity searchDeptById(Integer id);
    public int insertDept(MisDeptEntity dept);
    public int updateDept(MisDeptEntity dept);
    public int deleteDept(Integer id);
    public int batchDeleteDept(java.util.List<Integer> ids);
    public Integer getNextId();
    public Integer checkDeptName(String name, Integer excludeId);
}