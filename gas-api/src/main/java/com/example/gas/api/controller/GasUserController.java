package com.example.gas.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.example.gas.api.common.PageUtils;
import com.example.gas.api.common.R;
import com.example.gas.api.controller.form.InsertGasUserForm;
import com.example.gas.api.controller.form.SearchGasUserByIdForm;
import com.example.gas.api.controller.form.SearchGasUserByPageForm;
import com.example.gas.api.controller.form.UpdateGasUserForm;
import com.example.gas.api.db.pojo.AreaNodeEntity;
import com.example.gas.api.db.pojo.GasUserEntity;
import com.example.gas.api.service.AreaNodeService;
import com.example.gas.api.service.GasUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gas_user")
public class GasUserController {
    @Resource
    private GasUserService gasUserService;

    @Resource
    private AreaNodeService areaNodeService;

    @PostMapping("/searchByPage")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_USER:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchGasUserByPageForm form) {
        Map param = BeanUtil.beanToMap(form);
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        PageUtils pageUtils = gasUserService.searchByPage(param);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/searchById")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_USER:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestBody @Valid SearchGasUserByIdForm form) {
        HashMap map = gasUserService.searchByIdHashMap(form.getId());
        return R.ok(map);
    }

    @GetMapping("/info/{id}")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_USER:SELECT"}, mode = SaMode.OR)
    public R info(@PathVariable("id") Long id) {
        GasUserEntity entity = gasUserService.searchById(id);
        return R.ok().put("result", entity);
    }

    @PostMapping("/save")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_USER:INSERT"}, mode = SaMode.OR)
    public R save(@RequestBody @Valid InsertGasUserForm form) {
        if (gasUserService.checkUserNoExists(form.getUserNo(), null)) {
            return R.error("用户户号已存在");
        }
        GasUserEntity entity = new GasUserEntity();
        BeanUtil.copyProperties(form, entity);
        entity.setStatus((byte) 1);
        entity.setDeleted(false);
        gasUserService.insert(entity);
        return R.ok();
    }

    @PostMapping("/update")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_USER:UPDATE"}, mode = SaMode.OR)
    public R update(@RequestBody @Valid UpdateGasUserForm form) {
        if (gasUserService.checkUserNoExists(form.getUserNo(), form.getId())) {
            return R.error("用户户号已存在");
        }
        GasUserEntity entity = new GasUserEntity();
        BeanUtil.copyProperties(form, entity);
        gasUserService.update(entity);
        return R.ok();
    }

    @PostMapping("/delete")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_USER:DELETE"}, mode = SaMode.OR)
    public R delete(@RequestBody Map<String, Object> params) {
        Long id = params.get("id") != null ? Long.valueOf(params.get("id").toString()) : null;
        List<Long> ids = (List<Long>) params.get("ids");

        int result = 0;
        if (id != null) {
            result = gasUserService.deleteById(id);
        } else if (ids != null && !ids.isEmpty()) {
            result = gasUserService.deleteByIds(ids);
        }

        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @GetMapping("/areaList")
    @SaCheckLogin
    public R areaList(@RequestParam(required = false) String parentCode) {
        List<AreaNodeEntity> list;
        if (parentCode == null || parentCode.isEmpty()) {
            list = areaNodeService.searchByLevel(1);
        } else {
            list = areaNodeService.searchByParentCode(parentCode);
        }
        return R.ok().put("result", list);
    }

    @GetMapping("/areaAll")
    @SaCheckLogin
    public R areaAll() {
        List<AreaNodeEntity> list = areaNodeService.searchAll();
        return R.ok().put("result", list);
    }

    @GetMapping("/searchAll")
    @SaCheckLogin
    public R searchAll() {
        List<HashMap> list = gasUserService.searchAll();
        return R.ok().put("result", list);
    }
}
