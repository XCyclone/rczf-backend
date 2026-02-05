package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

@Data
public class BusinessUserUpdateDTO
{
    /** 保存时的校验分组 */
    public interface Save{}

    /** 用户ID */
    @NotBlank(message = "用户ID不能为空", groups = Save.class)
    private String id;

    /** 最高学历 */
    @Size(max = 64, message = "最高学历长度不能超过64个字符")
    private String highestEdu;

    /** 国籍 */
    @Size(max = 64, message = "国籍长度不能超过64个字符")
    private String nationality;

    /** 工作单位名称 */
    @Size(max = 128, message = "工作单位名称长度不能超过128个字符")
    private String companyName;

    /** 注册类型：1-企业员工；2-机关单位员工；3-领军、优青人才 */
    @NotNull(message = "请选择注册类型", groups = Save.class)
    @Min(value = 1, message = "注册类型参数错误", groups = Save.class)
    @Max(value = 3, message = "注册类型参数错误", groups = Save.class)
    private Integer regType;
}