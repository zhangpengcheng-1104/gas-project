package com.example.gas.patient.wx.api.service.impl;

import com.example.gas.patient.wx.api.db.dao.GasWorkerInspectionDao;
import com.example.gas.patient.wx.api.db.pojo.GasWorkerInspectionEntity;
import com.example.gas.patient.wx.api.service.GasWorkerInspectionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class GasWorkerInspectionServiceImpl implements GasWorkerInspectionService {

    @Resource
    private GasWorkerInspectionDao gasWorkerInspectionDao;

    @Override
    public int insert(GasWorkerInspectionEntity entity) {
        return gasWorkerInspectionDao.insert(entity);
    }

    @Override
    public int update(GasWorkerInspectionEntity entity) {
        return gasWorkerInspectionDao.update(entity);
    }

    @Override
    public HashMap searchByWorkerPlanId(Integer workerPlanId) {
        return gasWorkerInspectionDao.searchByWorkerPlanId(workerPlanId);
    }

    @Override
    public HashMap searchById(Integer id) {
        return gasWorkerInspectionDao.searchById(id);
    }
}
