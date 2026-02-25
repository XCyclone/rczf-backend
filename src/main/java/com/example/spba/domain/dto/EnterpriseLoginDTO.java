package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EnterpriseLoginDTO {
    
    /** 登录校验分组 */
    public interface Login {}
    
    /** 统一社会信用代码 */
    @NotBlank(message = "统一社会信用代码不能为空", groups = Login.class)
    private String uscc;
    
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