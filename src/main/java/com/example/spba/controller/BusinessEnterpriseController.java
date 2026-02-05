package com.example.spba.controller;

import com.example.spba.domain.dto.*;
import com.example.spba.domain.entity.BusinessEnterprise;
import com.example.spba.service.BusinessEnterpriseService;
import com.example.spba.service.CaptchaService;
import com.example.spba.domain.dto.EnterpriseUserResponseDTO;
import java.util.List;

import com.example.spba.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/business/enterprise")
public class BusinessEnterpriseController {

    @Autowired
    private BusinessEnterpriseService businessEnterpriseService;
    
    @Autowired
    private CaptchaService captchaService;

    /**
     * 企业注册申请
     * @param form 企业注册表单
     * @return 申请结果
     */
    @PostMapping("/register/apply")
    public R registerApply(@Validated(BusinessEnterpriseDTO.Save.class) @RequestBody BusinessEnterpriseDTO form) {
        boolean isCaptchaValid = captchaService.validateCaptcha(form.getCaptchaId(), form.getCaptchaCode());
        if (!isCaptchaValid) {
            return R.error("验证码错误或已失效");
        }
        
        // 这里已经在Service层做了验证，所以直接调用
        return businessEnterpriseService.registerApply(form);
    }

    /**
     * 审批企业注册申请（通过/拒绝）
     * @param form 审批表单
     * @return 审批结果
     */
    @PostMapping("/register/approve")
    public R approveApply(@Validated @RequestBody BusinessEnterpriseApproveDTO form) {
        try {
            businessEnterpriseService.approveApply(
                form.getBusinessEnterpriseId(),
                form.isApproved(),
                form.getInfo()
            );

            String message = form.isApproved() ? "企业注册审批成功" : "已拒绝企业注册申请";
            return R.success(message);
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            return R.error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 获取企业信息
     * @param request 包含企业ID的请求对象
     * @return 企业信息
     */
    @PostMapping("/getEnterpriseInfo")
    public R getEnterpriseInfo(@RequestBody EnterpriseInfoRequestDTO request) {
        try {
            String enterpriseId = request.getEnterprise_id();
            Object enterpriseInfoWithStatus = businessEnterpriseService.getEnterpriseInfoWithApprovalStatus(enterpriseId);
            if (enterpriseInfoWithStatus != null) {
                return R.success(enterpriseInfoWithStatus);
            } else {
                return R.error("未找到企业信息");
            }
        } catch (Exception e) {
            return R.error("获取企业信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 修改企业密码
     * @param request 包含企业ID、原密码和新密码的请求对象
     * @return
     */
    @PostMapping("/updatePassword")
    public R updatePassword(@RequestBody UpdateEnterprisePasswordRequestDTO request) {
        try {
            String enterpriseId = request.getEnterprise_id();
            String oldPassword = request.getOld_password();
            String newPassword = request.getNew_password();
            
            // 参数校验
            if (enterpriseId == null || enterpriseId.trim().isEmpty()) {
                return R.error("企业ID不能为空");
            }
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                return R.error("原密码不能为空");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return R.error("新密码不能为空");
            }
            
            return businessEnterpriseService.updatePassword(enterpriseId, oldPassword, newPassword);
        } catch (Exception e) {
            return R.error("修改密码失败：" + e.getMessage());
        }
    }
    
    /**
     * 企业信息修改申请
     * @param form 企业信息修改表单
     * @return 申请结果
     */
    @PostMapping("/update/apply")
    public R updateApply(@Validated(BusinessEnterpriseUpdateDTO.Save.class) @RequestBody BusinessEnterpriseUpdateDTO form) {
        return businessEnterpriseService.updateApply(form);
    }
    
    /**
     * 获取企业下的用户信息
     * @param request 包含企业ID的请求对象
     * @return 该企业下的用户信息列表
     */
    @PostMapping("/getUserInfo")
    public R getEnterpriseUsers(@RequestBody EnterpriseUsersRequestDTO request) {
        try {
            String enterpriseId = request.getEnterprise_id();
            if (enterpriseId == null || enterpriseId.trim().isEmpty()) {
                return R.error("企业ID不能为空");
            }
            
            List<EnterpriseUserResponseDTO> users = businessEnterpriseService.getEnterpriseUsers(enterpriseId);
            return R.success(users);
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            return R.error("查询用户信息失败：" + e.getMessage());
        }
    }
}