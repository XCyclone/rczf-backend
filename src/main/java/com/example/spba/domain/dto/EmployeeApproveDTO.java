package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class EmployeeApproveDTO {
    
    /** 审批时的校验分组 */
    public interface Approve {}

    /** 申请ID */
    @NotBlank(message = "申请ID不能为空", groups = Approve.class)
    private String applicationId;
    
    /** 审批状态：true-通过，false-拒绝 */
    @NotNull(message = "审批状态不能为空", groups = Approve.class)
    private Boolean approved;
    
    /** 审批备注 */
    private String remark;
    
    public Boolean isApproved() {
        return approved;
    }
}