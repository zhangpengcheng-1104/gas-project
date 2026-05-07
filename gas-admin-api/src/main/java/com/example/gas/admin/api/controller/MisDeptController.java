package com.example.gas.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.gas.api.common.PageUtils;
import com.example.gas.api.common.R;
import com.example.gas.api.db.pojo.MisDeptEntity;
import com.example.gas.api.service.MisDeptService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mis_dept")
public class MisDeptController {

    @Resource
    private MisDeptService misDeptService;

    @GetMapping("/searchAll")
    @SaCheckLogin
    public R searchAll() {
        ArrayList<HashMap> list = misDeptService.searchAll();
        return R.ok().put("result", list);
    }

    @PostMapping("/searchByPage")
    public R searchByPage(@RequestBody Map<String, Object> params) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("DEPT:SELECT")) {
            return R.error().put("msg", "无权限访问");
        }
        PageUtils pageUtils = misDeptService.searchDeptByPage(params);
        return R.ok().put("result", pageUtils);
    }

    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("DEPT:SELECT")) {
            return R.error().put("msg", "无权限访问");
        }
        MisDeptEntity dept = misDeptService.searchDeptById(id);
        return R.ok().put("dept", dept);
    }

    @PostMapping("/save")
    public R save(@RequestBody MisDeptEntity dept) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("DEPT:INSERT")) {
            return R.error().put("msg", "无权限访问");
        }
        Integer count = misDeptService.checkDeptName(dept.getName(), null);
        if (count > 0) {
            return R.error().put("msg", "部门名称已存在");
        }
        Integer nextId = misDeptService.getNextId();
        dept.setId(nextId);
        int result = misDeptService.insertDept(dept);
        if (result > 0) {
            return R.ok().put("id", dept.getId());
        } else {
            return R.error();
        }
    }

    @PostMapping("/update")
    public R update(@RequestBody MisDeptEntity dept) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("DEPT:UPDATE")) {
            return R.error().put("msg", "无权限访问");
        }
        Integer count = misDeptService.checkDeptName(dept.getName(), dept.getId());
        if (count > 0) {
            return R.error().put("msg", "部门名称已存在");
        }
        int result = misDeptService.updateDept(dept);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> params) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("DEPT:DELETE")) {
            return R.error().put("msg", "无权限访问");
        }
        Integer id = (Integer) params.get("id");
        int result = misDeptService.deleteDept(id);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @PostMapping("/batch-delete")
    public R batchDelete(@RequestBody Map<String, Object> params) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("DEPT:DELETE")) {
            return R.error().put("msg", "无权限访问");
        }
        java.util.List<Integer> ids = (java.util.List<Integer>) params.get("ids");
        int result = misDeptService.batchDeleteDept(ids);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }
}