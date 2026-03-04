package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 房源选择记录实体类
 */
@Data
@TableName("house_choice")
public class HouseChoice implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 申请 ID
     */
    private String applicationId;

    /**
     * 申请类型：1-产业人才房；2-领军、优青人才房；3-机关单位用房
     */
    private Integer applicationType;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 房源 ID
     */
    private String houseId;

    /**
     * 选择人证件号码
     */
    private String choiceZjhm;

    /**
     * 选择来源：1-PC 端；2-微信小程序
     */
    private Integer choiceSrc;

    /**
     * 选择日期（格式：yyyy-MM-dd）
     */
    private String choiceDate;

    /**
     * 选择时间（格式：HH:mm:ss）
     */
    private String choiceTime;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 保留字段 1
     */
    private String reserve1;

    /**
     * 保留字段 2
     */
    private String reserve2;

    /**
     * 保留字段 3
     */
    private String reserve3;

    /**
     * 保留字段 4
     */
    private String reserve4;

    /**
     * 保留字段 5
     */
    private String reserve5;

    /**
     * 保留字段 6
     */
    private String reserve6;
}
