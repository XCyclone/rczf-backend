package com.example.spba.dao;

import com.example.spba.domain.entity.InfoParameters;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InfoParametersMapper {
    
    @Select("SELECT par_key as parKey, par_value as parValue FROM public_parameters WHERE par_type = #{parType}")
    List<InfoParameters> selectByType(String parType);
}