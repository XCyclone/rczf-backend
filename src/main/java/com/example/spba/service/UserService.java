package com.example.spba.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spba.domain.entity.User;

import java.util.HashMap;
import java.util.List;

public interface UserService extends IService<User>
{

    /**
     * 登录验证
     * @param params
     * @return
     */
    public HashMap checkLogin(HashMap params);

    /**
     * 获取用户列表（分页）
     * @param page
     * @param params
     * @return
     */
    public Page<HashMap> getList(Page page, HashMap params);

    /**
     * 根据条件获取详情
     * @param params
     * @return
     */
    public HashMap getInfo(HashMap params);

    /**
     * 获取拥有某角色的用户列表
     * @param roleId
     * @return
     */
    public List<HashMap> getRoleUserAll(String roleId);

    /**
     * 获取用户的权限列表
     * @param userId
     * @return
     */
    public List<HashMap> getPermissionList(String userId);
}
