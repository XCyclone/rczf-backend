package com.example.spba.controller.base;

import com.example.spba.utils.UserContextUtil;

/**
 * 基础Controller类
 * 提供获取当前用户信息的便捷方法
 */
public class BaseController {
    
    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    protected String getCurrentUserId() {
        return UserContextUtil.getUserId();
    }
    
    /**
     * 获取当前用户名
     * @return 用户名
     */
    protected String getCurrentUserName() {
        return UserContextUtil.getUserName();
    }
    
    /**
     * 获取当前用户类型
     * @return 用户类型
     */
    protected Integer getCurrentUserType() {
        return UserContextUtil.getUserType();
    }
    
    /**
     * 检查用户是否已登录
     * @return 是否已登录
     */
    protected boolean isUserLoggedIn() {
        return getCurrentUserId() != null;
    }
}