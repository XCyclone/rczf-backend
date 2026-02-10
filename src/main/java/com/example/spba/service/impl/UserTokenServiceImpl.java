package com.example.spba.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.spba.dao.UserMapper;
import com.example.spba.domain.entity.Menu;
import com.example.spba.domain.entity.Role;
import com.example.spba.domain.entity.UserInfo;
import com.example.spba.domain.entity.User;
import com.example.spba.service.MenuService;
import com.example.spba.service.RoleService;
import com.example.spba.service.RoleUserRelService;
import com.example.spba.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息服务实现类
 */
@Service
public class UserTokenServiceImpl implements UserTokenService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleUserRelService roleUserRelService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @Override
    public UserInfo getUserInfoByToken(String token) throws RuntimeException {
        // 验证token有效性
        if (!isValidToken(token)) {
            throw new RuntimeException("Token无效或已过期");
        }

        // 从Sa-Token中获取用户ID
        String userId;
        try {
            // 设置临时token上下文来获取登录ID
            StpUtil.checkLogin();
            userId = StpUtil.getLoginIdAsString();
        } catch (Exception e) {
            throw new RuntimeException("无法从token获取用户信息: " + e.getMessage());
        }

        return getUserInfoById(userId);
    }

    @Override
    public UserInfo getUserInfoById(String userId) throws RuntimeException {
        if (!StringUtils.hasText(userId)) {
            throw new RuntimeException("用户ID不能为空");
        }

        // 查询用户基本信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 构建用户信息实体
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setStatus(user.getStatus());
        userInfo.setLoginIp(user.getLoginIp());
        userInfo.setLoginTime(user.getLoginTime());
        userInfo.setCreateTime(user.getCreateTime());

        // 获取用户角色信息
        List<String> roleIds = roleUserRelService.getRoleIdsByUserId(userId);
        if (!CollectionUtils.isEmpty(roleIds)) {
            userInfo.setRoleIds(roleIds);
            
            // 获取角色名称
            Collection<Role> rolesCollection = roleService.listByIds(roleIds);
            List<Role> roles = new ArrayList<>(rolesCollection);
            if (!CollectionUtils.isEmpty(roles)) {
                List<String> roleNames = roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
                userInfo.setRoleNames(roleNames);
            }
        }

//        // 获取用户权限信息
//        List<String> permissions = getMenuPermissionsByUserId(userId);
//        userInfo.setPermissions(permissions);
//
//        // 获取用户菜单权限
//        List<Menu> menus = getMenuListByUserId(userId);
//        userInfo.setMenus(menus);

        return userInfo;
    }

    @Override
    public boolean isValidToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            // 使用Sa-Token验证token
            return StpUtil.getLoginIdByToken(token) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getTokenExpireTime(String token) {
        if (!StringUtils.hasText(token)) {
            return -2; // token无效
        }

        try {
            // 获取token剩余有效期
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId != null) {
                return 3600L; // 默认返回1小时（示例值）
            }
            return -2; // token无效
        } catch (Exception e) {
            return -2; // token无效
        }
    }

    /**
     * 根据用户ID获取菜单权限标识列表
     * @param userId 用户ID
     * @return 权限标识列表
     */
    private List<String> getMenuPermissionsByUserId(String userId) {
        List<String> permissions = new ArrayList<>();
        
        // 查询用户拥有的角色
        List<String> roleIds = roleUserRelService.getRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return permissions;
        }

        // 查询角色对应的菜单权限
        for (String roleId : roleIds) {
            QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", roleId);
            queryWrapper.isNotNull("perms");
            queryWrapper.ne("perms", "");
            
            List<Menu> menus = menuService.list(queryWrapper);
            menus.forEach(menu -> {
                if (StringUtils.hasText(menu.getPerms())) {
                    permissions.add(menu.getPerms());
                }
            });
        }

        return permissions;
    }

    /**
     * 根据用户ID获取菜单列表
     * @param userId 用户ID
     * @return 菜单列表
     */
    private List<Menu> getMenuListByUserId(String userId) {
        // 查询用户拥有的角色
        List<String> roleIds = roleUserRelService.getRoleIdsByUserId(userId);
        if (CollectionUtils.isEmpty(roleIds)) {
            return new ArrayList<>();
        }

        // 获取角色对应的所有菜单
        List<Menu> allMenus = new ArrayList<>();
        for (String roleId : roleIds) {
            // 这里可以根据实际业务逻辑查询角色对应的菜单
            // 暂时返回所有启用的菜单作为示例
            QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", 1); // 只查询启用的菜单
            queryWrapper.orderByAsc("sort");
            
            List<Menu> menus = menuService.list(queryWrapper);
            allMenus.addAll(menus);
        }

        // 去重并排序
        return allMenus.stream()
            .distinct()
            .sorted((m1, m2) -> {
                int sort1 = m1.getSort() != null ? m1.getSort() : 0;
                int sort2 = m2.getSort() != null ? m2.getSort() : 0;
                return Integer.compare(sort1, sort2);
            })
            .collect(Collectors.toList());
    }
}