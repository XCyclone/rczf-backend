package com.example.spba.config;

import com.example.spba.interceptor.EnhancedUserContextInterceptor;
import com.example.spba.interceptor.SpbaInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer
{

    @Bean
    public SpbaInterceptor spbaInterceptor()
    {
        return new SpbaInterceptor();
    }
    
    @Bean
    public EnhancedUserContextInterceptor enhancedUserContextInterceptor()
    {
        return new EnhancedUserContextInterceptor();
    }

    /**
     * 拦截器
     * addPathPatterns 用于添加拦截规则
     * excludePathPatterns 用于排除拦截
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册增强版用户上下文拦截器（最先执行）
        registry.addInterceptor(enhancedUserContextInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login")
                .excludePathPatterns("/business/user/register")
                .excludePathPatterns("/public/**")  // 公共接口不需要用户上下文
                .excludePathPatterns("/demo/**");   // 演示接口可以排除
        
        // 注册原有的业务拦截器
        registry.addInterceptor(spbaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login")
                .excludePathPatterns("/business/user/register")
                .excludePathPatterns("/public/**")
                .excludePathPatterns("/demo/**");
    }
}
