package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.entity.HouseUsingJnl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HouseUsingJnlMapper extends BaseMapper<HouseUsingJnl> {
    
    /**
     * 检查用户是否有未结束的租房记录
     * @param userId 用户ID
     * @return 未结束的租房记录数量
     */
    @Select("SELECT COUNT(*) FROM house_using_jnl WHERE user_id = #{userId} AND end_date IS NULL OR end_date = ''")
    int countActiveRentals(@Param("userId") String userId);
}