package com.example.gas.patient.wx.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "amap")
public class AmapConfig {
    private String webKey;
    private String webServiceKey;
    private String securityJsCode;
    private String privateKey;
}
