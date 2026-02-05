package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件信息表
 */
@Data
@TableName("file_info")
public class FileInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.NONE)
    private String id;

    /**
     * 标签信息ID（关联application_tag_info表的id）
     */
    private String relationId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 存储的文件名 (UUID)
     */
    private String storedName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 访问URL
     */
    private String fileUrl;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 入库日期
     */
    private String lastUpdateDate;

    /**
     * 入库时间
     */
    private String lastUpdateTime;

    /**
     * 操作人
     */
    private String lastUpdater;

    /**
     * 保留字段1
     */
    private String reserve1;

    /**
     * 保留字段2
     */
    private String reserve2;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 保留字段3
     */
    private String reserve3;
}