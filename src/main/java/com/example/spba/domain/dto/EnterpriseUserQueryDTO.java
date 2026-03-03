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
    
    /** 当前页码，默认第1页 */
    private Integer page = 1;
    
    /** 每页大小，默认10条 */
    private Integer size = 10;

}