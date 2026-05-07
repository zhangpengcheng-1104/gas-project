package com.example.gas.api.db.dao;

import com.example.gas.api.db.pojo.MedicalDeptEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface MedicalDeptDao {
    public ArrayList<HashMap> searchAll();
}
