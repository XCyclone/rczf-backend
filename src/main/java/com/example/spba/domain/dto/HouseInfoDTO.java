package com.example.spba.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 房屋信息返回 DTO
 */
@Data
public class HouseInfoDTO {
    
    /**
     * 房屋 ID（确认选房传这个）
     */
  private String id;
    
    /**
     * 小区名称
     */
  private String communityName;
    
    /**
     * 小区 ID
     */
  private String communityId;
    
    /**
     * 小区位置
     */
  private String communityLocation;
    
    /**
     * 楼栋号
     */
  private String buildingNo;
    
    /**
     * 单元号
     */
  private String unitNo;
    
    /**
     * 楼层号
     */
  private String floorNo;
    
    /**
     * 房间号
     */
  private String roomNo;
    
    /**
     * 房型
     */
  private String roomType;
    
    /**
     * 租赁面积
     */
  private BigDecimal rentArea;
    
    /**
     * 租金价格
     */
  private BigDecimal rentPrice;
    
    /**
     * 户型图图片 URL 列表
     */
  private List<String> houseLayoutPicList;
    
    /**
     * 实景图图片 URL 列表
     */
  private List<String> housePicGroupList;
    
    /**
     * 标签 1
     */
  private String tag1;
    
    /**
     * 标签 2
     */
  private String tag2;
    
    /**
     * 标签 3
     */
  private String tag3;
    
    /**
     * 房屋描述
     */
  private String houseDesc;
   
   /**
    * 户型图图片组 ID（用于查询图片 URL 列表）
    */
  private String houseLayoutPicId;
   
   /**
    * 实景图图片组 ID（用于查询图片 URL 列表）
    */
  private String housePicGroupId;
}
