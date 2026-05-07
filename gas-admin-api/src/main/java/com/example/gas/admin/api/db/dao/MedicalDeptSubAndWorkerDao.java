package com.example.gas.api.db.dao;

import com.example.gas.api.db.pojo.MedicalDeptSubAndWorkerEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface MedicalDeptSubAndWorkerDao {
    void insert(MedicalDeptSubAndWorkerEntity entity);
    
    void updateWorkerSubDept(Map param);
}
