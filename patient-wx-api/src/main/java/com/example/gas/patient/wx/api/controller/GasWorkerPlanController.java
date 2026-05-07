package com.example.gas.patient.wx.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import com.example.gas.patient.wx.api.common.R;
import com.example.gas.patient.wx.api.controller.form.SearchCanWorkInDateRangeForm;
import com.example.gas.patient.wx.api.controller.form.SearchWorkerPlanInDayForm;
import com.example.gas.patient.wx.api.db.dao.UserInfoCardDao;
import com.example.gas.patient.wx.api.db.pojo.UserInfoCardEntity;
import com.example.gas.patient.wx.api.service.GasWorkerPlanService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/worker_plan")
public class GasWorkerPlanController {
    @Resource
    private GasWorkerPlanService gasWorkerPlanService;
    
    @Resource
    private UserInfoCardDao userInfoCardDao;

    @PostMapping("/searchCanWorkInDateRange")
    @SaCheckLogin
    public R searchCanWorkInDateRange(@RequestBody @Valid SearchCanWorkInDateRangeForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        UserInfoCardEntity card = userInfoCardDao.searchByUserId(userId);
        if (card == null || card.getWorkerId() == null) {
            return R.ok().put("result", new ArrayList<>());
        }
        
        Map<String, Object> param = BeanUtil.beanToMap(form);
        param.put("workerId", card.getWorkerId());
        ArrayList<HashMap> list = gasWorkerPlanService.searchCanWorkInDateRange(param);
        return R.ok().put("result", list);
    }

    @PostMapping("/searchWorkerPlanInDay")
    @SaCheckLogin
    public R searchWorkerPlanInDay(@RequestBody @Valid SearchWorkerPlanInDayForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        UserInfoCardEntity card = userInfoCardDao.searchByUserId(userId);
        if (card == null || card.getWorkerId() == null) {
            return R.ok().put("result", new ArrayList<>());
        }
        
        Map param = BeanUtil.beanToMap(form);
        param.put("workerId", card.getWorkerId());
        ArrayList<HashMap> list = gasWorkerPlanService.searchWorkerPlanInDay(param);
        return R.ok().put("result", list);
    }
}
