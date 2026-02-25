package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginDTO {
    
    private String idNumber;

    private String uscc;

    private String username;

    @NotBlank(message = "请输入密码")
    private String password;
    
    @NotBlank(message = "验证码ID不能为空")
    private String captchaId;
    
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;

    // 用户类型：1-企业；2-用户
    @NotNull(message = "用户类型不得为空")
    private Integer type;
}