package com.example.spba.service;

import com.example.spba.domain.dto.LeadingApplySubmitDTO;
import com.example.spba.domain.dto.LeadingApplyUpdateDTO;
import com.example.spba.utils.R;

public interface ApplicationLeadingService {
    
    /**
     * 领军优青人才申请提交
     * @param userId 用户ID
     * @param form 申请参数
     * @return 提交结果
     */
    R submitLeadingApply(String userId, LeadingApplySubmitDTO form);
    
    /**
     * 领军优青人才申请撤回
     * @param userId 用户ID
     * @param applicationId 申请ID
     * @return 撤回结果
     */
    R withdrawLeadingApply(String userId, String applicationId);
    
    /**
     * 领军优青人才申请修改
     * @param userId 用户ID
     * @param form 修改参数
     * @return 修改结果
     */
    R updateLeadingApply(String userId, LeadingApplyUpdateDTO form);
    
    /**
     * 查询领军优青人才的所有申请记录
     * @param userId 用户ID
     * @return 申请记录列表
     */
    R queryLeadingApplications(String userId);
}