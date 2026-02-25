package com.example.spba.controller;

import com.example.spba.domain.dto.AgencyApplySubmitDTO;
import com.example.spba.domain.dto.AgencyApplyUpdateDTO;
import com.example.spba.domain.dto.IndustryApplySubmitDTO;
import com.example.spba.domain.dto.IndustryApplyUpdateDTO;
import com.example.spba.domain.dto.LeadingApplySubmitDTO;
import com.example.spba.domain.dto.LeadingApplyUpdateDTO;
import com.example.spba.service.ApplicationAgencyService;
import com.example.spba.service.ApplicationIndustryService;
import com.example.spba.service.ApplicationLeadingService;
import com.example.spba.service.ApplicationService;
import com.example.spba.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

import static com.example.spba.utils.RequestAttributeUtil.CURRENT_USER_ID;

@RestController
@RequestMapping("/user/apply")
@Validated
public class UserApplyController {

    @Autowired
    private ApplicationAgencyService applicationAgencyService;
    
    @Autowired
    private ApplicationIndustryService applicationIndustryService;
    
    @Autowired
    private ApplicationLeadingService applicationLeadingService;
    
    @Autowired
    private ApplicationService applicationService;

    /**
     * 机关单位申请提交接口
     * @param form 申请参数
     * @return 提交结果
     */
    @PostMapping("/agency/submit")
    public R submitAgencyApply(@RequestAttribute(CURRENT_USER_ID) String userId,
                              @Validated(AgencyApplySubmitDTO.Save.class) @RequestBody AgencyApplySubmitDTO form) {
        return applicationAgencyService.submitAgencyApply(userId, form);
    }
    
    /**
     * 机关单位申请撤回接口
     * @param params 撤回参数
     * @return 撤回结果
     */
    @PostMapping("/agency/withdraw")
    public R withdrawAgencyApply(@RequestAttribute(CURRENT_USER_ID) String userId,
                                @RequestBody Map<String, String> params) {
        String applicationId = params.get("applicationId");
        if (applicationId == null || applicationId.trim().isEmpty()) {
            return R.error("申请ID不能为空");
        }
        return applicationAgencyService.withdrawAgencyApply(userId, applicationId);
    }
    
    /**
     * 机关单位申请修改接口
     * @param form 修改参数
     * @return 修改结果
     */
    @PostMapping("/agency/update")
    public R updateAgencyApply(@RequestAttribute(CURRENT_USER_ID) String userId,
                              @Validated(AgencyApplyUpdateDTO.Save.class) @RequestBody AgencyApplyUpdateDTO form) {
        return applicationAgencyService.updateAgencyApply(userId, form);
    }
    
    /**
     * 产业人才申请提交接口
     * @param form 申请参数
     * @return 提交结果
     */
    @PostMapping("/industry/submit")
    public R submitIndustryApply(@RequestAttribute(CURRENT_USER_ID) String userId,
                                @Validated(IndustryApplySubmitDTO.Save.class) @RequestBody IndustryApplySubmitDTO form) {
        return applicationIndustryService.submitIndustryApply(userId, form);
    }
    
    /**
     * 产业人才申请撤回接口
     * @param params 撤回参数
     * @return 撤回结果
     */
    @PostMapping("/industry/withdraw")
    public R withdrawIndustryApply(@RequestAttribute(CURRENT_USER_ID) String userId,
                                  @RequestBody Map<String, String> params) {
        String applicationId = params.get("applicationId");
        if (applicationId == null || applicationId.trim().isEmpty()) {
            return R.error("申请ID不能为空");
        }
        return applicationIndustryService.withdrawIndustryApply(userId, applicationId);
    }
    
    /**
     * 产业人才申请修改接口
     * @param form 修改参数
     * @return 修改结果
     */
    @PostMapping("/industry/update")
    public R updateIndustryApply(@RequestAttribute(CURRENT_USER_ID) String userId,
                                @Validated(IndustryApplyUpdateDTO.Save.class) @RequestBody IndustryApplyUpdateDTO form) {
        return applicationIndustryService.updateIndustryApply(userId, form);
    }
    
    /**
     * 领军优青人才申请提交接口
     * @param form 申请参数
     * @return 提交结果
     */
    @PostMapping("/leading/submit")
    public R submitLeadingApply(@RequestAttribute(CURRENT_USER_ID) String userId,
                               @Validated(LeadingApplySubmitDTO.Save.class) @RequestBody LeadingApplySubmitDTO form) {
        return applicationLeadingService.submitLeadingApply(userId, form);
    }
    
    /**
     * 领军优青人才申请撤回接口
     * @param params 撤回参数
     * @return 撤回结果
     */
    @PostMapping("/leading/withdraw")
    public R withdrawLeadingApply(@RequestAttribute(CURRENT_USER_ID) String userId,
                                 @RequestBody Map<String, String> params) {
        String applicationId = params.get("applicationId");
        if (applicationId == null || applicationId.trim().isEmpty()) {
            return R.error("申请ID不能为空");
        }
        return applicationLeadingService.withdrawLeadingApply(userId, applicationId);
    }
    
    /**
     * 领军优青人才申请修改接口
     * @param form 修改参数
     * @return 修改结果
     */
    @PostMapping("/leading/update")
    public R updateLeadingApply(@RequestAttribute(CURRENT_USER_ID) String userId,
                               @Validated(LeadingApplyUpdateDTO.Save.class) @RequestBody LeadingApplyUpdateDTO form) {
        return applicationLeadingService.updateLeadingApply(userId, form);
    }
    
    /**
     * 查询机关单位员工的所有申请记录
     * @return 申请记录列表
     */
    @PostMapping("/agency/query")
    public R queryAgencyApplications(@RequestAttribute(CURRENT_USER_ID) String userId) {
        return applicationAgencyService.queryAgencyApplications(userId);
    }
    
    /**
     * 查询产业人才的所有申请记录
     * @return 申请记录列表
     */
    @PostMapping("/industry/query")
    public R queryIndustryApplications(@RequestAttribute(CURRENT_USER_ID) String userId) {
        return applicationIndustryService.queryIndustryApplications(userId);
    }
    
    /**
     * 查询领军优青人才的所有申请记录
     * @return 申请记录列表
     */
    @PostMapping("/leading/query")
    public R queryLeadingApplications(@RequestAttribute(CURRENT_USER_ID) String userId) {
        return applicationLeadingService.queryLeadingApplications(userId);
    }
    
    /**
     * 统一查询用户的所有申请记录
     * 查询机关单位员工、产业人才、领军优青人才的所有申请记录
     * 考虑员工可能在不同时间申请不同类型的人才资格
     * @return 所有申请记录列表，按申请时间倒序排列
     */
    @PostMapping("/query")
    public R queryAllUserApplications(@RequestAttribute(CURRENT_USER_ID) String userId) {
        return applicationService.queryAllUserApplications(userId);
    }
}