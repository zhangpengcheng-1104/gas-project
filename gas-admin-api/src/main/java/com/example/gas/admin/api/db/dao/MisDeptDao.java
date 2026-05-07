package com.example.gas.api.db.dao;

import com.example.gas.api.db.pojo.MisDeptEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mapper
public interface MisDeptDao {
    public ArrayList<HashMap> searchAll();
    public ArrayList<HashMap> searchDeptByPage(Map<String, Object> params);
    public int searchDeptCount(Map<String, Object> params);
    public MisDeptEntity searchDeptById(Integer id);
    public int insertDept(MisDeptEntity dept);
    public int updateDept(MisDeptEntity dept);
    public int deleteDept(Integer id);
    public int batchDeleteDept(java.util.List<Integer> ids);
    public Integer getMaxId();
    public Integer checkDeptName(String name, Integer excludeId);
}