package com.example.gas.patient.wx.api.service;

import com.example.gas.patient.wx.api.common.PageUtils;

import java.util.HashMap;
import java.util.Map;

public interface GasHazardInfoService {
    
    HashMap searchById(Integer id);
    
    int update(Map param);
    
    PageUtils searchByAssigneeId(Map param);
}
