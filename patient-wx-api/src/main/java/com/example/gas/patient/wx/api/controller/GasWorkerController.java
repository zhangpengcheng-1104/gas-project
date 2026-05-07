package com.example.gas.patient.wx.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.gas.patient.wx.api.common.R;
import com.example.gas.patient.wx.api.service.GasWorkerService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;

@RestController
@RequestMapping("/worker")
public class GasWorkerController {
    @Resource
    private GasWorkerService gasWorkerService;

    @PostMapping("/searchContent")
    public R searchContent() {
        int userId = StpUtil.getLoginIdAsInt();
        HashMap map = gasWorkerService.searchContentByUserId(userId);
        return R.ok().put("result", map);
    }
}
