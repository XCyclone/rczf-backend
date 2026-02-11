package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EnterpriseUpdateDTO {

    @NotBlank(message = "申请ID不能为空")
    private String applicationId;

    @NotBlank(message = "运营项目ID不能为空")
    private String projectId;

    private String projectName;

    private String communityId1;

    private String communityId2;

    private String communityId3;

    private Integer houseCount;


}
