package com.example.spba.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 小区详细信息 DTO
 */
@Data
public class CommunityDetailDTO {
    
    /**
     * 小区图片列表
     */
    private List<String> communityPicList;
    
    /**
     * 小区名称
     */
    private String communityName;
    
    /**
     * 小区介绍
     */
    private String houseDesc;
    
    /**
     * 小区位置
     */
    private String communityLocation;
    
    /**
     * 小区标签 1
     */
    private String tag1;
    
    /**
     * 小区标签 2
     */
    private String tag2;
    
    /**
     * 小区标签 3
     */
    private String tag3;
    
    /**
     * 是否包含开间：0-否；1-是
     */
    private Integer hasZeroBedroom;
    
    /**
     * 是否包含一居室：0-否；1-是
     */
    private Integer hasOneBedroom;
    
    /**
     * 是否包含二居室：0-否；1-是
     */
    private Integer hasTwoBedroom;
    
    /**
     * 是否包含三居室：0-否；1-是
     */
    private Integer hasThreeBedroom;
    
    /**
     * 开间户型图图片列表
     */
    private List<String> zerobedroomLayoutPicList;
    
    /**
     * 开间实景图图片列表
     */
    private List<String> zerobedroomPhotoPicList;
    
    /**
     * 一居户型图图片列表
     */
    private List<String> onebedroomLayoutPicList;
    
    /**
     * 一居实景图图片列表
     */
    private List<String> onebedroomPhotoPicList;
    
    /**
     * 二居户型图图片列表
     */
    private List<String> twobedroomLayoutPicList;
    
    /**
     * 二居实景图图片列表
     */
    private List<String> twobedroomPhotoPicList;
    
    /**
     * 三居户型图图片列表
     */
    private List<String> threebedroomLayoutPicList;
    
    /**
     * 三居实景图图片列表
     */
    private List<String> threebedroomPhotoPicList;
}
