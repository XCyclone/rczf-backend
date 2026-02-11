package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {
    
    @NotBlank(message = "请输入账号")
    private String username;
    
    @NotBlank(message = "请输入密码")
    private String password;
    
    @NotBlank(message = "验证码ID不能为空")
    private String captchaId;
    
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;
}