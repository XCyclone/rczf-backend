package com.example.spba.service;

import com.example.spba.domain.dto.KeyValueItemDTO;
import com.example.spba.domain.dto.ProjectInfoDTO;
import com.example.spba.domain.dto.HouseInfoQueryDTO;
import com.example.spba.utils.R;

import java.util.List;
import java.util.Map;

public interface PublicService {
    
    /**
     * 将键值对格式转换为包含value和text字段的对象数组格式
     * 例如：{"0": "篮球", "1": "足球"} 转换为 [{value: "0", text: "篮球"}, {value: "1", text: "足球"}]
     * 
     * @param keyValueMap 键值对Map
     * @return 转换后的对象列表（Map格式）
     */
    List<Map<String, String>> convertKeyValueToValueText(Map<String, String> keyValueMap);
    
    /**
     * 将键值对格式转换为KeyValueItemDTO对象列表
     * 例如：{"0": "篮球", "1": "足球"} 转换为 [KeyValueItemDTO{value="0", text="篮球"}, KeyValueItemDTO{value="1", text="足球"}]
     * 
     * @param keyValueMap 键值对Map
     * @return 转换后的KeyValueItemDTO对象列表
     */
    List<KeyValueItemDTO> convertKeyValueToDTO(Map<String, String> keyValueMap);
    
    /**
     * 查询全量国家信息
     * @return 国家信息列表
     */
    R queryCountry();
    
    /**
     * 查询企业ID和企业名称
     * @return 企业信息列表
     */
    R queryEnterprise();
    
    /**
     * 查询项目信息-个人
     * @return 项目信息列表
     */
    R queryUserProject();

    /**
     * 查询项目信息-企业
     * @return 项目信息列表
     */
    R queryEnterpriseProject();
    /**
     * 查询企业属地信息
     * @return 企业属地信息列表
     */
    R queryEnterpriseLocation();
    
    /**
     * 根据项目 ID 查询关联的小区信息
     * @param projectId 项目 ID
     * @return 小区信息列表，包含小区 ID 和名称
     */
    R queryCommunitiesByProject(String projectId);
    
    /**
     * 公共查询房屋信息列表（支持模糊搜索）
     * @param queryDTO 查询条件
     * @return 房屋信息分页数据
     */
    R queryPublicHouseInfoList(HouseInfoQueryDTO queryDTO);
}