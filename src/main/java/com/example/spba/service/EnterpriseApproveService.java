package com.example.spba.service;

import com.example.spba.utils.R;

/**
 * 企业审批服务接口
 */
public interface EnterpriseApproveService {
    
    /**
     * 机关单位员工申请审批
     * @param applicationId 申请ID
     * @param approved 是否通过审批
     * @param remark 审批备注
     * @param currentUserId 当前用户ID（用于权限校验）
     * @return 审批结果
     */
    R approveAgencyApplication(String applicationId, Boolean approved, String remark, String currentUserId);
    
    /**
     * 产业人才申请审批
     * @param applicationId 申请ID
     * @param approved 是否通过审批
     * @param remark 审批备注
     * @param currentUserId 当前用户ID（用于权限校验）
     * @return 审批结果
     */
    R approveIndustryApplication(String applicationId, Boolean approved, String remark, String currentUserId);
    
    /**
     * 查询企业下产业人才申请记录
     * @param currentUserId 当前用户ID（用于权限校验）
     * @return 申请记录列表
     */
    R queryIndustryApplications(String currentUserId);
    
    /**
     * 查询企业下机关单位员工申请记录
     * @param currentUserId 当前用户ID（用于权限校验）
     * @return 申请记录列表
     */
    R queryAgencyApplications(String currentUserId);
}