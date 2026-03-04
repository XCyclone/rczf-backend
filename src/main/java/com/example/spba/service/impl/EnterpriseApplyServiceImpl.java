package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.dao.*;
import com.example.spba.domain.dto.EnterpriseApplicationQueryDTO;
import com.example.spba.domain.dto.EnterpriseSubmitDTO;
import com.example.spba.domain.dto.EnterpriseUpdateDTO;
import com.example.spba.domain.entity.ApplicationIndustry;
import com.example.spba.domain.entity.ProjectInfo;
import com.example.spba.domain.entity.ViewApplicationIndustry;
import com.example.spba.service.EnterpriseApplyService;
import com.example.spba.utils.R;
import com.example.spba.utils.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class EnterpriseApplyServiceImpl implements EnterpriseApplyService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnterpriseApplyServiceImpl.class);

    @Autowired
    private ApplicationIndustryMapper applicationIndustryMapper;
    
    @Autowired
    private ViewApplicationIndustryMapper viewApplicationIndustryMapper;

    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    /**
     * 判断当前时间是否在申请时间区间内
     * @param currentTime 当前时间 yyyyMMdd格式
     * @param startTime 开始时间 yyyyMMdd格式
     * @param endTime 结束时间 yyyyMMdd格式
     * @return 是否在时间区间内
     */
    private boolean isWithinApplyPeriod(String currentTime, String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            Date currentDate = sdf.parse(currentTime);

            // 如果开始时间为空，则认为无开始限制
            Date startDate = startTime != null && !startTime.isEmpty() ? sdf.parse(startTime) : null;

            // 如果结束时间为空，则认为无结束限制
            Date endDate = endTime != null && !endTime.isEmpty() ? sdf.parse(endTime) : null;

            // 校验开始时间
            if (startDate != null && currentDate.before(startDate)) {
                return false;
            }

            // 校验结束时间
            if (endDate != null && currentDate.after(endDate)) {
                return false;
            }

            return true;
        } catch (ParseException e) {
            // 时间格式解析错误，返回 false
            return false;
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public R submitApplication(EnterpriseSubmitDTO submitDTO, String userId, String userName) {
        try {
            logger.info("[企业申请提交] 开始处理企业申请，企业ID: {}, 企业名称: {}, 项目ID: {}", userId, userName, submitDTO.getProjectId());
            ProjectInfo project = projectInfoMapper.selectById(submitDTO.getProjectId());
            // 校验申请时间区间
            String currentTime = Time.getNowTimeDate("yyyyMMdd HH:mm:ss");
            if (!isWithinApplyPeriod(currentTime, project.getApplyStartTime(), project.getApplyEndTime())) {
                return R.error("当前不在该项目的申请时间范围内");
            }

            // 校验该企业该项目是否已有在途申请（除了拒绝状态3之外的其他状态）
            if (hasPendingNonRejectedApplication(userId, submitDTO.getProjectId())) {
                logger.warn("[企业申请提交] 存在在途申请，企业ID: {}, 项目ID: {}", userId, submitDTO.getProjectId());
                return R.error("该企业在该项目已有在途的审批记录，无法重复提交申请");
            }
            
            // 生成申请ID
            String applicationId = UUID.randomUUID().toString().replace("-", "");
            logger.info("[企业申请提交] 生成申请ID: {}", applicationId);
            
            // 创建申请记录
            ApplicationIndustry application = new ApplicationIndustry();
            application.setApplicationId(applicationId);
            application.setProjectId(submitDTO.getProjectId());
            application.setProjectName(submitDTO.getProjectName());
            application.setCommunityId1(submitDTO.getCommunityId1());
            application.setCommunityId2(submitDTO.getCommunityId2());
            application.setCommunityId3(submitDTO.getCommunityId3());
            application.setHouseCount(submitDTO.getHouseCount());
            application.setApplyDate(Time.getNowTimeDate("yyyy-MM-dd"));
            application.setApplyTime(Time.getNowTimeDate("HH:mm:ss"));
            application.setEnterpriseId(userId);
            application.setEnterpriseUscc(userName);
            application.setApplyStatus(1); // 提交/待审核状态
            
            // 保存申请记录
            logger.info("[企业申请提交] 准备保存申请记录");
            applicationIndustryMapper.insert(application);
            logger.info("[企业申请提交] 申请记录保存成功，申请ID: {}", applicationId);
            
            return R.success("企业申请提交成功", applicationId);
            
        } catch (Exception e) {
            logger.error("[企业申请提交] 提交失败，企业ID: {}, 异常: {}", userId, e.getMessage(), e);
            return R.error("企业申请提交失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查企业是否在指定项目上有在途申请（除了拒绝状态3之外的其他状态）
     * @param enterpriseId 企业ID
     * @param projectId 项目ID
     * @return 是否存在在途申请（非拒绝状态）
     */
    private boolean hasPendingNonRejectedApplication(String enterpriseId, String projectId) {
        QueryWrapper<ApplicationIndustry> wrapper = new QueryWrapper<>();
        wrapper.eq("enterprise_id", enterpriseId);
        wrapper.eq("project_id", projectId);
        // 排除拒绝状态3，其他状态都认为是在途申请
        wrapper.ne("apply_status", 3);
        
        int count = applicationIndustryMapper.selectCount(wrapper);
        return count > 0;
    }

    @Override
    public R queryViewApplications() {
        try {
            logger.info("[企业申请查询] 开始查询企业申请视图");
            
            // 查询视图中的企业申请记录
            List<ViewApplicationIndustry> applications = viewApplicationIndustryMapper.selectList(null);
            logger.info("[企业申请查询] 查询完成，返回记录数: {}", applications.size());
            
            return R.success(applications);
        } catch (Exception e) {
            logger.error("[企业申请查询] 查询失败，异常: {}", e.getMessage(), e);
            return R.error("查询企业申请视图失败: " + e.getMessage());
        }
    }
    
    @Override
    public R queryViewApplicationsWithPage(EnterpriseApplicationQueryDTO queryDTO) {
        try {
            logger.info("[企业申请分页查询] 开始分页查询企业申请视图，参数: {}", queryDTO);
            
            // 参数校验
            if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
                queryDTO.setPageNum(1);
            }
            if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
                queryDTO.setPageSize(10);
            }
            
            // 构建分页对象
            Page<ViewApplicationIndustry> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
            
            // 构建查询条件
            QueryWrapper<ViewApplicationIndustry> wrapper = new QueryWrapper<>();
            
            // 校验企业 ID 必须存在
            if (queryDTO.getEnterpriseId() == null || queryDTO.getEnterpriseId().trim().isEmpty()) {
                return R.error("企业 ID 不能为空");
            }
            
            // 只查询该企业的申请（enterprise_id 等于当前用户 ID）
            wrapper.eq("enterprise_id", queryDTO.getEnterpriseId());
            
            // 日期范围筛选
            if (queryDTO.getStartDate() != null && !queryDTO.getStartDate().trim().isEmpty()) {
                wrapper.ge("apply_date", queryDTO.getStartDate());
            }
            if (queryDTO.getEndDate() != null && !queryDTO.getEndDate().trim().isEmpty()) {
                wrapper.le("apply_date", queryDTO.getEndDate());
            }
            
            // 申请状态筛选
            if (queryDTO.getApplyStatus() != null) {
                wrapper.eq("apply_status", queryDTO.getApplyStatus());
            }
            
            // 按申请时间倒序排列
            wrapper.orderByDesc("apply_date", "apply_time");
            
            // 执行分页查询
            IPage<ViewApplicationIndustry> resultPage = viewApplicationIndustryMapper.selectPage(page, wrapper);
            
            logger.info("[企业申请分页查询] 查询完成，总记录数: {}, 当前页记录数: {}", 
                       resultPage.getTotal(), resultPage.getRecords().size());
            
            return R.success(resultPage);
        } catch (Exception e) {
            logger.error("[企业申请分页查询] 查询失败，异常: {}", e.getMessage(), e);
            return R.error("分页查询企业申请视图失败: " + e.getMessage());
        }
    }
    
    @Override
    public R withdrawApplication(String applicationId, String userId) {
        try {
            logger.info("[企业申请撤回] 开始处理撤回申请，申请ID: {}, 用户ID: {}", applicationId, userId);
            
            // 参数验证
            if (applicationId == null || applicationId.trim().isEmpty()) {
                logger.error("[企业申请撤回] 申请ID为空，用户ID: {}", userId);
                return R.error("申请ID不能为空");
            }
            
            // 查询申请记录
            logger.info("[企业申请撤回] 查询申请记录，申请ID: {}", applicationId);
            ApplicationIndustry application = applicationIndustryMapper.selectById(applicationId);
            if (application == null) {
                logger.warn("[企业申请撤回] 申请记录不存在，申请ID: {}", applicationId);
                return R.error("未找到对应的企业申请记录");
            }
            
            // 检查申请状态，只有状态为1（待审核）的记录才能撤回
            if (application.getApplyStatus() != 1) {
                logger.warn("[企业申请撤回] 申请状态不允许撤回，当前状态: {}, 申请ID: {}", application.getApplyStatus(), applicationId);
                return R.error("只有待审核状态的申请才能撤回");
            }

            // 检查用户信息，只有发起人可以撤回
            if (!Objects.equals(application.getEnterpriseId(), userId)) {
                logger.warn("[企业申请撤回] 权限不足，申请用户ID: {}, 操作用户ID: {}", application.getEnterpriseId(), userId);
                return R.error("只有申请提交人才能撤回");
            }
            
            // 更新申请状态为0（撤回/待提交）
            logger.info("[企业申请撤回] 更新申请状态为撤回");
            application.setApplyStatus(0);
            applicationIndustryMapper.updateById(application);
            logger.info("[企业申请撤回] 撤回操作完成，申请ID: {}", applicationId);
            
            return R.success("申请撤回成功");
            
        } catch (Exception e) {
            logger.error("[企业申请撤回] 撤回失败，申请ID: {}, 用户ID: {}, 异常: {}", applicationId, userId, e.getMessage(), e);
            return R.error("申请撤回失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateApplication(EnterpriseUpdateDTO updateDTO, String userId, String userName) {
        try {
            // 查询申请记录
            ApplicationIndustry application = applicationIndustryMapper.selectById(updateDTO.getApplicationId());
            if (application == null) {
                return R.error("未找到对应的企业申请记录");
            }
            
            // 检查申请状态，只有状态为0（撤回/待提交）的记录才能修改
            if (application.getApplyStatus() != 0) {
                return R.error("只有撤回状态的申请才能修改");
            }

            // 检查用户信息，只有发起人可以修改
            if (!Objects.equals(application.getEnterpriseId(), userId)) {
                return R.error("只有申请提交人才能修改");
            }
            
            // 更新申请信息
            application.setProjectId(updateDTO.getProjectId());
            application.setProjectName(updateDTO.getProjectName());
            application.setCommunityId1(updateDTO.getCommunityId1());
            application.setCommunityId2(updateDTO.getCommunityId2());
            application.setCommunityId3(updateDTO.getCommunityId3());
            application.setHouseCount(updateDTO.getHouseCount());
            application.setApplyStatus(1);
            // 更新时间
            application.setApplyDate(Time.getNowTimeDate("yyyy-MM-dd"));
            application.setApplyTime(Time.getNowTimeDate("HH:mm:ss"));
            
            // 保存更新
            applicationIndustryMapper.updateById(application);
            
            return R.success("申请修改成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("申请修改失败: " + e.getMessage());
        }
    }

}