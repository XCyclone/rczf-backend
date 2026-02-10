package com.example.spba.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件上传工具类
 * 统一处理文件上传路径和目录创建
 */
@Component
public class FileUploadUtil {

    @Value("${spba.upload.path:#{systemProperties['user.dir']}/upload}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        // 确保上传目录存在
        createUploadDirectory();
    }

    /**
     * 创建上传目录
     */
    private void createUploadDirectory() {
        try {
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("创建上传目录: " + uploadPath);
            }
        } catch (IOException e) {
            System.err.println("创建上传目录失败: " + e.getMessage());
            // 如果无法创建配置的目录，使用临时目录
            uploadPath = System.getProperty("java.io.tmpdir") + "/spba_upload";
            try {
                Path tempPath = Paths.get(uploadPath);
                if (!Files.exists(tempPath)) {
                    Files.createDirectories(tempPath);
                }
                System.out.println("使用临时上传目录: " + uploadPath);
            } catch (IOException ex) {
                throw new RuntimeException("无法创建上传目录", ex);
            }
        }
    }

    /**
     * 上传文件
     * @param file 要上传的文件
     * @param subDirectory 子目录名称（可选）
     * @return 文件信息包装对象
     * @throws IOException 文件上传异常
     */
    public UploadedFileInfo uploadFile(MultipartFile file, String subDirectory) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String storedFileName = UUID.randomUUID().toString().replace("-", "") + fileExtension;

        // 构建完整路径
        String fullPath;
        if (subDirectory != null && !subDirectory.isEmpty()) {
            fullPath = uploadPath + File.separator + subDirectory + File.separator + storedFileName;
            // 确保子目录存在
            Path subPath = Paths.get(uploadPath + File.separator + subDirectory);
            if (!Files.exists(subPath)) {
                Files.createDirectories(subPath);
            }
        } else {
            fullPath = uploadPath + File.separator + storedFileName;
        }

        // 保存文件
        File destFile = new File(fullPath);
        file.transferTo(destFile);

        // 返回文件信息
        UploadedFileInfo fileInfo = new UploadedFileInfo();
        fileInfo.setOriginalName(originalFilename);
        fileInfo.setStoredName(storedFileName);
        fileInfo.setFilePath(fullPath);
        fileInfo.setFileSize(file.getSize());
        fileInfo.setContentType(file.getContentType());
        fileInfo.setSubDirectory(subDirectory);

        return fileInfo;
    }

    /**
     * 上传文件到默认目录
     * @param file 要上传的文件
     * @return 文件信息包装对象
     * @throws IOException 文件上传异常
     */
    public UploadedFileInfo uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file, null);
    }

    /**
     * 获取文件扩展名
     * @param filename 文件名
     * @return 文件扩展名（包含点号）
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }

    /**
     * 获取上传根目录路径
     * @return 上传根目录路径
     */
    public String getUploadPath() {
        return uploadPath;
    }

    /**
     * 上传文件信息包装类
     */
    public static class UploadedFileInfo {
        private String originalName;    // 原始文件名
        private String storedName;      // 存储文件名
        private String filePath;        // 完整文件路径
        private long fileSize;          // 文件大小
        private String contentType;     // 文件类型
        private String subDirectory;    // 子目录

        // getter和setter方法
        public String getOriginalName() { return originalName; }
        public void setOriginalName(String originalName) { this.originalName = originalName; }

        public String getStoredName() { return storedName; }
        public void setStoredName(String storedName) { this.storedName = storedName; }

        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }

        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }

        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }

        public String getSubDirectory() { return subDirectory; }
        public void setSubDirectory(String subDirectory) { this.subDirectory = subDirectory; }
    }
}