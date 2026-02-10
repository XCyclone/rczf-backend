package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.entity.BusinessEnterpriseAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业办公地址Mapper接口
 */
@Mapper
public interface BusinessEnterpriseAddressMapper extends BaseMapper<BusinessEnterpriseAddress> {
}