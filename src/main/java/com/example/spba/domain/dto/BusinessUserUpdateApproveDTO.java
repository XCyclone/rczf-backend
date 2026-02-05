package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BusinessUserUpdateApproveDTO
{
    /** 审批时的校验分组 */
    public interface Approve{}

    /** 业务用户ID */
    @NotBlank(message = "业务用户ID不能为空", groups = Approve.class)
    private String businessUserId;
    
    /** 审批状态：true-通过，false-拒绝 */
    @NotNull(message = "审批状态不能为空", groups = Approve.class)
    private Boolean approved;
    
    /** 审批附言 */
    private String info;
    
    public Boolean isApproved() {
        return approved;
    }
}