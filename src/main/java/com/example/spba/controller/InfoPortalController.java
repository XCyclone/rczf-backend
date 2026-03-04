package com.example.spba.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.controller.base.BaseController;
import com.example.spba.domain.entity.InfoPortalContent;
import com.example.spba.service.InfoPortalService;
import com.example.spba.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/infoPartal")
public class InfoPortalController {

    private static final Logger logger = LoggerFactory.getLogger(InfoPortalController.class);

    @Autowired
    private InfoPortalService infoPortalService;

    /**
     * 查询通知发布列表
     * POST /api/infoPartal/content/list
     * Request Body: {"startPublishDate": "2024-01-01", "endPublishDate": "2024-12-31", "pageNum": 1, "pageSize": 10}
     */
    @PostMapping("/content/list")
    public R list(@RequestBody(required = false) Map<String, Object> params) {
        try {
            // 从请求参数中获取值
            String startPublishDate = params != null && params.containsKey("startPublishDate") 
                    ? params.get("startPublishDate").toString() : null;
            String endPublishDate = params != null && params.containsKey("endPublishDate") 
                    ? params.get("endPublishDate").toString() : null;
            Integer pageNum = params != null && params.containsKey("pageNum") 
                    ? Integer.parseInt(params.get("pageNum").toString()) : 1;
            Integer pageSize = params != null && params.containsKey("pageSize") 
                    ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            

            
            logger.info("[通知发布列表查询] startPublishDate: {}, endPublishDate: {}, pageNum: {}, pageSize: {}",
                       startPublishDate, endPublishDate, pageNum, pageSize);
            
            // 执行分页查询
            IPage<InfoPortalContent> page = new Page<>(pageNum, pageSize);
            IPage<InfoPortalContent> resultPage = infoPortalService.selectInfoPortalByDateWithPage(page, startPublishDate, endPublishDate, null);
            
            logger.info("[通知发布列表查询] 查询完成，总记录数：{}, 当前页记录数：{}", 
                       resultPage.getTotal(), resultPage.getRecords().size());
            
            return R.success(resultPage);
        } catch (Exception e) {
            logger.error("[通知发布列表查询] 查询失败，异常：{}", e.getMessage(), e);
            return R.error("查询失败：" + e.getMessage());
        }
    }

}
