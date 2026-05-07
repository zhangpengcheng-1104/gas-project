package com.example.gas.api.db.dao;

import com.example.gas.api.db.pojo.GasUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface GasUserDao {
    List<HashMap> searchByPage(Map param);
    
    long searchCount(Map param);
    
    GasUserEntity searchById(@Param("id") Long id);
    
    HashMap searchByIdHashMap(@Param("id") Long id);
    
    int insert(GasUserEntity entity);
    
    int update(GasUserEntity entity);
    
    int deleteById(@Param("id") Long id);
    
    int deleteByIds(@Param("ids") List<Long> ids);
    
    int checkUserNoExists(@Param("userNo") String userNo, @Param("id") Long id);

    List<HashMap> searchAll();
}
