package com.example.spba.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.dto.LoginDTO;
import com.example.spba.domain.entity.User;
import com.example.spba.domain.dto.UserDTO;
import com.example.spba.service.CaptchaService;
import com.example.spba.service.UserService;
import com.example.spba.service.RoleService;
import com.example.spba.service.RoleUserRelService;
import com.example.spba.utils.Function;
import com.example.spba.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Validated
@RestController
public class UserController
{

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleUserRelService roleUserRelService;

    @Autowired
    private CaptchaService captchaService;

    /**
     * 获取用户列表
     * @param username
     * @param role
     * @param status
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/users")
    public R getUserList(String username, String role, Integer status,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "15") Integer size)
    {
        HashMap where = new HashMap();
        where.put("username", username);
        where.put("role", role);
        where.put("status", status);

        Page<HashMap> pages = new Page<>(page, size);
        Page<HashMap> list = userService.getList(pages, where);

        return R.success(list);
    }

    /**
     * 获取用户详情
     * @param userId
     * @return
     */
    @GetMapping("/user/{id}")
    public R getUserInfo(@PathVariable("id") @NotBlank(message = "参数错误") String userId)
    {
        HashMap data = new HashMap();
        User info = userService.getById(userId);
        if (info != null) {
            data.put("id", info.getId());
            data.put("username", info.getUsername());
            data.put("status", info.getStatus());
            // 从 role_user_rel 表获取角色列表
            List<String> roleIds = roleUserRelService.getRoleIdsByUserId(userId);
            data.put("role", roleIds);
        }

        return R.success(data);
    }

    /**
     * 新增用户
     * @param form
     * @return
     */
    @PostMapping("/user")
    public R addUser(@Validated(UserDTO.Save.class) UserDTO form)
    {
        // 验证角色
        Boolean res = roleService.checkRole(form.getRoleIds());
        if (res.equals(false)) {
            return R.error();
        }

        HashMap where = new HashMap<>();
        where.put("username", form.getUsername());
        HashMap info = userService.getInfo(where);
        if (info != null) {
            return R.error("此名称已被使用，无法重复使用");
        }

        User user = new User();
        BeanUtils.copyProperties(form, user);
        user.setId(UUID.randomUUID().toString().replace("-", "")); // 生成32位UUID
        user.setSafe(Function.getRandomString(4));
        user.setPassword(DigestUtils.md5DigestAsHex((form.getPassword() + user.getSafe()).getBytes()));
        userService.save(user);

        // 保存角色关联
        if (StringUtils.hasText(form.getRoleIds())) {
            List<String> roleIdList = Arrays.stream(form.getRoleIds().split(","))
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());
            roleUserRelService.saveUserRoles(user.getId(), roleIdList);
        }

        return R.success();
    }

    /**
     * 编辑用户
     * @param form
     * @return
     */
    @PutMapping("/user")
    public R editUser(@Validated(UserDTO.Update.class) UserDTO form)
    {
        HashMap where = new HashMap<>();
        where.put("id", form.getId());
        HashMap info = userService.getInfo(where);
        if (info == null) {
            return R.error("用户不存在");
        }

        // 验证角色
        Boolean res = roleService.checkRole(form.getRoleIds());
        if (res.equals(false)) {
            return R.error();
        }

        User user = new User();
        BeanUtils.copyProperties(form, user);
        user.setUsername(null);

        if (form.getPassword().length() > 0) {
            String pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d\\W]{6,18}$";
            if (!Pattern.matches(pattern , form.getPassword())) {
                return R.error("密码必须包含字母和数字，且在6-18位之间");
            }
            user.setSafe(Function.getRandomString(4));
            user.setPassword(DigestUtils.md5DigestAsHex((form.getPassword() + user.getSafe()).getBytes()));
        } else {
            user.setPassword(null);
        }

        userService.updateById(user);

        // 更新角色关联
        if (StringUtils.hasText(form.getRoleIds())) {
            List<String> roleIdList = Arrays.stream(form.getRoleIds().split(","))
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());
            roleUserRelService.saveUserRoles(form.getId(), roleIdList);
        } else {
            roleUserRelService.removeUserRoles(form.getId());
        }

        if (form.getStatus().equals(0)) {
            StpUtil.logout(form.getId());
        }

        return R.success();
    }

    /**
     * 删除用户
     * @param userId
     * @return
     */
    @DeleteMapping("/user/{id}")
    public R delUser(@PathVariable("id") @NotBlank(message = "参数错误") String userId)
    {
        // 无法自我删除
        if (StpUtil.getLoginIdAsString().equals(userId)) {
            return R.error();
        }

        HashMap where = new HashMap<>();
        where.put("id", userId);
        HashMap info = userService.getInfo(where);
        if (info == null) {
            return R.error("用户不存在");
        }

        // 删除角色关联
        roleUserRelService.removeUserRoles(userId);

        Boolean res = userService.removeById(userId);
        if (res.equals(true)) {
            StpUtil.logout(userId);
            return R.success();
        }
        return R.error();
    }

    // ===================================login===================================

    /**
     * 密码登录
     * @param request
     * @param loginDTO
     * @return
     */
    @PostMapping("/login")
    public R login(HttpServletRequest request,
                   @Validated @RequestBody LoginDTO loginDTO)
    {
        // 验证验证码
        boolean isCaptchaValid = captchaService.validateCaptcha(loginDTO.getCaptchaId(), loginDTO.getCaptchaCode());
        if (!isCaptchaValid) {
            return R.error("验证码错误或已失效");
        }
        HashMap where = new HashMap<>();
        where.put("username", loginDTO.getUsername());
        where.put("password", loginDTO.getPassword());
        where.put("ip", ServletUtil.getClientIP(request));

        HashMap res = userService.checkLogin(where);
        if (res.get("status").equals(false)) {
            return R.error(res.get("message").toString());
        }
        JSONObject data = (JSONObject) JSONObject.toJSON(res.get("data"));

//        List<String> perms = new ArrayList<>();
//        List<HashMap> menus = userService.getPermissionList(StpUtil.getLoginIdAsString());
//        Iterator<HashMap> iterator = menus.iterator();
//        while (iterator.hasNext()) {
//            HashMap menu = iterator.next();
//            if (menu.get("perms") != null && menu.get("perms").toString().length() > 0 && menu.get("type").equals("F")) {
//                perms.add(menu.get("perms").toString());
//                iterator.remove();
//            }
//            menu.remove("sort");
//            menu.remove("status");
//            menu.remove("perms");
//            menu.remove("type");
//        }
//        List<Object> tree = Function.getTree(menus, 0);
//        data.put("menu", tree);
//        data.put("perms", perms);

        return R.success(data);
    }

    /**
     * 退出
     * @return
     */
    @GetMapping("/logout")
    public R logout()
    {
        StpUtil.logout();
        return R.success();
    }
}
