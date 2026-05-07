package com.example.gas.api;

import com.example.gas.api.config.AmapConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@ServletComponentScan
@ComponentScan("com.example.gas")
@MapperScan({"com.example.gas.api.db.dao", "com.example.gas.api.wx.db.dao"})
@SpringBootApplication
@EnableConfigurationProperties(AmapConfig.class)
public class GasApiApplication {


    public static void main(String[] args) {
        SpringApplication.run(GasApiApplication.class, args);
    }

}
