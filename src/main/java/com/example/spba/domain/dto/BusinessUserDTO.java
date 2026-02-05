package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class BusinessUserDTO
{
    /** 保存时的校验分组 */
    public interface Save{}

    /** 姓名 */
    @NotBlank(message = "请输入姓名", groups = Save.class)
    @Size(max = 64, message = "姓名长度不能超过64个字符", groups = Save.class)
    private String name;

    /** 性别：1-男；2-女 */
    @NotNull(message = "请选择性别", groups = Save.class)
    @Min(value = 1, message = "性别参数错误", groups = Save.class)
    @Max(value = 2, message = "性别参数错误", groups = Save.class)
    private Integer gender;

    /** 证件类型 */
    @NotNull(message = "请选择证件类型", groups = Save.class)
    private Integer idType;

    /** 证件号码 */
    @NotBlank(message = "请输入证件号码", groups = Save.class)
    @Size(max = 32, message = "证件号码长度不能超过32个字符", groups = Save.class)
    private String idNumber;

    /** 密码 */
    @NotBlank(message = "请输入密码", groups = Save.class)
    @Size(min = 6, max = 18, message = "密码长度必须在6-18位之间", groups = Save.class)
    private String password;

    /** 出生日期 */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "出生日期格式错误，应为yyyy-MM-dd", groups = Save.class)
    private String birthDate;

    /** 最高学历 */
    @Size(max = 64, message = "最高学历长度不能超过64个字符", groups = Save.class)
    private String highestEdu;

    /** 国籍 */
    @Size(max = 64, message = "国籍长度不能超过64个字符", groups = Save.class)
    private String nationality;

    /** 手机号码 */
    @NotBlank(message = "请输入手机号码", groups = Save.class)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式错误", groups = Save.class)
    private String mobile;

    /** 注册类型：1-企业员工；2-机关单位员工；3-领军、优青人才 */
    @NotNull(message = "请选择注册类型", groups = Save.class)
    @Min(value = 1, message = "注册类型参数错误", groups = Save.class)
    @Max(value = 3, message = "注册类型参数错误", groups = Save.class)
    private Integer regType;

    /** 工作单位ID */
    @Size(max = 32, message = "工作单位ID长度不能超过32个字符", groups = Save.class)
    private String companyId;

    /** 工作单位名称 */
    @Size(max = 128, message = "工作单位名称长度不能超过128个字符", groups = Save.class)
    private String companyName;
    
    /** 验证码ID */
    @NotBlank(message = "验证码ID不能为空", groups = Save.class)
    private String captchaId;
    
    /** 用户输入的验证码 */
    @NotBlank(message = "验证码不能为空", groups = Save.class)
    private String captchaCode;
}