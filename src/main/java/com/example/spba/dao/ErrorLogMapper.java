package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.entity.ErrorLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ErrorLogMapper extends BaseMapper<ErrorLog>
{
    @Select("<script>" +
            "SELECT * FROM int_error_log " +
            "<where>" +
            "<if test='params.start != null and params.start != \"\" and params.end != null and params.end != \"\"'>" +
            "AND create_time between #{params.start} and #{params.end} " +
            "</if>" +
            "</where>" +
            "ORDER BY id DESC" +
            "</script>")
    public Page<HashMap> getList(Page page, @Param("params") HashMap params);
}