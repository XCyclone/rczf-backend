package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 房屋信息实体类
 */
@Data
@TableName("house_info")
public class HouseInfo implements Serializable {
   private static final long serialVersionUID = 1L;

    /**
     * 主键 ID（房屋 ID）
     */
   @TableId(type = IdType.NONE)
  private String id;
    
    /**
     * 小区 ID
     */
  private String communityId;
    
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
     * 房型：0-开间，1-一居室，2-二居室，3-三居室，4-四居室
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
     * 户型图图片组 ID
     */
  private String houseLayoutPicId;
    
    /**
     * 实景图图片组 ID
     */
  private String housePicGroupId;
    
    /**
     * 房屋描述
     */
  private String houseDesc;
    
    /**
     * 房屋状态：0-未选，1-已选
     */
  private Integer status;
    
    /**
     * 最后更新日期
     */
  private String lastUpdateDate;
    
    /**
     * 最后更新时间
     */
  private String lastUpdateTime;
    
    /**
     * 最后更新人
     */
  private String lastUpdater;
}
