package com.example.spba.domain.dto;

import lombok.Data;

@Data
public class UpdatePasswordRequestDTO {
    private String user_id;
    private String old_password;
    private String new_password;
}