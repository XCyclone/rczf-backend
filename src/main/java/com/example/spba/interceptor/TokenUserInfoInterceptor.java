package com.example.spba.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.example.spba.domain.entity.UserInfo;
import com.example.spba.service.UserTokenService;
import com.example.spba.utils.RequestAttributeUtil;
import com.example.spba.utils.R;
import com.example.spba.utils.UserContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Token用户信息拦截器
 * 在preHandle中获取用户信息并存储到用户上下文中
 */
public class TokenUserInfoInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TokenUserInfoInterceptor.class);

    @Autowired
    private UserTokenService userTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 从请求头获取token
            String token = extractTokenFromRequest(request);
            
            if (StringUtils.hasText(token)) {
                // 验证token是否有效
                if (!userTokenService.isValidToken(token)) {
                    logger.warn("无效的token: {}", token);
                    return handleTokenError(response, "Token无效或已过期");
                }

                // 检查token是否即将过期
                long expireTime = userTokenService.getTokenExpireTime(token);
                if (expireTime <= 0) {
                    logger.warn("token已过期: {}", token);
                    return handleTokenError(response, "Token已过期");
                }

                // 获取用户完整信息
                UserInfo userInfo = userTokenService.getUserInfoByToken(token);
                
                // 将用户信息存储到ThreadLocal和Request Attribute中
                storeUserInfoToContext(userInfo);
                // 同时存储到Request Attribute供@RequestAttribute使用
                RequestAttributeUtil.setCurrentUserInfo(userInfo);
                
                logger.debug("成功获取用户信息 - 用户ID: {}, 用户名: {}", userInfo.getId(), userInfo.getUsername());
            } else {
                // 如果没有token，检查是否为免登录接口
                String requestURI = request.getRequestURI();
                if (!isPublicEndpoint(requestURI)) {
                    logger.warn("缺少访问凭证 - 请求路径: {}", requestURI);
                    return handleTokenError(response, "请提供有效的访问凭证");
                }
            }

            return true;
        } catch (Exception e) {
            logger.error("拦截器处理异常: {}", e.getMessage(), e);
            UserContextUtil.clear();
            return handleTokenError(response, "系统内部错误");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清理ThreadLocal和Request Attribute，防止内存泄漏
        UserContextUtil.clear();
        RequestAttributeUtil.clear();
    }

    /**
     * 从请求中提取token
     * @param request HTTP请求
     * @return token字符串
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 方式1: 从Authorization头获取Bearer token
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 方式2: 从自定义请求头获取
        String xTokenHeader = request.getHeader("X-Auth-Token");
        if (StringUtils.hasText(xTokenHeader)) {
            return xTokenHeader;
        }

        // 方式3: 从Sa-Token默认header获取
        String saTokenHeader = request.getHeader("Sa-Token");
        if (StringUtils.hasText(saTokenHeader)) {
            return saTokenHeader;
        }
        // 方式3: 从Sa-Token默认header获取
        String tokenHeader = request.getHeader("token");
        if (StringUtils.hasText(tokenHeader)) {
            return tokenHeader;
        }

        // 方式4: 从请求参数获取
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }

        return null;
    }

    /**
     * 将用户信息存储到上下文
     * @param userInfo 用户信息
     */
    private void storeUserInfoToContext(UserInfo userInfo) {
        if (userInfo != null) {
            UserContextUtil.setUserId(userInfo.getId());
            UserContextUtil.setUserName(userInfo.getUsername());
            // 在request中设置用户信息属性，供@RequestAttribute使用
            // 注意：这种方式需要配合ServletRequestAttributes使用
        }
    }

    /**
     * 判断是否为公共接口（不需要token验证）
     * @param requestURI 请求URI
     * @return 是否为公共接口
     */
    private boolean isPublicEndpoint(String requestURI) {
        // 定义免登录接口白名单
        String[] publicEndpoints = {
            "/login",
            "/public/",
            "/captcha",
            "/swagger-ui",
            "/v3/api-docs",
            "/webjars/",
            "/favicon.ico"
        };

        for (String endpoint : publicEndpoints) {
            if (requestURI.contains(endpoint)) {
                return true;
            }
        }

        // 特殊处理/public/**路径
        if (requestURI.matches(".*/public/.*")) {
            return true;
        }

        return false;
    }
    
    /**
     * 处理token错误，直接向客户端返回JSON格式的错误响应
     * @param response HttpServletResponse响应对象
     * @param errorMessage 错误信息
     * @return false 表示拦截请求
     */
    private boolean handleTokenError(HttpServletResponse response, String errorMessage) throws Exception {
        // 清理上下文避免污染
        UserContextUtil.clear();
        RequestAttributeUtil.clear();
        
        // 设置响应头
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        
        // 构造错误响应
        R errorResponse = R.error(501, errorMessage);
        String jsonResponse = JSON.toJSONString(errorResponse);
        
        // 写入响应
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
        
        return false;
    }
}