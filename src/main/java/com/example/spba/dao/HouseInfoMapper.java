package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.dto.HouseInfoDTO;
import com.example.spba.domain.entity.HouseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 房屋信息 Mapper 接口
 */
@Mapper
public interface HouseInfoMapper extends BaseMapper<HouseInfo> {
    
    /**
     * 分页查询可选房屋信息列表
     * @param query 查询条件
     * @return 房屋信息列表
     */
    @Select("<script>" +
            "SELECT " +
            "h.id, " +
            "ci.community_name AS communityName, " +
            "ci.id AS communityId, " +
            "ci.community_location AS communityLocation, " +
            "h.building_no AS buildingNo, " +
            "h.unit_no AS unitNo, " +
            "h.floor_no AS floorNo, " +
            "h.room_no AS roomNo, " +
            "h.room_type AS roomType, " +
            "h.rent_area AS rentArea, " +
            "h.rent_price AS rentPrice, " +
            "h.house_desc AS houseDesc, " +
            "ci.tag1, " +
            "ci.tag2, " +
            "ci.tag3, " +
            "h.house_layout_pic_id AS houseLayoutPicId, " +
            "h.house_pic_group_id AS housePicGroupId " +
            "FROM house_info h " +
            "INNER JOIN community_info ci ON h.community_id = ci.id " +
            "WHERE ci.delete_status = '0' and h.delete_status = '0' and h.status = 0 " +
            "AND h.community_id IN (SELECT community_id FROM project_community WHERE project_id = #{query.projectId})" +
            "<if test=\"query != null\">" +
            "<if test=\"query.community != null and query.community.size() > 0\">" +
            "AND h.community_id IN " +
            "<foreach item='communityId' collection='query.community' open='(' separator=',' close=')'>" +
            "#{communityId}" +
            "</foreach>" +
            "</if>" +
            "<if test=\"query.houseTypes != null and query.houseTypes.size() > 0\">" +
            "AND h.room_type IN " +
            "<foreach item='houseType' collection='query.houseTypes' open='(' separator=',' close=')'>" +
            "#{houseType}" +
            "</foreach>" +
            "</if>" +
            "<if test=\"query.rentRange != null and query.rentRange.size() == 2\">" +
            "AND h.rent_price >= #{query.rentRange[0]} AND h.rent_price &lt;= #{query.rentRange[1]}" +
            "</if>" +
            "<if test=\"query.floorRange != null and query.floorRange.size() == 2\">" +
            "AND CAST(h.floor_no AS SIGNED) >= #{query.floorRange[0]} AND CAST(h.floor_no AS SIGNED) &lt;= #{query.floorRange[1]}" +
            "</if>" +
            "<if test=\"query.unit != null and query.unit != ''\">" +
            "AND h.unit_no = #{query.unit}" +
            "</if>" +
            "<if test=\"query.location != null and query.location.size() > 0\">" +
            "AND ci.community_location_tag IN " +
            "<foreach item='location' collection='query.location' open='(' separator=',' close=')'>" +
            "#{location}" +
            "</foreach>" +
            "</if>" +
            "</if>" +
            "ORDER BY h.last_update_date DESC, h.last_update_time DESC" +
            "</script>")
    List<HouseInfoDTO> selectHouseInfoListByPage(@Param("query") Object query);
    
    /**
     * 根据房屋 ID 查询房屋信息（包含小区信息）
     * @param houseId 房屋 ID
     * @return 房屋信息 DTO
     */
    @Select("SELECT " +
            "h.id, " +
            "ci.community_name AS communityName, " +
            "ci.id AS communityId, " +
            "ci.community_location AS communityLocation, " +
            "h.building_no AS buildingNo, " +
            "h.unit_no AS unitNo, " +
            "h.floor_no AS floorNo, " +
            "h.room_no AS roomNo, " +
            "h.room_type AS roomType, " +
            "h.rent_area AS rentArea, " +
            "h.rent_price AS rentPrice, " +
            "h.house_desc AS houseDesc, " +
            "ci.tag1, " +
            "ci.tag2, " +
            "ci.tag3, " +
            "h.house_layout_pic_id AS houseLayoutPicId, " +
            "h.house_pic_group_id AS housePicGroupId " +
            "FROM house_info h " +
            "INNER JOIN community_info ci ON h.community_id = ci.id " +
            "WHERE h.id = #{houseId} AND ci.delete_status = '0' AND h.delete_status = '0'")
    HouseInfoDTO selectHouseInfoById(@Param("houseId") String houseId);
    
    /**
     * 公共查询房屋信息列表（支持模糊搜索）
     * @param query 查询条件
     * @return 房屋信息列表
     */
    @Select("<script>" +
            "SELECT " +
            "h.id, " +
            "ci.community_name AS communityName, " +
            "ci.id AS communityId, " +
            "ci.community_location AS communityLocation, " +
            "h.building_no AS buildingNo, " +
            "h.unit_no AS unitNo, " +
            "h.floor_no AS floorNo, " +
            "h.room_no AS roomNo, " +
            "h.room_type AS roomType, " +
            "h.rent_area AS rentArea, " +
            "h.rent_price AS rentPrice, " +
            "h.house_desc AS houseDesc, " +
            "ci.tag1, " +
            "ci.tag2, " +
            "ci.tag3, " +
            "h.house_layout_pic_id AS houseLayoutPicId, " +
            "h.house_pic_group_id AS housePicGroupId " +
            "FROM house_info h " +
            "INNER JOIN community_info ci ON h.community_id = ci.id " +
            "WHERE ci.delete_status = '0' and h.delete_status = '0' and h.status = '0' " +
            "<if test=\"query != null\">" +
            "<if test=\"query.community != null and query.community.size() > 0\">" +
            "AND h.community_id IN " +
            "<foreach item='communityId' collection='query.community' open='(' separator=',' close=')'>" +
            "#{communityId}" +
            "</foreach>" +
            "</if>" +
            "<if test=\"query.houseTypes != null and query.houseTypes.size() > 0\">" +
            "AND h.room_type IN " +
            "<foreach item='houseType' collection='query.houseTypes' open='(' separator=',' close=')'>" +
            "#{houseType}" +
            "</foreach>" +
            "</if>" +
            "<if test=\"query.rentRange != null and query.rentRange.size() == 2\">" +
            "AND h.rent_price >= #{query.rentRange[0]} AND h.rent_price &lt;= #{query.rentRange[1]}" +
            "</if>" +
            "<if test=\"query.floorRange != null and query.floorRange.size() == 2\">" +
            "AND CAST(h.floor_no AS SIGNED) >= #{query.floorRange[0]} AND CAST(h.floor_no AS SIGNED) &lt;= #{query.floorRange[1]}" +
            "</if>" +
            "<if test=\"query.unit != null and query.unit != ''\">" +
            "AND h.unit_no = #{query.unit}" +
            "</if>" +
            "<if test=\"query.communityName != null and query.communityName != ''\">" +
            "AND ci.community_name LIKE CONCAT('%', #{query.communityName}, '%')" +
            "</if>" +
            "</if>" +
            "ORDER BY h.last_update_date DESC, h.last_update_time DESC" +
            "</script>")
    List<HouseInfoDTO> selectPublicHouseInfoList(@Param("query") Object query);
}
