package com.example.spba.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.dto.*;
import com.example.spba.domain.entity.BusinessUser;
import com.example.spba.service.ChooseHouseService;
import com.example.spba.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.example.spba.utils.RequestAttributeUtil.CURRENT_USER_ID;

/**
 * 选房任务控制器
 */
@RestController
@RequestMapping("/choose/house")
public class ChooseHouseController {

    private static final Logger logger = LoggerFactory.getLogger(ChooseHouseController.class);

    @Autowired
    private ChooseHouseService chooseHouseService;

    /**
     * 检查用户选房资格
     * POST /api/choose-house/check-qualification
     *
     * @return 选房任务信息
     */
    @PostMapping("/check/qualification")
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

    /**
     * 查询位置标签列表
     * POST /choose/house/query/locTags
     *
     * @return 位置标签列表，格式为 [{"text": "xx", "value": "xx"}]
     */
    @PostMapping("/query/locTags")
    public R queryLocationTags() {
        try {
            logger.info("[查询位置标签] 开始查询位置标签列表");

            // 调用服务查询位置标签
            List<Map<String, String>> tags = chooseHouseService.queryLocationTags();

            logger.info("[查询位置标签] 查询成功，共找到 {} 个位置标签", tags.size());
            return R.success(tags);

        } catch (Exception e) {
            logger.error("[查询位置标签] 查询失败，error: {}", e.getMessage(), e);
            return R.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询可选房屋信息列表
     * POST /choose/house/query/house/info
     *
     * @param queryDTO 查询条件
     * @return 分页结果，包含 list 和 total
     */
    @PostMapping("/query/house/info")
    public R queryHouseInfoList(@RequestBody HouseInfoQueryDTO queryDTO) {
        try {
            logger.info("[查询房屋信息] 开始查询，projectId: {}, page: {}, size: {}",
                    queryDTO.getProjectId(), queryDTO.getPage(), queryDTO.getSize());

            // 调用服务查询房屋信息
            Page<HouseInfoDTO> page = chooseHouseService.queryHouseInfoList(queryDTO);

            logger.info("[查询房屋信息] 查询成功，共找到 {} 条记录", page.getRecords().size());
            return R.success(page);

        } catch (Exception e) {
            logger.error("[查询房屋信息] 查询失败，error: {}", e.getMessage(), e);
            return R.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 提交选房申请
     * POST /choose/house/submit
     *
     * @param submitDTO 选房提交请求
     * @return 选房记录 ID
     */
    @PostMapping("/submit")
    public R submitChooseHouse(@RequestAttribute(CURRENT_USER_ID) String userId, @RequestBody ChooseHouseSubmitDTO submitDTO) {
        try {
            if (userId == null || userId.isEmpty()) {
                logger.warn("[提交选房] 未登录或用户 ID 为空");
                return R.error("请先登录");
            }

            logger.info("[提交选房] userId: {}, projectId: {}, houseId: {}, choiceSrc: {}",
                    userId, submitDTO.getProjectId(), submitDTO.getHouseId(), submitDTO.getChoiceSrc());
            // 调用服务提交选房申请
            String houseChoiceId = chooseHouseService.submitChooseHouse(userId, submitDTO);

            logger.info("[提交选房] 选房成功，houseChoiceId: {}", houseChoiceId);
            return R.success(houseChoiceId);

        } catch (Exception e) {
            logger.error("[提交选房] 提交失败，userId: {}, error: {}", userId, e.getMessage(), e);
            return R.error("提交失败：" + e.getMessage());
        }
    }

    /**
   * 查询选房记录列表
   * POST /choose/house/query/record
   *
   * @param userId 用户 ID
   * @return 选房记录列表，未找到返回空列表
   */
  @PostMapping("/query/record")
 public R queryChooseHouseRecordList(@RequestAttribute(CURRENT_USER_ID) String userId) {
     try {
         logger.info("[查询选房记录列表] userId: {}", userId);
            
         if (userId == null || userId.isEmpty()) {
           logger.warn("[查询选房记录列表] 用户为空");
           return R.error("用户不能为空");
         }
          
           // 调用服务查询选房记录列表
           List<HouseInfoDTO> recordList = chooseHouseService.queryChooseHouseRecordList(userId);
            
       logger.info("[查询选房记录列表] 查询成功，userId: {}, 记录数：{}", userId, recordList.size());
       return R.success(recordList);
            
      } catch (Exception e) {
       logger.error("[查询选房记录列表] 查询失败，userId: {}, error: {}", userId, e.getMessage(), e);
       return R.error("查询失败：" + e.getMessage());
     }
  }
}
