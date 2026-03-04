package com.example.spba.domain;

import lombok.Data;

@Data
public class FileInfo {
    private String id;

    private String relationId; // 关联的业务ID
    private String fileCategory;
    private String originalName;
    private String storedName;
    private String filePath;
    private String fileUrl;
    private String fileType;

    // 通用字段
    private String lastUpdateDate;
    private String lastUpdateTime;
    private String lastUpdater;

    // 保留字段

    private String reserve1;
    private String reserve2;
    private String reserve3;

}