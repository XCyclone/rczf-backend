package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.spba.dao.ApplicationLeadingTalentMapper;
import com.example.spba.dao.BusinessEnterpriseMapper;
import com.example.spba.dao.BusinessUserMapper;
import com.example.spba.dao.ProjectInfoMapper;
import com.example.spba.domain.dto.LeadingApplySubmitDTO;
import com.example.spba.domain.dto.LeadingApplyUpdateDTO;
import com.example.spba.domain.entity.ApplicationLeadingTalent;
import com.example.spba.domain.entity.BusinessEnterprise;
import com.example.spba.domain.entity.BusinessUser;
import com.example.spba.domain.entity.ProjectInfo;
import com.example.spba.service.ApplicationLeadingService;
import com.example.spba.utils.R;
import com.example.spba.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationLeadingServiceImpl implements ApplicationLeadingService {

    @Autowired
    private ApplicationLeadingTalentMapper applicationLeadingTalentMapper;
    
    @Autowired
    private BusinessUserMapper businessUserMapper;
    
    @Autowired
    private BusinessEnterpriseMapper businessEnterpriseMapper;
    
    @Autowired
    private ProjectInfoMapper projectInfoMapper;

    @Override
    @Transactional
    public R submitLeadingApply(String userId, LeadingApplySubmitDTO form) {
        try {
            // 1. 查询项目信息并校验申请时间
            ProjectInfo project = projectInfoMapper.selectById(form.getProjectId());
            if (project == null) {
                return R.error("项目信息不存在");
            }
            
            // 校验项目状态（必须是开启状态）
            if (project.getStatus() == null || project.getStatus() != 1) {
                return R.error("该项目暂未开启申请");
            }
            
            // 校验申请时间区间
            String currentTime = Time.getNowTimeDate("yyyy-MM-dd");
            if (!isWithinApplyPeriod(currentTime, project.getApplyStartTime(), project.getApplyEndTime())) {
                return R.error("当前不在该项目的申请时间范围内");
            }

            // 2. 查询申请人信息
            BusinessUser applicant = businessUserMapper.selectById(userId);
            if (applicant == null) {
                return R.error("申请人信息不存在");
            }
            
            // 3. 校验申请人类型（必须是领军优青人才）
            // 注意：这里需要根据实际的领军优青人才类型来确定regType值
            // 暂时使用regType=4作为示例，实际项目中需要确认正确的值
            if (applicant.getRegType() == null || applicant.getRegType() != 4) {
                return R.error("只有领军优青人才才能提交领军优青人才申请");
            }
            
            // 4. 校验申请人状态（必须是审核通过状态）
            if (applicant.getStatus() == null || applicant.getStatus() != 1) {
                return R.error("申请人账户状态异常，请联系管理员");
            }

            // 5. 校验是否存在在途审批记录
            if (hasPendingApplication(userId, form.getProjectId())) {
                return R.error("您在该项目已有在途的审批记录，无法重复提交申请");
            }

            // 6. 创建申请记录
            ApplicationLeadingTalent leadingApply = new ApplicationLeadingTalent();
            
            // 生成申请ID
            String applicationId = UUID.randomUUID().toString().replace("-", "");
            leadingApply.setApplicationId(applicationId);
            
            // 设置基本申请信息
            leadingApply.setProjectId(form.getProjectId());
            leadingApply.setProjectName(form.getProjectName());
            leadingApply.setCommunityId1(form.getCommunityId1());
            leadingApply.setCommunityId2(form.getCommunityId2());
            leadingApply.setCommunityId3(form.getCommunityId3());
            leadingApply.setHouseType1(form.getHouseType1());
            leadingApply.setHouseType2(form.getHouseType2());
            leadingApply.setHouseType3(form.getHouseType3());
            leadingApply.setHouseType4(form.getHouseType4());
            
            // 设置申请时间和状态
            leadingApply.setApplyDate(Time.getNowTimeDate("yyyy-MM-dd"));
            leadingApply.setApplyTime(Time.getNowTimeDate("HH:mm:ss"));
            leadingApply.setApplyStatus(1); // 1-提交/待审核
            leadingApply.setApplySort(0);
            leadingApply.setCurrentSort(0);
            
            // 设置申请人相关信息
            leadingApply.setApplicantId(userId);
            leadingApply.setApplicantZjhm(applicant.getIdNumber());
            leadingApply.setApplicantName(applicant.getName());
            leadingApply.setApplicantCompanyId(applicant.getCompanyId());
            leadingApply.setApplicantCompanyUscc(getCompanyUscc(applicant.getCompanyId()));
            leadingApply.setExistLaborContract(form.getExistLaborContract()); // 使用传入的劳动合同关系参数
            
            // 插入数据库
            applicationLeadingTalentMapper.insert(leadingApply);
            
            return R.success("领军优青人才申请提交成功");
            
        } catch (Exception e) {
            return R.error("领军优青人才申请提交失败：" + e.getMessage());
        }
    }
    
    /**
     * 检查用户在指定项目是否已有在途审批记录
     * @param userId 用户ID
     * @param projectId 项目ID
     * @return 是否存在在途审批记录
     */
    private boolean hasPendingApplication(String userId, String projectId) {
        QueryWrapper<ApplicationLeadingTalent> wrapper = new QueryWrapper<>();
        wrapper.eq("applicant_id", userId);
        wrapper.eq("project_id", projectId);
        // 排除已拒绝的状态：4-工作单位审核拒绝、7-产促审核拒绝、10-住建委审核拒绝、13-组织部审核拒绝
        wrapper.notIn("apply_status", 4, 7, 10, 13);
        
        int count = applicationLeadingTalentMapper.selectCount(wrapper);
        return count > 0;
    }
    
    @Override
    @Transactional
    public R withdrawLeadingApply(String userId, String applicationId) {
        try {
            // 1. 查询申请记录
            ApplicationLeadingTalent leadingApply = applicationLeadingTalentMapper.selectById(applicationId);
            if (leadingApply == null) {
                return R.error("申请记录不存在");
            }
            
            // 2. 校验申请是否为该用户提交
            if (!userId.equals(leadingApply.getApplicantId())) {
                return R.error("无权操作他人的申请记录");
            }
            
            // 3. 校验申请状态是否为待审核（1）
            if (leadingApply.getApplyStatus() == null || leadingApply.getApplyStatus() != 1) {
                return R.error("只有待审核状态的申请才能撤回");
            }
            
            // 4. 执行撤回操作，将状态修改为撤回/待提交（0）
            leadingApply.setApplyStatus(0);
            applicationLeadingTalentMapper.updateById(leadingApply);
            
            return R.success("申请撤回成功");
            
        } catch (Exception e) {
            return R.error("申请撤回失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public R updateLeadingApply(String userId, LeadingApplyUpdateDTO form) {
        try {
            String applicationId = form.getApplicationId();
            
            // 1. 查询申请记录
            ApplicationLeadingTalent leadingApply = applicationLeadingTalentMapper.selectById(applicationId);
            if (leadingApply == null) {
                return R.error("申请记录不存在");
            }
            
            // 2. 校验申请是否为该用户提交
            if (!userId.equals(leadingApply.getApplicantId())) {
                return R.error("无权操作他人的申请记录");
            }
            
            // 3. 校验申请状态是否为撤回/待提交（0）
            if (leadingApply.getApplyStatus() == null || leadingApply.getApplyStatus() != 0) {
                return R.error("只有撤回/待提交状态的申请才能修改");
            }
            
            // 4. 查询项目信息并校验申请时间
            ProjectInfo project = projectInfoMapper.selectById(form.getProjectId());
            if (project == null) {
                return R.error("项目信息不存在");
            }
            
            // 校验项目状态（必须是开启状态）
            if (project.getStatus() == null || project.getStatus() != 1) {
                return R.error("该项目暂未开启申请");
            }
            
            // 校验申请时间区间
            String currentTime = Time.getNowTimeDate("yyyy-MM-dd");
            if (!isWithinApplyPeriod(currentTime, project.getApplyStartTime(), project.getApplyEndTime())) {
                return R.error("当前不在该项目的申请时间范围内");
            }

            // 5. 查询申请人信息
            BusinessUser applicant = businessUserMapper.selectById(userId);
            if (applicant == null) {
                return R.error("申请人信息不存在");
            }
            
            // 6. 校验申请人类型（必须是领军优青人才）
            // 注意：这里需要根据实际的领军优青人才类型来确定regType值
            // 暂时使用regType=4作为示例，实际项目中需要确认正确的值
            if (applicant.getRegType() == null || applicant.getRegType() != 4) {
                return R.error("只有领军优青人才才能提交领军优青人才申请");
            }
            
            // 7. 校验申请人状态（必须是审核通过状态）
            if (applicant.getStatus() == null || applicant.getStatus() != 1) {
                return R.error("申请人账户状态异常，请联系管理员");
            }

            // 8. 更新申请记录
            // 更新基本申请信息
            leadingApply.setProjectId(form.getProjectId());
            leadingApply.setProjectName(form.getProjectName());
            leadingApply.setCommunityId1(form.getCommunityId1());
            leadingApply.setCommunityId2(form.getCommunityId2());
            leadingApply.setCommunityId3(form.getCommunityId3());
            leadingApply.setHouseType1(form.getHouseType1());
            leadingApply.setHouseType2(form.getHouseType2());
            leadingApply.setHouseType3(form.getHouseType3());
            leadingApply.setHouseType4(form.getHouseType4());
            
            // 更新申请时间和状态
            leadingApply.setApplyDate(Time.getNowTimeDate("yyyy-MM-dd"));
            leadingApply.setApplyTime(Time.getNowTimeDate("HH:mm:ss"));
            leadingApply.setApplyStatus(1); // 1-提交/待审核
            
            // 更新申请人相关信息
            leadingApply.setApplicantZjhm(applicant.getIdNumber());
            leadingApply.setApplicantName(applicant.getName());
            leadingApply.setApplicantCompanyId(applicant.getCompanyId());
            leadingApply.setApplicantCompanyUscc(getCompanyUscc(applicant.getCompanyId()));
            leadingApply.setExistLaborContract(form.getExistLaborContract()); // 使用传入的劳动合同关系参数
            
            // 更新数据库
            applicationLeadingTalentMapper.updateById(leadingApply);
            
            return R.success("领军优青人才申请修改成功");
            
        } catch (Exception e) {
            return R.error("领军优青人才申请修改失败：" + e.getMessage());
        }
    }
    
    /**
     * 判断当前时间是否在申请时间区间内
     * @param currentTime 当前时间 yyyy-MM-dd格式
     * @param startTime 开始时间 yyyy-MM-dd格式
     * @param endTime 结束时间 yyyy-MM-dd格式
     * @return 是否在时间区间内
     */
    private boolean isWithinApplyPeriod(String currentTime, String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
            // 时间格式解析错误，返回false
            return false;
        }
    }
    
    /**
     * 根据公司ID获取统一信用代码
     * @param companyId 公司ID
     * @return 统一社会信用代码，如果查询不到则返回null
     */
    private String getCompanyUscc(String companyId) {
        if (companyId == null || companyId.isEmpty()) {
            return null;
        }
        
        try {
            BusinessEnterprise enterprise = businessEnterpriseMapper.selectById(companyId);
            if (enterprise != null) {
                return enterprise.getUscc();
            }
            return null;
        } catch (Exception e) {
            // 查询异常时返回null
            return null;
        }
    }
    
    @Override
    public R queryLeadingApplications(String userId) {
        try {
            // 1. 校验用户是否存在
            BusinessUser user = businessUserMapper.selectById(userId);
            if (user == null) {
                return R.error("用户信息不存在");
            }
            
            // 2. 校验用户类型（必须是领军优青人才）
            if (user.getRegType() == null || user.getRegType() != 4) {
                return R.error("只有领军优青人才才能查询申请记录");
            }
            
            // 3. 查询该用户的所有领军优青人才申请记录
            QueryWrapper<ApplicationLeadingTalent> wrapper = new QueryWrapper<>();
            wrapper.eq("applicant_id", userId);
            wrapper.orderByDesc("apply_date", "apply_time"); // 按申请时间倒序排列
            
            List<ApplicationLeadingTalent> applications = applicationLeadingTalentMapper.selectList(wrapper);
            
            return R.success(applications);
            
        } catch (Exception e) {
            return R.error("查询领军优青人才申请记录失败：" + e.getMessage());
        }
    }
    
    @Override
    public R queryAllApplications(String userId) {
        try {
            // 1. 校验用户是否存在
            BusinessUser user = businessUserMapper.selectById(userId);
            if (user == null) {
                return R.error("用户信息不存在");
            }
            
            // 2. 查询该用户的所有领军优青人才申请记录（不校验用户类型，查询所有记录）
            QueryWrapper<ApplicationLeadingTalent> wrapper = new QueryWrapper<>();
            wrapper.eq("applicant_id", userId);
            wrapper.orderByDesc("apply_date", "apply_time"); // 按申请时间倒序排列
            
            List<ApplicationLeadingTalent> applications = applicationLeadingTalentMapper.selectList(wrapper);
            
            return R.success(applications);
            
        } catch (Exception e) {
            return R.error("查询领军优青人才申请记录失败：" + e.getMessage());
        }
    }
}