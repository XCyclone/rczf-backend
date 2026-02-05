package com.example.spba.interceptor;

import com.example.spba.utils.UserContextUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户上下文拦截器
 * 用于在请求处理前获取并设置用户信息到ThreadLocal中
 */
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 从请求头中获取用户ID（如果有自定义header）
            String userId = request.getHeader("X-User-ID");
            String userName = request.getHeader("X-User-Name");
            String userTypeStr = request.getHeader("X-User-Type");
            
            // 如果请求头中有用户信息，则使用请求头中的信息
            if (userId != null && !userId.isEmpty()) {
                UserContextUtil.setUserId(userId);
                if (userName != null && !userName.isEmpty()) {
                    UserContextUtil.setUserName(userName);
                }
                if (userTypeStr != null && !userTypeStr.isEmpty()) {
                    try {
                        UserContextUtil.setUserType(Integer.valueOf(userTypeStr));
                    } catch (NumberFormatException e) {
                        // 忽略无效的用户类型
                    }
                }
            }
            // 如果没有请求头信息，则后续可以通过Sa-Token或其他方式获取
            
            return true;
        } catch (Exception e) {
            // 发生异常时也要确保清理ThreadLocal
            UserContextUtil.clear();
            throw e;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清理ThreadLocal，防止内存泄漏
        UserContextUtil.clear();
    }
}