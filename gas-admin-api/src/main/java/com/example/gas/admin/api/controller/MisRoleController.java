package com.example.gas.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.gas.api.common.R;
import com.example.gas.api.db.pojo.MisRoleEntity;
import com.example.gas.api.service.MisRoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
public class MisRoleController {

    @Resource
    private MisRoleService misRoleService;

    @PostMapping("/list")
    public R list(@RequestBody Map<String, Object> params) {
        // 检查是否有ROOT角色或MIS_ROLE:SELECT权限
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_ROLE:SELECT")) {
            return R.error().put("msg", "无权限访问");
        }
        // 处理分页参数
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int length = params.get("length") != null ? Integer.parseInt(params.get("length").toString()) : 10;
        int start = (page - 1) * length;
        params.put("start", start);
        params.put("length", length);
        
        ArrayList<HashMap> roleList = misRoleService.searchRoleListWithCount(params);
        // 获取总数
        int totalCount = misRoleService.searchRoleCount(params);
        return R.ok().put("list", roleList).put("total", totalCount);
    }

    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        // 检查是否有ROOT角色或MIS_ROLE:SELECT权限
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_ROLE:SELECT")) {
            return R.error().put("msg", "无权限访问");
        }
        MisRoleEntity role = misRoleService.searchRoleById(id);
        return R.ok().put("role", role);
    }

    @PostMapping("/save")
    public R save(@RequestBody MisRoleEntity role) {
        // 检查是否有ROOT角色或MIS_ROLE:INSERT权限
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_ROLE:INSERT")) {
            return R.error().put("msg", "无权限访问");
        }
        Integer nextId = misRoleService.getNextId();
        role.setId(nextId);
        int result = misRoleService.insertRole(role);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @PostMapping("/update")
    public R update(@RequestBody MisRoleEntity role) {
        // 检查是否有ROOT角色或MIS_ROLE:UPDATE权限
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_ROLE:UPDATE")) {
            return R.error().put("msg", "无权限访问");
        }
        // 禁止修改超级管理员
        if (role.getId() != null && role.getId() == 0) {
            return R.error().put("msg", "超级管理员不可修改");
        }
        int result = misRoleService.updateRole(role);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> params) {
        // 检查是否有ROOT角色或MIS_ROLE:DELETE权限
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_ROLE:DELETE")) {
            return R.error().put("msg", "无权限访问");
        }
        Integer id = (Integer) params.get("id");
        // 禁止删除超级管理员
        if (id != null && id == 0) {
            return R.error().put("msg", "超级管理员不可删除");
        }
        int result = misRoleService.deleteRole(id);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @PostMapping("/batch-delete")
    public R batchDelete(@RequestBody Map<String, Object> params) {
        // 检查是否有ROOT角色或MIS_ROLE:DELETE权限
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_ROLE:DELETE")) {
            return R.error().put("msg", "无权限访问");
        }
        List<Integer> ids = (List<Integer>) params.get("ids");
        // 禁止删除超级管理员
        if (ids != null && ids.contains(0)) {
            return R.error().put("msg", "超级管理员不可删除");
        }
        int result = misRoleService.batchDeleteRole(ids);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @GetMapping("/permissions/{roleId}")
    public R permissions(@PathVariable("roleId") Integer roleId) {
        // 检查是否有ROOT角色或MIS_ROLE:SELECT权限
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_ROLE:SELECT")) {
            return R.error().put("msg", "无权限访问");
        }
        List<String> permissions = misRoleService.searchRolePermissions(roleId);
        return R.ok().put("permissions", permissions);
    }

    @PostMapping("/save-permissions")
    public R savePermissions(@RequestBody Map<String, Object> params) {
        // 检查是否有ROOT角色或MIS_ROLE:UPDATE权限
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_ROLE:UPDATE")) {
            return R.error().put("msg", "无权限访问");
        }
        Integer roleId = (Integer) params.get("roleId");
        // 禁止修改超级管理员权限
        if (roleId != null && roleId == 0) {
            return R.error().put("msg", "超级管理员拥有所有权限，无需分配");
        }
        List<String> permissions = (List<String>) params.get("permissions");
        // 禁止为角色赋予ROOT权限
        if (permissions != null && permissions.contains("ROOT")) {
            return R.error().put("msg", "不能为角色赋予ROOT权限");
        }
        int result = misRoleService.saveRolePermission(roleId, permissions);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @GetMapping("/simple-list")
    public R simpleList() {
        // 检查是否登录
        StpUtil.checkLogin();
        ArrayList<HashMap> list = misRoleService.searchSimpleRoleList();
        return R.ok().put("list", list);
    }

    @GetMapping("/all-permissions")
    public R allPermissions() {
        // 检查是否登录
        StpUtil.checkLogin();
        ArrayList<HashMap> permissions = misRoleService.searchAllPermissions();
        // 处理权限名称，格式化为模块名+动作名
        for (HashMap permission : permissions) {
            String moduleName = (String) permission.get("moduleName");
            String actionName = (String) permission.get("actionName");
            String permissionName = "";
            if (moduleName != null) {
                permissionName += moduleName;
            }
            if (actionName != null) {
                permissionName += actionName;
            }
            // 如果没有模块名和动作名，用permissionCode
            if (permissionName.isEmpty()) {
                permissionName = (String) permission.get("permissionCode");
            }
            permission.put("permissionName", permissionName);
        }
        return R.ok().put("list", permissions);
    }

}
