package com.example.spba.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.entity.Role;
import com.example.spba.domain.entity.RoleUserRel;
import com.example.spba.domain.dto.RoleDTO;
import com.example.spba.service.UserService;
import com.example.spba.service.RoleService;
import com.example.spba.service.RoleUserRelService;
import com.example.spba.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;

@Validated
@RestController
public class RoleController
{

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleUserRelService roleUserRelService;

    /**
     * 获取角色列表
     * @param name
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/roles")
    public R getRoleList(String name,
                         @RequestParam(name = "page", defaultValue = "1") Integer page,
                         @RequestParam(name = "size", defaultValue = "15") Integer size)
    {
        HashMap where = new HashMap();
        where.put("name", name);

        Page<HashMap> pages = new Page<>(page, size);
        Page<HashMap> list = roleService.getList(pages, where);

        return R.success(list);
    }

    /**
     * 获取角色详情
     * @param roleId
     * @return
     */
    @GetMapping("/role/{id}")
    public R getRoleInfo(@PathVariable("id") @NotBlank(message = "参数错误") String roleId)
    {
        HashMap data = new HashMap();
        Role info = roleService.getById(roleId);
        if (info != null) {
            data.put("roleId", info.getRoleId());
            data.put("name", info.getName());
        }

        return R.success(data);
    }

    /**
     * 新增角色
     * @param form
     * @return
     */
    @PostMapping("/role")
    public R addRole(@Validated(RoleDTO.Save.class) RoleDTO form)
    {
        // 检查角色ID是否已存在
        Role existRole = roleService.getById(form.getRoleId());
        if (existRole != null) {
            return R.error("角色ID已存在");
        }

        Role role = new Role();
        role.setRoleId(form.getRoleId());
        role.setName(form.getName());
        roleService.save(role);

        return R.success();
    }

    /**
     * 编辑角色
     * @param form
     * @return
     */
    @PutMapping("/role")
    public R editRole(@Validated(RoleDTO.Update.class) RoleDTO form)
    {
        Role info = roleService.getById(form.getRoleId());
        if (info == null) {
            return R.error("角色不存在");
        }

        Role role = new Role();
        role.setRoleId(form.getRoleId());
        role.setName(form.getName());
        roleService.updateById(role);

        return R.success();
    }

    /**
     * 删除角色
     * @param roleId
     * @return
     */
    @DeleteMapping("/role/{id}")
    public R delRole(@PathVariable("id") @NotBlank(message = "参数错误") String roleId)
    {
        Role info = roleService.getById(roleId);
        if (info == null) {
            return R.error("角色不存在");
        }

        List<HashMap> users = userService.getRoleUserAll(roleId);
        if (users.size() > 0) {
            return R.error("有用户使用此角色，无法删除");
        }

        // 删除角色关联关系
        QueryWrapper<RoleUserRel> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", roleId);
        roleUserRelService.remove(wrapper);

        Boolean res = roleService.removeById(roleId);
        if (res.equals(true)) {
            return R.success();
        }
        return R.error();
    }
}
