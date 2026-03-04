package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.dto.CommunityInfoQuery;
import com.example.spba.domain.entity.CommunityInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 小区信息 Mapper 接口
 */
@Mapper
public interface CommunityInfoMapper extends BaseMapper<CommunityInfo> {
    
    /**
     * 根据ID列表查询小区信息
     * @param ids 小区ID列表
     * @return 小区信息列表
     */
    @Select("<script>" +
            "SELECT id, community_name FROM community_info " +
            "WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<CommunityInfo> selectByIds(@Param("ids") List<String> ids);

    /**
     * 查询所有项目列表
     */
    @Select("SELECT * FROM community_info where delete_status='0' ORDER BY last_update_date DESC, last_update_time DESC")
    @Results({
            @Result(property = "id", column = "id", id = true), // 主键建议标记为id=true
            @Result(property = "communityCode", column = "community_code"),
            @Result(property = "communityName", column = "community_name"),
            @Result(property = "buildingCount", column = "building_count"),
            @Result(property = "houseCount", column = "house_count"),
            @Result(property = "houseAddCount", column = "house_add_count"),
            @Result(property = "communityLocationTag", column = "community_location_tag"),
            @Result(property = "communityLocation", column = "community_location"),
            @Result(property = "builtYear", column = "built_year"),
            @Result(property = "heatingType", column = "heating_type"),
            @Result(property = "firstOperationDate", column = "first_operation_date"),
            @Result(property = "longitude", column = "longitude"),
            @Result(property = "latitude", column = "latitude"),
            @Result(property = "serviceStationId", column = "service_station_id"),
            @Result(property = "serviceStationName", column = "service_station_name"),
            @Result(property = "hasZeroBedroom", column = "has_zero_bedroom"),
            @Result(property = "hasOneBedroom", column = "has_one_bedroom"),
            @Result(property = "hasTwoBedroom", column = "has_two_bedroom"),
            @Result(property = "hasThreeBedroom", column = "has_three_bedroom"),
            @Result(property = "hasFourBedroom", column = "has_four_bedroom"),
            @Result(property = "communityDesc", column = "community_desc"),
            @Result(property = "zerobedroomLayoutPicId", column = "zerobedroom_layout_pic_id"),
            @Result(property = "zerobedroomPhotoPicId", column = "zerobedroom_photo_pic_id"),
            @Result(property = "onebedroomLayoutPicId", column = "onebedroom_layout_pic_id"),
            @Result(property = "onebedroomPhotoPicId", column = "onebedroom_photo_pic_id"),
            @Result(property = "twobedroomLayoutPicId", column = "twobedroom_layout_pic_id"),
            @Result(property = "twobedroomPhotoPicId", column = "twobedroom_photo_pic_id"),
            @Result(property = "threebedroomLayoutPicId", column = "threebedroom_layout_pic_id"),
            @Result(property = "threebedroomPhotoPicId", column = "threebedroom_photo_pic_id"),
            @Result(property = "communityPicGroupId", column = "community_pic_group_id"),
            @Result(property = "isIndustryTalentSubsidy", column = "is_industry_talent_subsidy"),
            @Result(property = "industryTalentRate", column = "industry_talent_rate"),
            @Result(property = "tag1", column = "tag1"),
            @Result(property = "tag2", column = "tag2"),
            @Result(property = "tag3", column = "tag3"),
            @Result(property = "lastUpdateDate", column = "last_update_date"),
            @Result(property = "lastUpdateTime", column = "last_update_time"),
            @Result(property = "lastUpdater", column = "last_updater"),
            @Result(property = "reserve1", column = "reserve1"),
            @Result(property = "reserve2", column = "reserve2"),
            @Result(property = "reserve3", column = "reserve3"),
            @Result(property = "reserve4", column = "reserve4"),
            @Result(property = "reserve5", column = "reserve5"),
            @Result(property = "reserve6", column = "reserve6")
    })
    List<Map<String,Object>>  selectAll();

    /**
     * 依据项目编号或项目名称查询项目
     */
    @Select({
            "<script>",
            "SELECT * FROM community_info ",
            "WHERE delete_status='0' ",
            "<if test='communityCode != null'> and community_code = #{communityCode}  </if>",
            "<if test='communityName != null'> and community_name LIKE CONCAT('%', #{communityName}, '%') </if>",
            "ORDER BY last_update_date DESC, last_update_time DESC",
            "</script>"
    })
    @Results({
            @Result(property = "id", column = "id", id = true), // 主键建议标记为id=true
            @Result(property = "communityCode", column = "community_code"),
            @Result(property = "communityName", column = "community_name"),
            @Result(property = "buildingCount", column = "building_count"),
            @Result(property = "houseCount", column = "house_count"),
            @Result(property = "houseAddCount", column = "house_add_count"),
            @Result(property = "communityLocationTag", column = "community_location_tag"),
            @Result(property = "communityLocation", column = "community_location"),
            @Result(property = "builtYear", column = "built_year"),
            @Result(property = "heatingType", column = "heating_type"),
            @Result(property = "firstOperationDate", column = "first_operation_date"),
            @Result(property = "longitude", column = "longitude"),
            @Result(property = "latitude", column = "latitude"),
            @Result(property = "serviceStationId", column = "service_station_id"),
            @Result(property = "serviceStationName", column = "service_station_name"),
            @Result(property = "hasZeroBedroom", column = "has_zero_bedroom"),
            @Result(property = "hasOneBedroom", column = "has_one_bedroom"),
            @Result(property = "hasTwoBedroom", column = "has_two_bedroom"),
            @Result(property = "hasThreeBedroom", column = "has_three_bedroom"),
            @Result(property = "hasFourBedroom", column = "has_four_bedroom"),
            @Result(property = "communityDesc", column = "community_desc"),
            @Result(property = "zerobedroomLayoutPicId", column = "zerobedroom_layout_pic_id"),
            @Result(property = "zerobedroomPhotoPicId", column = "zerobedroom_photo_pic_id"),
            @Result(property = "onebedroomLayoutPicId", column = "onebedroom_layout_pic_id"),
            @Result(property = "onebedroomPhotoPicId", column = "onebedroom_photo_pic_id"),
            @Result(property = "twobedroomLayoutPicId", column = "twobedroom_layout_pic_id"),
            @Result(property = "twobedroomPhotoPicId", column = "twobedroom_photo_pic_id"),
            @Result(property = "threebedroomLayoutPicId", column = "threebedroom_layout_pic_id"),
            @Result(property = "threebedroomPhotoPicId", column = "threebedroom_photo_pic_id"),
            @Result(property = "communityPicGroupId", column = "community_pic_group_id"),
            @Result(property = "isIndustryTalentSubsidy", column = "is_industry_talent_subsidy"),
            @Result(property = "industryTalentRate", column = "industry_talent_rate"),
            @Result(property = "tag1", column = "tag1"),
            @Result(property = "tag2", column = "tag2"),
            @Result(property = "tag3", column = "tag3"),
            @Result(property = "lastUpdateDate", column = "last_update_date"),
            @Result(property = "lastUpdateTime", column = "last_update_time"),
            @Result(property = "lastUpdater", column = "last_updater"),
            @Result(property = "reserve1", column = "reserve1"),
            @Result(property = "reserve2", column = "reserve2"),
            @Result(property = "reserve3", column = "reserve3"),
            @Result(property = "reserve4", column = "reserve4"),
            @Result(property = "reserve5", column = "reserve5"),
            @Result(property = "reserve6", column = "reserve6")
    })
    List<Map<String,Object>> selectCommunityInfoList(@Param("communityCode") String communityCode, @Param("communityName") String communityName);
    
    /**
     * 分页查询小区列表（支持按小区名称模糊查询）
     */
    @Select({
            "<script>",
            "SELECT * FROM community_info ",
            "WHERE delete_status='0' ",
            "<if test='communityName != null and communityName != \"\"'> and community_name LIKE CONCAT('%', #{communityName}, '%') </if>",
            "ORDER BY last_update_date DESC, last_update_time DESC",
            "</script>"
    })
    IPage<CommunityInfoQuery> selectCommunityInfoListWithPage(IPage<CommunityInfoQuery> page, @Param("communityName") String communityName);

}