package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业黑名单实体类
 */
@Data
@TableName("enterprise_blacklist")
public class EnterpriseBlacklist implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 自增 id */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 企业名称 */
    private String enterpriseName;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /** 状态：0-生效；1-作废 */
    private Integer status;
}
