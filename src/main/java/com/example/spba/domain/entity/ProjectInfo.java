package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 项目信息表
 */
@Data
@TableName("project_info")
public class ProjectInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.NONE)
    private String id;

    /**
     * 项目类型:1-产业人才项目；2-领军优青项目；3-机关单位项目
     */
    private Integer projectType;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 小区总数
     */
    private Integer communityCount;

    /**
     * 发布房源总套数
     */
    private Integer houseCount;

    /**
     * 已分配给企业申请的房源套数-企业属地
     */
    private Integer houseEnterpriselocationCount;

    /**
     * 已分配给企业申请的房源套数-产促中心
     */
    private Integer houseEnterpriseidpcCount;

    /**
     * 已审核通过人数
     */
    private Integer approvalCount;

    /**
     * 企业申请开始时间
     */
    private String enterpriseStartTime;

    /**
     * 企业申请截止时间
     */
    private String enterpriseEndTime;

    /**
     * 企业属地和产促中心审核配额天数
     */
    private Integer enterpriseQuotaDays;

    /**
     * 属地和产促配额审核开始时间
     */
    private String quotaStartTime;

    /**
     * 属地和产促配额审核结束时间
     */
    private String quotaEndTime;

    /**
     * 个人申请开始时间
     */
    private String applyStartTime;

    /**
     * 个人申请截止时间
     */
    private String applyEndTime;

    /**
     * 申请审核结束时间
     */
    private String auditEndTime;

    /**
     * 选房开始时间
     */
    private String selectStartTime;

    /**
     * 补选开始时间
     */
    private String bselectStartTime;

    /**
     * 补选截止时间
     */
    private String bselectEndTime;

    /**
     * 项目关闭时间
     */
    private String projectEndTime;

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
     * 项目是否已完成选房排序：1-未完成；2-已完成
     */
    private Integer sort;

    /**
     * 项目状态：1-开启；2-关闭
     */
    private Integer status;

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