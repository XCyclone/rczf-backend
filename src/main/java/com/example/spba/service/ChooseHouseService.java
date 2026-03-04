package com.example.spba.service;

import com.example.spba.domain.dto.ChooseHouseTask;
import com.example.spba.domain.entity.CommunityInfo;

import java.util.List;

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

    /**
     * 查询用户可参与选房的小区信息列表
     * @param userId 用户 ID
     * @return 小区信息列表
     */
    List<CommunityInfo> queryAvailableCommunitiesForUser(String userId);
}
