package com.example.gas.patient.wx.api.db.dao;

import com.example.gas.patient.wx.api.db.pojo.GasWorkerEntity;

public interface GasWorkerDao {
    public GasWorkerEntity searchByTel(String tel);
    
    public GasWorkerEntity searchById(Integer id);
}
