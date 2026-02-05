package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EnterpriseSubmitDTO {

    @NotBlank(message = "运营项目ID不能为空")
    private String projectId;

    private String projectName;

    @NotBlank(message = "意向小区ID不能为空")
    private String communityId;

    private Integer houseCount;

    @NotBlank(message = "期望入住时间不能为空")
    private String expectedMoveinDate;

    private List<String> tagIds;
}