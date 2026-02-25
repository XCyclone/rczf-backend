package com.example.spba.domain.dto;

import lombok.Data;

@Data
public class UpdatePasswordRequestDTO {
    private String oldPassword;
    private String newPassword;
}