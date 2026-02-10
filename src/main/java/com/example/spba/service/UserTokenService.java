package com.example.spba.service;

import com.example.spba.domain.entity.UserInfo;

/**
 * 用户信息服务接口
 */
public interface UserTokenService {
    
    /**
     * 根据token获取用户完整信息
     * @param token 用户token
     * @return 用户信息实体
     * @throws RuntimeException 当token无效或用户不存在时抛出异常
     */
    UserInfo getUserInfoByToken(String token) throws RuntimeException;
    
    /**
     * 根据用户ID获取用户完整信息
     * @param userId 用户ID
     * @return 用户信息实体
     * @throws RuntimeException 当用户不存在时抛出异常
     */
    UserInfo getUserInfoById(String userId) throws RuntimeException;
    
    /**
     * 验证token是否有效且未过期
     * @param token 用户token
     * @return 是否有效
     */
    boolean isValidToken(String token);
    
    /**
     * 获取token的剩余有效时间（秒）
     * @param token 用户token
     * @return 剩余有效时间，-1表示已过期，-2表示token无效
     */
    long getTokenExpireTime(String token);
}