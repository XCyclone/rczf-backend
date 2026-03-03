package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 企业申请视图实体
 */
@Data
@TableName("view_application_industry")
public class ViewApplicationIndustry implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 申请ID */
    private String applicationId;

    /** 申请对应的运营项目ID */
    private String projectId;

    /** 申请对应的项目名称 */
    private String projectName;

    /** 申请意向小区1 */
    private String communityId1;

    /** 申请意向小区2 */
    private String communityId2;

    /** 申请意向小区3 */
    private String communityId3;

    /** 申请意向小区房源套数 */
    private Integer houseCount;

    /** 申请日期 */
    private String applyDate;

    /** 申请时间 */
    private String applyTime;

    /** 申请企业统一信用代码 */
    private String enterpriseUscc;

    /** 申请企业id */
    private String enterpriseId;

    /** 经办人ID */
    private String operatorId;

    /** 经办人姓名 */
    private String operatorName;

    /** 申请附件ID */
    private String attachmentId;

    /** 申请状态 */
    private Integer applyStatus;

    /** 申请排序 */
    private Integer applySort;

    /** 当前排序 */
    private Integer currentSort;

    /** 企业属地审核日期 */
    private String enterpriseLocationAuditDate;

    /** 企业属地审核时间 */
    private String enterpriseLocationAuditTime;

    /** 企业属地审核人 */
    private String enterpriseLocationAuditor;

    /** 企业属地审核意见 */
    private String enterpriseLocationAuditRemark;

    /** 产促审核日期 */
    private String proPromoAuditDate;

    /** 产促审核时间 */
    private String proPromoAuditTime;

    /** 产促审核人 */
    private String proPromoAuditor;

    /** 产促审核意见 */
    private String proPromoAuditRemark;

    /** 企业名称 */
    private String enterpriseName;
        
    /** 企业属地ID */
    private String locationId;
        
    /** 申请意向小区1名称 */
    private String communityName1;
        
    /** 申请意向小区2名称 */
    private String communityName2;
        
    /** 申请意向小区3名称 */
    private String communityName3;
}