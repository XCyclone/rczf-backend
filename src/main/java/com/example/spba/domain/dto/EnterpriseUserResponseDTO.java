package com.example.spba.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class EnterpriseUserResponseDTO {
    /** 用户ID */
    private String user_id;
    
    /** 姓名 */
    private String name;
    
    /** 性别：1-男；2-女 */
    private Integer gender;
    
    /** 证件类型 */
    private Integer id_type;
    
    /** 证件号码 */
    private String id_number;
    
    /** 手机号码 */
    private String mobile;
    
    /** 出生日期 */
    private String birth_date;
    
    /** 最高学历 */
    private String highest_edu;
    
    /** 国籍 */
    private String nationality;
    
    /** 注册类型：1-企业员工；2-机关单位员工；3-领军、优青人才 */
    private Integer reg_type;
    
    /** 工作单位ID */
    private String company_id;
    
    /** 工作单位名称 */
    private String company_name;
    
    /** 注册日期 */
    private String reg_date;
    
    /** 注册时间 */
    private String reg_time;
    
    /** 当前状态：0-提交/待审核；1-审核通过；2-审核拒绝 */
    private Integer status;
    
    /** 操作：1-注册；2-修改信息 */
    private Integer operation;
    
    /** 备注 */
    private String info;
    
    /** 创建时间 */
    private Date create_time;
    
    /** 更新时间 */
    private Date update_time;
}