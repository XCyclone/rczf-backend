package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.entity.BusinessUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BusinessUserMapper extends BaseMapper<BusinessUser>
{
}
