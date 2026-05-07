package com.example.gas.api.service.iml;

import cn.hutool.core.map.MapUtil;
import com.example.gas.api.common.PageUtils;
import com.example.gas.api.db.dao.GasWorkerPlanDao;
import com.example.gas.api.db.pojo.GasWorkerPlanEntity;
import com.example.gas.api.service.MedicalDeptSubWorkerPlanService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MedicalDeptSubWorkerPlanServiceImpl implements MedicalDeptSubWorkerPlanService {

    @Resource
    private GasWorkerPlanDao gasWorkerPlanDao;

    @Override
    public ArrayList<HashMap> searchWorkerPlanInRange(Map param) {
        ArrayList<HashMap> list = gasWorkerPlanDao.searchGasWorkerPlanInRange(param);
        return list;
    }

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = gasWorkerPlanDao.searchByPage(param);
        long totalCount = gasWorkerPlanDao.searchCount(param);
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, totalCount, page, length);
        return pageUtils;
    }

    @Override
    public HashMap searchById(Integer id) {
        return gasWorkerPlanDao.searchById(id);
    }

    @Override
    public int insert(GasWorkerPlanEntity entity) {
        return gasWorkerPlanDao.insert(entity);
    }

    @Override
    public int update(GasWorkerPlanEntity entity) {
        return gasWorkerPlanDao.update(entity);
    }

    @Override
    public int deleteById(Integer id) {
        return gasWorkerPlanDao.deleteById(id);
    }

    @Override
    public int deleteByIds(List<Integer> ids) {
        return gasWorkerPlanDao.deleteByIds(ids);
    }

    @Override
    public int countByDateAndDeptSub(Map param) {
        return gasWorkerPlanDao.countByDateAndDeptSub(param);
    }

    @Override
    public Integer searchMaximumByDateAndDeptSub(Map param) {
        return gasWorkerPlanDao.searchMaximumByDateAndDeptSub(param);
    }
}
