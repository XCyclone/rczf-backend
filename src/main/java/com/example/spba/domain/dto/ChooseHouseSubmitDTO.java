package com.example.spba.domain.dto;

import lombok.Data;

/**
 * 选房提交请求 DTO
 */
@Data
public class ChooseHouseSubmitDTO {
    
    /**
     * 项目 ID
     */
  private String projectId;
    
    /**
     * 房屋 ID
     */
  private String houseId;
    
    /**
     * 选择来源：1-PC 端；2-微信小程序
     */
  private Integer choiceSrc;
}
