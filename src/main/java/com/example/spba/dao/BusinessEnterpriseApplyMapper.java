package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.entity.BusinessEnterpriseApply;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BusinessEnterpriseApplyMapper extends BaseMapper<BusinessEnterpriseApply> {
    // 显式声明一些常用方法，帮助IDE识别
    int insert(BusinessEnterpriseApply entity);
    BusinessEnterpriseApply selectById(String id);
    int updateById(BusinessEnterpriseApply entity);
}