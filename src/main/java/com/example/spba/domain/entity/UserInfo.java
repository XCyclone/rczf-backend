package com.example.spba.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户完整信息实体类
 * 包含用户基本信息、角色信息、权限信息等
 */
@Data
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private String id;

    /** 用户名 */
    private String username;

    /** 姓名 */
    private String name;

    /** 手机号码 */
    private String mobile;

    /** 邮箱 */
    private String email;

    /** 帐号状态（0停用 1正常）*/
    private Integer status;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    private Date loginTime;

    /** 创建时间 */
    private Date createTime;

    /** 角色ID列表 */
    private List<String> roleIds;

    /** 角色名称列表 */
    private List<String> roleNames;

    /** 权限标识列表 */
    private List<String> permissions;

    /** 菜单权限列表 */
    private List<Menu> menus;

    /** 所属部门ID */
    private String departmentId;

    /** 所属部门名称 */
    private String departmentName;

    /** 用户类型 */
    private Integer userType;

    /** 备注信息 */
    private String remark;

    /**
     * 判断用户是否具有指定权限
     * @param permission 权限标识
     * @return 是否具有权限
     */
    public boolean hasPermission(String permission) {
        if (permissions == null || permission == null) {
            return false;
        }
        return permissions.contains(permission);
    }

    /**
     * 判断用户是否具有指定角色
     * @param roleId 角色ID
     * @return 是否具有角色
     */
    public boolean hasRole(String roleId) {
        if (roleIds == null || roleId == null) {
            return false;
        }
        return roleIds.contains(roleId);
    }

    /**
     * 判断用户是否为超级管理员
     * @return 是否为超级管理员
     */
    public boolean isSuperAdmin() {
        if (roleIds == null) {
            return false;
        }
        // 假设超级管理员角色ID为"admin"或根据实际业务定义
        return roleIds.contains("admin") || roleIds.contains("ROOT");
    }
}