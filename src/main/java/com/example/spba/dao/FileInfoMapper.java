package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.dto.FileInfoQuery;
import com.example.spba.domain.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    /**
     * 根据 ID 查询项目
     */
    @Select("SELECT id, relation_id as relationId, original_name as originalName, file_url as fileUrl, file_type as fileType,file_category as fileCategory FROM file_info WHERE relation_id = #{id}")
    List<FileInfo> selectByRelId(@Param("id") String id);
    
    /**
     * 分页查询文件列表
     */
    @Select("SELECT id, relation_id as relationId, original_name as originalName, file_url as fileUrl, file_type as fileType, file_category as fileCategory FROM file_info WHERE relation_id = #{relationId} ORDER BY last_update_date DESC, last_update_time DESC")
    IPage<FileInfoQuery> selectByRelationIdWithPage(IPage<FileInfoQuery> page, @Param("relationId") String relationId);
}