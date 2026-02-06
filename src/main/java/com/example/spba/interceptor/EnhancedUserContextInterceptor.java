package com.example.spba.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.example.spba.utils.UserContextUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class EnhancedUserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 方式1：从请求头获取用户信息
            extractFromHeaders(request);
            
            // 方式2：从Sa-Token获取登录用户信息
            extractFromSaToken();
            
            // 方式3：从JWT Token获取用户信息（如果有的话）
            extractFromJwtToken(request);
            
            return true;
        } catch (Exception e) {
            // 发生异常时也要确保清理ThreadLocal
            UserContextUtil.clear();
            throw e;
        }
    }

    /**
     * 从请求头中提取用户信息
     */
    private void extractFromHeaders(HttpServletRequest request) {
        String userId = request.getHeader("X-User-ID");
        String userName = request.getHeader("X-User-Name");
        String userTypeStr = request.getHeader("X-User-Type");
        
        if (userId != null && !userId.isEmpty()) {
            UserContextUtil.setUserId(userId);
        }
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

    /**
     * 从Sa-Token中提取用户信息
     */
    private void extractFromSaToken() {
        if (StpUtil.isLogin()) {
            String userId = StpUtil.getLoginIdAsString();
            if (userId != null && !UserContextUtil.getUserId().equals(userId)) {
                UserContextUtil.setUserId(userId);
            }
        }
    }

    /**
     * 从JWT Token中提取用户信息（示例实现）
     */
    private void extractFromJwtToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // 这里可以解析JWT token获取用户信息
            // 示例：parseJwtToken(token);
        }
    }

    /**
     * 解析JWT Token获取用户信息（示例方法）
     * 实际使用时需要根据具体的JWT实现来解析
     */
    private void parseJwtToken(String token) {
        // 示例解析逻辑
        /*
        try {
            Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
            
            String userId = claims.get("userId", String.class);
            String userName = claims.get("userName", String.class);
            Integer userType = claims.get("userType", Integer.class);
            
            if (userId != null) {
                UserContextUtil.setUserId(userId);
            }
            if (userName != null) {
                UserContextUtil.setUserName(userName);
            }
            if (userType != null) {
                UserContextUtil.setUserType(userType);
            }
        } catch (Exception e) {
            // JWT解析失败，忽略
        }
        */
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清理ThreadLocal，防止内存泄漏
        UserContextUtil.clear();
    }
}