package com.example.spba.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spba.domain.dto.UserOperationInfoDTO;
import com.example.spba.domain.entity.UserOperation;

import java.util.List;

public interface UserOperationService extends IService<UserOperation> {
    /**
     * 记录用户注册申请操作
     * @param userId 用户ID
     * @param type 用户类型：1-个人；2-企业
     * @param businessUserDTO 业务用户信息
     * @return 插入记录的ID
     */
    Integer recordRegisterApply(String userId, Integer type, Object businessUserDTO);

    /**
     * 记录用户信息修改申请操作
     * @param userId 用户ID
     * @param type 用户类型：1-个人；2-企业
     * @param businessUserUpdateDTO 业务用户更新信息
     * @return 插入记录的ID
     */
    Integer recordUpdateApply(String userId, Integer type, Object businessUserUpdateDTO);

    /**
     * 更新用户审批操作
     * @param userId 用户ID
     * @param type 用户类型：1-个人；2-企业
     * @param action 审批动作：2-同意；3-拒绝
     */
    void updateApproveAction(String userId, Integer type, Integer action, String info);
    
    /**
     * 查询所有审批相关信息
     * @return 审批信息列表
     */
    List<UserOperationInfoDTO> getAllOperationInfo();
}