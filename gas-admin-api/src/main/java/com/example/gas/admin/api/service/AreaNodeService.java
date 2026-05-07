package com.example.gas.api.service;

import com.example.gas.api.db.pojo.AreaNodeEntity;

import java.util.List;

public interface AreaNodeService {
    List<AreaNodeEntity> searchByParentCode(String parentCode);
    
    List<AreaNodeEntity> searchByLevel(Integer level);
    
    AreaNodeEntity searchByCode(String code);
    
    List<AreaNodeEntity> searchAll();
}
