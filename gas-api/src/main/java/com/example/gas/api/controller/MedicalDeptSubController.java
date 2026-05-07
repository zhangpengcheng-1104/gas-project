package com.example.gas.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.gas.api.common.R;
import com.example.gas.api.service.MedicalDeptSubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/medical_dept_sub")
public class MedicalDeptSubController {

    @Resource
    private MedicalDeptSubService medicalDeptSubService;

    @GetMapping("/searchByDeptId")
    @SaCheckLogin
    public R searchByDeptId(@RequestParam("deptId") Integer deptId) {
        ArrayList<HashMap> list = medicalDeptSubService.searchByDeptId(deptId);
        return R.ok().put("result", list);
    }
}
