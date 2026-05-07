package com.example.gas.api.service;

import com.example.gas.api.common.PageUtils;
import com.example.gas.api.db.pojo.GasWorkerEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface GasWorkerService {
    PageUtils searchByPage(Map param);
    HashMap searchContent(int id);
    GasWorkerEntity searchById(Integer id);
    HashMap searchByIdHashMap(int id);
    int insert(GasWorkerEntity entity);
    void insert(Map param);
    int update(GasWorkerEntity entity);
    void update(Map param);
    int deleteById(Integer id);
    int deleteByIds(List<Integer> ids);
    Integer getNextId();
    void updatePhoto(MultipartFile file, Integer workerId);
    void updatePhoto(String photo, Integer workerId);
    ArrayList<HashMap> searchByDeptSubId(Integer deptSubId);
    ArrayList<HashMap> searchAll();
}
