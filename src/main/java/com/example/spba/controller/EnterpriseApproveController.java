package com.example.spba.controller;

import com.example.spba.domain.dto.EmployeeApproveDTO;
import com.example.spba.service.EnterpriseApproveService;
import com.example.spba.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.spba.utils.RequestAttributeUtil.CURRENT_USER_ID;

@RestController
@RequestMapping("/enterprise/approve")
@Validated
public class EnterpriseApproveController {
    
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseApproveController.class);

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
        logger.info("[机关单位申请审批] 企业ID: {}, 申请ID: {}, 审批结果: {}, 备注: {}", 
                   userId, form.getApplicationId(), form.isApproved() ? "通过" : "拒绝", form.getRemark());
        
        R result = enterpriseApproveService.approveAgencyApplication(
            form.getApplicationId(), 
            form.isApproved(), 
            form.getRemark(),
            userId
        );
        
        logger.info("[机关单位申请审批] 完成，企业ID: {}, 申请ID: {}, 结果: {}", userId, form.getApplicationId(), result.getMessage());
        return result;
    }
    
    /**
     * 产业人才申请审批接口
     * @param form 审批参数
     * @return 审批结果
     */
    @PostMapping("/industry")
    public R approveIndustryApplication(@RequestAttribute(CURRENT_USER_ID) String userId,
                                       @Validated(EmployeeApproveDTO.Approve.class) @RequestBody EmployeeApproveDTO form) {
        logger.info("[产业人才申请审批] 企业ID: {}, 申请ID: {}, 审批结果: {}, 备注: {}", 
                   userId, form.getApplicationId(), form.isApproved() ? "通过" : "拒绝", form.getRemark());
        
        R result = enterpriseApproveService.approveIndustryApplication(
            form.getApplicationId(), 
            form.isApproved(), 
            form.getRemark(),
            userId
        );
        
        logger.info("[产业人才申请审批] 完成，企业ID: {}, 申请ID: {}, 结果: {}", userId, form.getApplicationId(), result.getMessage());
        return result;
    }
    
    /**
     * 查询企业下产业人才申请记录
     * @return 申请记录列表
     */
    @PostMapping("/industry/list")
    public R queryIndustryApplications(@RequestAttribute(CURRENT_USER_ID) String userId) {
        logger.info("[查询产业人才申请] 企业ID: {}", userId);
        
        R result = enterpriseApproveService.queryIndustryApplications(userId);
        logger.info("[查询产业人才申请] 完成，企业ID: {}, 结果: {}", userId, result.getMessage());
        return result;
    }
    
    /**
     * 查询企业下机关单位员工申请记录
     * @return 申请记录列表
     */
    @PostMapping("/agency/list")
    public R queryAgencyApplications(@RequestAttribute(CURRENT_USER_ID) String userId) {
        logger.info("[查询机关单位申请] 企业ID: {}", userId);
        
        R result = enterpriseApproveService.queryAgencyApplications(userId);
        logger.info("[查询机关单位申请] 完成，企业ID: {}, 结果: {}", userId, result.getMessage());
        return result;
    }
}