package com.example.spba.service;

import com.example.spba.utils.R;

/**
 * 选房相关服务接口
 * @author Generated
 */
public interface ChooseHouseService {
    
    /**
     * 判断当前用户是否在选房时间内
     * @param userId 用户 ID
     * @return true-在选房时间内，false-不在选房时间内
     */
    R judgeChooseHouseTime(String userId);
    
    /**
     * 判断当前企业用户是否在选房时间内
     * @param userId 企业用户 ID
     * @return true-在选房时间内，false-不在选房时间内
     */
    R enterpriseJudgeChooseHouseTime(String userId);
    
    /**
     * 获取个人选房信息
     * @param userId 用户 ID
     * @return 个人选房信息
     */
    R getUserChooseHouseInfo(String userId);
}