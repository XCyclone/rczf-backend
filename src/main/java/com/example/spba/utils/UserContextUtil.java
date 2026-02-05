package com.example.spba.utils;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 用户上下文工具类
 * 用于在线程本地存储用户相关信息
 */
public class UserContextUtil {
    
    private static final ThreadLocal<String> USER_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_NAME_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<Integer> USER_TYPE_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     * @param userId 用户ID
     */
    public static void setUserId(String userId) {
        USER_ID_HOLDER.set(userId);
    }

    /**
     * 获取当前用户ID
     * @return 用户ID
     */
    public static String getUserId() {
        // 首先尝试从ThreadLocal获取
        String userId = USER_ID_HOLDER.get();
        if (userId != null) {
            return userId;
        }
        
        // 如果ThreadLocal中没有，则从Sa-Token中获取
        if (StpUtil.isLogin()) {
            return StpUtil.getLoginIdAsString();
        }
        
        return null;
    }

    /**
     * 设置当前用户名
     * @param userName 用户名
     */
    public static void setUserName(String userName) {
        USER_NAME_HOLDER.set(userName);
    }

    /**
     * 获取当前用户名
     * @return 用户名
     */
    public static String getUserName() {
        return USER_NAME_HOLDER.get();
    }

    /**
     * 设置当前用户类型
     * @param userType 用户类型
     */
    public static void setUserType(Integer userType) {
        USER_TYPE_HOLDER.set(userType);
    }

    /**
     * 获取当前用户类型
     * @return 用户类型
     */
    public static Integer getUserType() {
        return USER_TYPE_HOLDER.get();
    }

    /**
     * 清除当前线程的用户上下文
     */
    public static void clear() {
        USER_ID_HOLDER.remove();
        USER_NAME_HOLDER.remove();
        USER_TYPE_HOLDER.remove();
    }
}