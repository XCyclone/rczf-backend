package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class IndustryApplyUpdateDTO {
    
    /** 保存时的校验分组 */
    public interface Save {}

    /** 申请ID */
    @NotBlank(message = "申请ID不能为空", groups = Save.class)
    private String applicationId;

    /** 申请对应的运营项目ID */
    @NotBlank(message = "项目ID不能为空", groups = Save.class)
    private String projectId;

    /** 项目名称 */
    @NotBlank(message = "项目名称不能为空", groups = Save.class)
    private String projectName;

    /** 申请意向小区1 */
    private String communityId1;

    /** 申请意向小区2 */
    private String communityId2;

    /** 申请意向小区3 */
    private String communityId3;

    /** 申请意向房型1 */
    private Integer houseType1;

    /** 申请意向房型2 */
    private Integer houseType2;

    /** 申请意向房型3 */
    private Integer houseType3;

    /** 申请意向房型4 */
    private Integer houseType4;
    
    /** 是否存在劳动合同关系：1-是；0-否 */
    @NotNull(message = "劳动合同关系不能为空", groups = Save.class)
    private Integer existLaborContract;
}