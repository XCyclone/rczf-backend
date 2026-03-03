package com.example.spba.controller;

import com.example.spba.domain.dto.AgencyApplySubmitDTO;
import com.example.spba.domain.dto.AgencyApplyUpdateDTO;
import com.example.spba.domain.dto.IndustryApplySubmitDTO;
import com.example.spba.domain.dto.IndustryApplyUpdateDTO;
import com.example.spba.domain.dto.LeadingApplySubmitDTO;
import com.example.spba.domain.dto.LeadingApplyUpdateDTO;
import com.example.spba.domain.dto.UserApplicationQueryDTO;
import com.example.spba.service.*;
import com.example.spba.service.impl.BusinessUserServiceImpl;
import com.example.spba.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    private PublicService publicService;

    private static final Logger logger = LoggerFactory.getLogger(UserApplyController.class);

    /**
     * 查询项目信息-员工用户
     * @return 项目信息列表
     */
    @PostMapping("/query/project")
    public R queryUserProject() {
        logger.info("[查询项目信息] 调用公共服务查询项目信息");
        return publicService.queryUserProject();
    }

    /**
     * 查询项目下的小区信息
     * @param param 包含项目ID的参数
     * @return 该项目关联的小区ID和名称列表
     */
    @PostMapping("/query/community")
    public R queryCommunitiesByProject(@RequestBody Map<String, String> param) {
        String projectId = param.get("projectId");
        logger.info("[查询项目小区] 调用公共服务查询项目小区信息，项目ID: {}", projectId);

        return publicService.queryCommunitiesByProject(projectId);
    }


    /**
     * 机关单位申请提交接口
     * @param form 申请参数
     * @return 提交结果
     */
    @PostMapping("/agency/submit")
    public R submitAgencyApply(@RequestAttribute(CURRENT_USER_ID) String userId,
                              @Validated(AgencyApplySubmitDTO.Save.class) @RequestBody AgencyApplySubmitDTO form) {
        logger.info("[机关单位申请提交] 用户ID: {}, 申请参数: {}", userId, form);
        R result = applicationAgencyService.submitAgencyApply(userId, form);
        logger.info("[机关单位申请提交] 完成，用户ID: {}, 结果: {}", userId, result.getMessage());
        return result;
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
            logger.warn("[机关单位申请撤回] 申请ID为空，用户ID: {}", userId);
            return R.error("申请ID不能为空");
        }
        logger.info("[机关单位申请撤回] 用户ID: {}, 申请ID: {}", userId, applicationId);
        R result = applicationAgencyService.withdrawAgencyApply(userId, applicationId);
        logger.info("[机关单位申请撤回] 完成，用户ID: {}, 申请ID: {}, 结果: {}", userId, applicationId, result.getMessage());
        return result;
    }
    
    /**
     * 机关单位申请修改接口
     * @param form 修改参数
     * @return 修改结果
     */
    @PostMapping("/agency/update")
    public R updateAgencyApply(@RequestAttribute(CURRENT_USER_ID) String userId,
                              @Validated(AgencyApplyUpdateDTO.Save.class) @RequestBody AgencyApplyUpdateDTO form) {
        logger.info("[机关单位申请修改] 用户ID: {}, 修改参数: {}", userId, form);
        R result = applicationAgencyService.updateAgencyApply(userId, form);
        logger.info("[机关单位申请修改] 完成，用户ID: {}, 结果: {}", userId, result.getMessage());
        return result;
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
     * 查询机关单位员工的所有申请记录（支持分页）
     * @param queryDTO 分页查询参数（可选）
     * @return 申请记录列表
     */
    @PostMapping("/agency/query")
    public R queryAgencyApplications(@RequestAttribute(CURRENT_USER_ID) String userId,
                                    @RequestBody(required = false) UserApplicationQueryDTO queryDTO) {
        try {
            logger.info("[机关单位申请查询] 用户ID: {}, 查询参数: {}", userId, queryDTO);
            
            // 如果查询参数为空，调用原有的不分页查询方法
            if (queryDTO == null) {
                return applicationAgencyService.queryAgencyApplications(userId);
            } else {
                // 调用分页查询方法
                return applicationAgencyService.queryAgencyApplicationsWithPage(userId, queryDTO);
            }
        } catch (Exception e) {
            logger.error("[机关单位申请查询] 查询失败，用户ID: {}, 异常: {}", userId, e.getMessage(), e);
            return R.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询产业人才的所有申请记录（支持分页）
     * @param queryDTO 分页查询参数（可选）
     * @return 申请记录列表
     */
    @PostMapping("/industry/query")
    public R queryIndustryApplications(@RequestAttribute(CURRENT_USER_ID) String userId,
                                      @RequestBody(required = false) UserApplicationQueryDTO queryDTO) {
        try {
            logger.info("[产业人才申请查询] 用户ID: {}, 查询参数: {}", userId, queryDTO);
            
            // 如果查询参数为空，调用原有的不分页查询方法
            if (queryDTO == null) {
                return applicationIndustryService.queryIndustryApplications(userId);
            } else {
                // 调用分页查询方法
                return applicationIndustryService.queryIndustryApplicationsWithPage(userId, queryDTO);
            }
        } catch (Exception e) {
            logger.error("[产业人才申请查询] 查询失败，用户ID: {}, 异常: {}", userId, e.getMessage(), e);
            return R.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询领军优青人才的所有申请记录（支持分页）
     * @param queryDTO 分页查询参数（可选）
     * @return 申请记录列表
     */
    @PostMapping("/leading/query")
    public R queryLeadingApplications(@RequestAttribute(CURRENT_USER_ID) String userId,
                                     @RequestBody(required = false) UserApplicationQueryDTO queryDTO) {
        try {
            logger.info("[领军优青人才申请查询] 用户ID: {}, 查询参数: {}", userId, queryDTO);
            
            // 如果查询参数为空，调用原有的不分页查询方法
            if (queryDTO == null) {
                return applicationLeadingService.queryLeadingApplications(userId);
            } else {
                // 调用分页查询方法
                return applicationLeadingService.queryLeadingApplicationsWithPage(userId, queryDTO);
            }
        } catch (Exception e) {
            logger.error("[领军优青人才申请查询] 查询失败，用户ID: {}, 异常: {}", userId, e.getMessage(), e);
            return R.error("查询失败: " + e.getMessage());
        }
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