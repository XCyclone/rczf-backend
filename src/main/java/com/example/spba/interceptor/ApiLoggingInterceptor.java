package com.example.spba.interceptor;

import com.alibaba.fastjson.JSON;
import com.example.spba.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * API调用日志拦截器
 * 统一记录所有接口的调用日志和异常日志，包含URL信息
 */
public class ApiLoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiLoggingInterceptor.class);
    
    // 请求开始时间的ThreadLocal存储
    private static final ThreadLocal<Long> startTimeHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> requestIdHolder = new ThreadLocal<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 生成请求ID
        String requestId = UUID.randomUUID().toString().replace("-", "");
        requestIdHolder.set(requestId);
        
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        startTimeHolder.set(startTime);
        
        // 获取请求信息
        String url = getRequestUrl(request);
        String method = request.getMethod();
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String contentType = request.getContentType();
        
        // 记录请求参数（注意敏感信息脱敏）
        String parameters = getSafeParameters(request);
        
        // 记录调用日志
        logger.info("[API调用开始] 请求ID: {}, URL: {}, Method: {}, IP: {}, UserAgent: {}, ContentType: {}, Parameters: {}", 
                   requestId, url, method, clientIp, userAgent, contentType, parameters);
        
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 这里可以记录响应信息，但通常在afterCompletion中处理更完整
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            String requestId = requestIdHolder.get();
            Long startTime = startTimeHolder.get();
            
            if (requestId != null && startTime != null) {
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                String url = getRequestUrl(request);
                int statusCode = response.getStatus();
                
                if (ex != null) {
                    // 记录异常日志
                    logger.error("[API调用异常] 请求ID: {}, URL: {}, 状态码: {}, 耗时: {}ms, 异常: {}", 
                               requestId, url, statusCode, duration, ex.getMessage(), ex);
                } else {
                    // 记录正常完成日志
                    logger.info("[API调用完成] 请求ID: {}, URL: {}, 状态码: {}, 耗时: {}ms", 
                               requestId, url, statusCode, duration);
                }
            }
        } finally {
            // 清理ThreadLocal避免内存泄漏
            cleanupThreadLocals();
        }
    }
    
    /**
     * 获取完整的请求URL
     */
    private String getRequestUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 获取安全的请求参数（脱敏敏感信息）
     */
    private String getSafeParameters(HttpServletRequest request) {
        try {
            // 对于POST请求，如果是JSON格式，记录body摘要
            if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
                String contentType = request.getContentType();
                if (contentType != null && contentType.contains("application/json")) {
                    return "[JSON Body]";
                }
            }
            
            // 获取查询参数
            StringBuilder params = new StringBuilder();
            request.getParameterMap().forEach((key, values) -> {
                // 对敏感参数进行脱敏处理
                String safeValue = maskSensitiveParameter(key, values.length > 0 ? values[0] : "");
                params.append(key).append("=").append(safeValue).append("&");
            });
            
            // 移除末尾的&
            if (params.length() > 0) {
                params.deleteCharAt(params.length() - 1);
            }
            
            return params.toString();
        } catch (Exception e) {
            return "[参数获取失败]";
        }
    }
    
    /**
     * 对敏感参数进行脱敏处理
     */
    private String maskSensitiveParameter(String paramName, String paramValue) {
        if (paramValue == null || paramValue.isEmpty()) {
            return paramValue;
        }
        
        // 敏感参数名称列表
        String[] sensitiveParams = {"password", "pwd", "token", "authorization", "auth"};
        
        for (String sensitive : sensitiveParams) {
            if (paramName.toLowerCase().contains(sensitive)) {
                return "***";
            }
        }
        
        // 对长字符串进行截断
        if (paramValue.length() > 100) {
            return paramValue.substring(0, 100) + "...";
        }
        
        return paramValue;
    }
    
    /**
     * 清理ThreadLocal变量
     */
    private void cleanupThreadLocals() {
        startTimeHolder.remove();
        requestIdHolder.remove();
    }
}