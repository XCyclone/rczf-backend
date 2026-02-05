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

    /** 申请意向小区ID */
    private String communityId;

    /** 申请意向小区房源套数 */
    private Integer houseCount;

    /** 期望入住时间 */
    private String expectedMoveinDate;

    /** 申请日期 */
    private String applyDate;

    /** 申请时间 */
    private String applyTime;

    /** 申请企业ID */
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
    private String companyAuditDate;

    /** 企业属地审核时间 */
    private String companyAuditTime;

    /** 企业属地审核人 */
    private String companyAuditor;

    /** 企业属地审核意见 */
    private String companyAuditRemark;

    /** 产促审核日期 */
    private String chargeUnitAuditDate;

    /** 产促审核时间 */
    private String chargeUnitAuditTime;

    /** 产促审核人 */
    private String chargeUnitAuditor;

    /** 产促审核意见 */
    private String chargeUnitAuditRemark;

    /** 企业名称 */
    private String enterpriseName;

    /** 小区名称 */
    private String communityName;
}