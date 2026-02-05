package com.example.spba.domain.entity.constants;

/**
 * BusinessUser实体相关常量
 */
public class BusinessUserConstants {
    
    /**
     * 性别常量
     */
    public static class Gender {
        // 男
        public static final Integer MALE = 1;
        
        // 女
        public static final Integer FEMALE = 2;
    }
    
    /**
     * 是否登录过常量
     */
    public static class EverLogged {
        // 否 - 未登录过
        public static final Integer NO = 0;
        
        // 是 - 已登录过
        public static final Integer YES = 1;
    }
    
    /**
     * 注册类型常量
     */
    public static class RegType {
        // 企业员工
        public static final Integer ENTERPRISE_EMPLOYEE = 1;
        
        // 机关单位员工
        public static final Integer GOVERNMENT_EMPLOYEE = 2;
    }
    
    /**
     * 状态常量
     */
    public static class Status {
        // 待审核
        public static final Integer PENDING = 0;
        
        // 审核通过
        public static final Integer APPROVED = 1;
    }
}