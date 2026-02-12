package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("application_leading_talent")
public class ApplicationLeadingTalent implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 申请ID（主键） */
    @TableId(type = IdType.INPUT)
    private String applicationId;

    /** 申请对应的运营项目ID */
    private String projectId;

    /** 项目名称 */
    private String projectName;

    /** 申请意向小区1 */
    private String communityId1;

    /** 申请意向小区2 */
    private String communityId2;

    /** 申请意向小区3 */
    private String communityId3;

    /** 申请意向房型1 */
    private Integer houseType1;

    /** 申请意向房型2 */
    private Integer houseType2;

    /** 申请意向房型3 */
    private Integer houseType3;

    /** 申请意向房型4 */
    private Integer houseType4;

    /** 申请日期 */
    private String applyDate;

    /** 申请时间 */
    private String applyTime;

    /** 申请人ID */
    private String applicantId;

    /** 申请人证件号 */
    private String applicantZjhm;

    /** 申请人姓名 */
    private String applicantName;

    /** 申请人工作单位ID */
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

    /** 申请附件ID */
    private String attachmentId;

    /** 申请状态：0-撤回/待提交；1-提交/待审核；2-工作单位审核中；3-工作单位审核通过；4-工作单位审核拒绝；5-产促审核中；6-产促审核通过；7-产促审核拒绝；8-住建委审核中；9-住建委审核通过；10-住建委审核拒绝；11-组织部审核中；12-组织部审核通过；13-组织部审核拒绝；14-轮候排序；15-确定配租/待选房；16-选房完成 */
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

    /** 产促审核日期 */
    private String proPromoAuditDate;

    /** 产促审核时间 */
    private String proPromoAuditTime;

    /** 产促审核人 */
    private String proPromoAuditor;

    /** 产促审核意见 */
    private String proPromoAuditRemark;

    /** 住建委审核日期 */
    private String housingAuditDate;

    /** 住建委审核时间 */
    private String housingAuditTime;

    /** 住建委审核人 */
    private String housingAuditor;

    /** 住建委审核意见 */
    private String housingAuditRemark;

    /** 住建委审核是否有房产true-是；false-否 */
    private Boolean housingAuditHouse;

    /** 住建委审核是否享有保障房待遇true-是；false-否 */
    private Boolean housingAuditQualification;

    /** 组织部审核日期 */
    private String orgAuditDate;

    /** 组织部审核时间 */
    private String orgAuditTime;

    /** 组织部审核人 */
    private String orgAuditor;

    /** 组织部审核意见 */
    private String orgAuditRemark;

    /** 保留字段1 */
    private String reserve1;

    /** 保留字段2 */
    private String reserve2;

    /** 保留字段3 */
    private String reserve3;

    /** 保留字段4 */
    private String reserve4;

    /** 保留字段5 */
    private String reserve5;

    /** 保留字段6 */
    private String reserve6;
}