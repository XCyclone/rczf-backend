package com.example.spba.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件信息查询 DTO
 * 用于封装查询结果，避免直接返回实体类的所有字段
 */
@Data
public class FileInfoQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    private String id;

    /**
     * 关联 ID
     */
    private String relationId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件 URL
     */
    private String fileUrl;

    /**
     * 文件类型（MIME 类型）
     */
    private String fileType;

    /**
     * 文件分类
     */
    private String fileCategory;
}
