package com.example.spba.domain.dto;

import lombok.Data;

@Data
public class UpdateEnterprisePasswordRequestDTO {
    private String enterprise_id;
    private String old_password;
    private String new_password;
}