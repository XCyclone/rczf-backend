package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("business_enterprise")
public class BusinessEnterprise implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.NONE)
    private String id;

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

    /** 是否登录过：0-否；1-是 */
    private Integer everLogged;

    /** 注册类型：1-企业；2-机关单位 */
    private Integer regCategory;

    /** 当前状态：0-提交/待审核；1-审核通过 */
    private Integer status;

    /** 企业标签1 */
    private String tag1;

    /** 企业标签2 */
    private String tag2;

    /** 企业标签3 */
    private String tag3;

    /** 保留字段1 */
    private String reserve1;

    /** 保留字段2 */
    private String reserve2;

    /** 保留字段3 */
    private String reserve3;

    /** 保留字段4 */
    private String reserve4;

    /** 保留字段5 */
    private String reserve5;

    /** 保留字段6 */
    private String reserve6;
}