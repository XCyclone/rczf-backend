package com.example.spba.config;

import com.example.spba.interceptor.EnhancedUserContextInterceptor;
import com.example.spba.interceptor.SpbaInterceptor;
import com.example.spba.interceptor.TokenUserInfoInterceptor;
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
    
    @Bean
    public TokenUserInfoInterceptor tokenUserInfoInterceptor()
    {
        return new TokenUserInfoInterceptor();
    }

    /**
     * 拦截器
     * addPathPatterns 用于添加拦截规则
     * excludePathPatterns 用于排除拦截
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册token用户信息拦截器（最先执行，用于验证和获取用户信息）
        registry.addInterceptor(tokenUserInfoInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login")
                .excludePathPatterns("/business/user/register")
                .excludePathPatterns("/business/enterprise/register/apply")
                .excludePathPatterns("/business/enterprise/addTag")
                .excludePathPatterns("/business/enterprise/register/approve")
                .excludePathPatterns("/business/user/register/apply")
                .excludePathPatterns("/business/user/register/approve")
                .excludePathPatterns("/public/**")  // 公共接口不需要token验证
                .excludePathPatterns("/demo/**")    // 演示接口可以排除
                .excludePathPatterns("/captcha/**") // 验证码接口
                .excludePathPatterns("/swagger-ui/**") // Swagger UI
                .excludePathPatterns("/v3/api-docs/**") // API文档
                .excludePathPatterns("/webjars/**") // 静态资源
                .excludePathPatterns("/favicon.ico"); // favicon
        
        // 注册增强版用户上下文拦截器（在token验证之后执行）
        registry.addInterceptor(enhancedUserContextInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login")
                .excludePathPatterns("/business/user/register")
                .excludePathPatterns("/business/enterprise/register/apply")
                .excludePathPatterns("/business/enterprise/register/approve")
                .excludePathPatterns("/business/user/register/apply")
                .excludePathPatterns("/business/user/register/approve")
                .excludePathPatterns("/business/enterprise/addTag")
                .excludePathPatterns("/public/**")  // 公共接口不需要用户上下文
                .excludePathPatterns("/demo/**");   // 演示接口可以排除
        
        // 注册原有的业务拦截器
        registry.addInterceptor(spbaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login")
                .excludePathPatterns("/business/user/register")
                .excludePathPatterns("/business/enterprise/register/apply")
                .excludePathPatterns("/business/enterprise/register/approve")
                .excludePathPatterns("/business/enterprise/addTag")
                .excludePathPatterns("/business/user/register/apply")
                .excludePathPatterns("/business/user/register/approve")
                .excludePathPatterns("/public/**")
                .excludePathPatterns("/demo/**");
    }
}
