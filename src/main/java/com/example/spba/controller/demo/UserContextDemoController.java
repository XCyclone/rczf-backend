package com.example.spba.controller.demo;

import com.example.spba.controller.base.BaseController;
import com.example.spba.utils.R;
import com.example.spba.utils.UserContextUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户上下文演示Controller
 * 展示如何在Controller中获取用户ID等信息
 */
@RestController
@RequestMapping("/demo/user-context")
public class UserContextDemoController extends BaseController {
    
    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    @GetMapping("/current-user")
    public R getCurrentUserInfo() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", getCurrentUserId());
        userInfo.put("userName", getCurrentUserName());
        userInfo.put("userType", getCurrentUserType());
        userInfo.put("isLoggedIn", isUserLoggedIn());
        
        return R.success(userInfo);
    }
    
    /**
     * 直接使用工具类获取用户ID
     * @return 用户ID
     */
    @GetMapping("/user-id")
    public R getUserIdDirectly() {
        String userId = UserContextUtil.getUserId();
        return R.success("当前用户ID: " + userId);
    }
    
    /**
     * 测试用户上下文功能
     * @return 测试结果
     */
    @GetMapping("/test")
    public R testUserContext() {
        // 这里可以进行一些需要用户ID的业务逻辑
        String userId = getCurrentUserId();
        
        if (userId == null) {
            return R.error("未获取到用户ID，请检查请求头或登录状态");
        }
        
        // 模拟业务处理
        Map<String, Object> result = new HashMap<>();
        result.put("message", "用户上下文获取成功");
        result.put("userId", userId);
        result.put("processingTime", System.currentTimeMillis());
        
        return R.success(result);
    }
}