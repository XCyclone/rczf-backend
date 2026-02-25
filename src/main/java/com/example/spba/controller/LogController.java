package com.example.spba.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.service.ErrorLogService;
import com.example.spba.service.LoginLogService;
import com.example.spba.service.OperateLogService;
import com.example.spba.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class LogController
{
    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private ErrorLogService errorLogService;

    @Autowired
    private OperateLogService operateLogService;

    /**
     * 获取登录日志列表
     * @param username
     * @param start
     * @param end
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/login/logs")
    public R getLoginLogList(String username, String start, String end,
                             @RequestParam(name = "page", defaultValue = "1") Integer page,
                             @RequestParam(name = "size", defaultValue = "15") Integer size)
    {
        logger.info("[登录日志查询] username: {}, start: {}, end: {}, page: {}, size: {}", username, start, end, page, size);
        
        HashMap where = new HashMap();
        where.put("username", username);
        where.put("start", start);
        where.put("end", end);

        Page<HashMap> pages = new Page<>(page, size);
        Page<HashMap> list = loginLogService.getList(pages, where);
        
        logger.info("[登录日志查询] 查询完成，返回记录数: {}", list.getRecords().size());
        return R.success(list);
    }

    /**
     * 获取错误日志列表
     * @param start
     * @param end
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/error/logs")
    public R getErrorLogList(String start, String end,
                             @RequestParam(name = "page", defaultValue = "1") Integer page,
                             @RequestParam(name = "size", defaultValue = "15") Integer size)
    {
        logger.info("[错误日志查询] start: {}, end: {}, page: {}, size: {}", start, end, page, size);
        
        HashMap where = new HashMap();
        where.put("start", start);
        where.put("end", end);

        Page<HashMap> pages = new Page<>(page, size);
        Page<HashMap> list = errorLogService.getList(pages, where);
        
        logger.info("[错误日志查询] 查询完成，返回记录数: {}", list.getRecords().size());
        return R.success(list);
    }

    /**
     * 获取操作日志列表
     * @param username
     * @param start
     * @param end
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/operate/logs")
    public R getOperateLogList(String username, String start, String end,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "15") Integer size)
    {
        logger.info("[操作日志查询] username: {}, start: {}, end: {}, page: {}, size: {}", username, start, end, page, size);
        
        HashMap where = new HashMap();
        where.put("username", username);
        where.put("start", start);
        where.put("end", end);

        Page<HashMap> pages = new Page<>(page, size);
        Page<HashMap> list = operateLogService.getList(pages, where);
        
        logger.info("[操作日志查询] 查询完成，返回记录数: {}", list.getRecords().size());
        return R.success(list);
    }
}
