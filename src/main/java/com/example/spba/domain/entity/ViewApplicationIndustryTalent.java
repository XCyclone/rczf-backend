package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 产业人才申请视图实体类
 */
@Data
@TableName("view_application_industry_talent")
public class ViewApplicationIndustryTalent implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 申请 ID */
    @TableId(type = IdType.INPUT)
    private String applicationId;

    /** 申请对应的运营项目 ID */
    private String projectId;

    /** 申请对应的项目名称 */
    private String projectName;

    /** 申请意向小区 1 */
    private String communityId1;

    /** 申请意向小区 2 */
    private String communityId2;

    /** 申请意向小区 3 */
    private String communityId3;

    /** 申请意向房型 1 */
    private Integer houseType1;

    /** 申请意向房型 2 */
    private Integer houseType2;

    /** 申请意向房型 3 */
    private Integer houseType3;

    /** 申请意向房型 4 */
    private Integer houseType4;

    /** 申请日期 */
    private String applyDate;

    /** 申请时间 */
    private String applyTime;

    /** 申请人 ID */
    private String applicantId;

    /** 申请人证件号 */
    private String applicantZjhm;

    /** 申请人姓名 */
    private String applicantName;

    /** 申请人工作单位 ID */
    private String applicantCompanyId;

    /** 申请人工作单位统一信用代码 */
    private String applicantCompanyUscc;

    /** 是否存在劳动合同关系：1-是 */
    private Integer existLaborContract;

    /** 申请人是否有房产 */
    private Boolean hashouse;

    /** 申请人房产证号 */
    private String houseCertificate;

    /** 申请人房产地址 */
    private String houseaddress;

    /** 申请状态 */
    private Integer applyStatus;

    /** 申请排序 */
    private Integer applySort;

    /** 当前排序 */
    private Integer currentSort;

    /** 企业审核日期 */
    private String companyAuditDate;

    /** 企业审核时间 */
    private String companyAuditTime;

    /** 企业审核人 */
    private String companyAuditor;

    /** 企业审核意见 */
    private String companyAuditRemark;

    /** 住建委审核日期 */
    private String housingAuditDate;

    /** 住建委审核时间 */
    private String housingAuditTime;

    /** 住建委审核人 */
    private String housingAuditor;

    /** 住建委审核意见 */
    private String housingAuditRemark;

    /** 住建委审核是否有房产 true-是；false-否 */
    private Boolean housingAuditHouse;

    /** 住建委审核是否享有保障房待遇 true-是；false-否 */
    private Boolean housingAuditQualification;

    /** 申请人工作单位名称（来自关联表） */
    private String applicantCompanyName;

    /** 申请意向小区 1 名称（来自关联表） */
    private String communityName1;

    /** 申请意向小区 2 名称（来自关联表） */
    private String communityName2;

    /** 申请意向小区 3 名称（来自关联表） */
    private String communityName3;


}
