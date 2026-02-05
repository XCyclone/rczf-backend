package com.example.spba.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spba.domain.entity.RoleUserRel;

import java.util.List;

public interface RoleUserRelService extends IService<RoleUserRel>
{
    /**
     * 根据用户ID获取角色ID列表
     * @param userId
     * @return
     */
    List<String> getRoleIdsByUserId(String userId);

    /**
     * 保存用户角色关联
     * @param userId
     * @param roleIds
     */
    void saveUserRoles(String userId, List<String> roleIds);

    /**
     * 删除用户的所有角色关联
     * @param userId
     */
    void removeUserRoles(String userId);
}
