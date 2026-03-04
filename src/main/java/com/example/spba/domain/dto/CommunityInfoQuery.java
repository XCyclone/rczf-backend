package com.example.spba.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 小区信息查询 DTO
 * 用于封装查询结果，避免直接返回实体类的所有字段
 */
@Data
public class CommunityInfoQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    private String id;

    /**
     * 小区编号
     */
    private String communityCode;

    /**
     * 小区名称
     */
    private String communityName;

    /**
     * 楼栋总数
     */
    private Short buildingCount;

    /**
     * 房源套数
     */
    private Integer houseCount;

    /**
     * 小区地址
     */
    private String communityLocation;

    /**
     * 服务站 ID
     */
    private String serviceStationId;

    /**
     * 服务站名称
     */
    private String serviceStationName;

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
     * 是否包含四居室：0-否；1-是
     */
    private Integer hasFourBedroom;

    /**
     * 小区描述
     */
    private String communityDesc;

    /**
     * 小区图片 id
     */
    private String communityPicGroupId;

    /**
     * 产业人才是否享受补贴：0-否；1-是
     */
    private Byte isIndustryTalentSubsidy;

    /**
     * 产业人才是否享受补贴比例
     */
    private BigDecimal industryTalentRate;

    /**
     * 小区标签 1
     */
    private String tag1;

    /**
     * 小区标签 2
     */
    private String tag2;

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
