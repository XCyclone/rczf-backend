package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class StaffLoginDTO {
    
    /** 登录校验分组 */
    public interface Login {}
    
    /** 手机号码 */
    @NotBlank(message = "手机号码不能为空", groups = Login.class)
    private String mobile;
    
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