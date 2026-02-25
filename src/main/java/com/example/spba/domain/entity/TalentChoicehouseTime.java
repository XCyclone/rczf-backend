package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 个人选房时间表
 * @author Generated
 */
@Data
@TableName("talent_choicehouse_time")
public class TalentChoicehouseTime implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 申请id
     */
    private String applicationId;

    /**
     * 申请人证件号
     */
    private String applicantZjhm;

    /**
     * 选房开始时间
     */
    private String startTime;

    /**
     * 选房结束时间
     */
    private String endTime;

    /**
     * 最后更新日期
     */
    private String impDate;

    /**
     * 最后更新时间
     */
    private String impTime;

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
}