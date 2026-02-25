package com.example.spba.service.impl;

import com.example.spba.domain.dto.KeyValueItemDTO;
import com.example.spba.service.PublicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PublicServiceImplTest {

    @Autowired
    private PublicService publicService;

    @Test
    public void testConvertKeyValueToValueText() {
        // 准备测试数据
        Map<String, String> input = new HashMap<>();
        input.put("0", "篮球");
        input.put("1", "足球");
        
        // 执行转换
        List<Map<String, String>> result = publicService.convertKeyValueToValueText(input);
        
        // 验证结果
        assertEquals(2, result.size());
        assertEquals("0", result.get(0).get("value"));
        assertEquals("篮球", result.get(0).get("text"));
        assertEquals("1", result.get(1).get("value"));
        assertEquals("足球", result.get(1).get("text"));
    }
    
    @Test
    public void testConvertKeyValueToDTO() {
        // 准备测试数据
        Map<String, String> input = new HashMap<>();
        input.put("0", "篮球");
        input.put("1", "足球");
        
        // 执行转换
        List<KeyValueItemDTO> result = publicService.convertKeyValueToDTO(input);
        
        // 验证结果
        assertEquals(2, result.size());
        assertEquals("0", result.get(0).getValue());
        assertEquals("篮球", result.get(0).getText());
        assertEquals("1", result.get(1).getValue());
        assertEquals("足球", result.get(1).getText());
    }
    
    @Test
    public void testConvertKeyValueWithEmptyMap() {
        // 测试空Map
        Map<String, String> input = new HashMap<>();
        List<Map<String, String>> result = publicService.convertKeyValueToValueText(input);
        assertTrue(result.isEmpty());
        
        List<KeyValueItemDTO> dtoResult = publicService.convertKeyValueToDTO(input);
        assertTrue(dtoResult.isEmpty());
    }
    
    @Test
    public void testConvertKeyValueWithNull() {
        // 测试null输入
        List<Map<String, String>> result = publicService.convertKeyValueToValueText(null);
        assertTrue(result.isEmpty());
        
        List<KeyValueItemDTO> dtoResult = publicService.convertKeyValueToDTO(null);
        assertTrue(dtoResult.isEmpty());
    }
}