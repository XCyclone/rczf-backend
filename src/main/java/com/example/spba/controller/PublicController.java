package com.example.spba.controller;

import com.example.spba.service.CaptchaService;
import com.example.spba.service.PublicService;
import com.example.spba.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {
    
    private static final Logger logger = LoggerFactory.getLogger(PublicController.class);

    @Autowired
    private CaptchaService captchaService;
    
    @Autowired
    private PublicService publicService;

    /**
     * 查询全量国家信息
     * @return 国家信息列表
     */
    @PostMapping("/query/country")
    public R queryCountry() {
        logger.info("[查询国家信息] 调用公共服务查询国家信息");
        return publicService.queryCountry();
    }


    /**
     * 查询企业ID和企业名称
     * @return 企业信息列表
     */
    @PostMapping("/query/enterprise")
    public R queryEnterprise() {
        logger.info("[查询企业信息] 调用公共服务查询企业信息");
        return publicService.queryEnterprise();
    }
    
    /**
     * 获取验证码
     * @return 包含验证码ID和图片数据的Map
     */
    @PostMapping("/captcha")
    public R getCaptcha() {
        try {
            logger.info("[获取验证码] 开始生成验证码");
            
            // 生成5分钟过期的验证码
            Map<String, Object> captchaResult = captchaService.generateCaptcha(5);
            logger.info("[获取验证码] 验证码生成完成");
            
            return R.success(captchaResult);
        } catch (Exception e) {
            logger.error("[获取验证码] 生成失败，异常: {}", e.getMessage(), e);
            return R.error("获取验证码失败：" + e.getMessage());
        }
    }
//
//    /**
//     * 验证验证码
//     * @param params 包含captchaId和userInput的参数
//     * @return 验证结果
//     */
//    @PostMapping("/validate-captcha")
//    public R validateCaptcha(@RequestBody Map<String, String> params) {
//        try {
//            String captchaId = params.get("captchaId");
//            String userInput = params.get("userInput");
//
//            if (captchaId == null || userInput == null) {
//                return R.error("验证码ID和用户输入不能为空");
//            }
//
//            boolean isValid = captchaService.validateCaptcha(captchaId, userInput);
//
//            if (isValid) {
//                return R.success("验证码验证成功");
//            } else {
//                return R.error("验证码错误或已失效");
//            }
//        } catch (Exception e) {
//            return R.error("验证码验证失败：" + e.getMessage());
//        }
//    }



    
    /**
     * 查询企业属地信息
     * @return 企业属地信息列表
     */
    @PostMapping("/query/enterpriseLoc")
    public R queryEnterpriseLocation() {
        logger.info("[查询企业属地信息] 调用公共服务查询企业属地信息");
        return publicService.queryEnterpriseLocation();
    }
}