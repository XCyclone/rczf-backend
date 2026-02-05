package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.entity.OperateLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;

@Mapper
public interface OperateLogMapper extends BaseMapper<OperateLog>
{
    @Select("<script>" +
            "SELECT id,username,url,method,params,ip,create_time FROM int_operate_log " +
            "<where> " +
            "<if test='params.username != null and params.username != \"\"'>AND username LIKE CONCAT('%',#{params.username},'%')</if> " +
            "<if test='params.start != null and params.start != \"\" and params.end != null and params.end != \"\"'> " +
            "AND create_time between #{params.start} and #{params.end} " +
            "</if> " +
            "</where> " +
            "ORDER BY id DESC" +
            "</script>")
    public Page<HashMap> getList(Page page, @Param("params") HashMap params);
}