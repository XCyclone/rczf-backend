package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.spba.dao.*;
import com.example.spba.domain.dto.EnterpriseSubmitDTO;
import com.example.spba.domain.dto.EnterpriseUpdateDTO;
import com.example.spba.domain.entity.ApplicationIndustry;
import com.example.spba.domain.entity.ViewApplicationIndustry;
import com.example.spba.service.EnterpriseApplyService;
import com.example.spba.utils.R;
import com.example.spba.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EnterpriseApplyServiceImpl implements EnterpriseApplyService {

    @Autowired
    private ApplicationIndustryMapper applicationIndustryMapper;
    
    @Autowired
    private ViewApplicationIndustryMapper viewApplicationIndustryMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public R submitApplication(EnterpriseSubmitDTO submitDTO, String userId, String userName) {
        try {
            // 校验该企业该项目是否已有在途申请（除了拒绝状态3之外的其他状态）
            if (hasPendingNonRejectedApplication(userId, submitDTO.getProjectId())) {
                return R.error("该企业在该项目已有在途的审批记录，无法重复提交申请");
            }
            
            // 生成申请ID
            String applicationId = UUID.randomUUID().toString().replace("-", "");
            
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
            applicationIndustryMapper.insert(application);
            
            return R.success("企业申请提交成功", applicationId);
            
        } catch (Exception e) {
            e.printStackTrace();
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
            // 查询视图中的企业申请记录
            List<ViewApplicationIndustry> applications = viewApplicationIndustryMapper.selectList(null);
            return R.success(applications);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("查询企业申请视图失败: " + e.getMessage());
        }
    }
    
    @Override
    public R withdrawApplication(String applicationId, String userId) {
        try {
            // 参数验证
            if (applicationId == null || applicationId.trim().isEmpty()) {
                return R.error("申请ID不能为空");
            }
            
            // 查询申请记录
            ApplicationIndustry application = applicationIndustryMapper.selectById(applicationId);
            if (application == null) {
                return R.error("未找到对应的企业申请记录");
            }
            
            // 检查申请状态，只有状态为1（待审核）的记录才能撤回
            if (application.getApplyStatus() != 1) {
                return R.error("只有待审核状态的申请才能撤回");
            }

            // 检查用户信息，只有发起人可以撤回
            if (application.getEnterpriseId() != userId) {
                return R.error("只有申请提交人才能撤回");
            }
            
            // 更新申请状态为0（撤回/待提交）
            application.setApplyStatus(0);
            applicationIndustryMapper.updateById(application);
            
            return R.success("申请撤回成功");
            
        } catch (Exception e) {
            e.printStackTrace();
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
            if (application.getEnterpriseId() != userId) {
                return R.error("只有申请提交人才能修改");
            }
            
            // 更新申请信息
            application.setProjectId(updateDTO.getProjectId());
            application.setProjectName(updateDTO.getProjectName());
            application.setCommunityId1(updateDTO.getCommunityId1());
            application.setCommunityId2(updateDTO.getCommunityId2());
            application.setCommunityId3(updateDTO.getCommunityId3());
            application.setHouseCount(updateDTO.getHouseCount());
            // 更新时间和操作人信息
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