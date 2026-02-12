package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 项目小区对应表
 */
@Data
@TableName("project_community")
public class ProjectCommunity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 小区ID
     */
    private String communityId;

    /**
     * 最后更新日期
     */
    private String lastUpdateDate;

    /**
     * 最后更新时间
     */
    private String lastUpdateTime;

    /**
     * 最后更新人
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
     * 保留字段3
     */
    private String reserve3;

    /**
     * 保留字段4
     */
    private String reserve4;

    /**
     * 保留字段5
     */
    private String reserve5;

    /**
     * 保留字段6
     */
    private String reserve6;
}