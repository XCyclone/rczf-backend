package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.domain.entity.InfoPortalContent;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InfoPortalContentMapper extends BaseMapper<InfoPortalContent> {


    @Select({
            "<script>",
            "SELECT * FROM info_portal_content ",
            "WHERE del_status = '0' ",
            "<if test='startPublishDate != null and startPublishDate != \"\"'> and publish_date &gt;= #{startPublishDate}</if>",
            "<if test='endPublishDate != null and endPublishDate != \"\"'> and publish_date &lt;= #{endPublishDate}</if>",
            "ORDER BY publish_date DESC, publish_time DESC",
            "</script>"
    })
    IPage<InfoPortalContent> selectInfoPortalByDateWithPage(IPage<InfoPortalContent> page,
                                                            @Param("startPublishDate") String startPublishDate,
                                                            @Param("endPublishDate") String endPublishDate,
                                                            @Param("publisher") String publisher);

}

