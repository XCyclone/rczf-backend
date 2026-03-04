package com.example.spba.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.dto.CommunityInfoQuery;
import com.example.spba.domain.entity.CommunityInfo;

import java.util.List;
import java.util.Map;

public interface CommunityInfoService {

//    List<Map<String,Object>>  selectCommunityInfoListByCommunityName(String communityName);
//
    /**
     * 分页查询小区列表
     * @param page 分页对象
     * @param communityName 小区名称
     * @return 分页结果
     */
    IPage<CommunityInfoQuery> selectCommunityInfoListByCommunityNameWithPage(IPage<CommunityInfoQuery> page, String communityName);

}
