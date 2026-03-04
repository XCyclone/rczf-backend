package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.entity.HouseChoice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 房源选择记录 Mapper 接口
 */
@Mapper
public interface HouseChoiceMapper extends BaseMapper<HouseChoice> {
}
