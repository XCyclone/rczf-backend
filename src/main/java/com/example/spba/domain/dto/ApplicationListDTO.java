package com.example.spba.domain.dto;

import lombok.Data;

/**
 * 申请列表数据传输对象
 */
@Data
public class ApplicationListDTO {
    private String applicationId;         // 申请ID
    private String projectId;             // 申请项目ID
    private String projectName;           // 申请项目名称
    private String expectedMoveinDate;    // 期望入住时间
    private String applyDate;             // 申请日期
    private String applyTime;             // 申请时间
    private Integer enterpriseLocationQuota; // 属地发放指标数
    private Integer proPromoQuota;        // 产促确定配额数
}