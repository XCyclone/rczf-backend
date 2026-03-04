package com.example.spba.controller;

import com.example.spba.controller.base.BaseController;
import com.example.spba.dao.ProjectCommunityMapper;
import com.example.spba.dao.ProjectInfoMapper;
import com.example.spba.domain.dto.EnterpriseApplicationQueryDTO;
import com.example.spba.domain.dto.EnterpriseSubmitDTO;
import com.example.spba.domain.dto.EnterpriseUpdateDTO;
import com.example.spba.domain.entity.ProjectInfo;
import com.example.spba.service.EnterpriseApplyService;
import com.example.spba.service.PublicService;
import com.example.spba.utils.R;
import com.example.spba.utils.Time;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.spba.utils.RequestAttributeUtil.CURRENT_USERNAME;
import static com.example.spba.utils.RequestAttributeUtil.CURRENT_USER_ID;

@RestController
@RequestMapping("/enterprise/apply")
@Validated
public class EnterpriseApplyController {
    
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseApplyController.class);

    @Resource
    private EnterpriseApplyService enterpriseApplyService;

    @Autowired
    private PublicService publicService;

    /**
     * 企业申请提交接口
     * @param submitDTO 申请信息
     * @return 操作结果
     */
    @PostMapping("/submit")
    public R submitApplication(@RequestAttribute(CURRENT_USER_ID) String userId, @RequestAttribute(CURRENT_USERNAME) String userName, @Valid @RequestBody EnterpriseSubmitDTO submitDTO) {
        logger.info("[企业申请提交] 用户ID: {}, 用户名: {}, 申请参数: {}", userId, userName, submitDTO);
        R result = enterpriseApplyService.submitApplication(submitDTO, userId, userName);
        logger.info("[企业申请提交] 完成，用户ID: {}, 结果: {}", userId, result.getMessage());
        return result;
    }

    
    /**
     * 查询企业申请视图信息（支持分页）
     * @param queryDTO 分页查询参数（可选）
     * @return 企业申请视图列表，包含企业名称和小区名称等关联信息
     */
    @PostMapping("/query")
    public R queryViewApplications(@RequestAttribute(CURRENT_USER_ID) String userId,
                                  @RequestBody(required = false) EnterpriseApplicationQueryDTO queryDTO) {
        try {
            logger.info("[企业申请查询] 用户 ID: {}, 查询参数：{}", userId, queryDTO);
                        
            // 创建默认分页参数
            if (queryDTO == null) {
                queryDTO = new EnterpriseApplicationQueryDTO();
            }
                        
            // 设置企业 ID 为当前用户 ID，确保只查询该企业的申请
            queryDTO.setEnterpriseId(userId);
            // 调用分页查询方法
            R result = enterpriseApplyService.queryViewApplicationsWithPage(queryDTO);
            logger.info("[企业申请分页查询] 完成，用户ID: {}, 结果: {}", userId, result.getMessage());
            return result;

        } catch (Exception e) {
            logger.error("[企业申请查询] 查询失败，用户ID: {}, 异常: {}", userId, e.getMessage(), e);
            return R.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 企业申请撤回接口
     * @param  param 申请ID
     * @return 操作结果
     */
    @PostMapping("/withdraw")
    public R withdrawApplication(@RequestAttribute(CURRENT_USER_ID) String userId, @RequestBody Map<String,String> param) {
        // 获取当前用户ID，用于权限验证
        String applicationId = param.get("applicationId");
        logger.info("[企业申请撤回] 用户ID: {}, 申请ID: {}", userId, applicationId);
        
        R result = enterpriseApplyService.withdrawApplication(applicationId, userId);
        logger.info("[企业申请撤回] 完成，用户ID: {}, 申请ID: {}, 结果: {}", userId, applicationId, result.getMessage());
        return result;
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
     * 企业申请修改接口
     * 只有撤回状态(apply_status=0)的申请才能修改
     * @param submitDTO 包含申请ID和修改信息的参数
     * @return 操作结果
     */
    @PostMapping("/update/submit")
    public R updateApplication(@RequestAttribute(CURRENT_USER_ID) String userId, 
                              @RequestAttribute(CURRENT_USERNAME) String userName,
             @Valid @RequestBody EnterpriseUpdateDTO submitDTO) {
        try {
            // 获取申请ID
            String applicationId = submitDTO.getApplicationId();
            logger.info("[企业申请修改] 用户ID: {}, 用户名: {}, 申请ID: {}, 修改参数: {}", userId, userName, applicationId, submitDTO);
            
            if (applicationId == null || applicationId.trim().isEmpty()) {
                logger.error("[企业申请修改] 申请ID为空，用户ID: {}", userId);
                return R.error("申请ID不能为空");
            }
            
            R result = enterpriseApplyService.updateApplication(submitDTO, userId, userName);
            logger.info("[企业申请修改] 完成，用户ID: {}, 申请ID: {}, 结果: {}", userId, applicationId, result.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("[企业申请修改] 修改失败，用户ID: {}, 申请ID: {}, 异常: {}", userId, submitDTO.getApplicationId(), e.getMessage(), e);
            return R.error("申请修改失败: " + e.getMessage());
        }
    }

    /**
     * 查询项目信息-企业用户
     * @return 项目信息列表
     */
    @PostMapping("/query/project")
    public R queryEnterpriseProject() {
        logger.info("[查询项目信息] 调用公共服务查询项目信息");
        return publicService.queryEnterpriseProject();
    }
    
//    /**
//     * 查询是否开放企业申请
//     * 参考 PublicController 中的 queryEnterpriseProject 方法
//     * @return 是否开放申请状态
//     */
//    @PostMapping("/check/apply/status")
//    public R checkApplyStatus() {
//        logger.info("[检查企业申请状态] 开始检查企业申请是否开放");
//
//        try {
//            // 获取当前时间 格式yyyyMMdd HH:mm:ss
//            String currentDate = Time.getNowTimeDate("yyyyMMdd HH:mm:ss");
//
//            // 构建查询条件
//            QueryWrapper<ProjectInfo> queryWrapper = new QueryWrapper<>();
//            queryWrapper.lt("enterprise_start_time", currentDate);
//            queryWrapper.gt("enterprise_end_time", currentDate);
//            queryWrapper.eq("status", 1); // 只查询开启状态的项目
//
//            // 查询符合条件的项目数量
//            int projectCount = projectInfoMapper.selectCount(queryWrapper);
//
//            logger.info("[检查企业申请状态] 查询完成，当前开放申请的项目数量: {}", projectCount);
//
//            Map<String, String> result = new HashMap<>();
//            // 如果有项目在申请时间内且状态为开启，则返回开放状态
//            if (projectCount > 0) {
//                result.put("flag", "1");
//                return R.success(result, "企业申请已开放");
//            } else {
//                result.put("flag", "0");
//                return R.success(result, "当前非申请时段，请在申请时段提交申请");
//            }
//
//        } catch (Exception e) {
//            logger.error("[检查企业申请状态] 查询失败，异常: {}", e.getMessage(), e);
//            return R.error("检查申请状态失败：" + e.getMessage());
//        }
//    }
}