package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu>
{
    @Select("<script>" +
            "SELECT id,name,parent_id,type,path,perms,icon,sort,status FROM int_menu " +
            "<where> " +
            "<if test='name != null and name != \"\"'>AND name LIKE CONCAT('%',#{name},'%')</if> " +
            "<if test='parent_id != null'>AND parent_id = #{parent_id}</if> " +
            "<if test='status != null'>AND status = #{status}</if> " +
            "<if test='menu_ids != null'> " +
            "AND id IN " +
            "<foreach collection='menu_ids' item='item' index='index' open='(' close=')' separator=','> " +
            "#{item} " +
            "</foreach> " +
            "</if> " +
            "</where> " +
            "</script>")
    public List<HashMap> getAll(HashMap params);
}