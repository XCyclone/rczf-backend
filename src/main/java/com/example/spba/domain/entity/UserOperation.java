package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户操作表
 */
@Data
@TableName("int_user_operation")
public class UserOperation implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 用户ID */
    private String userId;

    /** 类型：1-个人；2-企业 */
    private Integer type;

    /** 操作：1-注册；2-修改信息 */
    private Integer operation;

    /** 动作：1-申请；2-同意；3-拒绝 */
    private Integer action;

    /** 备注 */
    private String info;

    /** 内容 */
    private String content;
    
    /** 创建时间 */
    @TableField("create_time")
    private Date createTime;
    
    /** 更新时间 */
    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private Date updateTime;
}