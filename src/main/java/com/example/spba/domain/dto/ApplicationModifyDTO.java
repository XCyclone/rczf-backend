package com.example.spba.domain.dto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ApplicationModifyDTO {

    @NotBlank(message = "申请ID不能为空")
    private String applicationId;         // 申请ID

    @NotBlank(message = "申请对应的运营项目ID不能为空")
    private String projectId;             // 申请对应的运营项目ID

    private String projectName;           // 申请对应的项目名称

    private String communityId;           // 申请意向小区ID

    private String communityName;         // 申请意向小区名称

    private Integer houseCount;           // 申请意向小区房源套数

    @NotBlank(message = "期望入住时间不能为空")
    private String expectedMoveinDate;    // 期望入住时间

    private String enterpriseId;          // 申请企业ID

    private String enterpriseName;        // 申请企业名称

    private List<ApplicationTagInfoDTO> tagInfoList;  // 标签信息列表
}