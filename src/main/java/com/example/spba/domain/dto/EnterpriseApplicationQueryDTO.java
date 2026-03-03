package com.example.spba.domain.dto;

import lombok.Data;

@Data
public class EnterpriseApplicationQueryDTO {

    private String enterpriseId;
    /**
     * 当前页码（从 1 开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 开始日期（格式：yyyy-MM-dd）
     */
    private String startDate;
    
    /**
     * 结束日期（格式：yyyy-MM-dd）
     */
    private String endDate;
    
    /**
     * 申请状态
     */
    private Integer applyStatus;
}