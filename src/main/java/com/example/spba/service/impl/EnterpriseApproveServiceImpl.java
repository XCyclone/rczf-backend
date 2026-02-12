package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.spba.dao.ApplicationAgencyTalentMapper;
import com.example.spba.dao.ApplicationIndustryTalentMapper;
import com.example.spba.dao.BusinessEnterpriseMapper;
import com.example.spba.dao.BusinessUserMapper;
import com.example.spba.domain.entity.ApplicationAgencyTalent;
import com.example.spba.domain.entity.ApplicationIndustryTalent;
import com.example.spba.domain.entity.BusinessEnterprise;
import com.example.spba.domain.entity.BusinessUser;
import com.example.spba.service.EnterpriseApproveService;
import com.example.spba.utils.R;
import com.example.spba.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnterpriseApproveServiceImpl implements EnterpriseApproveService {

    @Autowired
    private ApplicationAgencyTalentMapper applicationAgencyTalentMapper;
    
    @Autowired
    private ApplicationIndustryTalentMapper applicationIndustryTalentMapper;
    
    @Autowired
    private BusinessEnterpriseMapper businessEnterpriseMapper;
    
    @Autowired
    private BusinessUserMapper businessUserMapper;

    @Override
    @Transactional
    public R approveAgencyApplication(String applicationId, Boolean approved, String remark, String currentUserId) {
        try {
            // 1. 查询申请记录
            ApplicationAgencyTalent agencyApply = applicationAgencyTalentMapper.selectById(applicationId);
            if (agencyApply == null) {
                return R.error("申请记录不存在");
            }
            
            // 2. 校验申请状态（必须是待审核状态1）
            if (agencyApply.getApplyStatus() == null || agencyApply.getApplyStatus() != 1) {
                return R.error("只有待审核状态的申请才能进行审批");
            }
            
            // 3. 权限校验：校验当前用户是否属于该企业
            String companyId = agencyApply.getApplicantCompanyId();
            if (!hasUserEnterprisePermission(currentUserId, companyId)) {
                return R.error("您无权审批该企业的员工申请");
            }
            
            // 4. 获取企业经办人信息
            String auditorName = getEnterpriseOperatorName(companyId);
            
            // 5. 执行审批操作
            if (approved != null && approved) {
                // 审批通过 - 状态改为工作单位审核通过（3）
                agencyApply.setApplyStatus(3);
            } else {
                // 审批拒绝 - 状态改为工作单位审核拒绝（4）
                agencyApply.setApplyStatus(4);
            }
            
            // 6. 记录审批时间和审批人信息
            agencyApply.setCompanyAuditDate(Time.getNowTimeDate("yyyy-MM-dd"));
            agencyApply.setCompanyAuditTime(Time.getNowTimeDate("HH:mm:ss"));
            agencyApply.setCompanyAuditor(auditorName);
            agencyApply.setCompanyAuditRemark(remark);
            
            // 7. 更新数据库
            applicationAgencyTalentMapper.updateById(agencyApply);
            
            String message = approved != null && approved ? "机关单位员工申请审批通过" : "机关单位员工申请审批拒绝";
            return R.success(message);
            
        } catch (Exception e) {
            return R.error("机关单位员工申请审批失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public R approveIndustryApplication(String applicationId, Boolean approved, String remark, String currentUserId) {
        try {
            // 1. 查询申请记录
            ApplicationIndustryTalent industryApply = applicationIndustryTalentMapper.selectById(applicationId);
            if (industryApply == null) {
                return R.error("申请记录不存在");
            }
            
            // 2. 校验申请状态（必须是待审核状态1）
            if (industryApply.getApplyStatus() == null || industryApply.getApplyStatus() != 1) {
                return R.error("只有待审核状态的申请才能进行审批");
            }
            
            // 3. 权限校验：校验当前用户是否属于该企业
            String companyId = industryApply.getApplicantCompanyId();
            if (!hasUserEnterprisePermission(currentUserId, companyId)) {
                return R.error("您无权审批该企业的员工申请");
            }
            
            // 4. 获取企业经办人信息
            String auditorName = getEnterpriseOperatorName(companyId);
            
            // 5. 执行审批操作
            if (approved != null && approved) {
                // 审批通过 - 状态改为工作单位审核通过（3）
                industryApply.setApplyStatus(3);
            } else {
                // 审批拒绝 - 状态改为工作单位审核拒绝（4）
                industryApply.setApplyStatus(4);
            }
            
            // 6. 记录审批时间和审批人信息
            industryApply.setCompanyAuditDate(Time.getNowTimeDate("yyyy-MM-dd"));
            industryApply.setCompanyAuditTime(Time.getNowTimeDate("HH:mm:ss"));
            industryApply.setCompanyAuditor(auditorName);
            industryApply.setCompanyAuditRemark(remark);
            
            // 7. 更新数据库
            applicationIndustryTalentMapper.updateById(industryApply);
            
            String message = approved != null && approved ? "产业人才申请审批通过" : "产业人才申请审批拒绝";
            return R.success(message);
            
        } catch (Exception e) {
            return R.error("产业人才申请审批失败：" + e.getMessage());
        }
    }
    
    /**
     * 校验用户是否具有该企业的审批权限
     * @param userId 当前用户ID
     * @param companyId 企业ID
     * @return 是否有权限
     */
    private boolean hasUserEnterprisePermission(String userId, String companyId) {
        if (userId == null || companyId == null) {
            return false;
        }
        
        try {
            // 查询用户信息
            BusinessUser user = businessUserMapper.selectById(userId);
            if (user == null) {
                return false;
            }
            
            // 校验用户的企业ID是否匹配
            return companyId.equals(user.getCompanyId());
        } catch (Exception e) {
            // 查询异常时返回false
            return false;
        }
    }
    
    /**
     * 根据企业ID获取企业经办人姓名
     * @param companyId 企业ID
     * @return 经办人姓名，如果查询不到则返回默认值
     */
    private String getEnterpriseOperatorName(String companyId) {
        if (companyId == null || companyId.isEmpty()) {
            return "unknown_auditor";
        }
        
        try {
            BusinessEnterprise enterprise = businessEnterpriseMapper.selectById(companyId);
            if (enterprise != null && enterprise.getOperatorName() != null) {
                return enterprise.getOperatorName();
            }
            return "enterprise_operator";
        } catch (Exception e) {
            // 查询异常时返回默认值
            return "system_auditor";
        }
    }
    
    @Override
    public R queryIndustryApplications(String currentUserId) {
        try {
            // 1. 权限校验：获取当前用户的企业ID
            String companyId = getCurrentUserCompanyId(currentUserId);
            if (companyId == null) {
                return R.error("用户信息异常，无法查询申请记录");
            }
            
            // 2. 查询该企业下所有的产业人才申请记录
            QueryWrapper<ApplicationIndustryTalent> wrapper = new QueryWrapper<>();
            wrapper.eq("applicant_company_id", companyId);
//            wrapper.eq("apply_status", 1); // 只查询待审核状态
            wrapper.orderByDesc("apply_date", "apply_time"); // 按申请时间倒序排列
            
            List<ApplicationIndustryTalent> applications = applicationIndustryTalentMapper.selectList(wrapper);
            
            return R.success(applications);
            
        } catch (Exception e) {
            return R.error("查询产业人才申请记录失败：" + e.getMessage());
        }
    }
    
    @Override
    public R queryAgencyApplications(String currentUserId) {
        try {
            // 1. 权限校验：获取当前用户的企业ID
            String companyId = getCurrentUserCompanyId(currentUserId);
            if (companyId == null) {
                return R.error("用户信息异常，无法查询申请记录");
            }
            
            // 2. 查询该企业下所有的机关单位员工申请记录
            QueryWrapper<ApplicationAgencyTalent> wrapper = new QueryWrapper<>();
            wrapper.eq("applicant_company_id", companyId);
//            wrapper.eq("apply_status", 1); // 只查询待审核状态
            wrapper.orderByDesc("apply_date", "apply_time"); // 按申请时间倒序排列
            
            List<ApplicationAgencyTalent> applications = applicationAgencyTalentMapper.selectList(wrapper);
            
            return R.success(applications);
            
        } catch (Exception e) {
            return R.error("查询机关单位员工申请记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户所属的企业ID
     * @param userId 用户ID
     * @return 企业ID，如果查询不到则返回null
     */
    private String getCurrentUserCompanyId(String userId) {
        if (userId == null) {
            return null;
        }
        
        try {
            BusinessUser user = businessUserMapper.selectById(userId);
            if (user != null) {
                return user.getCompanyId();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}