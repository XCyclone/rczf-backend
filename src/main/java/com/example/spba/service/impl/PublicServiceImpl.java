package com.example.spba.service.impl;

import com.example.spba.domain.dto.KeyValueItemDTO;
import com.example.spba.service.PublicService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PublicServiceImpl implements PublicService {
    
    @Override
    public List<Map<String, String>> convertKeyValueToValueText(Map<String, String> keyValueMap) {
        List<Map<String, String>> result = new ArrayList<>();
        
        if (keyValueMap != null && !keyValueMap.isEmpty()) {
            for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
                Map<String, String> item = new HashMap<>();
                item.put("value", entry.getKey());
                item.put("text", entry.getValue());
                result.add(item);
            }
        }
        
        return result;
    }
    
    @Override
    public List<KeyValueItemDTO> convertKeyValueToDTO(Map<String, String> keyValueMap) {
        List<KeyValueItemDTO> result = new ArrayList<>();
        
        if (keyValueMap != null && !keyValueMap.isEmpty()) {
            for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
                KeyValueItemDTO item = new KeyValueItemDTO(entry.getKey(), entry.getValue());
                result.add(item);
            }
        }
        
        return result;
    }
}