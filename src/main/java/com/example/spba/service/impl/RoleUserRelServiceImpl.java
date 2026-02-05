package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spba.dao.RoleUserRelMapper;
import com.example.spba.domain.entity.RoleUserRel;
import com.example.spba.service.RoleUserRelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleUserRelServiceImpl extends ServiceImpl<RoleUserRelMapper, RoleUserRel> implements RoleUserRelService
{
    @Override
    public List<String> getRoleIdsByUserId(String userId)
    {
        QueryWrapper<RoleUserRel> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<RoleUserRel> list = this.list(wrapper);
        return list.stream().map(RoleUserRel::getRoleId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUserRoles(String userId, List<String> roleIds)
    {
        // 先删除原有关联
        this.removeUserRoles(userId);
        
        // 保存新关联
        if (roleIds != null && !roleIds.isEmpty()) {
            List<RoleUserRel> relList = new ArrayList<>();
            for (String roleId : roleIds) {
                RoleUserRel rel = new RoleUserRel();
                rel.setUserId(userId);
                rel.setRoleId(roleId);
                relList.add(rel);
            }
            this.saveBatch(relList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserRoles(String userId)
    {
        QueryWrapper<RoleUserRel> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        this.remove(wrapper);
    }
}
