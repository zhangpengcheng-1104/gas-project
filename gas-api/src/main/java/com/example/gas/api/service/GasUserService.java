package com.example.gas.api.service;

import com.example.gas.api.common.PageUtils;
import com.example.gas.api.db.pojo.GasUserEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface GasUserService {
    PageUtils searchByPage(Map param);
    
    GasUserEntity searchById(Long id);
    
    HashMap searchByIdHashMap(Long id);
    
    int insert(GasUserEntity entity);
    
    int update(GasUserEntity entity);
    
    int deleteById(Long id);
    
    int deleteByIds(List<Long> ids);
    
    boolean checkUserNoExists(String userNo, Long id);

    List<HashMap> searchAll();
}
