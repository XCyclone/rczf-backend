package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 企业申请标签信息表
 */
@Data
@TableName("application_tag_info")
public class ApplicationTagInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.NONE)
    private String id;

    /**
     * 企业申请id
     */
    private String applicationId;

    /**
     * 申请标签
     */
    private String tag;

    /**
     * 名称
     */
    private String title;

    /**
     * 保留字段1
     */
    private String reserve1;

    /**
     * 保留字段2
     */
    private String reserve2;

    /**
     * 保留字段3
     */
    private String reserve3;
}