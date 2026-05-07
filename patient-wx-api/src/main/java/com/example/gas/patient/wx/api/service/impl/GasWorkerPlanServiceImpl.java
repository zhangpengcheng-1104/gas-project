package com.example.gas.patient.wx.api.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.example.gas.patient.wx.api.db.dao.GasWorkerPlanDao;
import com.example.gas.patient.wx.api.service.GasWorkerPlanService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class GasWorkerPlanServiceImpl implements GasWorkerPlanService {

    @Resource
    private GasWorkerPlanDao gasWorkerPlanDao;

    @Override
    public ArrayList<HashMap> searchCanWorkInDateRange(Map param) {
        ArrayList<String> list = gasWorkerPlanDao.searchCanWorkInDateRange(param);
        DateTime startDate = DateUtil.parse(MapUtil.getStr(param, "startDate"));
        DateTime endDate = DateUtil.parse(MapUtil.getStr(param, "endDate"));
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH);
        ArrayList result = new ArrayList();
        while (range.hasNext()) {
            String date = range.next().toDateStr();
            if (list.contains(date)) {
                result.add(new HashMap() {{
                    put("date", date);
                    put("status", "巡检");
                }});
            } else {
                result.add(new HashMap() {{
                    put("date", date);
                    put("status", "无");

                }});
            }
        }
        return result;
    }

    @Override
    public ArrayList<HashMap> searchWorkerPlanInDay(Map param) {
        ArrayList<HashMap> list = gasWorkerPlanDao.searchWorkerPlanInDay(param);
        return list;
    }
}
