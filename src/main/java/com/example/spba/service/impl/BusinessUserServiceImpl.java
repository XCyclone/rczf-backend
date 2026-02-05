package com.example.spba.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spba.dao.BusinessUserApplyMapper;
import com.example.spba.dao.BusinessUserMapper;
import com.example.spba.domain.dto.BusinessUserDTO;
import com.example.spba.domain.dto.BusinessUserUpdateDTO;
import com.example.spba.domain.entity.BusinessUserApply;
import com.example.spba.domain.entity.User;
import com.example.spba.domain.entity.BusinessUser;
import com.example.spba.service.UserService;
import com.example.spba.service.BusinessUserService;
import com.example.spba.service.RoleUserRelService;
import com.example.spba.constant.ProjectConstants;
import com.example.spba.utils.R;
import com.example.spba.utils.Time;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.UUID;

@Service
public class BusinessUserServiceImpl implements BusinessUserService
{
    @Autowired
    private UserService userService;

    @Autowired
    private RoleUserRelService roleUserRelService;

    @Autowired
    private BusinessUserMapper businessUserMapper;
    


    @Autowired
    private BusinessUserApplyMapper businessUserApplyMapper;

    @Override
    public BusinessUser getByMobile(String mobile)
    {
        QueryWrapper<BusinessUser> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        return businessUserMapper.selectOne( wrapper);
    }

    @Override
    public BusinessUser getByIdNumber(String idNumber)
    {
        QueryWrapper<BusinessUser> wrapper = new QueryWrapper<>();
        wrapper.eq("id_number", idNumber);
        return businessUserMapper.selectOne(wrapper);
    }

    @Override
    public R register(BusinessUserDTO form) {
        // 创建用户申请实体
        BusinessUserApply apply = new BusinessUserApply();
        
        // 生成申请ID和用户ID
        String applyId = UUID.randomUUID().toString().replace("-", "");
        String userId = UUID.randomUUID().toString().replace("-", "");
        apply.setId(applyId);
        apply.setBusinessUserId(userId);
        
        // 复制基本信息
        apply.setName(form.getName());
        apply.setGender(form.getGender());
        apply.setIdType(form.getIdType());
        apply.setIdNumber(form.getIdNumber());
        apply.setPassword(DigestUtils.md5DigestAsHex(form.getPassword().getBytes()));
        apply.setBirthDate(form.getBirthDate());
        apply.setHighestEdu(form.getHighestEdu());
        apply.setNationality(form.getNationality());
        apply.setMobile(form.getMobile());
        apply.setRegType(form.getRegType());
        apply.setCompanyId(form.getCompanyId());
        apply.setCompanyName(form.getCompanyName());
        
        // 设置注册日期和时间
        apply.setRegDate(Time.getNowTimeDate("yyyy-MM-dd"));
        apply.setRegTime(Time.getNowTimeDate("HH:mm:ss"));
        
        // 设置操作类型和状态
        apply.setOperation(1); // 1-注册
        apply.setStatus(0); // 0-提交/待审核
        apply.setInfo(""); // 初始备注为空
        
        // 保存申请信息到business_user_apply表
        businessUserApplyMapper.insert(apply);
        
        return R.success("注册申请已提交，请等待审核", applyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(String applyId, boolean approveStatus, String info)
    {
        // 1. 查询申请记录
        BusinessUserApply apply = businessUserApplyMapper.selectById(applyId);
        if (apply == null) {
            throw new IllegalArgumentException("申请记录不存在");
        }
        
        // 2. 检查申请状态
        if (apply.getStatus() != 0) { // 0-提交/待审核
            throw new IllegalArgumentException("该申请不在待审核状态，无法审批");
        }
        
        // 3. 根据审批结果决定后续操作
        if (approveStatus) {
            // 审批通过 - 同步数据到business_user表
            syncApplyToUser(apply);
            // 更新申请状态为通过
            updateApplyStatus(applyId, 1, info); // 1-审核通过
        } else {
            // 审批拒绝
            updateApplyStatus(applyId, 2, info); // 2-审核拒绝
        }
    }
    
    /**
     * 同步申请数据到用户表
     */
    private void syncApplyToUser(BusinessUserApply apply) {
        // 创建用户实体
        BusinessUser user = new BusinessUser();
        
        // 使用申请中存储的用户ID
        user.setId(apply.getBusinessUserId());
        user.setName(apply.getName());
        user.setGender(apply.getGender());
        user.setIdType(apply.getIdType());
        user.setIdNumber(apply.getIdNumber());
        user.setPassword(apply.getPassword());
        user.setBirthDate(apply.getBirthDate());
        user.setHighestEdu(apply.getHighestEdu());
        user.setNationality(apply.getNationality());
        user.setMobile(apply.getMobile());
        user.setRegDate(apply.getRegDate());
        user.setRegTime(apply.getRegTime());
        user.setRegType(apply.getRegType());
        user.setCompanyId(apply.getCompanyId());
        user.setCompanyName(apply.getCompanyName());
        user.setStatus(1); // 审核通过
        user.setEverLogged(0); // 未登录过
        
        // 保存到business_user表
        businessUserMapper.insert(user);
        
        // 创建用户账号
        createUserAccount(user);
    }
    
    /**
     * 更新申请状态
     */
    private void updateApplyStatus(String applyId, Integer status, String info) {
        BusinessUserApply update = new BusinessUserApply();
        update.setId(applyId);
        update.setStatus(status);
        update.setInfo(info != null ? info : "");
        
        QueryWrapper<BusinessUserApply> wrapper = new QueryWrapper<>();
        wrapper.eq("id", applyId);
        businessUserApplyMapper.update(update, wrapper);
    }
    
    /**
     * 创建用户账号
     */
    private void createUserAccount(BusinessUser user) {
        // 根据注册类型确定角色ID
        String roleId;
        if (user.getRegType() != null) {
            if (user.getRegType().equals(1)) {
                roleId = "R0004"; // 企业员工对应R0004
            } else if (user.getRegType().equals(2)) {
                roleId = "R0005"; // 机关单位员工对应R0005
            } else if (user.getRegType().equals(3)) {
                roleId = "R0006"; // 领军、优青人才对应R0006
            } else {
                throw new IllegalArgumentException("未知的注册类型：" + user.getRegType());
            }
        } else {
            throw new IllegalArgumentException("注册类型不能为空");
        }

        // 检查用户账号是否已存在（根据手机号）
        String username = user.getMobile();
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("业务用户手机号为空，无法创建用户账号");
        }

        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.eq("username", username);
        userWrapper.eq("status", 1);
        User existUser = userService.getOne(userWrapper);
        
        if (existUser != null) {
            // 如果用户已存在，只更新角色关联
            throw new IllegalArgumentException("手机号已存在且正在使用，无法创建用户账号");

        }

        // 创建用户账号
        User newUser = new User();
        newUser.setId(UUID.randomUUID().toString().replace("-", "")); // 生成32位UUID
        newUser.setUsername(username);
        newUser.setSafe(ProjectConstants.DefaultValue.DEFAULT_SAFE);
        newUser.setPassword(user.getPassword()); // 直接使用 business_user 的 MD5 密码
        newUser.setStatus(ProjectConstants.UserStatus.NORMAL); // 正常状态
        userService.save(newUser);

        // 保存角色关联
        roleUserRelService.saveUserRoles(newUser.getId(), Arrays.asList(roleId));
    }
    
    /**
     * 执行审批通过的逻辑
     */
    private void doApprove(String businessUserId, BusinessUser user) {
        // 根据注册类型确定角色ID
        String roleId;
        if (user.getRegType() != null) {
            if (user.getRegType().equals(1)) {
                roleId = "R0004"; // 企业员工对应R0004
            } else if (user.getRegType().equals(2)) {
                roleId = "R0005"; // 机关单位员工对应R0005
            } else {
                throw new IllegalArgumentException("未知的注册类型：" + user.getRegType());
            }
        } else {
            throw new IllegalArgumentException("注册类型不能为空");
        }

        // 更新业务用户状态和单位信息
        BusinessUser update = new BusinessUser();
        update.setId(user.getId());
        update.setStatus(1); // 审核通过
        QueryWrapper wrapper = new QueryWrapper<>();
        wrapper.eq("id", user.getId());
        businessUserMapper.update(update, wrapper);

        // 检查用户账号是否已存在（根据手机号）
        String username = user.getMobile();
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("业务用户手机号为空，无法创建用户账号");
        }

        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.eq("username", username);
        User existUser = userService.getOne(userWrapper);
        
        if (existUser != null) {
            // 如果用户已存在，只更新角色关联
            roleUserRelService.saveUserRoles(existUser.getId(), Arrays.asList(roleId));
            return;
        }

        // 创建用户账号
        User newUser = new User();
        newUser.setId(UUID.randomUUID().toString().replace("-", "")); // 生成32位UUID
        newUser.setUsername(username);
        // business_user 的 password 已经是 MD5 加密的
        // user 表的登录逻辑是：MD5(输入密码 + safe) == password
        // 为了兼容，我们设置 safe 为空字符串，这样登录时验证：MD5(输入密码 + "") == password
        // 即：MD5(输入密码) == password，这样就能使用 business_user 的 MD5 密码
        newUser.setSafe(ProjectConstants.DefaultValue.DEFAULT_SAFE);
        newUser.setPassword(user.getPassword()); // 直接使用 business_user 的 MD5 密码
        newUser.setStatus(ProjectConstants.UserStatus.NORMAL); // 正常状态
        userService.save(newUser);

        // 保存角色关联
        roleUserRelService.saveUserRoles(newUser.getId(), Arrays.asList(roleId));
    }
    
    @Override
    public BusinessUser getUserInfo(String userId) {
        return businessUserMapper.selectById(userId);
    }
    
    @Override
    public Object getUserInfoWithApprovalStatus(String userId) {
        // 获取用户基本信息
        BusinessUser userInfo = businessUserMapper.selectById(userId);
        
        if (userInfo == null) {
            return null;
        }
        
        // 检查是否有待审批的修改信息申请（只查询数量）
        QueryWrapper<BusinessUserApply> wrapper = new QueryWrapper<>();
        wrapper.eq("business_user_id", userId)
               .eq("operation", 2)  // 修改信息操作
               .eq("status", 0);    // 0-提交/待审核状态
        
        int pendingUpdateCount = businessUserApplyMapper.selectCount(wrapper);
        
        // 使用BeanMap将对象转换为Map
        org.springframework.cglib.beans.BeanMap beanMap = org.springframework.cglib.beans.BeanMap.create(userInfo);
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        
        // 复制BeanMap中的所有属性到结果Map
        for (Object key : beanMap.keySet()) {
            if (key.toString().equals("password"))  continue;       // 不返回密码
            result.put((String) key, beanMap.get(key));
        }
        
        // 添加审批状态标志（根据数量判断）
        result.put("updateFlag", pendingUpdateCount > 0 ? 1 : 0); // 0表示无待审批，1表示有待审批
        
        return result;
    }
    
    @Override
    public R updateUserPassword(String userId, String oldPassword, String newPassword) {
        // 根据用户ID查询用户
        BusinessUser user = businessUserMapper.selectById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }
        
        // 对原密码进行MD5加密并与数据库中的密码比较
        String encryptedOldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!user.getPassword().equals(encryptedOldPassword)) {
            return R.error("原密码不正确");
        }
        
        // 验证新密码强度（简单验证）
        if (newPassword == null || newPassword.length() < 6 || newPassword.length() > 18) {
            return R.error("新密码长度应在6-18位之间");
        }
        
        // 对新密码进行MD5加密
        String encryptedNewPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        
        // 更新用户密码
        BusinessUser update = new BusinessUser();
        update.setId(userId);
        update.setPassword(encryptedNewPassword);
        
        QueryWrapper<BusinessUser> wrapper = new QueryWrapper<>();
        wrapper.eq("id", userId);
        int result = businessUserMapper.update(update, wrapper);
        
        if (result > 0) {
            // 同时更新user表中的密码（如果存在关联的user记录）
            try {
                // 查找关联的用户账号（通常使用手机号作为用户名）
                QueryWrapper<User> userWrapper = new QueryWrapper<>();
                userWrapper.eq("username", user.getMobile());
                User linkedUser = userService.getOne(userWrapper);
                
                if (linkedUser != null) {
                    // 更新关联用户账号的密码
                    linkedUser.setPassword(encryptedNewPassword);
                    userService.updateById(linkedUser);
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
    public R updateUser(BusinessUserUpdateDTO form) {
        // 检查用户是否存在
        BusinessUser existingUser = businessUserMapper.selectById(form.getId());
        if (existingUser == null) {
            return R.error("用户不存在");
        }

        // 检查是否有待审批的修改申请
        QueryWrapper<BusinessUserApply> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("business_user_id", form.getId())
                     .eq("status", 0)  // 0-提交/待审核
                     .eq("operation", 2); // 2-修改信息
        
        int pendingCount = businessUserApplyMapper.selectCount(pendingWrapper);
        if (pendingCount > 0) {
            return R.error("该用户已有待审批的修改申请，请等待审批完成后再提交新的申请");
        }

        // 验证注册类型的有效性
        if (form.getRegType() != null) {
            if (form.getRegType() < 1 || form.getRegType() > 3) {
                return R.error("注册类型参数错误，有效值为1-3");
            }
        }

        // 验证工作单位相关信息
        if (form.getCompanyName() != null && form.getCompanyName().trim().isEmpty()) {
            return R.error("工作单位名称不能为空");
        }

        // 创建用户修改申请实体
        BusinessUserApply apply = new BusinessUserApply();
        
        // 生成申请ID
        String applyId = UUID.randomUUID().toString().replace("-", "");
        apply.setId(applyId);
        apply.setBusinessUserId(form.getId());
        
        // 复制要修改的信息（只包含现在支持的字段）
        apply.setHighestEdu(form.getHighestEdu());
        apply.setNationality(form.getNationality());
        apply.setCompanyName(form.getCompanyName());
        apply.setRegType(form.getRegType());
        
        // 保持原有信息
        apply.setName(existingUser.getName());
        apply.setGender(existingUser.getGender());
        apply.setIdType(existingUser.getIdType());
        apply.setIdNumber(existingUser.getIdNumber());
        apply.setPassword(existingUser.getPassword());
        apply.setBirthDate(existingUser.getBirthDate());
        apply.setMobile(existingUser.getMobile());
        apply.setRegDate(existingUser.getRegDate());
        apply.setRegTime(existingUser.getRegTime());
        apply.setCompanyId(existingUser.getCompanyId());
        
        // 设置操作类型和状态
        apply.setOperation(2); // 2-修改信息
        apply.setStatus(0); // 0-提交/待审核
        apply.setInfo(""); // 初始备注为空
        
        // 保存申请信息到business_user_apply表
        businessUserApplyMapper.insert(apply);

        return R.success("信息修改申请已提交，请等待审核", applyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveUpdate(String businessUserId, boolean approveStatus, String info) {
        // 1. 查询业务用户
        BusinessUser user = businessUserMapper.selectById(businessUserId);
        if (user == null) {
            throw new IllegalArgumentException("业务用户不存在");
        }

        // 2. 获取最新的信息修改申请记录（状态为0-待审核，操作类型为2-修改信息）
        QueryWrapper<BusinessUserApply> applyWrapper = new QueryWrapper<>();
        applyWrapper.eq("business_user_id", businessUserId)
                   .eq("status", 0)  // 0-提交/待审核
                   .eq("operation", 2) // 2-修改信息
                   .orderByDesc("id"); // 获取最新的申请记录
                
        BusinessUserApply latestApply = businessUserApplyMapper.selectOne(applyWrapper);

        if (latestApply == null) {
            throw new IllegalArgumentException("未找到信息修改申请记录");
        }

        if (approveStatus) {
            // 审批通过：更新用户信息
            BusinessUser updatedUser = new BusinessUser();
            updatedUser.setId(businessUserId);
            
            // 只更新现在支持的字段
            updatedUser.setHighestEdu(latestApply.getHighestEdu());
            updatedUser.setNationality(latestApply.getNationality());
            updatedUser.setCompanyName(latestApply.getCompanyName());
            updatedUser.setRegType(latestApply.getRegType());
            
            // 保持原有信息
            updatedUser.setName(user.getName());
            updatedUser.setGender(user.getGender());
            updatedUser.setIdType(user.getIdType());
            updatedUser.setIdNumber(user.getIdNumber());
            updatedUser.setPassword(user.getPassword());
            updatedUser.setBirthDate(user.getBirthDate());
            updatedUser.setMobile(user.getMobile());
            updatedUser.setRegDate(user.getRegDate());
            updatedUser.setRegTime(user.getRegTime());
            updatedUser.setCompanyId(user.getCompanyId());
            updatedUser.setStatus(user.getStatus());
            updatedUser.setEverLogged(user.getEverLogged());

            // 更新业务用户信息
            QueryWrapper<BusinessUser> wrapper = new QueryWrapper<>();
            wrapper.eq("id", businessUserId);
            businessUserMapper.update(updatedUser, wrapper);

            // 更新申请状态为通过
            updateApplyStatus(latestApply.getId(), 1, info); // 1-审核通过
        } else {
            // 审批拒绝
            // 更新申请状态为拒绝
            updateApplyStatus(latestApply.getId(), 2, info); // 2-审核拒绝
        }
    }
}