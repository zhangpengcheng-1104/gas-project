package com.example.gas.api.db.dao;

import com.example.gas.api.db.pojo.MedicalDeptSubEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface MedicalDeptSubDao {
    public ArrayList<HashMap> searchByDeptId(Integer deptId);
}
