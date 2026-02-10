package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.entity.ApplicationTagInfo;
import com.example.spba.domain.entity.BusinessEnterpriseTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业标签信息Mapper接口
 */
@Mapper
public interface BusinessEnterpriseTagMapper extends BaseMapper<BusinessEnterpriseTag> {

}