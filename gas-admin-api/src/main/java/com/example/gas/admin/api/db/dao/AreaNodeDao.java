package com.example.gas.api.db.dao;

import com.example.gas.api.db.pojo.AreaNodeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AreaNodeDao {
    List<AreaNodeEntity> searchByParentCode(@Param("parentCode") String parentCode);
    
    List<AreaNodeEntity> searchByLevel(@Param("level") Integer level);
    
    AreaNodeEntity searchByCode(@Param("code") String code);
    
    List<AreaNodeEntity> searchAll();
}
