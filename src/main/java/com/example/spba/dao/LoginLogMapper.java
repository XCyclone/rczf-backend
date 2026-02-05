package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;

@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog>
{
    @Select("<script>" +
            "SELECT ll.id,ll.login_ip,ll.create_time,u.username, " +
            "( SELECT GROUP_CONCAT(r.name SEPARATOR ' | ' ) FROM int_role_user_rel rur " +
            "LEFT JOIN int_role r ON rur.role_id = r.role_id " +
            "WHERE rur.user_id = CONVERT(u.id, CHAR) ) AS roles " +
            "FROM int_login_log as ll " +
            "LEFT JOIN int_user as u on ll.user_id = CONVERT(u.id, CHAR) " +
            "<where> " +
            "<if test='params.username != null and params.username != \"\"'>AND u.username LIKE CONCAT('%',#{params.username},'%')</if> " +
            "<if test='params.start != null and params.start != \"\" and params.end != null and params.end != \"\"'> " +
            "AND ll.create_time between #{params.start} and #{params.end} " +
            "</if> " +
            "</where> " +
            "ORDER BY ll.id DESC" +
            "</script>")
    public Page<HashMap> getList(Page page, @Param("params") HashMap params);
}