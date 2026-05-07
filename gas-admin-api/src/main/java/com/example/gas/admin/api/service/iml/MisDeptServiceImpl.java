package com.example.gas.api.service.iml;

import com.example.gas.api.common.PageUtils;
import com.example.gas.api.db.dao.MisDeptDao;
import com.example.gas.api.db.pojo.MisDeptEntity;
import com.example.gas.api.service.MisDeptService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class MisDeptServiceImpl implements MisDeptService {
    @Resource
    private MisDeptDao misDeptDao;

    @Override
    public ArrayList<HashMap> searchAll() {
        ArrayList<HashMap> list = misDeptDao.searchAll();
        return list;
    }

    @Override
    public PageUtils searchDeptByPage(Map<String, Object> params) {
        int page = (Integer) params.get("page");
        int length = (Integer) params.get("length");
        int start = (page - 1) * length;
        params.put("page", start);
        ArrayList<HashMap> list = misDeptDao.searchDeptByPage(params);
        int count = misDeptDao.searchDeptCount(params);
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    public MisDeptEntity searchDeptById(Integer id) {
        return misDeptDao.searchDeptById(id);
    }

    @Override
    public int insertDept(MisDeptEntity dept) {
        return misDeptDao.insertDept(dept);
    }

    @Override
    public int updateDept(MisDeptEntity dept) {
        return misDeptDao.updateDept(dept);
    }

    @Override
    public int deleteDept(Integer id) {
        return misDeptDao.deleteDept(id);
    }

    @Override
    public int batchDeleteDept(java.util.List<Integer> ids) {
        return misDeptDao.batchDeleteDept(ids);
    }

    @Override
    public Integer getNextId() {
        Integer maxId = misDeptDao.getMaxId();
        return maxId == null ? 1 : maxId + 1;
    }

    @Override
    public Integer checkDeptName(String name, Integer excludeId) {
        return misDeptDao.checkDeptName(name, excludeId);
    }
}