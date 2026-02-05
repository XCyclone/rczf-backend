package com.example.spba.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spba.dao.BusinessUserMapper;
import com.example.spba.dao.UserOperationMapper;
import com.example.spba.domain.dto.UserOperationInfoDTO;
import com.example.spba.domain.entity.BusinessUser;
import com.example.spba.domain.entity.UserOperation;
import com.example.spba.service.BusinessUserService;
import com.example.spba.service.UserOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserOperationServiceImpl extends ServiceImpl<UserOperationMapper, UserOperation> implements UserOperationService {

    @Autowired
    private UserOperationMapper userOperationMapper;
    
    @Autowired
    private BusinessUserMapper businessUserMapper;

    @Override
    public Integer recordRegisterApply(String userId, Integer type, Object businessUserDTO) {
        UserOperation operation = new UserOperation();
        operation.setUserId(userId);
        operation.setType(type); // 1-个人；2-企业
        operation.setOperation(1); // 1-注册
        operation.setAction(1); // 1-申请
        operation.setContent(JSON.toJSONString(businessUserDTO)); // 将注册信息以JSON格式存储
        operation.setInfo(""); // 申请时info字段为空
        operation.setCreateTime(new java.util.Date());
        operation.setUpdateTime(new java.util.Date());
        userOperationMapper.insert(operation);
        return operation.getId(); // 返回插入记录的ID
    }

    @Override
    public Integer recordUpdateApply(String userId, Integer type, Object businessUserUpdateDTO) {
        UserOperation operation = new UserOperation();
        operation.setUserId(userId);
        operation.setType(type); // 1-个人；2-企业
        operation.setOperation(2); // 2-修改信息
        operation.setAction(1); // 1-申请
        operation.setContent(JSON.toJSONString(businessUserUpdateDTO)); // 将更新信息以JSON格式存储
        operation.setInfo(""); // 申请时info字段为空

        userOperationMapper.insert(operation);
        return operation.getId(); // 返回插入记录的ID
    }

    @Override
    public void updateApproveAction(String userId, Integer type, Integer action, String info) {
        // 查询该用户的最近操作记录
        QueryWrapper<UserOperation> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .in("operation", 1, 2) // 注册操作或修改信息操作
               .eq("action", 1)  // 申请动作
               .orderByDesc("id"); // 获取最新的申请记录
        
        UserOperation operation = userOperationMapper.selectOne(wrapper);
        
        if (operation != null) {
            // 更新记录为审批状态
            UserOperation updateOp = new UserOperation();
            updateOp.setId(operation.getId()); // 设置ID以更新特定记录
            updateOp.setType(type); // 1-个人；2-企业
            updateOp.setAction(action); // 2-同意；3-拒绝
            updateOp.setInfo(info != null ? info : ""); // 审批附言信息
            
            userOperationMapper.updateById(updateOp);
        }
    }
    
    @Override
    public List<UserOperationInfoDTO> getAllOperationInfo() {
        // 查询所有操作记录
        List<UserOperation> operations = userOperationMapper.selectList(null);
        
        return operations.stream().map(operation -> {
            UserOperationInfoDTO dto = new UserOperationInfoDTO(operation);
            
            // 根据用户ID获取用户姓名
            BusinessUser user = businessUserMapper.selectById(operation.getUserId());
            if (user != null) {
                dto.setUserName(user.getName());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
}