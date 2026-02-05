package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role>
{
    @Select("<script>" +
            "SELECT * FROM int_role " +
            "<where> " +
            "<if test='role_ids != null'> " +
            "AND role_id IN " +
            "<foreach collection='role_ids' item='item' index='index' open='(' close=')' separator=','> " +
            "#{item} " +
            "</foreach> " +
            "</if> " +
            "</where> " +
            "</script>")
    public List<Role> getAll(HashMap params);

    @Select("<script>" +
            "SELECT r.role_id, r.name, " +
            "( SELECT COUNT( * ) FROM int_role_user_rel WHERE role_id = r.role_id) as admin_count " +
            "FROM int_role r " +
            "<where> " +
            "<if test='params.name != null and params.name != \"\"'>AND r.name LIKE CONCAT('%',#{params.name},'%')</if> " +
            "</where> " +
            "</script>")
    public Page<HashMap> getList(Page page, @Param("params") HashMap params);
}