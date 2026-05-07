package com.example.gas.patient.wx.api.service;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.gas.patient.wx.api.config.AmapConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class AmapService {

    private static final String BASE_URL = "https://restapi.amap.com/v3";
    private static final String GEO_URL = BASE_URL + "/geocode/geo";

    @Resource
    private AmapConfig amapConfig;

    public JSONObject geocode(String address, String city) {
        Map<String, Object> params = new TreeMap<>();
        params.put("key", amapConfig.getWebServiceKey());
        params.put("output", "json");
        params.put("address", address);
        if (city != null && !city.isEmpty()) {
            params.put("city", city);
        }
        String sig = generateSig(params);
        params.put("sig", sig);
        String response = HttpUtil.get(GEO_URL, params);
        return JSONUtil.parseObj(response);
    }

    public Map<String, Object> getWebConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("webKey", amapConfig.getWebKey());
        config.put("securityJsCode", amapConfig.getSecurityJsCode());
        return config;
    }

    private String generateSig(Map<String, Object> params) {
        TreeMap<String, Object> sortedParams = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        sb.append(amapConfig.getPrivateKey());
        return DigestUtil.md5Hex(sb.toString());
    }
}
