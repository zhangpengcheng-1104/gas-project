package com.example.gas.api.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.gas.api.config.AmapConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Service
public class AmapService {

    private static final String BASE_URL = "https://restapi.amap.com/v3";
    
    private static final String GEO_URL = BASE_URL + "/geocode/geo";
    private static final String REGEO_URL = BASE_URL + "/geocode/regeo";
    private static final String AUTOCOMPLETE_URL = BASE_URL + "/assistant/inputtips";
    private static final String POI_SEARCH_URL = BASE_URL + "/place/text";
    private static final String POI_AROUND_URL = BASE_URL + "/place/around";
    private static final String POI_DETAIL_URL = BASE_URL + "/place/detail";

    @Resource
    private AmapConfig amapConfig;

    public JSONObject geocode(String address, String city) {
        Map<String, Object> params = buildBaseParams();
        params.put("address", address);
        if (city != null && !city.isEmpty()) {
            params.put("city", city);
        }
        String response = HttpUtil.get(GEO_URL, params);
        return JSONUtil.parseObj(response);
    }

    public JSONObject regeocode(String location, String extensions) {
        Map<String, Object> params = buildBaseParams();
        params.put("location", location);
        params.put("extensions", extensions != null ? extensions : "base");
        String response = HttpUtil.get(REGEO_URL, params);
        return JSONUtil.parseObj(response);
    }

    public JSONObject inputTips(String keywords, String city, String cityLimit) {
        Map<String, Object> params = buildBaseParams();
        params.put("keywords", keywords);
        if (city != null && !city.isEmpty()) {
            params.put("city", city);
            params.put("citylimit", cityLimit != null ? cityLimit : "false");
        }
        params.put("datatype", "all");
        String response = HttpUtil.get(AUTOCOMPLETE_URL, params);
        return JSONUtil.parseObj(response);
    }

    public JSONObject searchPoi(String keywords, String city, String cityLimit, Integer offset, Integer page) {
        Map<String, Object> params = buildBaseParams();
        params.put("keywords", keywords);
        if (city != null && !city.isEmpty()) {
            params.put("city", city);
            params.put("citylimit", cityLimit != null ? cityLimit : "false");
        }
        params.put("offset", offset != null ? offset : 20);
        params.put("page", page != null ? page : 1);
        params.put("extensions", "all");
        String response = HttpUtil.get(POI_SEARCH_URL, params);
        return JSONUtil.parseObj(response);
    }

    public JSONObject getPoiDetail(String id) {
        Map<String, Object> params = buildBaseParams();
        params.put("id", id);
        String response = HttpUtil.get(POI_DETAIL_URL, params);
        return JSONUtil.parseObj(response);
    }

    public JSONObject searchNearby(String location, Integer radius, Integer offset, Integer page) {
        try {
            Map<String, Object> params = buildBaseParams();
            params.put("location", location);
            params.put("radius", radius != null ? radius : 1000);
            params.put("offset", offset != null ? offset : 20);
            params.put("page", page != null ? page : 1);
            params.put("extensions", "all");
            buildParamsWithSignature(params);
            String response = HttpUtil.get(POI_AROUND_URL, params);
            return JSONUtil.parseObj(response);
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.set("status", "0");
            error.set("info", "ERROR: " + e.getMessage());
            return error;
        }
    }

    public Map<String, Object> getWebConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("webKey", amapConfig.getWebKey());
        config.put("securityJsCode", amapConfig.getSecurityJsCode());
        return config;
    }

    private Map<String, Object> buildBaseParams() {
        Map<String, Object> params = new HashMap<>();
        if (amapConfig == null || amapConfig.getWebServiceKey() == null) {
            throw new RuntimeException("高德地图配置未正确加载，请检查application.yml中的amap配置");
        }
        params.put("key", amapConfig.getWebServiceKey());
        params.put("output", "json");
        return params;
    }
    
    private Map<String, Object> buildParamsWithSignature(Map<String, Object> params) {
        if (amapConfig.getPrivateKey() != null && !amapConfig.getPrivateKey().isEmpty()) {
            String sig = generateSignature(params);
            params.put("sig", sig);
        }
        return params;
    }

    private String generateSignature(Map<String, Object> params) {
        try {
            StringBuilder sb = new StringBuilder();
            params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(entry.getKey()).append("=").append(entry.getValue());
                });
            sb.append(amapConfig.getPrivateKey());
            
            String signStr = sb.toString();
            System.out.println("签名原串: " + signStr);
            
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(signStr.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            String signature = hexString.toString();
            System.out.println("签名结果: " + signature);
            return signature;
        } catch (Exception e) {
            throw new RuntimeException("生成签名失败", e);
        }
    }
}
