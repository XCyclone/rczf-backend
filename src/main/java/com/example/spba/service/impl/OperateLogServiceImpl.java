package com.example.spba.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spba.domain.entity.User;
import com.example.spba.domain.entity.OperateLog;
import com.example.spba.dao.OperateLogMapper;
import com.example.spba.service.UserService;
import com.example.spba.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class OperateLogServiceImpl extends ServiceImpl<OperateLogMapper, OperateLog> implements OperateLogService
{

    @Autowired
    private UserService userService;

    @Override
    public void save(String userId, String url, String method, String params, String ip) {
        User user = userService.getById(userId);
        OperateLog log = new OperateLog();
        // OperateLog 的 adminId 字段类型是 Integer，需要转换
        try {
            log.setUserId(userId);
        } catch (NumberFormatException e) {
            log.setUserId(String.valueOf(userId.hashCode()));
        }
        log.setUsername(user.getUsername());
        log.setUrl(url);
        log.setMethod(method);
        log.setParams(params);
        log.setIp(ip);
        this.save(log);
    }

    @Override
    public Page<HashMap> getList(Page page, HashMap params) {
        return this.baseMapper.getList(page, params);
    }
}
