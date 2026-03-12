package com.example.spba.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 可选房屋信息查询 DTO
 */
@Data
public class HouseInfoQueryDTO {
    
    /**
     * 项目 ID
     */
   private String projectId;
    
    /**
     * 页码
     */
   private Integer page;
    
    /**
     * 每页大小
     */
   private Integer size;
    
    /**
     * 房型列表，如 ["1","0"]
     */
   private List<String> houseTypes;
    
    /**
     * 租金范围，如 [5,99999]
     */
   private List<Integer> rentRange;
    
    /**
     * 小区 ID 列表
     */
   private List<String> community;
    
    /**
     * 位置标签列表
     */
   private List<String> location;
    
    /**
     * 单元号（只有一个值）
     */
   private String unit;
    
    /**
     * 楼层范围，如 [1,31]
     */
   private List<Integer> floorRange;
    
    /**
     * 小区名称（支持模糊搜索）
     */
   private String communityName;
}
