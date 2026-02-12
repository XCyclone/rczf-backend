package com.example.spba.controller;

import com.example.spba.domain.dto.EmployeeApproveDTO;
import com.example.spba.service.EnterpriseApproveService;
import com.example.spba.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.spba.utils.RequestAttributeUtil.CURRENT_USER_ID;

@RestController
@RequestMapping("/enterprise/approve")
@Validated
public class EnterpriseApproveController {

    @Autowired
    private EnterpriseApproveService enterpriseApproveService;

    /**
     * 机关单位员工申请审批接口
     * @param form 审批参数
     * @return 审批结果
     */
    @PostMapping("/agency")
    public R approveAgencyApplication(@RequestAttribute(CURRENT_USER_ID) String userId,
                                     @Validated(EmployeeApproveDTO.Approve.class) @RequestBody EmployeeApproveDTO form) {
        return enterpriseApproveService.approveAgencyApplication(
            form.getApplicationId(), 
            form.isApproved(), 
            form.getRemark(),
            userId
        );
    }
    
    /**
     * 产业人才申请审批接口
     * @param form 审批参数
     * @return 审批结果
     */
    @PostMapping("/industry")
    public R approveIndustryApplication(@RequestAttribute(CURRENT_USER_ID) String userId,
                                       @Validated(EmployeeApproveDTO.Approve.class) @RequestBody EmployeeApproveDTO form) {
        return enterpriseApproveService.approveIndustryApplication(
            form.getApplicationId(), 
            form.isApproved(), 
            form.getRemark(),
            userId
        );
    }
    
    /**
     * 查询企业下产业人才申请记录
     * @return 申请记录列表
     */
    @PostMapping("/industry/list")
    public R queryIndustryApplications(@RequestAttribute(CURRENT_USER_ID) String userId) {
        return enterpriseApproveService.queryIndustryApplications(userId);
    }
    
    /**
     * 查询企业下机关单位员工申请记录
     * @return 申请记录列表
     */
    @PostMapping("/agency/list")
    public R queryAgencyApplications(@RequestAttribute(CURRENT_USER_ID) String userId) {
        return enterpriseApproveService.queryAgencyApplications(userId);
    }
}