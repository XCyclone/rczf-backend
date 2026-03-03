package com.example.spba.service;

import com.example.spba.domain.dto.AgencyApplySubmitDTO;
import com.example.spba.domain.dto.AgencyApplyUpdateDTO;
import com.example.spba.domain.dto.UserApplicationQueryDTO;
import com.example.spba.utils.R;

public interface ApplicationAgencyService {
    
    /**
     * 机关单位申请提交
     * @param userId 用户ID
     * @param form 申请参数
     * @return 提交结果
     */
    R submitAgencyApply(String userId, AgencyApplySubmitDTO form);
    
    /**
     * 机关单位申请撤回
     * @param userId 用户ID
     * @param applicationId 申请ID
     * @return 撤回结果
     */
    R withdrawAgencyApply(String userId, String applicationId);
    
    /**
     * 机关单位申请修改
     * @param userId 用户ID
     * @param form 修改参数
     * @return 修改结果
     */
    R updateAgencyApply(String userId, AgencyApplyUpdateDTO form);
    
    /**
     * 查询机关单位员工的所有申请记录
     * @param userId 用户ID
     * @return 申请记录列表
     */
    R queryAgencyApplications(String userId);
    
    /**
     * 分页查询机关单位员工的所有申请记录
     * @param userId 用户ID
     * @param queryDTO 分页查询参数
     * @return 分页的申请记录列表
     */
    R queryAgencyApplicationsWithPage(String userId, UserApplicationQueryDTO queryDTO);
    
    /**
     * 查询用户的所有申请记录（统一查询接口）
     * @param userId 用户ID
     * @return 所有申请记录列表
     */
    R queryAllApplications(String userId);
}