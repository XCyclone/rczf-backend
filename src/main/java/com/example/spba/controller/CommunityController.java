package com.example.spba.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.controller.base.BaseController;
import com.example.spba.domain.dto.CommunityInfoQuery;
import com.example.spba.service.CommunityInfoService;
import com.example.spba.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 小区管理
 *
 *
 */
@RestController
@RequestMapping("/api/community")
public class CommunityController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(CommunityController.class);
    
    @Autowired
    private CommunityInfoService communityInfoService;


    /**
     * 分页查询小区列表
     * POST /api/community/list
     * Request Body: {"communityName": "xxx", "pageNum": 1, "pageSize": 10}
     */
    @PostMapping("/list")
    public R list(@RequestBody(required = false) Map<String, Object> params) {
        try {
            // 从请求参数中获取值，支持旧格式和新格式
            String communityName = params != null && params.containsKey("communityName") 
                    ? params.get("communityName").toString() : "";
            Integer pageNum = params != null && params.containsKey("pageNum") 
                    ? Integer.parseInt(params.get("pageNum").toString()) : 1;
            Integer pageSize = params != null && params.containsKey("pageSize") 
                    ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            
            logger.info("[小区列表查询] communityName: {}, pageNum: {}, pageSize: {}", communityName, pageNum, pageSize);
            
            // 构建分页对象
            IPage<CommunityInfoQuery> page = new Page<>(pageNum, pageSize);
            
            // 执行分页查询
            IPage<CommunityInfoQuery> resultPage = communityInfoService.selectCommunityInfoListByCommunityNameWithPage(page, communityName);
            
            logger.info("[小区列表查询] 查询完成，总记录数：{}, 当前页记录数：{}", 
                       resultPage.getTotal(), resultPage.getRecords().size());
            
            return R.success(resultPage);
        } catch (Exception e) {
            logger.error("[小区列表查询] 查询失败，异常：{}", e.getMessage(), e);
            return R.error("查询失败：" + e.getMessage());
        }
    }


}
