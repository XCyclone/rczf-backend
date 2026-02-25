package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

@Data
public class UnifiedLoginDTO {
    
    /** 登录校验分组 */
    public interface Login {}
    
    /** 登录类型：1-企业用户；2-员工用户 */
    @NotNull(message = "登录类型不能为空", groups = Login.class)
    @Min(value = 1, message = "登录类型参数错误", groups = Login.class)
    @Max(value = 2, message = "登录类型参数错误", groups = Login.class)
    private Integer type;
    
    /** 统一社会信用代码（企业用户使用） */
    private String uscc;
    
    /** 身份证号（员工用户使用） */
    private String idNumber;
    
    /** 登录密码 */
    @NotBlank(message = "密码不能为空", groups = Login.class)
    private String password;
    
    /** 验证码ID */
    @NotBlank(message = "验证码ID不能为空", groups = Login.class)
    private String captchaId;
    
    /** 验证码 */
    @NotBlank(message = "验证码不能为空", groups = Login.class)
    private String captchaCode;
}