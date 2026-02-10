package com.example.spba.dao;

import com.example.spba.domain.entity.InfoParameters;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface InfoParametersMapper {
    
    @Select("SELECT par_value FROM public_parameters WHERE par_type = #{parType} order by par_key")
    List<String> selectByType(String parType);
}