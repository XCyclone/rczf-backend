package com.example.spba.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spba.domain.entity.Role;
import com.example.spba.dao.RoleMapper;
import com.example.spba.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService
{

    @Override
    public Page<HashMap> getList(Page page, HashMap params) {
        return this.baseMapper.getList(page, params);
    }

    @Override
    public Boolean checkRole(String role)
    {
        // 新结构中不再有 root 字段，此方法保留用于兼容，直接返回 true
        // 如果需要验证角色，可以在这里添加其他验证逻辑
        return true;
    }

    @Override
    public List<Role> getAll(HashMap params) {
        return this.baseMapper.getAll(params);
    }
}
