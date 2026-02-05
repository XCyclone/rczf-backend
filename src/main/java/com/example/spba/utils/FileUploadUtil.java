package com.example.spba.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class FileUploadUtil {

    // 定义允许上传的文件类型
    private static final String[] ALLOWED_EXTENSIONS = {".zip"};

    /**
     * 上传文件
     * @param file 文件
     * @param uploadDir 上传目录
     * @return 上传后的文件路径
     */
    public static String uploadFile(MultipartFile file, String uploadDir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 检查文件扩展名
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IOException("文件名为空");
        }

        String extension = getFileExtension(fileName);
        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(extension.toLowerCase())) {
            throw new IOException("不允许的文件类型: " + extension + ", 只允许: " + String.join(",", ALLOWED_EXTENSIONS));
        }

        // 创建上传目录
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        // 生成唯一文件名
        String uniqueFileName = UUID.randomUUID().toString().replace("-", "") + extension;
        File destFile = new File(uploadDir, uniqueFileName);

        // 保存文件
        file.transferTo(destFile);

        return uniqueFileName;
    }

    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 扩展名
     */
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }
}