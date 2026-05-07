package com.example.gas.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.example.gas.api.common.PageUtils;
import com.example.gas.api.common.R;
import com.example.gas.api.controller.form.InsertGasWorkerForm;
import com.example.gas.api.controller.form.SearchGasWorkerByIdForm;
import com.example.gas.api.controller.form.SearchGasWorkerByPageForm;
import com.example.gas.api.controller.form.SearchWorkerByDeptSubIdForm;
import com.example.gas.api.controller.form.SearchWorkerContentForm;
import com.example.gas.api.controller.form.UpdateGasWorkerForm;
import com.example.gas.api.controller.form.UpdateGasWorkerPhotoForm;
import com.example.gas.api.db.pojo.GasWorkerEntity;
import com.example.gas.api.service.GasWorkerService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gas_worker")
public class GasWorkerController {
    @Resource
    private GasWorkerService gasWorkerService;

    @PostMapping("/searchByPage")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_WORKER:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchGasWorkerByPageForm form) {
        Map param = BeanUtil.beanToMap(form);
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        PageUtils pageUtils = gasWorkerService.searchByPage(param);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/searchContent")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_WORKER:SELECT"}, mode = SaMode.OR)
    public R searchContent(@RequestBody @Valid SearchWorkerContentForm form) {
        HashMap map = gasWorkerService.searchContent(form.getId());
        return R.ok(map);
    }

    @GetMapping("/info/{id}")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_WORKER:SELECT"}, mode = SaMode.OR)
    public R info(@PathVariable("id") Integer id) {
        GasWorkerEntity entity = gasWorkerService.searchById(id);
        return R.ok().put("result", entity);
    }

    @PostMapping("/searchById")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_WORKER:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestBody @Valid SearchGasWorkerByIdForm form) {
        HashMap map = gasWorkerService.searchByIdHashMap(form.getId());
        return R.ok(map);
    }

    @PostMapping("/save")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_WORKER:INSERT"}, mode = SaMode.OR)
    public R save(@RequestBody @Valid InsertGasWorkerForm form) {
        Map param = BeanUtil.beanToMap(form);
        // 生成UUID
        param.put("uuid", IdUtil.simpleUUID().toUpperCase());
        // 处理tag字段
        if (form.getTag() != null && !form.getTag().isEmpty()) {
            try {
                String json = JSONUtil.parseArray(form.getTag()).toString();
                param.replace("tag", json);
            } catch (Exception e) {
                // 如果不是合法的JSON数组字符串，可能是逗号分隔的字符串，尝试转换为JSON数组
                String[] tags = form.getTag().split(",");
                String json = JSONUtil.parseArray(tags).toString();
                param.replace("tag", json);
            }
        }
        gasWorkerService.insert(param);
        return R.ok();
    }

    @PostMapping("/update")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_WORKER:UPDATE"}, mode = SaMode.OR)
    public R update(@RequestBody @Valid UpdateGasWorkerForm form) {
        Map param = BeanUtil.beanToMap(form);
        // 处理tag字段
        if (form.getTag() != null && !form.getTag().isEmpty()) {
            try {
                String json = JSONUtil.parseArray(form.getTag()).toString();
                param.replace("tag", json);
            } catch (Exception e) {
                // 如果不是合法的JSON数组字符串，可能是逗号分隔的字符串，尝试转换为JSON数组
                String[] tags = form.getTag().split(",");
                String json = JSONUtil.parseArray(tags).toString();
                param.replace("tag", json);
            }
        }
        gasWorkerService.update(param);
        return R.ok();
    }

    @PostMapping("/delete")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_WORKER:DELETE"}, mode = SaMode.OR)
    public R delete(@RequestBody Map<String, Object> params) {
        // 支持单个删除和批量删除
        Integer id = (Integer) params.get("id");
        List<Integer> ids = (List<Integer>) params.get("ids");
        
        int result = 0;
        if (id != null) {
            // 单个删除
            result = gasWorkerService.deleteById(id);
        } else if (ids != null && !ids.isEmpty()) {
            // 批量删除
            result = gasWorkerService.deleteByIds(ids);
        }
        
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @PostMapping("/updatePhoto")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_WORKER:UPDATE"}, mode = SaMode.OR)
    public R updatePhoto(@RequestParam("file") MultipartFile file, @RequestParam("workerId") Integer workerId) {
        gasWorkerService.updatePhoto(file, workerId);
        return R.ok();
    }

    @PostMapping("/updatePhotoUrl")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_WORKER:UPDATE"}, mode = SaMode.OR)
    public R updatePhotoUrl(@RequestBody @Valid UpdateGasWorkerPhotoForm form) {
        gasWorkerService.updatePhoto(form.getPhoto(), form.getWorkerId());
        HashMap<String, Object> result = new HashMap<>();
        result.put("photo", form.getPhoto());
        result.put("url", form.getPhoto());
        result.put("path", form.getPhoto());
        return R.ok()
                .put("result", result)
                .put("photo", form.getPhoto())
                .put("url", form.getPhoto())
                .put("path", form.getPhoto());
    }

    @PostMapping("/searchByDeptSubId")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "GAS_WORKER:SELECT"}, mode = SaMode.OR)
    public R searchByDeptSubId(@RequestBody @Valid SearchWorkerByDeptSubIdForm form) {
        ArrayList<HashMap> list = gasWorkerService.searchByDeptSubId(form.getDeptSubId());
        return R.ok().put("result", list);
    }

    @GetMapping("/searchAll")
    @SaCheckLogin
    public R searchAll() {
        ArrayList<HashMap> list = gasWorkerService.searchAll();
        return R.ok().put("result", list);
    }
}
