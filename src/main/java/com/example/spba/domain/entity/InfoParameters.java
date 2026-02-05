package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统参数表
 */
@Data
@TableName("public_parameters")
public class InfoParameters implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 参数键 */
    private String parKey;

    /** 参数值 */
    private String parValue;

    /** 参数类型 */
    private String parType;

    /** 参数说明 */
    private String parDesc;

    /** 排序 */
    private Integer sort;

    /** 状态：0-禁用；1-启用 */
    private Integer status;
}