package com.example.gas.patient.wx.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.example.gas.patient.wx.api.common.R;
import com.example.gas.patient.wx.api.service.AmapService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/amap")
public class AmapController {

    @Resource
    private AmapService amapService;

    @GetMapping("/config")
    @SaCheckLogin
    public R getWebConfig() {
        Map<String, Object> config = amapService.getWebConfig();
        return R.ok().put("result", config);
    }

    @GetMapping("/geocode")
    @SaCheckLogin
    public R geocode(
            @RequestParam String address,
            @RequestParam(required = false) String city) {
        JSONObject result = amapService.geocode(address, city);
        return R.ok().put("result", result);
    }
}
