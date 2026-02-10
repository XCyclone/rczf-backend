package com.example.spba.service;

import com.example.spba.domain.dto.KeyValueItemDTO;

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
}