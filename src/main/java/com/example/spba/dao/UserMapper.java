package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User>
{
    @Select("<script>" +
            "SELECT a.id,a.username,a.password,a.safe,a.status, " +
            "( SELECT GROUP_CONCAT(rur.role_id) FROM int_role_user_rel rur WHERE rur.user_id = CONVERT(a.id, CHAR) ) AS role " +
            "FROM int_user a " +
            "<where> " +
            "<if test='id != null'>AND a.id = #{id}</if> " +
            "<if test='username != null and username != \"\"'>AND username = #{username}</if> " +
            "</where> " +
            "limit 1 " +
            "</script>")
    public HashMap getInfo(HashMap params);

    @Select("<script>" +
            "SELECT a.id,a.username,a.status,a.login_time,a.login_ip, " +
            "( SELECT count(*) FROM int_login_log WHERE a.id = user_id) AS login_count, " +
            "( SELECT GROUP_CONCAT(r.name SEPARATOR ' | ' ) FROM int_role_user_rel rur " +
            "LEFT JOIN int_role r ON rur.role_id = r.role_id " +
            "WHERE rur.user_id = CONVERT(a.id, CHAR) ) AS roles " +
            "FROM int_user a " +
            "<where> " +
            "<if test='params.username != null and params.username != \"\"'>AND a.username LIKE CONCAT('%',#{params.username},'%')</if> " +
            "<if test='params.status != null'>AND a.status = #{params.status}</if> " +
            "<if test='params.role != null'> " +
            "AND EXISTS ( " +
            "SELECT 1 FROM int_role_user_rel rur " +
            "WHERE rur.user_id = CONVERT(a.id, CHAR) " +
            "AND rur.role_id = #{params.role} " +
            ") " +
            "</if> " +
            "</where> " +
            "</script>")
    public Page<HashMap> getList(Page page, @Param("params") HashMap params);

    @Select("<script>" +
            "SELECT a.id FROM int_user a " +
            "INNER JOIN int_role_user_rel rur ON rur.user_id = CONVERT(a.id, CHAR) " +
            "WHERE rur.role_id = #{roleId} " +
            "</script>")
    public List<HashMap> getRoleUserAll(@Param("roleId") String roleId);
}