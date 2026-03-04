package com.example.spba.domain.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfoPortalSection {

    private Integer id;

    private String sectionName;

    private String parentSectionId;

    private String isDisplay;

    private String allowPublish;

    private String showInWechat;

    private String wechatShowOrder;

}
