package com.example.spba.controller;

import com.example.spba.domain.dto.*;
import com.example.spba.domain.entity.BusinessUser;
import com.example.spba.service.BusinessUserService;
import com.example.spba.service.CaptchaService;
import com.example.spba.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.example.spba.utils.RequestAttributeUtil.CURRENT_USER_ID;

@RestController
@RequestMapping("/business/user")
public class UserRegisterController
{
    private static final Logger logger = LoggerFactory.getLogger(UserRegisterController.class);
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
        logger.info("[用户注册申请] 开始处理注册申请，姓名: {}, 手机号: {}, 证件号码: {}", 
                   form.getName(), form.getMobile(), form.getIdNumber());
        
        // 验证验证码
        boolean isCaptchaValid = captchaService.validateCaptcha(form.getCaptchaId(), form.getCaptchaCode());
        if (!isCaptchaValid) {
            logger.warn("[用户注册申请] 验证码验证失败，证件号码: {}", form.getIdNumber());
            return R.error("验证码错误或已失效");
        }

        // 验证证件号码是否已注册
        BusinessUser existIdNumber = businessUserService.getByIdNumber(form.getIdNumber());
        if (existIdNumber != null) {
            logger.warn("[用户注册申请] 证件号码已存在，证件号码: {}", form.getIdNumber());
            return R.error("该证件号码已被注册");
        }

        R result = businessUserService.register(form);
        logger.info("[用户注册申请] 处理完成，证件号码: {}, 结果: {}", form.getIdNumber(), result.getMessage());
        return result;
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
            logger.info("[用户信息更新申请] 开始处理更新申请，用户ID: {}, 更新字段: {}", form.getId(), form);
            
            R result = businessUserService.updateUser(form);
            logger.info("[用户信息更新申请] 处理完成，用户ID: {}, 结果: {}", form.getId(), result.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("[用户信息更新申请] 处理失败，用户ID: {}, 异常: {}", form.getId(), e.getMessage(), e);
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
            logger.info("[获取用户信息] 开始获取用户信息，用户ID: {}", userId);
            
            Object userInfoWithStatus = businessUserService.getUserInfoWithApprovalStatus(userId);
            if (userInfoWithStatus != null) {
                logger.info("[获取用户信息] 获取成功，用户ID: {}", userId);
                return R.success(userInfoWithStatus);
            } else {
                logger.warn("[获取用户信息] 未找到用户信息，用户ID: {}", userId);
                return R.error("未找到用户信息");
            }
        } catch (Exception e) {
            logger.error("[获取用户信息] 获取失败，用户ID: {}, 异常: {}", userId, e.getMessage(), e);
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
            
            logger.info("[修改用户密码] 开始修改密码，用户ID: {}", userId);
            
            // 参数校验
            if (userId == null || userId.trim().isEmpty()) {
                logger.error("[修改用户密码] 用户ID为空");
                return R.error("用户ID不能为空");
            }
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                logger.error("[修改用户密码] 原密码为空，用户ID: {}", userId);
                return R.error("原密码不能为空");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                logger.error("[修改用户密码] 新密码为空，用户ID: {}", userId);
                return R.error("新密码不能为空");
            }
            
            R result = businessUserService.updateUserPassword(userId, oldPassword, newPassword);
            logger.info("[修改用户密码] 修改完成，用户ID: {}, 结果: {}", userId, result.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("[修改用户密码] 修改失败，用户ID: {}, 异常: {}", userId, e.getMessage(), e);
            return R.error("修改密码失败：" + e.getMessage());
        }
    }
}