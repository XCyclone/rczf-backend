package com.example.spba.service;

import com.example.spba.domain.dto.IndustryApplySubmitDTO;
import com.example.spba.domain.dto.IndustryApplyUpdateDTO;
import com.example.spba.utils.R;

public interface ApplicationIndustryService {
    
    /**
     * 产业人才申请提交
     * @param userId 用户ID
     * @param form 申请参数
     * @return 提交结果
     */
    R submitIndustryApply(String userId, IndustryApplySubmitDTO form);
    
    /**
     * 产业人才申请撤回
     * @param userId 用户ID
     * @param applicationId 申请ID
     * @return 撤回结果
     */
    R withdrawIndustryApply(String userId, String applicationId);
    
    /**
     * 产业人才申请修改
     * @param userId 用户ID
     * @param form 修改参数
     * @return 修改结果
     */
    R updateIndustryApply(String userId, IndustryApplyUpdateDTO form);
    
    /**
     * 查询产业人才的所有申请记录
     * @param userId 用户ID
     * @return 申请记录列表
     */
    R queryIndustryApplications(String userId);
}