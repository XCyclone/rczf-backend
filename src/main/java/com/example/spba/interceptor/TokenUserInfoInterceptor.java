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
 * Token 用户信息拦截器
 * 在 preHandle 中获取用户信息并存储到用户上下文中
 */
public class TokenUserInfoInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TokenUserInfoInterceptor.class);

    @Autowired
    private UserTokenService userTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 从请求头获取 token
            String token = extractTokenFromRequest(request);
            
            if (StringUtils.hasText(token)) {
                // 使用 Sa-Token 验证 token 有效性并获取登录 ID
                Object loginId = StpUtil.getLoginIdByToken(token);
               if (loginId == null) {
                    logger.warn("无效的 token: {}", token);
                   return handleTokenError(response, "Token 无效或已过期");
                }

                // 注意：不需要额外检查 token 是否过期
                // 因为 getLoginIdByToken() 已经验证了 token 的有效性（包括是否过期）
                // 如果能获取到 loginId，说明 token 是有效的且未过期

                // 获取用户完整信息
                UserInfo userInfo = getUserInfoByLoginId(loginId.toString());
                
                // 将用户信息存储到 ThreadLocal 和 Request Attribute 中
                storeUserInfoToContext(userInfo);
                // 同时存储到 Request Attribute 供@RequestAttribute 使用
                RequestAttributeUtil.setCurrentUserInfo(userInfo);
                
                logger.debug("成功获取用户信息 - 用户 ID: {}, 用户名：{}", userInfo.getId(), userInfo.getUsername());
            } else {
                // 如果没有 token，检查是否为免登录接口
                String requestURI = request.getRequestURI();
                if (!isPublicEndpoint(requestURI)) {
                    logger.warn("缺少访问凭证 - 请求路径：{}", requestURI);
                    return handleTokenError(response, "请提供有效的访问凭证");
                }
            }

            return true;
        } catch (Exception e) {
            logger.error("拦截器处理异常：{}", e.getMessage(), e);
            UserContextUtil.clear();
            
            // 特殊处理 Token 过期情况
            if (e.getMessage() != null && e.getMessage().startsWith("TOKEN_EXPIRED:")) {
                String errorMessage = e.getMessage().substring("TOKEN_EXPIRED:".length());
                return handleTokenExpiredError(response, errorMessage);
            }
            
            return handleTokenError(response, "系统内部错误");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清理 ThreadLocal 和 Request Attribute，防止内存泄漏
        UserContextUtil.clear();
        RequestAttributeUtil.clear();
    }

    /**
     * 从请求中提取 token
     * @param request HTTP 请求
     * @return token 字符串
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 方式 1: 从 Authorization 头获取 Bearer token
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 方式 2: 从自定义请求头获取
        String xTokenHeader = request.getHeader("X-Auth-Token");
        if (StringUtils.hasText(xTokenHeader)) {
            return xTokenHeader;
        }

        // 方式 3: 从 Sa-Token 默认 header 获取
        String saTokenHeader = request.getHeader("Sa-Token");
        if (StringUtils.hasText(saTokenHeader)) {
            return saTokenHeader;
        }
        // 方式 4: 从自定义 token header 获取
        String tokenHeader = request.getHeader("token");
        if (StringUtils.hasText(tokenHeader)) {
            return tokenHeader;
        }

        // 方式 5: 从请求参数获取
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }

        return null;
    }

    /**
     * 根据登录 ID 获取用户信息
     * @param loginId 登录 ID
     * @return 用户信息
     */
    private UserInfo getUserInfoByLoginId(String loginId) {
        try {
            return userTokenService.getUserInfoById(loginId);
        } catch (Exception e) {
            logger.error("获取用户信息失败，loginId: {}, error: {}", loginId, e.getMessage());
            return null;
        }
    }

    /**
     * 将用户信息存储到上下文
     * @param userInfo 用户信息
     */
    private void storeUserInfoToContext(UserInfo userInfo) {
        if (userInfo != null) {
            UserContextUtil.setUserId(userInfo.getId());
            UserContextUtil.setUserName(userInfo.getUsername());
        }
    }

    /**
     * 判断是否为公共接口（不需要 token 验证）
     * @param requestURI 请求 URI
     * @return 是否为公共接口
     */
    private boolean isPublicEndpoint(String requestURI) {
        // 定义免登录接口白名单
        String[] publicEndpoints = {
            "/login",
            "/public/",
            "/api/",
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
        // 特殊处理/api/**路径
        if (requestURI.matches(".*/api/.*")) {
            return true;
        }

        return false;
    }
    
    /**
     * 处理 token 错误，直接向客户端返回 JSON 格式的错误响应
     * @param response HttpServletResponse 响应对象
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
    
    /**
     * 专门处理 Token 过期错误，返回友好的提示信息
     * @param response HttpServletResponse 响应对象
     * @param errorMessage 错误信息
     * @return false 表示拦截请求
     */
    private boolean handleTokenExpiredError(HttpServletResponse response, String errorMessage) throws Exception {
        // 清理上下文避免污染
        UserContextUtil.clear();
        RequestAttributeUtil.clear();
        
        // 设置响应头
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        
        // 构造 Token 过期的友好提示响应
        R errorResponse = R.error(666, "登录已过期，请重新登录");
        String jsonResponse = JSON.toJSONString(errorResponse);
        
        // 写入响应
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
        
        return false;
    }
}
