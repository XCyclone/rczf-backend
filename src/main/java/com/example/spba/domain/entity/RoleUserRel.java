package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色用户关联表
 */
@Data
@TableName("int_role_user_rel")
public class RoleUserRel implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 角色ID */
    private String roleId;

    /** 用户ID */
    private String userId;
}