package com.example.spba.constant;

/**
 * 系统常量类
 */
public class ProjectConstants {

    /**
     * 角色常量
     */
    public static class Role {
        // 企业员工角色ID
        public static final String ROLE_ENTERPRISE_EMPLOYEE = "R0002";
        
        // 机关单位员工角色ID
        public static final String ROLE_GOVERNMENT_EMPLOYEE = "R0003";
    }

    /**
     * 用户状态常量
     */
    public static class UserStatus {
        // 正常状态
        public static final Integer NORMAL = 1;
        
        // 停用状态
        public static final Integer DISABLED = 0;
    }

    /**
     * 业务用户状态常量
     */
    public static class BusinessUserStatus {
        // 待审核状态
        public static final Integer PENDING_APPROVAL = 0;
        
        // 审核通过状态
        public static final Integer APPROVED = 1;
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
     * 性别常量
     */
    public static class Gender {
        // 男性
        public static final Integer MALE = 1;
        
        // 女性
        public static final Integer FEMALE = 2;
    }

    /**
     * 是否标志常量
     */
    public static class YesOrNo {
        // 是/已登录
        public static final Integer YES = 1;
        
        // 否/未登录
        public static final Integer NO = 0;
    }

    /**
     * 系统默认值常量
     */
    public static class DefaultValue {
        // 空字符串
        public static final String EMPTY_STRING = "";
        
        // 默认安全码
        public static final String DEFAULT_SAFE = "";
    }
    
    /**
     * 通用状态常量
     */
    public static class CommonStatus {
        // 成功
        public static final Integer SUCCESS = 1;
        
        // 失败
        public static final Integer FAILURE = 0;
    }
}