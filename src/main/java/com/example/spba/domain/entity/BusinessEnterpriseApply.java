package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("business_enterprise_apply")
public class BusinessEnterpriseApply implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.NONE)
    private String id;

    /** 企业ID */
    private String businessEnterpriseId;

    /** 企业名称 */
    private String enterpriseName;

    /** 简称 */
    private String shortName;

    /** 所属行业 */
    private String industry;

    /** 员工人数 */
    private Integer staffSize;

    /** 统一社会信用代码 */
    private String uscc;

    /** 法人 */
    private String legalPerson;

    /** 法人身份证号码 */
    private String legalIdNumber;

    /** 企业地址 */
    private String enterpriseAddr;

    /** 企业属地id */
    private String enterpriseLocationId;

    /** 企业属地名称 */
    private String enterpriseLocationName;

    /** 经办人 */
    private String operatorName;

    /** 经办人手机号 */
    private String operatorMobile;

    /** 登录密码（密文） */
    private String loginPwd;

    /** 注册日期 */
    private String regDate;

    /** 注册时间 */
    private String regTime;

    /** 注册类型：1-企业；2-机关单位 */
    private Integer regCategory;

    /** 操作：1-注册；2-修改信息 */
    private Integer operation;

    /** 当前状态：0-提交/待审核；1-审核通过；2-审核拒绝 */
    private Integer status;

    /** 备注 */
    private String info;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /** 保留字段1 */
    private String reserve1;

    /** 保留字段2 */
    private String reserve2;

    /** 保留字段3 */
    private String reserve3;
}