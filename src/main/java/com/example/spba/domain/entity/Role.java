package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色表
 */
@Data
@TableName("int_role")
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableId(type = IdType.INPUT)
    private String roleId;

    /**
     * 角色名称
     */
    private String name;

//    /**
//     * 权限集合
//     */
//    private String permission;
//
//    /**
//     * 超级管理员（0否 1是）
//     */
//    private Integer root;
//
//    /**
//     * 角色状态（0停用 1正常）
//     */
//    private Integer status;
//
//    /**
//     * 更新时间
//     */
//    private Date updateTime;
//
//    /**
//     * 创建时间
//     */
//    private Date createTime;
}