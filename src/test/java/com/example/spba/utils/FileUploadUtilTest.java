package com.example.spba.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@SpringBootTest
public class FileUploadUtilTest {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Test
    public void testFileUpload() {
        try {
            // 创建模拟文件
            byte[] content = "test content".getBytes();
            MultipartFile mockFile = new MockMultipartFile("test.txt", "test.txt", "text/plain", content);
            
            // 测试文件上传
            FileUploadUtil.UploadedFileInfo fileInfo = fileUploadUtil.uploadFile(mockFile, "test");
            
            System.out.println("原始文件名: " + fileInfo.getOriginalName());
            System.out.println("存储文件名: " + fileInfo.getStoredName());
            System.out.println("文件路径: " + fileInfo.getFilePath());
            System.out.println("文件大小: " + fileInfo.getFileSize());
            System.out.println("上传目录: " + fileUploadUtil.getUploadPath());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}