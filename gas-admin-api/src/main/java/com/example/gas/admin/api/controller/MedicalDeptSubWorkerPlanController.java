package com.example.gas.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.example.gas.api.common.PageUtils;
import com.example.gas.api.common.R;
import com.example.gas.api.controller.form.DeleteGasWorkerPlanForm;
import com.example.gas.api.controller.form.InsertGasWorkerPlanForm;
import com.example.gas.api.controller.form.SearchGasWorkerPlanByIdForm;
import com.example.gas.api.controller.form.SearchGasWorkerPlanByPageForm;
import com.example.gas.api.controller.form.SearchWorkerPlanInRangeForm;
import com.example.gas.api.controller.form.UpdateGasWorkerPlanForm;
import com.example.gas.api.db.pojo.GasWorkerPlanEntity;
import com.example.gas.api.service.MedicalDeptSubWorkerPlanService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/worker_plan")
public class MedicalDeptSubWorkerPlanController {
    @Resource
    private MedicalDeptSubWorkerPlanService medicalDeptSubWorkerPlanService;

    @PostMapping("/searchWorkerPlanInRange")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "SCHEDULE:SELECT"}, mode = SaMode.OR)
    public R searchWorkerPlanInRange(@RequestBody @Valid SearchWorkerPlanInRangeForm form) {
        Map<String, Object> param = new HashMap<>();
        if (form.getStartDate() != null && !form.getStartDate().isEmpty()) {
            param.put("startDate", form.getStartDate());
        }
        if (form.getEndDate() != null && !form.getEndDate().isEmpty()) {
            param.put("endDate", form.getEndDate());
        }
        if (form.getDeptSubId() != null) {
            param.put("deptSubId", form.getDeptSubId());
        }
        if (form.getDeptId() != null) {
            param.put("deptId", form.getDeptId());
        }
        ArrayList<HashMap> list = medicalDeptSubWorkerPlanService.searchWorkerPlanInRange(param);
        return R.ok().put("result", list);
    }

    @PostMapping("/searchByPage")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "SCHEDULE:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchGasWorkerPlanByPageForm form) {
        Map param = BeanUtil.beanToMap(form);
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        PageUtils pageUtils = medicalDeptSubWorkerPlanService.searchByPage(param);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/searchById")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "SCHEDULE:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestBody @Valid SearchGasWorkerPlanByIdForm form) {
        HashMap map = medicalDeptSubWorkerPlanService.searchById(form.getId());
        return R.ok(map);
    }

    @PostMapping("/insert")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "SCHEDULE:INSERT"}, mode = SaMode.OR)
    public R insert(@RequestBody @Valid InsertGasWorkerPlanForm form) {
        Map<String, Object> param = new HashMap<>();
        param.put("deptSubId", form.getDeptSubId());
        param.put("date", form.getDate());
        int actualCount = medicalDeptSubWorkerPlanService.countByDateAndDeptSub(param);
        Integer existingMaximum = medicalDeptSubWorkerPlanService.searchMaximumByDateAndDeptSub(param);
        
        int maximum = form.getMaximum() != null ? form.getMaximum() : 1;
        if (existingMaximum != null && existingMaximum > 0) {
            maximum = existingMaximum;
        }
        
        if (actualCount >= maximum) {
            return R.error("当天该班组已达到最大人数(" + maximum + "人)，无法继续派遣");
        }
        
        GasWorkerPlanEntity entity = new GasWorkerPlanEntity();
        entity.setDeptSubId(form.getDeptSubId());
        entity.setWorkerId(form.getWorkerId());
        if (form.getDate() != null && !form.getDate().isEmpty()) {
            entity.setDate(DateUtil.parse(form.getDate()));
        }
        entity.setUserId(form.getUserId());
        entity.setMaximum(maximum);
        entity.setDescription(form.getDescription());
        int result = medicalDeptSubWorkerPlanService.insert(entity);
        return result > 0 ? R.ok() : R.error("新增巡检计划失败");
    }

    @PostMapping("/update")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "SCHEDULE:UPDATE"}, mode = SaMode.OR)
    public R update(@RequestBody @Valid UpdateGasWorkerPlanForm form) {
        Map<String, Object> param = new HashMap<>();
        param.put("deptSubId", form.getDeptSubId());
        param.put("date", form.getDate());
        int actualCount = medicalDeptSubWorkerPlanService.countByDateAndDeptSub(param);
        int maximum = form.getMaximum() != null ? form.getMaximum() : 1;
        if (maximum < actualCount) {
            return R.error("最大人数不能小于实际人数(" + actualCount + "人)");
        }
        
        GasWorkerPlanEntity entity = new GasWorkerPlanEntity();
        entity.setId(form.getId());
        entity.setDeptSubId(form.getDeptSubId());
        entity.setWorkerId(form.getWorkerId());
        if (form.getDate() != null && !form.getDate().isEmpty()) {
            entity.setDate(DateUtil.parse(form.getDate()));
        }
        entity.setUserId(form.getUserId());
        entity.setMaximum(maximum);
        entity.setDescription(form.getDescription());
        int result = medicalDeptSubWorkerPlanService.update(entity);
        return result > 0 ? R.ok() : R.error("更新巡检计划失败");
    }

    @PostMapping("/countByDateAndDeptSub")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "SCHEDULE:SELECT"}, mode = SaMode.OR)
    public R countByDateAndDeptSub(@RequestBody Map<String, Object> params) {
        int count = medicalDeptSubWorkerPlanService.countByDateAndDeptSub(params);
        return R.ok().put("count", count);
    }

    @PostMapping("/searchMaximumByDateAndDeptSub")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "SCHEDULE:SELECT"}, mode = SaMode.OR)
    public R searchMaximumByDateAndDeptSub(@RequestBody Map<String, Object> params) {
        Integer maximum = medicalDeptSubWorkerPlanService.searchMaximumByDateAndDeptSub(params);
        return R.ok().put("maximum", maximum);
    }

    @PostMapping("/delete")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "SCHEDULE:DELETE"}, mode = SaMode.OR)
    public R delete(@RequestBody @Valid DeleteGasWorkerPlanForm form) {
        int result = medicalDeptSubWorkerPlanService.deleteById(form.getId());
        return result > 0 ? R.ok() : R.error("删除巡检计划失败");
    }

    @PostMapping("/deleteBatch")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "SCHEDULE:DELETE"}, mode = SaMode.OR)
    public R deleteBatch(@RequestBody Map<String, Object> params) {
        List<Integer> ids = (List<Integer>) params.get("ids");
        int result = medicalDeptSubWorkerPlanService.deleteByIds(ids);
        return result > 0 ? R.ok() : R.error("批量删除巡检计划失败");
    }
}
