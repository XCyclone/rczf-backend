package com.example.spba.domain.dto;

import lombok.Data;

@Data
public class UserApplicationQueryDTO {

    /**
     * 当前页码（从1开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}