package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("house_using_jnl")
public class HouseUsingJnl implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.INPUT)
    private String id;

    /** 房源ID */
    private String houseId;

    /** 租房开始时间 */
    private String startDate;

    /** 租房结束时间 */
    private String endDate;

    /** 租金已交到日期 */
    private String rentDate;

    /** 补贴前租金 */
    private java.math.BigDecimal rentWithoutSubsidy;

    /** 补贴金额 */
    private java.math.BigDecimal subsidyAmount;

    /** 补贴后租金 */
    private java.math.BigDecimal rentWithSubsidy;

    /** 承租人id */
    private String userId;

    /** 承租人证件号码 */
    private String idNumber;

    /** 承租人姓名 */
    private String userName;

    /** 保留字段1 */
    private String reserve1;

    /** 保留字段2 */
    private String reserve2;

    /** 保留字段3 */
    private String reserve3;
}