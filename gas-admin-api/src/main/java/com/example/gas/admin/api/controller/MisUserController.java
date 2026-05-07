package com.example.gas.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.example.gas.api.common.PageUtils;
import com.example.gas.api.controller.form.LoginForm;
import com.example.gas.api.controller.form.SearchMisUserByPageForm;
import com.example.gas.api.common.R;
import com.example.gas.api.db.pojo.MisUserEntity;
import com.example.gas.api.service.MisUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mis_user")
public class MisUserController {
    @Resource
    private MisUserService misUserService;

    @PostMapping("/login")
    public R login(@RequestBody @Valid LoginForm form) {
        Map param = BeanUtil.beanToMap(form);
        Integer userId = misUserService.login(param);
        if (userId != null) {
            StpUtil.login(userId);
            String token = StpUtil.getTokenValue();
            List<String> permissions = StpUtil.getPermissionList();
            MisUserEntity user = misUserService.searchUserById(userId);
            return R.ok().put("result", true).put("token", token)
                    .put("permissions", permissions).put("user", user);

        }
        return R.ok().put("result", false);

    }

    @GetMapping("/logout")
    @SaCheckLogin
    public R logout() {
        StpUtil.logout();
        return R.ok();
    }

    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_USER:SELECT")) {
            return R.error().put("msg", "无权限访问");
        }
        List<MisUserEntity> userList = misUserService.searchUserList(params);
        return R.ok().put("list", userList);
    }

    @PostMapping("/searchByPage")
    public R searchByPage(@RequestBody @Valid SearchMisUserByPageForm form) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_USER:SELECT")) {
            return R.error().put("msg", "无权限访问");
        }
        Map param = BeanUtil.beanToMap(form);
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        PageUtils pageUtils = misUserService.searchByPage(param);
        return R.ok().put("result", pageUtils);
    }

    @GetMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_USER:SELECT")) {
            return R.error().put("msg", "无权限访问");
        }
        MisUserEntity user = misUserService.searchUserById(id);
        return R.ok().put("user", user);
    }

    @PostMapping("/save")
    public R save(@RequestBody @Valid MisUserEntity user) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_USER:INSERT")) {
            return R.error().put("msg", "无权限访问");
        }
        Integer nextId = misUserService.getNextId();
        user.setId(nextId);
        int result = misUserService.insertUser(user);
        if (result > 0) {
            return R.ok().put("id", user.getId());
        } else {
            return R.error();
        }
    }

    @PostMapping("/update")
    public R update(@RequestBody @Valid MisUserEntity user) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_USER:UPDATE")) {
            return R.error().put("msg", "无权限访问");
        }
        if (user.getId() != null && user.getId() == 0) {
            return R.error().put("msg", "超级管理员不可修改");
        }
        int result = misUserService.updateUser(user);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @PostMapping("/delete")
    public R delete(@RequestBody Map<String, Object> params) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_USER:DELETE")) {
            return R.error().put("msg", "无权限访问");
        }
        Integer id = (Integer) params.get("id");
        if (id != null && id == 0) {
            return R.error().put("msg", "超级管理员不可删除");
        }
        int result = misUserService.deleteUser(id);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @PostMapping("/batch-delete")
    public R batchDelete(@RequestBody Map<String, Object> params) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_USER:DELETE")) {
            return R.error().put("msg", "无权限访问");
        }
        List<Integer> ids = (List<Integer>) params.get("ids");
        if (ids != null && ids.contains(0)) {
            return R.error().put("msg", "超级管理员不可删除");
        }
        int result = misUserService.batchDeleteUsers(ids);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @GetMapping("/roles/{userId}")
    public R roles(@PathVariable("userId") Integer userId) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_USER:SELECT")) {
            return R.error().put("msg", "无权限访问");
        }
        List<Integer> roles = misUserService.searchUserRoles(userId);
        return R.ok().put("roles", roles);
    }

    @PostMapping("/save-roles")
    public R saveRoles(@RequestBody Map<String, Object> params) {
        if (!StpUtil.hasRole("ROOT") && !StpUtil.hasPermission("MIS_USER:UPDATE")) {
            return R.error().put("msg", "无权限访问");
        }
        Integer userId = (Integer) params.get("userId");
        List<Integer> roles = (List<Integer>) params.get("roles");
        int result = misUserService.saveUserRoles(userId, roles);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @SaCheckLogin
    @PostMapping("/update-password")
    public R updatePassword(@RequestBody Map<String, Object> params) {
        Integer userId = StpUtil.getLoginIdAsInt();
        String oldPassword = (String) params.get("oldPassword");
        String newPassword = (String) params.get("newPassword");
        int result = misUserService.updatePassword(userId, oldPassword, newPassword);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error().put("msg", "原密码错误");
        }
    }

}