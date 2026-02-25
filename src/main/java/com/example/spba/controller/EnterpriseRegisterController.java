package com.example.spba.controller;

import com.example.spba.controller.base.BaseController;
import com.example.spba.domain.dto.*;
import com.example.spba.service.BusinessEnterpriseService;
import com.example.spba.service.CaptchaService;
import com.example.spba.domain.dto.EnterpriseUserResponseDTO;
import java.util.List;

import com.example.spba.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.example.spba.utils.RequestAttributeUtil.CURRENT_USER_ID;

@RestController
@RequestMapping("/business/enterprise")
public class EnterpriseRegisterController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseRegisterController.class);

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
        logger.info("[企业注册申请] 开始处理注册申请，企业名称: {}, 统一社会信用代码: {}", 
                   form.getEnterpriseName(), form.getUscc());
        
        boolean isCaptchaValid = captchaService.validateCaptcha(form.getCaptchaId(), form.getCaptchaCode());
        if (!isCaptchaValid) {
            logger.warn("[企业注册申请] 验证码验证失败，统一社会信用代码: {}", form.getUscc());
            return R.error("验证码错误或已失效");
        }
        
        // 这里已经在Service层做了验证，所以直接调用
        R result = businessEnterpriseService.registerApply(form);
        logger.info("[企业注册申请] 处理完成，统一社会信用代码: {}, 结果: {}", form.getUscc(), result.getMessage());
        return result;
    }

    /**
     * 新增标签接口
     * @param tag 申请标签
     * @param title 标签名称
     * @param files 图片文件列表
     * @return 操作结果
     */
    @PostMapping(value = "/addTag", consumes = "multipart/form-data")
    public R addTag(@RequestParam("tag") String tag,
                    @RequestParam("title") String title,
                    @RequestPart("files") List<MultipartFile> files) {
        logger.info("[新增标签] 标签: {}, 标题: {}, 文件数量: {}", tag, title, files.size());
        
        R result = businessEnterpriseService.addTag(tag, title, files);
        logger.info("[新增标签] 处理完成，标签: {}, 结果: {}", tag, result.getMessage());
        return result;
    }

    /**
     * 审批企业注册申请（通过/拒绝）
     * @param form 审批表单
     * @return 审批结果
     */
    @PostMapping("/register/approve")
    public R approveApply(@Validated @RequestBody BusinessEnterpriseApproveDTO form) {
        try {
            logger.info("[企业注册审批] 开始审批，企业ID: {}, 审批结果: {}, 审批意见: {}", 
                       form.getBusinessEnterpriseId(), form.isApproved() ? "通过" : "拒绝", form.getInfo());
            
            businessEnterpriseService.approveApply(
                form.getBusinessEnterpriseId(),
                form.isApproved(),
                form.getInfo()
            );

            String message = form.isApproved() ? "企业注册审批成功" : "已拒绝企业注册申请";
            logger.info("[企业注册审批] 审批完成，企业ID: {}, 结果: {}", form.getBusinessEnterpriseId(), message);
            return R.success(message);
        } catch (IllegalArgumentException e) {
            logger.error("[企业注册审批] 参数异常，企业ID: {}, 异常: {}", form.getBusinessEnterpriseId(), e.getMessage(), e);
            return R.error(e.getMessage());
        } catch (Exception e) {
            logger.error("[企业注册审批] 审批失败，企业ID: {}, 异常: {}", form.getBusinessEnterpriseId(), e.getMessage(), e);
            return R.error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 获取企业信息
     * @return 企业信息
     */
    @PostMapping("/getEnterpriseInfo")
    public R getEnterpriseInfo(@RequestAttribute(CURRENT_USER_ID) String userId) {
        try {
            logger.info("[获取企业信息] 开始获取企业信息，企业ID: {}", userId);
            
            String enterpriseId = userId; // 获取当前用户ID
            if (enterpriseId == null || enterpriseId.trim().isEmpty()) {
                logger.error("[获取企业信息] 企业ID为空");
                return R.error("用户未登录或用户ID为空");
            }
            
            Object enterpriseInfoWithStatus = businessEnterpriseService.getEnterpriseInfoWithApprovalStatus(enterpriseId);
            if (enterpriseInfoWithStatus != null) {
                logger.info("[获取企业信息] 获取成功，企业ID: {}", userId);
                return R.success(enterpriseInfoWithStatus);
            } else {
                logger.warn("[获取企业信息] 未找到企业信息，企业ID: {}", userId);
                return R.error("未找到企业信息");
            }
        } catch (Exception e) {
            logger.error("[获取企业信息] 获取失败，企业ID: {}, 异常: {}", userId, e.getMessage(), e);
            return R.error("获取企业信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 修改企业密码
     * @param request 包含企业ID、原密码和新密码的请求对象
     * @return
     */
    @PostMapping("/updatePassword")
    public R updatePassword(@RequestAttribute(CURRENT_USER_ID) String userId, @RequestBody UpdateEnterprisePasswordRequestDTO request) {
        try {
            String enterpriseId = userId; // 获取当前用户ID
            String oldPassword = request.getOld_password();
            String newPassword = request.getNew_password();
            
            logger.info("[修改企业密码] 开始修改密码，企业ID: {}", enterpriseId);
            
            // 参数校验
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                logger.error("[修改企业密码] 原密码为空，企业ID: {}", enterpriseId);
                return R.error("原密码不能为空");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                logger.error("[修改企业密码] 新密码为空，企业ID: {}", enterpriseId);
                return R.error("新密码不能为空");
            }
            
            R result = businessEnterpriseService.updatePassword(enterpriseId, oldPassword, newPassword);
            logger.info("[修改企业密码] 修改完成，企业ID: {}, 结果: {}", enterpriseId, result.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("[修改企业密码] 修改失败，企业ID: {}, 异常: {}", userId, e.getMessage(), e);
            return R.error("修改密码失败：" + e.getMessage());
        }
    }
    
    /**
     * 企业信息修改申请
     * @param form 企业信息修改表单
     * @return 申请结果
     */
    @PostMapping("/update/apply")
    public R updateApply(@RequestAttribute(CURRENT_USER_ID) String userId, @Validated(BusinessEnterpriseUpdateDTO.Save.class) @RequestBody BusinessEnterpriseUpdateDTO form) {
        logger.info("[企业信息修改申请] 开始处理修改申请，企业ID: {}, 修改内容: {}", userId, form);
        
        R result = businessEnterpriseService.updateApply(form, userId);
        logger.info("[企业信息修改申请] 处理完成，企业ID: {}, 结果: {}", userId, result.getMessage());
        return result;
    }
    

}