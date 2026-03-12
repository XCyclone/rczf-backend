package com.example.spba.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.dto.ChooseHouseSubmitDTO;
import com.example.spba.domain.dto.ChooseHouseTask;
import com.example.spba.domain.dto.HouseInfoDTO;
import com.example.spba.domain.dto.HouseInfoQueryDTO;

import java.util.List;
import java.util.Map;

/**
 * 选房任务服务接口
 */
public interface ChooseHouseService {

    /**
     * 判断用户是否有选房资格并返回选房任务信息
     * @param userId 用户 ID
     * @return 选房任务信息
     */
    ChooseHouseTask checkUserChooseHouseQualification(String userId);

//    /**
//     * 查询用户可参与选房的小区信息列表
//     * @param userId 用户 ID
//     * @return 小区信息列表
//     */
//    List<CommunityInfo> queryAvailableCommunitiesForUser(String userId);
    
    /**
     * 查询位置标签列表
     * @return 位置标签列表，格式为 [{"text": "xx", "value": "xx"}]
     */
    List<Map<String, String>> queryLocationTags();
    
    /**
     * 分页查询可选房屋信息列表
     * @param queryDTO 查询条件
     * @return 分页结果，包含 list 和 total
     */
    Page<HouseInfoDTO> queryHouseInfoList(HouseInfoQueryDTO queryDTO);
  
  /**
   * 提交选房申请
   * @param userId 用户 ID
   * @param submitDTO 选房提交请求
   * @return 选房记录 ID
   */
  String submitChooseHouse(String userId, ChooseHouseSubmitDTO submitDTO);
  
  /**
   * 查询选房记录列表
   * @param userId 用户 ID
   * @return 选房记录列表，未找到返回空列表
   */
  List<HouseInfoDTO> queryChooseHouseRecordList(String userId);
}
