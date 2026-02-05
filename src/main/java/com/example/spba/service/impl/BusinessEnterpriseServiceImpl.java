package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spba.constant.ProjectConstants;
import com.example.spba.dao.BusinessEnterpriseApplyMapper;
import com.example.spba.dao.BusinessEnterpriseMapper;
import com.example.spba.dao.BusinessUserApplyMapper;
import com.example.spba.dao.UserMapper;
import com.example.spba.domain.dto.BusinessEnterpriseDTO;
import com.example.spba.domain.dto.BusinessEnterpriseUpdateDTO;
import com.example.spba.domain.dto.EnterpriseUserResponseDTO;
import com.example.spba.domain.entity.BusinessEnterprise;
import com.example.spba.domain.entity.BusinessEnterpriseApply;
import com.example.spba.domain.entity.BusinessUserApply;
import com.example.spba.domain.entity.User;
import com.example.spba.service.BusinessEnterpriseService;
import com.example.spba.service.RoleUserRelService;
import com.example.spba.utils.R;
import com.example.spba.utils.Time;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BusinessEnterpriseServiceImpl extends ServiceImpl<BusinessEnterpriseMapper, BusinessEnterprise> implements BusinessEnterpriseService {

    @Autowired
    private BusinessEnterpriseMapper businessEnterpriseMapper;

    @Autowired
    private BusinessEnterpriseApplyMapper businessEnterpriseApplyMapper;

    @Autowired
    private BusinessUserApplyMapper businessUserApplyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleUserRelService roleUserRelService;

    /**
     * 根据统一社会信用代码查询企业
     * @param uscc 统一社会信用代码
     * @return 企业信息
     */
    public BusinessEnterprise getByUscc(String uscc) {
        QueryWrapper<BusinessEnterprise> wrapper = new QueryWrapper<>();
        wrapper.eq("uscc", uscc);
        return businessEnterpriseMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R registerApply(BusinessEnterpriseDTO form) {
        // 检查统一社会信用代码是否已存在
        BusinessEnterprise existByUscc = this.getByUscc(form.getUscc());
        if (existByUscc != null) {
            return R.error("该统一社会信用代码已被注册");
        }

//        // 检查经办人手机号是否已存在
//        BusinessEnterprise existByMobile = this.getByOperatorMobile(form.getOperatorMobile());
//        if (existByMobile != null) {
//            return R.error("该经办人手机号已被注册");
//        }

        // 创建企业申请实体
        BusinessEnterpriseApply apply = new BusinessEnterpriseApply();
        
        // 生成申请ID和企业ID
        String applyId = UUID.randomUUID().toString().replace("-", "");
        String enterpriseId = UUID.randomUUID().toString().replace("-", "");
        apply.setId(applyId);
        apply.setBusinessEnterpriseId(enterpriseId);
        
        // 复制基本信息
        apply.setEnterpriseName(form.getEnterpriseName());
        apply.setShortName(form.getShortName());
        apply.setIndustry(form.getIndustry());
        apply.setStaffSize(form.getStaffSize());
        apply.setUscc(form.getUscc());
        apply.setLegalPerson(form.getLegalPerson());
        apply.setLegalIdNumber(form.getLegalIdNumber());
        apply.setEnterpriseAddr(form.getEnterpriseAddr());
        apply.setEnterpriseLocationId(form.getEnterpriseLocationId());
        apply.setEnterpriseLocationName(form.getEnterpriseLocationName());
        apply.setOperatorName(form.getOperatorName());
        apply.setOperatorMobile(form.getOperatorMobile());
        
        // MD5加密密码
        apply.setLoginPwd(DigestUtils.md5DigestAsHex(form.getLoginPwd().getBytes()));
        
        // 设置注册日期和时间
        apply.setRegDate(Time.getNowTimeDate("yyyy-MM-dd"));
        apply.setRegTime(Time.getNowTimeDate("HH:mm:ss"));
        
        // 设置注册类型和其他状态
        apply.setRegCategory(form.getRegCategory());
        apply.setOperation(1); // 1-注册
        apply.setStatus(0); // 0-提交/待审核
        apply.setInfo(""); // 初始备注为空
        
        // 保存申请信息到business_enterprise_apply表
        businessEnterpriseApplyMapper.insert(apply);
        
        return R.success("企业注册申请已提交，请等待审核", applyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveApply(String applyId, boolean approveStatus, String info) {
        // 查询申请记录
        BusinessEnterpriseApply apply = businessEnterpriseApplyMapper.selectById(applyId);
        if (apply == null) {
            throw new IllegalArgumentException("申请记录不存在");
        }
        
        // 检查申请状态
        if (apply.getStatus() != 0) { // 0-提交/待审核
            throw new IllegalArgumentException("该申请不在待审核状态，无法审批");
        }
        
        if (approveStatus) {
            // 审批通过 - 同步数据到business_enterprise表
            syncApplyToEnterprise(apply);
            // 更新申请状态为通过
            updateApplyStatus(applyId, 1, info); // 1-审核通过
        } else {
            // 审批拒绝
            updateApplyStatus(applyId, 2, info); // 2-审核拒绝
        }
    }
    
    /**
     * 同步申请数据到企业表
     */
    private void syncApplyToEnterprise(BusinessEnterpriseApply apply) {
        // 创建企业实体
        BusinessEnterprise enterprise = new BusinessEnterprise();
        
        // 使用申请中存储的企业ID
        enterprise.setId(apply.getBusinessEnterpriseId());
        enterprise.setEnterpriseName(apply.getEnterpriseName());
        enterprise.setShortName(apply.getShortName());
        enterprise.setIndustry(apply.getIndustry());
        enterprise.setStaffSize(apply.getStaffSize());
        enterprise.setUscc(apply.getUscc());
        enterprise.setLegalPerson(apply.getLegalPerson());
        enterprise.setLegalIdNumber(apply.getLegalIdNumber());
        enterprise.setEnterpriseAddr(apply.getEnterpriseAddr());
        enterprise.setEnterpriseLocationId(apply.getEnterpriseLocationId());
        enterprise.setEnterpriseLocationName(apply.getEnterpriseLocationName());
        enterprise.setOperatorName(apply.getOperatorName());
        enterprise.setOperatorMobile(apply.getOperatorMobile());
        enterprise.setLoginPwd(apply.getLoginPwd());
        enterprise.setRegDate(apply.getRegDate());
        enterprise.setRegTime(apply.getRegTime());
        enterprise.setRegCategory(apply.getRegCategory());
        enterprise.setStatus(1); // 审核通过
        enterprise.setEverLogged(0); // 未登录过
        
        // 保存到business_enterprise表
        businessEnterpriseMapper.insert(enterprise);
        
        // 创建用户账号
        createUserAccount(enterprise);
    }
    
    /**
     * 更新申请状态
     */
    private void updateApplyStatus(String applyId, Integer status, String info) {
        BusinessEnterpriseApply update = new BusinessEnterpriseApply();
        update.setId(applyId);
        update.setStatus(status);
        update.setInfo(info != null ? info : "");
        
        QueryWrapper<BusinessEnterpriseApply> wrapper = new QueryWrapper<>();
        wrapper.eq("id", applyId);
        businessEnterpriseApplyMapper.update(update, wrapper);
    }
    
    /**
     * 创建用户账号
     */
    private void createUserAccount(BusinessEnterprise enterprise) {
        // 根据注册类型确定角色ID
        String roleId;
        if (enterprise.getRegCategory() != null) {
            if (enterprise.getRegCategory().equals(1)) {
                roleId = "R0002"; // 企业对应R0002
            } else if (enterprise.getRegCategory().equals(2)) {
                roleId = "R0003"; // 机关单位对应R0003
            } else {
                throw new IllegalArgumentException("未知的注册类型：" + enterprise.getRegCategory());
            }
        } else {
            throw new IllegalArgumentException("注册类型不能为空");
        }

        // 检查用户账号是否已存在（根据统一社会信用代码）
        String username = enterprise.getUscc();
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("统一社会信用代码为空，无法创建用户账号");
        }

        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.eq("username", username);
        userWrapper.eq("status", 1);
        User existUser = userMapper.selectOne(userWrapper);

        if (existUser != null) {
            throw new IllegalArgumentException("统一社会信用代码已存在且正在使用，无法创建用户账号");
        }

        // 创建用户账号
        User newUser = new User();
        newUser.setId(enterprise.getId()); // 使用企业ID作为用户ID
        newUser.setUsername(username);
        newUser.setSafe(ProjectConstants.DefaultValue.DEFAULT_SAFE);
        newUser.setPassword(enterprise.getLoginPwd());
        newUser.setStatus(ProjectConstants.UserStatus.NORMAL);
        userMapper.insert(newUser);

        // 保存角色关联
        roleUserRelService.saveUserRoles(newUser.getId(), Arrays.asList(roleId));
    }

    @Override
    public BusinessEnterprise getEnterpriseInfo(String enterpriseId) {
        return businessEnterpriseMapper.selectById(enterpriseId);
    }
    
    @Override
    public Object getEnterpriseInfoWithApprovalStatus(String enterpriseId) {
        // 获取企业基本信息
        BusinessEnterprise enterpriseInfo = businessEnterpriseMapper.selectById(enterpriseId);
        
        if (enterpriseInfo == null) {
            return null;
        }
        
        // 检查是否有待审批的企业信息修改申请
        // 查询business_enterprise_apply表中该企业ID对应的状态为0（待审核）的记录
        QueryWrapper<BusinessEnterpriseApply> wrapper = new QueryWrapper<>();
        wrapper.eq("business_enterprise_id", enterpriseId)
               .eq("status", 0)  // 0-提交/待审核
               .eq("operation", 2); // 2-修改信息操作
        
        int pendingUpdateCount = businessEnterpriseApplyMapper.selectCount(wrapper);
        
        // 使用BeanMap将对象转换为Map
        org.springframework.cglib.beans.BeanMap beanMap = org.springframework.cglib.beans.BeanMap.create(enterpriseInfo);
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        
        // 复制BeanMap中的所有属性到结果Map
        for (Object key : beanMap.keySet()) {
            if (key.toString().equals("loginPwd"))  continue;       // 不返回密码
            result.put((String) key, beanMap.get(key));
        }
        
        // 添加审批状态标志（根据数量判断）
        result.put("updateFlag", pendingUpdateCount > 0 ? 1 : 0); // 0表示无待审批，1表示有待审批
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updatePassword(String enterpriseId, String oldPassword, String newPassword) {
        // 根据企业ID查询企业信息
        BusinessEnterprise enterprise = businessEnterpriseMapper.selectById(enterpriseId);
        if (enterprise == null) {
            return R.error("企业不存在");
        }
        
        // 对原密码进行MD5加密并与数据库中的密码比较
        String encryptedOldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!enterprise.getLoginPwd().equals(encryptedOldPassword)) {
            return R.error("原密码不正确");
        }
        
        // 验证新密码强度（简单验证）
        if (newPassword == null || newPassword.length() < 6 || newPassword.length() > 18) {
            return R.error("新密码长度应在6-18位之间");
        }
        
        // 对新密码进行MD5加密
        String encryptedNewPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        
        // 更新企业密码
        BusinessEnterprise update = new BusinessEnterprise();
        update.setId(enterpriseId);
        update.setLoginPwd(encryptedNewPassword);
        
        QueryWrapper<BusinessEnterprise> wrapper = new QueryWrapper<>();
        wrapper.eq("id", enterpriseId);
        int result = businessEnterpriseMapper.update(update, wrapper);
        
        if (result > 0) {
            // 同时更新user表中的密码（如果存在关联的user记录）
            try {
                // 查找关联的用户账号（通常使用统一社会信用代码作为用户名）
                QueryWrapper<User> userWrapper = new QueryWrapper<>();
                userWrapper.eq("username", enterprise.getUscc());
                User linkedUser = userMapper.selectOne(userWrapper);
                
                if (linkedUser != null) {
                    // 更新关联用户账号的密码
                    linkedUser.setPassword(encryptedNewPassword);
                    userMapper.updateById(linkedUser);
                }
            } catch (Exception e) {
                // 记录错误但不中断主流程，因为user表更新不是关键步骤
                e.printStackTrace();
            }
            
            return R.success("密码修改成功");
        } else {
            return R.error("密码修改失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateApply(BusinessEnterpriseUpdateDTO form) {
        // 验证企业是否存在
        BusinessEnterprise enterprise = businessEnterpriseMapper.selectById(form.getEnterpriseId());
        if (enterprise == null) {
            return R.error("企业不存在");
        }
        
        // 检查是否有待审批的修改申请
        QueryWrapper<BusinessEnterpriseApply> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("business_enterprise_id", form.getEnterpriseId())
                     .eq("status", 0)  // 0-提交/待审核
                     .eq("operation", 2); // 2-修改信息
        
        int pendingCount = businessEnterpriseApplyMapper.selectCount(pendingWrapper);
        if (pendingCount > 0) {
            return R.error("该企业已有待审批的修改申请，请等待审批完成后再提交新的申请");
        }
        
        // 创建企业修改申请实体
        BusinessEnterpriseApply apply = new BusinessEnterpriseApply();
        
        // 生成申请ID
        String applyId = UUID.randomUUID().toString().replace("-", "");
        apply.setId(applyId);
        apply.setBusinessEnterpriseId(form.getEnterpriseId());
        
        // 复制要修改的信息
        if (form.getEnterpriseName() != null) {
            apply.setEnterpriseName(form.getEnterpriseName());
        } else {
            apply.setEnterpriseName(enterprise.getEnterpriseName());
        }
        
        if (form.getShortName() != null) {
            apply.setShortName(form.getShortName());
        } else {
            apply.setShortName(enterprise.getShortName());
        }
        
        if (form.getIndustry() != null) {
            apply.setIndustry(form.getIndustry());
        } else {
            apply.setIndustry(enterprise.getIndustry());
        }
        
        if (form.getStaffSize() != null) {
            apply.setStaffSize(form.getStaffSize());
        } else {
            apply.setStaffSize(enterprise.getStaffSize());
        }
        
        // 统一社会信用代码不允许修改
        apply.setUscc(enterprise.getUscc());
        
        if (form.getLegalPerson() != null) {
            apply.setLegalPerson(form.getLegalPerson());
        } else {
            apply.setLegalPerson(enterprise.getLegalPerson());
        }
        
        if (form.getLegalIdNumber() != null) {
            apply.setLegalIdNumber(form.getLegalIdNumber());
        } else {
            apply.setLegalIdNumber(enterprise.getLegalIdNumber());
        }
        
        if (form.getEnterpriseAddr() != null) {
            apply.setEnterpriseAddr(form.getEnterpriseAddr());
        } else {
            apply.setEnterpriseAddr(enterprise.getEnterpriseAddr());
        }
        
        // 企业属地ID保持不变
        apply.setEnterpriseLocationId(enterprise.getEnterpriseLocationId());
        if (form.getEnterpriseLocationId() != null) {
            apply.setEnterpriseLocationId(form.getEnterpriseLocationId());
        } else {
            apply.setEnterpriseLocationId(enterprise.getEnterpriseLocationId());
        }
        
        if (form.getEnterpriseLocationName() != null) {
            apply.setEnterpriseLocationName(form.getEnterpriseLocationName());
        } else {
            apply.setEnterpriseLocationName(enterprise.getEnterpriseLocationName());
        }
        
        if (form.getOperatorName() != null) {
            apply.setOperatorName(form.getOperatorName());
        } else {
            apply.setOperatorName(enterprise.getOperatorName());
        }
        
        if (form.getOperatorMobile() != null) {
            apply.setOperatorMobile(form.getOperatorMobile());
        } else {
            apply.setOperatorMobile(enterprise.getOperatorMobile());
        }

        if (form.getRegCategory() != null) {
            apply.setRegCategory(form.getRegCategory());
        } else {
            apply.setRegCategory(enterprise.getRegCategory());
        }
        
        // 设置操作类型和状态
        apply.setOperation(2); // 2-修改信息
        apply.setStatus(0); // 0-提交/待审核
        apply.setInfo(""); // 初始备注为空
        
        // 保存申请信息到business_enterprise_apply表
        businessEnterpriseApplyMapper.insert(apply);
        
        return R.success("企业信息修改申请已提交，请等待审核", applyId);
    }
    
    @Override
    public List<EnterpriseUserResponseDTO> getEnterpriseUsers(String enterpriseId) {
        // 首先验证企业是否存在
        BusinessEnterprise enterprise = businessEnterpriseMapper.selectById(enterpriseId);
        if (enterprise == null) {
            throw new IllegalArgumentException("企业不存在");
        }
        
        // 查询该企业下的所有用户申请记录（全量数据）
        QueryWrapper<BusinessUserApply> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", enterpriseId);
        // 不过滤状态和操作类型，返回所有记录
        
        List<BusinessUserApply> userApplies = businessUserApplyMapper.selectList(wrapper);
        
        // 转换为返回格式（不包含密码字段）
        List<EnterpriseUserResponseDTO> userList = new ArrayList<>();
        for (BusinessUserApply apply : userApplies) {
            EnterpriseUserResponseDTO userInfo = new EnterpriseUserResponseDTO();
            userInfo.setUser_id(apply.getBusinessUserId());
            userInfo.setName(apply.getName());
            userInfo.setGender(apply.getGender());
            userInfo.setId_type(apply.getIdType());
            userInfo.setId_number(apply.getIdNumber());
            userInfo.setMobile(apply.getMobile());
            userInfo.setBirth_date(apply.getBirthDate());
            userInfo.setHighest_edu(apply.getHighestEdu());
            userInfo.setNationality(apply.getNationality());
            userInfo.setReg_type(apply.getRegType());
            userInfo.setCompany_id(apply.getCompanyId());
            userInfo.setCompany_name(apply.getCompanyName());
            userInfo.setReg_date(apply.getRegDate());
            userInfo.setReg_time(apply.getRegTime());
            userInfo.setStatus(apply.getStatus());
            userInfo.setOperation(apply.getOperation());
            userInfo.setInfo(apply.getInfo());
            userInfo.setCreate_time(apply.getCreateTime());
            userInfo.setUpdate_time(apply.getUpdateTime());
            userList.add(userInfo);
        }
        
        return userList;
    }
}