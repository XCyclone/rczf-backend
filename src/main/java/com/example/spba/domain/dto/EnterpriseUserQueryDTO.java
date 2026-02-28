package com.example.spba.domain.dto;

import lombok.Data;

@Data
public class EnterpriseUserQueryDTO {
    /** 员工姓名（模糊查询） */
    private String name;
    
    /** 性别：1-男；2-女 */
    private Integer gender;
    
    /** 民族 */
    private String nationality;

}