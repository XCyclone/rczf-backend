package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 小区信息实体类
 */
@Data
@TableName("community_info")
public class CommunityInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.NONE)
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
     * 已增加房源套数
     */
    private Integer houseAddCount;

    /**
     * 小区所属片区标签
     */
    private String communityLocationTag;

    /**
     * 小区地址
     */
    private String communityLocation;

    /**
     * 建成年份
     */
    private String builtYear;

    /**
     * 供暖方式：0-无 1-自采暖 2-集中供暖
     */
    private String heatingType;

    /**
     * 首次运营日期
     */
    private String firstOperationDate;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 服务站ID
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
     * 开间户型图图片组id
     */
    private String zerobedroomLayoutPicId;

    /**
     * 开间实景图图片组id
     */
    private String zerobedroomPhotoPicId;

    /**
     * 一居户型图图片组id
     */
    private String onebedroomLayoutPicId;

    /**
     * 一居实景图图片组id
     */
    private String onebedroomPhotoPicId;

    /**
     * 二居户型图图片组id
     */
    private String twobedroomLayoutPicId;

    /**
     * 二居实景图图片组id
     */
    private String twobedroomPhotoPicId;

    /**
     * 三居户型图图片组id
     */
    private String threebedroomLayoutPicId;

    /**
     * 三居实景图图片组id
     */
    private String threebedroomPhotoPicId;

    /**
     * 小区图片id
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
     * 房源是否被删除：1-是；0-否
     */
    private Byte deleteStatus;

    /**
     * 小区标签1
     */
    private String tag1;

    /**
     * 小区标签2
     */
    private String tag2;

    /**
     * 小区标签3
     */
    private String tag3;

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

    /**
     * 保留字段1
     */
    private String reserve1;

    /**
     * 保留字段2
     */
    private String reserve2;

    /**
     * 保留字段3
     */
    private String reserve3;

    /**
     * 保留字段4
     */
    private String reserve4;

    /**
     * 保留字段5
     */
    private String reserve5;

    /**
     * 保留字段6
     */
    private String reserve6;
}