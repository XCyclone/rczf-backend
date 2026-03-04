package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.dao.FileInfoMapper;
import com.example.spba.domain.dto.FileInfoQuery;
import com.example.spba.service.FileService;
import com.example.spba.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private FileInfoMapper fileInfoMapper;


    @Override
    public List<Map<String,Object>> selectFileInfoListByRelationId(String relationId){
        return fileInfoMapper.selectById(relationId);
    }
    
    @Override
    public IPage<FileInfoQuery> selectFileInfoListByRelationIdWithPage(IPage<FileInfoQuery> page, String relationId) {
        return fileInfoMapper.selectByRelationIdWithPage(page, relationId);
    }


}
