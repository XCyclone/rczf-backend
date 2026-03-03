package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.entity.CommunityInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 小区信息 Mapper 接口
 */
@Mapper
public interface CommunityInfoMapper extends BaseMapper<CommunityInfo> {
    
    /**
     * 根据ID列表查询小区信息
     * @param ids 小区ID列表
     * @return 小区信息列表
     */
    @Select("<script>" +
            "SELECT id, community_name FROM community_info " +
            "WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<CommunityInfo> selectByIds(@Param("ids") List<String> ids);
}