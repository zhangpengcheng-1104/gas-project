package com.example.gas.patient.wx.api.service.impl;

import cn.hutool.core.map.MapUtil;
import com.example.gas.patient.wx.api.common.PageUtils;
import com.example.gas.patient.wx.api.db.dao.GasHazardInfoDao;
import com.example.gas.patient.wx.api.service.GasHazardInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GasHazardInfoServiceImpl implements GasHazardInfoService {

    @Resource
    private GasHazardInfoDao gasHazardInfoDao;

    @Override
    public HashMap searchById(Integer id) {
        return gasHazardInfoDao.searchById(id);
    }

    @Override
    public int update(Map param) {
        return gasHazardInfoDao.update(param);
    }

    @Override
    public PageUtils searchByAssigneeId(Map param) {
        List<HashMap> list = null;
        long count = gasHazardInfoDao.searchCountByAssigneeId(param);
        if (count > 0) {
            list = gasHazardInfoDao.searchByAssigneeId(param);
        } else {
            list = new ArrayList<>();
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }
}
