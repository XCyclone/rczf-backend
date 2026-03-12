package com.example.spba.dao;

import com.example.spba.domain.entity.InfoParameters;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PublicParametersMapper {
    
    @Select("SELECT par_value FROM public_parameters WHERE par_type = #{parType} order by par_key")
    List<String> selectByType(String parType);
    
    /**
     * 根据参数类型查询所有参数
     * @param parType 参数类型
     * @return 参数列表
     */
    @Select("SELECT par_key, par_value FROM public_parameters WHERE par_type = #{parType} ")
    List<InfoParameters> selectByParType(@Param("parType") String parType);
}