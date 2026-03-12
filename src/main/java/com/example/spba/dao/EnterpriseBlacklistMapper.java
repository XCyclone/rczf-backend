package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.entity.EnterpriseBlacklist;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业黑名单 Mapper 接口
 */
@Mapper
public interface EnterpriseBlacklistMapper extends BaseMapper<EnterpriseBlacklist> {
}
