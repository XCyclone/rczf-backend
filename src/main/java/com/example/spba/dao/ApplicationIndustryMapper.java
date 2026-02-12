package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.entity.ApplicationAgencyTalent;
import com.example.spba.domain.entity.ApplicationIndustry;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApplicationIndustryMapper extends BaseMapper<ApplicationIndustry> {
}
