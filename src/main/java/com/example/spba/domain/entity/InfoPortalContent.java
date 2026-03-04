package com.example.spba.domain.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfoPortalContent {

    private Integer id;

    private String sectionId;

    private String sectionName;

    private String title;

    private String content;

    private String publishDate;

    private String publishTime;

    private String publisher;

    private Integer status; //0-草稿，1-发布

    private Integer delStatus;//0-未删除，1-已删除

}
