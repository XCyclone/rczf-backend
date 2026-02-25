package com.example.spba.controller;

import com.example.spba.domain.dto.*;
import com.example.spba.domain.entity.BusinessUser;
import com.example.spba.service.BusinessUserService;
import com.example.spba.service.CaptchaService;
import com.example.spba.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.example.spba.utils.RequestAttributeUtil.CURRENT_USER_ID;

@RestController
@RequestMapping("/business/user")
public class UserRegisterController
{
    @Autowired
    private BusinessUserService businessUserService;
    
    @Autowired
    private CaptchaService captchaService;

    /**
     * 个人用户注册
     * @param form
     * @return
     */
    @PostMapping("/register/apply")
    public R register(@Validated(BusinessUserDTO.Save.class) @RequestBody BusinessUserDTO form)
    {
        // 验证验证码
        boolean isCaptchaValid = captchaService.validateCaptcha(form.getCaptchaId(), form.getCaptchaCode());
        if (!isCaptchaValid) {
            return R.error("验证码错误或已失效");
        }

        // 验证证件号码是否已注册
        BusinessUser existIdNumber = businessUserService.getByIdNumber(form.getIdNumber());
        if (existIdNumber != null) {
            return R.error("该证件号码已被注册");
        }

        return businessUserService.register( form);
    }



    /**
     * 申请更新用户信息
     * 可更新字段包括：姓名、性别、证件类型、证件号码、密码、出生日期、
     * 最高学历、国籍、手机号码、注册类型、工作单位名称
     * @param form 用户信息更新申请表单
     * @return 申请结果
     */
    @PostMapping("/update/apply")
    public R updateApply(@Validated(BusinessUserUpdateDTO.Save.class) @RequestBody BusinessUserUpdateDTO form)
    {
        try {
            return businessUserService.updateUser(form);
        } catch (Exception e) {
            return R.error("申请失败：" + e.getMessage());
        }
    }



    /**
     * 获取用户信息
     * @param
     * @return
     */
    @PostMapping("/getUserInfo")
    public R getUserInfo(@RequestAttribute(CURRENT_USER_ID) String userId)
    {
        try {
            Object userInfoWithStatus = businessUserService.getUserInfoWithApprovalStatus(userId);
            if (userInfoWithStatus != null) {
                return R.success(userInfoWithStatus);
            } else {
                return R.error("未找到用户信息");
            }
        } catch (Exception e) {
            return R.error("获取用户信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 修改用户密码
     * @param request 包含用户ID、原密码和新密码的请求对象
     * @return
     */
    @PostMapping("/updateUserPassword")
    public R updateUserPassword(@RequestAttribute(CURRENT_USER_ID) String userId, @RequestBody UpdatePasswordRequestDTO request)
    {
        try {
            String oldPassword = request.getOldPassword();
            String newPassword = request.getNewPassword();
            
            // 参数校验
            if (userId == null || userId.trim().isEmpty()) {
                return R.error("用户ID不能为空");
            }
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                return R.error("原密码不能为空");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return R.error("新密码不能为空");
            }
            
            return businessUserService.updateUserPassword(userId, oldPassword, newPassword);
        } catch (Exception e) {
            return R.error("修改密码失败：" + e.getMessage());
        }
    }
}