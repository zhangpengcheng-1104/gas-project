package com.example.gas.api.service;

import com.example.gas.api.common.PageUtils;
import com.example.gas.api.db.pojo.GasHazardInfoEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface GasHazardInfoService {
    
    PageUtils searchByPage(Map param);
    
    GasHazardInfoEntity searchById(Integer id);
    
    HashMap searchByIdHashMap(Integer id);
    
    int insert(GasHazardInfoEntity entity);
    
    int update(GasHazardInfoEntity entity);
    
    int deleteById(Integer id);
    
    int deleteByIds(List<Integer> ids);
}
