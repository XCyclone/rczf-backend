package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 企业标签信息表
 */
@Data
@TableName("business_enterprise_tag")
public class BusinessEnterpriseTag implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.NONE)
    private String id;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 企业标签
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