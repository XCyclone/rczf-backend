package com.example.spba.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.entity.InfoPortalContent;
import com.example.spba.domain.entity.InfoPortalSection;

import java.util.List;

public interface InfoPortalService {
    /**
     * 分页查询通知发布列表
     * @param page 分页对象
     * @param startPublishDate 开始日期
     * @param endPublishDate 结束日期
     * @param publisher 发布者
     * @return 分页结果
     */
    IPage<InfoPortalContent> selectInfoPortalByDateWithPage(IPage<InfoPortalContent> page, String startPublishDate, String endPublishDate, String publisher);
    

}
