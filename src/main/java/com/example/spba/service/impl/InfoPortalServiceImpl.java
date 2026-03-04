package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.dao.InfoPortalContentMapper;
import com.example.spba.domain.entity.InfoPortalContent;
import com.example.spba.domain.entity.InfoPortalSection;
import com.example.spba.service.InfoPortalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InfoPortalServiceImpl implements InfoPortalService {

    @Autowired
    private InfoPortalContentMapper infoPortalContentMapper;

    @Override
    public IPage<InfoPortalContent> selectInfoPortalByDateWithPage(IPage<InfoPortalContent> page, String startPublishDate, String endPublishDate, String publisher) {
        return infoPortalContentMapper.selectInfoPortalByDateWithPage(page, startPublishDate, endPublishDate, publisher);
    }

}
