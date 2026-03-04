package com.example.spba.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.controller.base.BaseController;
import com.example.spba.domain.dto.FileInfoQuery;
import com.example.spba.service.FileService;
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
@RequestMapping("/api/profile")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;
    /**
     * 查询上传文件信息
     * POST /api/profile/list
     * Request Body: {"relationId": "xxx", "pageNum": 1, "pageSize": 10}
     */
    @PostMapping("/list")
    public R listFiles(@RequestBody(required = false) Map<String, Object> params) {
        try {
            // 从请求参数中获取 relationId
            String relationId = null;
            if (params != null && params.containsKey("relationId")) {
                relationId = params.get("relationId").toString();
            }
            
            if (relationId == null || "".equals(relationId.trim())) {
                logger.warn("[文件列表查询] 请求参数缺失 relationId");
                return R.error("请求参数缺失");
            }
            
            // 获取分页参数（支持不分页的情况）
            Integer pageNum = params.containsKey("pageNum")
                    ? Integer.parseInt(params.get("pageNum").toString()) : 1;
            Integer pageSize = params.containsKey("pageSize")
                    ? Integer.parseInt(params.get("pageSize").toString()) : 10;
            
            logger.info("[文件列表查询] relationId: {}, pageNum: {}, pageSize: {}", relationId, pageNum, pageSize);
            
            // 执行分页查询
            IPage<FileInfoQuery> page = new Page<>(pageNum, pageSize);
            IPage<FileInfoQuery> resultPage = fileService.selectFileInfoListByRelationIdWithPage(page, relationId);
            
            logger.info("[文件列表查询] 查询完成，总记录数：{}, 当前页记录数：{}", 
                       resultPage.getTotal(), resultPage.getRecords().size());
            
            return R.success(resultPage);
        } catch (Exception e) {
            logger.error("[文件列表查询] 查询失败，异常：{}", e.getMessage(), e);
            return R.error("查询失败：" + e.getMessage());
        }
    }

}
