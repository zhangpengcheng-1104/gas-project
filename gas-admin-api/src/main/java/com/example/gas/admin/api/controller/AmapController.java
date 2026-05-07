package com.example.gas.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.example.gas.api.common.R;
import com.example.gas.api.service.AmapService;
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

    @GetMapping("/regeocode")
    @SaCheckLogin
    public R regeocode(
            @RequestParam String location,
            @RequestParam(required = false, defaultValue = "base") String extensions) {
        JSONObject result = amapService.regeocode(location, extensions);
        return R.ok().put("result", result);
    }

    @GetMapping("/inputTips")
    @SaCheckLogin
    public R inputTips(
            @RequestParam String keywords,
            @RequestParam(required = false) String city,
            @RequestParam(required = false, defaultValue = "false") String cityLimit) {
        JSONObject result = amapService.inputTips(keywords, city, cityLimit);
        return R.ok().put("result", result);
    }

    @GetMapping("/searchPoi")
    @SaCheckLogin
    public R searchPoi(
            @RequestParam String keywords,
            @RequestParam(required = false) String city,
            @RequestParam(required = false, defaultValue = "false") String cityLimit,
            @RequestParam(required = false, defaultValue = "20") Integer offset,
            @RequestParam(required = false, defaultValue = "1") Integer page) {
        JSONObject result = amapService.searchPoi(keywords, city, cityLimit, offset, page);
        return R.ok().put("result", result);
    }

    @GetMapping("/poiDetail")
    @SaCheckLogin
    public R getPoiDetail(@RequestParam String id) {
        JSONObject result = amapService.getPoiDetail(id);
        return R.ok().put("result", result);
    }

    @GetMapping("/searchNearby")
    @SaCheckLogin
    public R searchNearby(
            @RequestParam String location,
            @RequestParam(required = false, defaultValue = "1000") Integer radius,
            @RequestParam(required = false, defaultValue = "20") Integer offset,
            @RequestParam(required = false, defaultValue = "1") Integer page) {
        JSONObject result = amapService.searchNearby(location, radius, offset, page);
        return R.ok().put("result", result);
    }
}
