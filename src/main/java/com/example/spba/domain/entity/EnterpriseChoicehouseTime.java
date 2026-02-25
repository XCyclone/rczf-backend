package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 企业选房时间表
 * @author Generated
 */
@Data
@TableName("enterprise_choicehouse_time")
public class EnterpriseChoicehouseTime implements Serializable {
    
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
     * 企业统一信用代码
     */
    private String enterpriseUscc;

    /**
     * 选房人数
     */
    private Integer applyCnt;

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