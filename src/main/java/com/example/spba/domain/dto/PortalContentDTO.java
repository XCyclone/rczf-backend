package com.example.spba.domain.dto;

import lombok.Data;

/**
 * 通知公告详细信息 DTO
 */
@Data
public class PortalContentDTO {

    /**
     * 发布日期
     */
    private String publishDate;
    
    /**
     * 发布时间
     */
    private String publishTime;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
}
