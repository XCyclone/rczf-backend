package com.example.spba.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 个人选房信息返回 DTO
 */
@Data
public class UserChooseHouseInfoDTO {
    
    /**
     * 是否有选房资格（有待选房申请）
     */
    private Boolean hasApproved;
    
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
    public static class ApplicationInfo {
        
        /**
         * 项目 ID
         */
        private String projectId;
        
        /**
         * 项目名称
         */
        private String projectName;
        
        /**
         * 申请时间
         */
        private String applyTime;
        
        /**
         * 期望小区列表
         */
        private List<String> expectedCommunity;
        
        /**
         * 期望房型值列表
         */
        private List<String> expectedHouseTypeValues;
    }
}
