package com.example.gas.patient.wx.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.example.gas.patient.wx.api.common.R;
import com.example.gas.patient.wx.api.controller.form.SaveInspectionForm;
import com.example.gas.patient.wx.api.controller.form.SearchInspectionForm;
import com.example.gas.patient.wx.api.db.dao.GasWorkerInspectionDao;
import com.example.gas.patient.wx.api.db.dao.UserInfoCardDao;
import com.example.gas.patient.wx.api.db.pojo.GasWorkerInspectionEntity;
import com.example.gas.patient.wx.api.db.pojo.UserInfoCardEntity;
import com.example.gas.patient.wx.api.service.GasWorkerInspectionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/worker_inspection")
public class GasWorkerInspectionController {

    @Resource
    private GasWorkerInspectionService gasWorkerInspectionService;

    @Resource
    private GasWorkerInspectionDao gasWorkerInspectionDao;

    @Resource
    private UserInfoCardDao userInfoCardDao;

    @PostMapping("/searchByWorkerPlanId")
    @SaCheckLogin
    public R searchByWorkerPlanId(@RequestBody HashMap param) {
        Integer workerPlanId = null;
        Object obj = param.get("workerPlanId");
        if (obj instanceof Number) {
            workerPlanId = ((Number) obj).intValue();
        } else if (obj instanceof String) {
            workerPlanId = Integer.parseInt((String) obj);
        }
        if (workerPlanId == null) {
            return R.error("参数错误");
        }
        HashMap map = gasWorkerInspectionService.searchByWorkerPlanId(workerPlanId);
        return R.ok().put("result", map);
    }

    @PostMapping("/save")
    @SaCheckLogin
    public R save(@RequestBody @Valid SaveInspectionForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        UserInfoCardEntity card = userInfoCardDao.searchByUserId(userId);
        if (card == null || card.getWorkerId() == null) {
            return R.error("未绑定巡检员信息");
        }

        HashMap existing = gasWorkerInspectionService.searchByWorkerPlanId(form.getWorkerPlanId());
        
        GasWorkerInspectionEntity entity = new GasWorkerInspectionEntity();
        entity.setWorkerPlanId(form.getWorkerPlanId());
        entity.setWorkerId(card.getWorkerId());
        entity.setInspectDate(new Date());
        entity.setHasDanger(form.getHasDanger());
        entity.setInspectDesc(form.getInspectDesc());
        entity.setInspectPhoto(form.getInspectPhoto());
        entity.setInspectResult(form.getInspectResult());

        if (existing != null && existing.get("id") != null) {
            Object idObj = existing.get("id");
            Integer id = null;
            if (idObj instanceof Number) {
                id = ((Number) idObj).intValue();
            } else if (idObj instanceof String) {
                id = Integer.parseInt((String) idObj);
            }
            if (id != null) {
                entity.setId(id);
                gasWorkerInspectionService.update(entity);
            } else {
                gasWorkerInspectionService.insert(entity);
            }
        } else {
            gasWorkerInspectionService.insert(entity);
        }
        
        return R.ok();
    }

    @PostMapping("/update")
    @SaCheckLogin
    public R update(@RequestBody HashMap param) {
        Integer id = null;
        Object idObj = param.get("id");
        if (idObj instanceof Number) {
            id = ((Number) idObj).intValue();
        } else if (idObj instanceof String) {
            id = Integer.parseInt((String) idObj);
        }
        if (id == null) {
            return R.error("ID不能为空");
        }
        
        HashMap existing = gasWorkerInspectionService.searchById(id);
        if (existing == null) {
            return R.error("记录不存在");
        }

        GasWorkerInspectionEntity entity = new GasWorkerInspectionEntity();
        entity.setId(id);
        
        if (param.containsKey("hasDanger")) {
            Object hasDangerObj = param.get("hasDanger");
            Integer hasDanger = null;
            if (hasDangerObj instanceof Number) {
                hasDanger = ((Number) hasDangerObj).intValue();
            } else if (hasDangerObj instanceof String) {
                hasDanger = Integer.parseInt((String) hasDangerObj);
            }
            if (hasDanger != null) {
                entity.setHasDanger(hasDanger);
            }
        }
        if (param.containsKey("inspectDesc")) {
            entity.setInspectDesc((String) param.get("inspectDesc"));
        }
        if (param.containsKey("inspectPhoto")) {
            entity.setInspectPhoto((String) param.get("inspectPhoto"));
        }
        if (param.containsKey("inspectResult")) {
            entity.setInspectResult((String) param.get("inspectResult"));
        }
        entity.setInspectDate(new Date());

        gasWorkerInspectionService.update(entity);
        return R.ok();
    }
}
