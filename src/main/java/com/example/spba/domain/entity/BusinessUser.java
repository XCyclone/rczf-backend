package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("business_user")
public class BusinessUser implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.INPUT)
    private String id;

    /** 姓名 */
    private String name;

    /** 性别：1-男；2-女 */
    private Integer gender;

    /** 证件类型 */
    private Integer idType;

    /** 证件号码 */
    private String idNumber;

    /** 密码（密文） */
    private String password;

    /** 出生日期 */
    private String birthDate;

    /** 最高学历 */
    private String highestEdu;

    /** 国籍 */
    private String nationality;

    /** 手机号码 */
    private String mobile;

    /** 注册日期 */
    private String regDate;

    /** 注册时间 */
    private String regTime;

    /** 是否登录过：0-否；1-是 */
    private Integer everLogged;

    /** 注册类型：1-企业员工；2-机关单位员工；3-领军、优青人才 */
    private Integer regType;

    /** 当前状态：0-提交/待审核；1-审核通过 */
    private Integer status;

    /** 工作单位ID */
    private String companyId;

    /** 工作单位名称 */
    private String companyName;

    /** 保留字段1 */
    private String reserve1;

    /** 保留字段2 */
    private String reserve2;

    /** 保留字段3 */
    private String reserve3;

}
