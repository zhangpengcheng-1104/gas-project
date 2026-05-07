package com.example.gas.api.db.dao;

import com.example.gas.api.db.pojo.GasHazardInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface GasHazardInfoDao {
    
    List<HashMap> searchByPage(Map param);
    
    long searchCount(Map param);
    
    GasHazardInfoEntity searchById(Integer id);
    
    HashMap searchByIdHashMap(Integer id);
    
    int insert(GasHazardInfoEntity entity);
    
    int update(GasHazardInfoEntity entity);
    
    int deleteById(Integer id);
    
    int deleteByIds(Map param);
}
