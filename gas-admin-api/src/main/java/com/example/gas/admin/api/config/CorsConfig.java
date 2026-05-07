package com.example.gas.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:${user.home}/gas-upload/}")
    private String uploadPath;

    @Value("${spring.servlet.multipart.location:${java.io.tmpdir}gas-api-upload/}")
    private String multipartLocation;

    @PostConstruct
    public void initPath() throws Exception {
        Files.createDirectories(Paths.get(uploadPath));
        Files.createDirectories(Paths.get(multipartLocation));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(Paths.get(uploadPath).toUri().toString());
    }
}
