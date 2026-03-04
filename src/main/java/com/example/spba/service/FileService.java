package com.example.spba.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.dto.FileInfoQuery;
import com.example.spba.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {

    /**
     * 根据关联 ID 查询文件列表（不分页）
     * @param relationId 关联 ID
     * @return 文件列表
     */
    List<Map<String,Object>> selectFileInfoListByRelationId(String relationId);
    
    /**
     * 根据关联 ID 查询文件列表（分页）
     * @param page 分页对象
     * @param relationId 关联 ID
     * @return 分页结果
     */
    IPage<FileInfoQuery> selectFileInfoListByRelationIdWithPage(IPage<FileInfoQuery> page, String relationId);
}
