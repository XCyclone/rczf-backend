package com.example.spba.domain.dto;

import lombok.Data;

@Data
public class KeyValueItemDTO {
    private String value;
    private String text;
    
    public KeyValueItemDTO() {}
    
    public KeyValueItemDTO(String value, String text) {
        this.value = value;
        this.text = text;
    }
}