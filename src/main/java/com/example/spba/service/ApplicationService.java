package com.example.spba.service;

import com.example.spba.utils.R;

/**
 * 统一申请服务接口
 * 处理跨类型的申请记录查询功能
 */
public interface ApplicationService {
    
    /**
     * 统一查询用户的所有申请记录
     * 查询机关单位员工、产业人才、领军优青人才的所有申请记录
     * 考虑员工可能在不同时间申请不同类型的人才资格
     * @param userId 用户ID
     * @return 所有申请记录列表，按申请时间倒序排列
     */
    R queryAllUserApplications(String userId);
}