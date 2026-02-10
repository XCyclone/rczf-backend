package com.example.spba.utils;

import com.example.spba.domain.entity.UserInfo;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Request属性工具类
 * 用于在拦截器和Controller之间传递用户信息
 */
public class RequestAttributeUtil {
    
    public static final String CURRENT_USER_INFO = "currentUserInfo";
    public static final String CURRENT_USER_ID = "currentUserId";
    public static final String CURRENT_USERNAME = "currentUsername";

    /**
     * 在request中设置当前用户信息
     * @param userInfo 用户信息
     */
    public static void setCurrentUserInfo(UserInfo userInfo) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            request.setAttribute(CURRENT_USER_INFO, userInfo);
            if (userInfo != null) {
                request.setAttribute(CURRENT_USER_ID, userInfo.getId());
                request.setAttribute(CURRENT_USERNAME, userInfo.getUsername());
            }
        }
    }

    /**
     * 从request中获取当前用户信息
     * @return 用户信息
     */
    public static UserInfo getCurrentUserInfo() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return (UserInfo) request.getAttribute(CURRENT_USER_INFO);
        }
        return null;
    }

    /**
     * 从request中获取当前用户ID
     * @return 用户ID
     */
    public static String getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return (String) request.getAttribute(CURRENT_USER_ID);
        }
        return null;
    }

    /**
     * 从request中获取当前用户名
     * @return 用户名
     */
    public static String getCurrentUsername() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return (String) request.getAttribute(CURRENT_USERNAME);
        }
        return null;
    }

    /**
     * 清除request中的用户信息属性
     */
    public static void clear() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            request.removeAttribute(CURRENT_USER_INFO);
            request.removeAttribute(CURRENT_USER_ID);
            request.removeAttribute(CURRENT_USERNAME);
        }
    }
}