package com.example.spba.controller.demo;

import com.example.spba.controller.base.BaseController;
import com.example.spba.domain.entity.UserInfo;
import com.example.spba.service.UserTokenService;
import com.example.spba.utils.R;
import com.example.spba.utils.UserContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Token用户信息演示Controller
 * 展示如何使用新的token拦截器获取用户信息
 */
@RestController
@RequestMapping("/demo/token-user")
public class TokenUserDemoController extends BaseController {

    @Autowired
    private UserTokenService userTokenService;

    /**
     * 获取当前用户完整信息
     * @return 用户完整信息
     */
    @GetMapping("/current-user-info")
    public R getCurrentUserInfo() {
        try {
            String userId = getCurrentUserId();
            if (userId == null) {
                return R.error("未获取到用户信息，请检查token是否有效");
            }

            UserInfo userInfo = userTokenService.getUserInfoById(userId);
            return R.success(userInfo);
        } catch (Exception e) {
            return R.error("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 验证token有效性
     * @param token token字符串
     * @return 验证结果
     */
    @PostMapping("/validate-token")
    public R validateToken(@RequestParam String token) {
        Map<String, Object> result = new HashMap<>();
        
        boolean isValid = userTokenService.isValidToken(token);
        result.put("valid", isValid);
        
        if (isValid) {
            long expireTime = userTokenService.getTokenExpireTime(token);
            result.put("expireTime", expireTime);
            result.put("message", "Token有效");
            
            if (expireTime > 0) {
                result.put("expireSeconds", expireTime);
                result.put("expireMinutes", expireTime / 60);
                result.put("expireHours", expireTime / 3600);
            } else if (expireTime == 0) {
                result.put("message", "Token即将过期");
            }
        } else {
            result.put("message", "Token无效或已过期");
        }
        
        return R.success(result);
    }

    /**
     * 获取当前用户基本信息（通过UserContextUtil）
     * @return 用户基本信息
     */
    @GetMapping("/basic-info")
    public R getBasicUserInfo() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", UserContextUtil.getUserId());
        userInfo.put("userName", UserContextUtil.getUserName());
        userInfo.put("userType", UserContextUtil.getUserType());
        userInfo.put("isLoggedIn", UserContextUtil.getUserId() != null);
        
        return R.success(userInfo);
    }

    /**
     * 测试需要token的接口
     * @return 测试结果
     */
    @GetMapping("/protected-resource")
    public R accessProtectedResource() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return R.error("访问被拒绝：未提供有效token");
        }
        
        return R.success("访问成功！当前用户ID: " + userId);
    }

    /**
     * 获取token信息
     * @return token相关信息
     */
    @GetMapping("/token-info")
    public R getTokenInfo(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> tokenInfo = new HashMap<>();
        
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        
        tokenInfo.put("providedToken", token);
        tokenInfo.put("currentUserId", getCurrentUserId());
        tokenInfo.put("currentUser", getCurrentUserName());
        
        if (token != null) {
            tokenInfo.put("isValid", userTokenService.isValidToken(token));
            tokenInfo.put("expireTime", userTokenService.getTokenExpireTime(token));
        }
        
        return R.success(tokenInfo);
    }
}