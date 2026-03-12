package com.example.spba.domain.dto;

import lombok.Data;

/**
 * 小区轮播图信息 DTO
 */
@Data
public class CommunityCarouselDTO {
    
    /**
     * 小区 ID
     */
    private String communityId;
    
    /**
     * 小区名称
     */
    private String communityName;
    
    /**
     * 小区图片（取第一张）
     */
    private String communityPic;
}
