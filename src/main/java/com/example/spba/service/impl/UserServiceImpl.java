package com.example.spba.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spba.constant.ProjectConstants;
import com.example.spba.dao.*;
import com.example.spba.domain.entity.*;
import com.example.spba.service.UserService;
import com.example.spba.service.LoginLogService;
import com.example.spba.service.MenuService;
import com.example.spba.service.RoleUserRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService
{

    @Autowired
    private MenuService menuService;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private RoleUserRelService roleUserRelService;

    @Autowired
    private BusinessEnterpriseMapper businessEnterpriseMapper;

    @Autowired
    private BusinessUserMapper businessUserMapper;

    @Autowired
    private BusinessEnterpriseApplyMapper businessEnterpriseApplyMapper;

    @Autowired
    private BusinessUserApplyMapper businessUserApplyMapper;


    @Override
    public HashMap checkLogin(HashMap params)
    {
        HashMap result = new HashMap();
        result.put("status", false);
        String name = null;

        HashMap info = this.baseMapper.getInfo(params);
        if (params.get("type").equals(1)){
            BusinessEnterprise businessEnterprise = businessEnterpriseMapper.selectById(info.get("id").toString());
            if(businessEnterprise == null){
                QueryWrapper<BusinessEnterpriseApply> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("enterprise_id", info.get("id").toString());
                if(businessEnterpriseApplyMapper.selectOne(queryWrapper) != null){
                    result.put("message", "已申请，请等待审批");
                }
                else {
                    result.put("message", "无此用户");
                }
                return result;
            }
            else{
                name = businessEnterprise.getEnterpriseName();
            }
        }else {
            BusinessUser businessUser = businessUserMapper.selectById(info.get("id").toString());
            if(businessUser == null){
                QueryWrapper<BusinessUserApply> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("company_id", info.get("id").toString());
                if(businessUserApplyMapper.selectOne(queryWrapper) != null){
                    result.put("message", "已申请，请等待审批");
                }
                else {
                    result.put("message", "无此用户");
                }
                return result;
            }
            else{
                name = businessUser.getName();
            }
        }
        if (info == null || info.get("status").equals(0)) {
            result.put("message", "登录失败");
            return result;
        }
        if (!DigestUtils.md5DigestAsHex((params.get("password") + info.get("safe").toString()).getBytes()).equals(info.get("password"))) {
            result.put("message", "密码错误");
            return result;
        }

        // 验证是否有角色（从 role_user_rel 表获取）
        String roleStr = info.get("role") != null ? info.get("role").toString() : "";
        if (!StringUtils.hasText(roleStr)) {
            result.put("message", "登录失败：用户未分配角色");
            return result;
        }


        // 登录
        StpUtil.login(info.get("id"));

        // 更新登录信息
        updateLogin(info.get("id").toString(), params.get("ip").toString());

        HashMap data = new HashMap<>();
//        data.put("avatar", info.get("avatar"));
        data.put("userId", info.get("username"));
        data.put("token", StpUtil.getTokenValue());
        data.put("name", name);
        result.put("data", data);
        result.put("status", true);

        return result;
    }

    @Override
    public Page<HashMap> getList(Page page, HashMap params) {
        return this.baseMapper.getList(page, params);
    }

    @Override
    public HashMap getInfo(HashMap params) {
        return this.baseMapper.getInfo(params);
    }

    @Override
    public List<HashMap> getRoleUserAll(String roleId) {
        return this.baseMapper.getRoleUserAll(roleId);
    }

    @Override
    public List<HashMap> getPermissionList(String userId)
    {
        List<HashMap> list = new ArrayList<>();
        User user = this.getById(userId);
        if (user.getStatus().equals(0)) {
            return list;
        }

        // 从 role_user_rel 表获取角色ID列表
        List<String> roleIds = roleUserRelService.getRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return list;
        }

        // 新结构中 role 表没有 permission 字段，权限系统需要重新设计
        // 这里暂时返回空列表，或者可以根据业务需求从其他地方获取权限
        // 如果需要保留原有权限逻辑，可能需要将权限信息存储在其他表中
        
        HashMap query = new HashMap();
        query.put("status", ProjectConstants.UserStatus.NORMAL);
        // 由于新结构中没有 permission 字段，这里暂时返回所有菜单
        // 实际业务中可能需要根据 roleIds 从其他表获取权限信息
        List<HashMap> menus = menuService.getAll(query);

        return menus;
    }

    private void updateLogin(String id, String ip)
    {
        User update = new User();
        update.setId(id);
        update.setLoginIp(ip);
        update.setLoginTime(new Date());
        this.baseMapper.updateById(update);

        LoginLog log = new LoginLog();
        // LoginLog 的 userId 是 Integer，需要转换
        try {
            log.setUserId(id);
        } catch (NumberFormatException e) {
            // 如果 id 不是数字，使用 hashcode 或其他方式
            log.setUserId(String.valueOf(id.hashCode()));
        }
        log.setLoginIp(ip);
        loginLogService.save(log);
    }
}
