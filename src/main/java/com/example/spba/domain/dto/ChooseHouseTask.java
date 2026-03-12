package com.example.spba.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 选房任务 DTO
 * 用于封装用户选房相关的所有信息
 */
@Data
public class ChooseHouseTask implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 是否有选房资格（即有待选房申请）
     */
    private Boolean isApproved;

    /**
     * 是否是选房时间
     */
    private Boolean isSelectionTime;

    /**
     * 选房开始时间
     */
    private String selectionStartTime;

    /**
     * 选房结束时间
     */
    private String selectionEndTime;

    /**
     * 申请信息
     */
    private ApplicationInfo applicationInfo;

    /**
     * 申请信息内部类
     */
    @Data
    public static class ApplicationInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 项目 ID
         */
        private String projectId;

        /**
         * 申请 ID
         */
        private String applicationId;

        /**
         * 申请时间
         */
        private String applyDateTime;

        /**
         * 期望小区列表
         */
        private String[] expectedCommunity;

        /**
         * 期望房型列表
         * 0-开间，1-一居室，2-二居室，3-三居室，4-四居室
         */
        private String[] expectedHouseType;
    }
}
