package com.example.spba.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业申请标签信息数据传输对象
 */
@Data
public class ApplicationTagInfoDTO {
    private String tag;              // 申请标签
    private String title;            // 名称
    @JsonIgnore
    private List<MultipartFile> fileList; // 文件列表（JSON序列化时忽略）
    private Integer fileCount;       // 文件数量提示，用于在解析JSON时确定该标签应分配多少文件
    
    // 辅助方法，用于获取文件数量提示
    public int getFileListSizeHint() {
        return fileCount != null ? fileCount : 0;
    }
    
    // 获取文件ID列表的方法（用于Service层处理）
    @JsonIgnore
    public List<String> getFileIdList() {
        List<String> fileIdList = new ArrayList<>();
        if (fileList != null) {
            for (MultipartFile file : fileList) {
                // 这里应该从文件中提取或生成文件ID
                // 在实际应用中，这些文件应该已经被上传并获得了ID
                fileIdList.add("temp_file_id_" + System.currentTimeMillis());
            }
        }
        return fileIdList;
    }
}