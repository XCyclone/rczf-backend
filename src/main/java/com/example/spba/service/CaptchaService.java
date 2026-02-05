package com.example.spba.service;

import com.example.spba.utils.CaptchaUtil;
import com.example.spba.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务类
 */
@Service
public class CaptchaService {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 生成验证码
     * @param expireTime 过期时间（分钟）
     * @return 包含验证码ID和图片数据的Map
     */
    public Map<String, Object> generateCaptcha(int expireTime) {
        // 生成随机验证码
        String captchaText = CaptchaUtil.generateCaptchaText(4);
        
        // 生成验证码图片
        java.awt.image.BufferedImage image = CaptchaUtil.generateCaptchaImage(120, 40, captchaText);
        
        // 将图片转换为Base64字符串
        String base64Image = convertImageToBase64(image);
        
        // 生成验证码ID
        String captchaId = UUID.randomUUID().toString();
        
        // 将验证码存储到Redis中，设置过期时间
        redisUtil.setCaptcha(captchaId, captchaText, expireTime * 60L); // 转换为秒
        
        // 返回验证码ID和图片数据
        Map<String, Object> result = new HashMap<>();
        result.put("captchaId", captchaId);
        result.put("image", base64Image);
        result.put("expireTime", expireTime);
        
        return result;
    }
    
    /**
     * 验证验证码
     * @param captchaId 验证码ID
     * @param userInput 用户输入的验证码
     * @return 验证结果
     */
    public boolean validateCaptcha(String captchaId, String userInput) {
        if (captchaId == null || userInput == null) {
            return false;
        }
        
        // 使用RedisUtil中的验证方法
        return redisUtil.validateAndDeleteCaptcha(captchaId, userInput);
    }
    
    /**
     * 将BufferedImage转换为Base64字符串
     * @param image 图片对象
     * @return Base64字符串
     */
    private String convertImageToBase64(java.awt.image.BufferedImage image) {
        try {
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "png", outputStream);
            byte[] imageData = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}