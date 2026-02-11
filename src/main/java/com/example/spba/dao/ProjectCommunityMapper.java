package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.entity.ProjectCommunity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProjectCommunityMapper extends BaseMapper<ProjectCommunity> {
    
    /**
     * 根据项目ID查询关联的小区列表
     * @param projectId 项目ID
     * @return 小区列表
     */
    @Select("SELECT community_id FROM project_community WHERE project_id = #{projectId}")
    List<String> selectCommunityIdsByProjectId(@Param("projectId") String projectId);
}