package com.example.spba.controller.demo;

import com.example.spba.controller.base.BaseController;
import com.example.spba.domain.entity.UserInfo;
import com.example.spba.utils.R;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * RequestAttribute使用演示Controller
 * 展示如何通过@RequestAttribute注解获取用户信息
 */
@RestController
@RequestMapping("/demo/request-attr")
public class RequestAttributeDemoController extends BaseController {

    /**
     * 通过@RequestAttribute获取用户信息
     * @param userInfo 通过@RequestAttribute注入的用户信息
     * @return 用户信息
     */
    @GetMapping("/user-info")
    public R getUserInfoByRequestAttribute(@RequestAttribute("currentUserInfo") UserInfo userInfo) {
        return R.success(userInfo);
    }

    /**
     * 通过@RequestAttribute获取用户ID
     * @param userId 通过@RequestAttribute注入的用户ID
     * @return 用户ID
     */
    @GetMapping("/user-id")
    public R getUserIdByRequestAttribute(@RequestAttribute("currentUserId") String userId) {
        return R.success("当前用户ID: " + userId);
    }

    /**
     * 通过@RequestAttribute获取用户名
     * @param username 通过@RequestAttribute注入的用户名
     * @return 用户名
     */
    @GetMapping("/username")
    public R getUsernameByRequestAttribute(@RequestAttribute("currentUsername") String username) {
        return R.success("当前用户名: " + username);
    }

    /**
     * 同时获取多个RequestAttribute
     * @param userInfo 用户信息
     * @param userId 用户ID
     * @param username 用户名
     * @return 组合信息
     */
    @GetMapping("/combined")
    public R getCombinedInfo(
            @RequestAttribute("currentUserInfo") UserInfo userInfo,
            @RequestAttribute("currentUserId") String userId,
            @RequestAttribute("currentUsername") String username) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("userInfo", userInfo);
        result.put("userId", userId);
        result.put("username", username);
        result.put("message", "通过@RequestAttribute成功获取用户信息");
        
        return R.success(result);
    }

    /**
     * 测试可选的RequestAttribute（使用required=false）
     * @param userInfo 可选的用户信息
     * @return 结果
     */
    @GetMapping("/optional")
    public R getOptionalUserInfo(@RequestAttribute(value = "currentUserInfo", required = false) UserInfo userInfo) {
        if (userInfo == null) {
            return R.success("未获取到用户信息（可能是在公共接口中）");
        }
        return R.success("获取到用户信息: " + userInfo.getUsername());
    }
}