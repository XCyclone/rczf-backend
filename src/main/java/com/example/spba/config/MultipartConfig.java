package com.example.spba.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置单个文件最大大小
        factory.setMaxFileSize(DataSize.ofMegabytes(10)); // 10MB
        // 设置总上传数据最大大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(50)); // 50MB
        return factory.createMultipartConfig();
    }
}