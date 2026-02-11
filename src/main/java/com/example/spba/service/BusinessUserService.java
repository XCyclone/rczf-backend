package com.example.spba.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spba.domain.dto.BusinessUserDTO;
import com.example.spba.domain.dto.BusinessUserUpdateDTO;
import com.example.spba.domain.entity.BusinessUser;
import com.example.spba.utils.R;

public interface BusinessUserService
{
    /**
     * 根据手机号查询用户
     * @param mobile
     * @return
     */
    BusinessUser getByMobile(String mobile);

    /**
     * 根据证件号码查询用户
     * @param idNumber
     * @return
     */
    BusinessUser getByIdNumber(String idNumber);

    /**
     * 个人用户注册
     * @param form
     * @return
     */
    R register(BusinessUserDTO form);

    /**
     * 审批业务用户：更新business_user_apply状态，审批通过后同步到business_user表
     * @param applyId 申请ID
     * @param approveStatus 审批状态：true-通过，false-拒绝
     * @param info 审批附言
     */
    void approve(String applyId, boolean approveStatus, String info, String userId);
    
    /**
     * 根据用户ID查询用户详细信息
     * @param userId 用户ID
     * @return 用户详细信息
     */
    BusinessUser getUserInfo(String userId);
    
    /**
     * 根据用户ID查询用户详细信息及审批状态
     * @param userId 用户ID
     * @return 用户详细信息及审批状态
     */
    Object getUserInfoWithApprovalStatus(String userId);
    
    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    R updateUserPassword(String userId, String oldPassword, String newPassword);
    
    /**
     * 申请更新用户信息
     * @param form 用户信息更新申请表单
     * @return 申请结果
     */
    R updateUser(BusinessUserUpdateDTO form);
    
    /**
     * 审批用户信息更新申请
     * @param businessUserId 业务用户ID
     * @param approveStatus 审批状态：true-通过，false-拒绝
     * @param info 审批附言
     */
    void approveUpdate(String businessUserId, boolean approveStatus, String info);


    R delUser(String userId);
}