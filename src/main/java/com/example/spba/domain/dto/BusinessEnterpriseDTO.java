package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class BusinessEnterpriseDTO {
    /** 保存时的校验分组 */
    public interface Save{}

    /** 企业名称 */
    @NotBlank(message = "请输入企业名称", groups = Save.class)
    @Size(max = 128, message = "企业名称长度不能超过128个字符", groups = Save.class)
    private String enterpriseName;

    /** 简称 */
    @Size(max = 64, message = "简称长度不能超过64个字符", groups = Save.class)
    private String shortName;

    /** 所属行业 */
    @Size(max = 64, message = "所属行业长度不能超过64个字符", groups = Save.class)
    private String industry;

    /** 员工人数 */
    private Integer staffSize;

    /** 统一社会信用代码 */
    @NotBlank(message = "请输入统一社会信用代码", groups = Save.class)
    @Size(min = 18, max = 18, message = "统一社会信用代码应为18位", groups = Save.class)
    private String uscc;

    /** 法人 */
    @NotBlank(message = "请输入法人姓名", groups = Save.class)
    @Size(max = 64, message = "法人姓名长度不能超过64个字符", groups = Save.class)
    private String legalPerson;

    /** 法人身份证号码 */
    @NotBlank(message = "请输入法人身份证号码", groups = Save.class)
    @Size(min = 18, max = 18, message = "身份证号码应为18位", groups = Save.class)
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "法人身份证号码格式错误", groups = Save.class)
    private String legalIdNumber;

    /** 企业地址 */
    @Size(max = 256, message = "企业地址长度不能超过256个字符", groups = Save.class)
    private String enterpriseAddr;

    /** 企业属地id */
    @Size(max = 32, message = "企业属地id长度不能超过32个字符", groups = Save.class)
    private String enterpriseLocationId;

    /** 企业属地名称 */
    @Size(max = 128, message = "企业属地名称长度不能超过128个字符", groups = Save.class)
    private String enterpriseLocationName;

    /** 经办人 */
    @NotBlank(message = "请输入经办人姓名", groups = Save.class)
    @Size(max = 64, message = "经办人姓名长度不能超过64个字符", groups = Save.class)
    private String operatorName;

    /** 经办人手机号 */
    @NotBlank(message = "请输入经办人手机号", groups = Save.class)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "经办人手机号格式错误", groups = Save.class)
    private String operatorMobile;

    /** 登录密码 */
    @NotBlank(message = "请输入登录密码", groups = Save.class)
    @Size(min = 6, max = 18, message = "密码长度必须在6-18位之间", groups = Save.class)
    private String loginPwd;

    /** 注册类型：1-企业；2-机关单位 */
    @NotNull(message = "请选择注册类型", groups = Save.class)
    private Integer regCategory;

    /** 企业标签1 */
    @Size(max = 32, message = "企业标签1长度不能超过32个字符", groups = Save.class)
    private String tag1;

    /** 企业标签2 */
    @Size(max = 32, message = "企业标签2长度不能超过32个字符", groups = Save.class)
    private String tag2;

    /** 企业标签3 */
    @Size(max = 32, message = "企业标签3长度不能超过32个字符", groups = Save.class)
    private String tag3;
    
    /** 验证码ID */
    @NotBlank(message = "验证码ID不能为空", groups = Save.class)
    private String captchaId;
    
    /** 用户输入的验证码 */
    @NotBlank(message = "验证码不能为空", groups = Save.class)
    private String captchaCode;
}