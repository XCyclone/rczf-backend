package com.example.spba.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class UploadConfig implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // 确保上传目录存在
        String uploadDir = "upload";
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
    }
}