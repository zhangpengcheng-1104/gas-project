package com.example.gas.api.db.dao;

import com.example.gas.api.db.pojo.GasWorkerEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface GasWorkerDao {
    ArrayList<HashMap> searchByPage(Map param);
    long searchCount(Map param);
    HashMap searchContent(int id);
    GasWorkerEntity searchById(Integer id);
    HashMap searchByIdHashMap(int id);
    int insert(GasWorkerEntity entity);
    int update(GasWorkerEntity entity);
    int deleteById(Integer id);
    int deleteByIds(List<Integer> ids);
    Integer getMaxId();
    void updatePhoto(Map param);
    Integer searchIdByUuid(String uuid);
    ArrayList<HashMap> searchByDeptSubId(Integer deptSubId);
    ArrayList<HashMap> searchAll();
}
