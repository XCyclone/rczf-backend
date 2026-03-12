package com.example.spba.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.example.spba.utils.UserContextUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 增强版用户上下文拦截器
 * 从 Sa-Token 获取用户信息并存储到 ThreadLocal 中
 */
public class EnhancedUserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 从 Sa-Token 获取登录用户信息
            extractFromSaToken();
            
            return true;
        } catch (Exception e) {
            // 发生异常时也要确保清理 ThreadLocal
            UserContextUtil.clear();
            throw e;
        }
    }

    /**
     * 从 Sa-Token 中提取用户信息
     */
    private void extractFromSaToken() {
        if (StpUtil.isLogin()) {
            String userId = StpUtil.getLoginIdAsString();
            if (userId != null && !UserContextUtil.getUserId().equals(userId)) {
                UserContextUtil.setUserId(userId);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清理 ThreadLocal，防止内存泄漏
        UserContextUtil.clear();
    }
}
