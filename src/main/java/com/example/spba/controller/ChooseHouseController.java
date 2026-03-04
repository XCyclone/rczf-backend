package com.example.spba.controller;

import com.example.spba.controller.base.BaseController;
import com.example.spba.domain.dto.ChooseHouseTask;
import com.example.spba.domain.entity.CommunityInfo;
import com.example.spba.service.ChooseHouseService;
import com.example.spba.utils.R;
import com.example.spba.utils.UserContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.spba.utils.RequestAttributeUtil.CURRENT_USER_ID;

/**
 * 选房任务控制器
 */
@RestController
@RequestMapping("/choose-house")
public class ChooseHouseController  {

    private static final Logger logger = LoggerFactory.getLogger(ChooseHouseController.class);

    @Autowired
    private ChooseHouseService chooseHouseService;

    /**
     * 检查用户选房资格
     * GET /api/choose-house/check-qualification
     * 
     * @return 选房任务信息
     */
    @PostMapping("/check-qualification")
    public R checkQualification(@RequestAttribute(CURRENT_USER_ID) String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                logger.warn("[选房资格检查] 未登录或用户 ID 为空");
                return R.error("请先登录");
            }

            logger.info("[选房资格检查] userId: {}", userId);
            
            // 调用服务检查选房资格
            ChooseHouseTask task = chooseHouseService.checkUserChooseHouseQualification(userId);
            
            logger.info("[选房资格检查] 检查结果 - isApproved: {}, isSelectionTime: {}", 
                       task.getIsApproved(), task.getIsSelectionTime());
            
            return R.success(task);
            
        } catch (Exception e) {
            logger.error("[选房资格检查] 检查失败，error: {}", e.getMessage(), e);
            return R.error("检查失败：" + e.getMessage());
        }
    }




}
