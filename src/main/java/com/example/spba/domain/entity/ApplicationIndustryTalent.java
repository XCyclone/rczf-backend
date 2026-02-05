package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("application_industry")
public class ApplicationIndustryTalent implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 申请ID（主键） */
    @TableId(type = IdType.NONE)
    private String applicationId;

    /** 申请对应的运营项目ID */
    private String projectId;

    /** 申请对应的项目名称 */
    private String projectName;

    /** 申请意向小区ID */
    private String communityId;

    /** 申请意向小区名称 */
    private String communityName;

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

    /** 申请企业名称 */
    private String enterpriseName;

    /** 经办人ID */
    private String operatorId;

    /** 经办人姓名 */
    private String operatorName;

    /** 申请附件ID */
    private String attachmentId;

    /** 申请状态：0-撤回/待提交；1-提交/待审核；2-属地审核中；3-属地审核通过/待确定配额；4-属地审核拒绝；5-已确定配额/待资格公示；6-资格公示中 */
    private Integer applyStatus;

    /** 申请排序 */
    private Integer applySort;

    /** 当前排序 */
    private Integer currentSort;

    /** 产促审核日期 */
    private String proPromoAuditDate;

    /** 产促审核日期 */
    private String proPromoAuditTime;

    /** 产促审核人 */
    private String proPromoAuditor;

    /** 产促审核意见 */
    private String proPromoAuditRemark;

    /** 住建委审核日期 */
    private String housingAuditDate;

    /** 住建委审核日期 */
    private String housingAuditTime;

    /** 住建委审核人 */
    private String housingAuditor;

    /** 住建委审核意见 */
    private String housingAuditRemark;

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
    
    /** 属地发放指标数 */
    private Integer enterpriseLocationQuota;

    /** 产促确定配额数 */
    private Integer proPromoQuota;
}