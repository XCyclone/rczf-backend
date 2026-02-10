package com.example.spba.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spba.domain.dto.BusinessEnterpriseDTO;
import com.example.spba.domain.dto.BusinessEnterpriseUpdateDTO;
import com.example.spba.domain.entity.BusinessEnterprise;
import com.example.spba.domain.entity.BusinessEnterpriseApply;
import com.example.spba.domain.dto.EnterpriseUserResponseDTO;
import com.example.spba.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BusinessEnterpriseService {
    /**
     * 企业注册申请
     * @param form 企业注册表单
     * @return 申请结果
     */
    R registerApply(BusinessEnterpriseDTO form);

    /**
     * 新增标签接口
     * @param tag 申请标签
     * @param title 标签名称
     * @param files 图片文件列表
     * @return 操作结果
     */
    public R addTag(String tag, String title, List<MultipartFile> files);

    /**
     * 审批企业注册申请
     * @param applyId 申请ID
     * @param approveStatus 审批状态：true-通过，false-拒绝
     * @param info 审批附言
     */
    void approveApply(String applyId, boolean approveStatus, String info);
    
    /**
     * 根据企业ID查询企业信息
     * @param enterpriseId 企业ID
     * @return 企业信息
     */
    BusinessEnterprise getEnterpriseInfo(String enterpriseId);
    
    /**
     * 根据企业ID查询企业信息及审批状态
     * @param enterpriseId 企业ID
     * @return 企业信息及审批状态
     */
    Object getEnterpriseInfoWithApprovalStatus(String enterpriseId);
    
    /**
     * 修改企业密码
     * @param enterpriseId 企业ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    R updatePassword(String enterpriseId, String oldPassword, String newPassword);
    
    /**
     * 企业信息修改申请
     * @param form 企业信息修改表单
     * @return 申请结果
     */
    R updateApply(BusinessEnterpriseUpdateDTO form);
    
    /**
     * 根据企业ID查询该企业下的所有用户信息
     * @param enterpriseId 企业ID
     * @return 用户信息列表
     */
    List<EnterpriseUserResponseDTO> getEnterpriseUsers(String enterpriseId);
}