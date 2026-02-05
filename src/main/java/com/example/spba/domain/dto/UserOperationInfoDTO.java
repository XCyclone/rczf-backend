package com.example.spba.domain.dto;

import com.example.spba.domain.entity.UserOperation;
import lombok.Data;

import java.util.Date;

@Data
public class UserOperationInfoDTO {
    private Integer id;
    private String userId;
    private String userName; // 用户姓名
    private Integer type; // 类型：1-个人；2-企业
    private Integer operation; // 操作：1-注册；2-修改信息
    private Integer action; // 动作：1-申请；2-同意；3-拒绝
    private String info; // 备注
    private String content; // 内容（JSON格式）
    private Date createTime; // 创建时间
    private Date updateTime; // 更新时间
    private String operationName; // 操作名称
    private String actionName; // 动作名称
    private String typeName; // 类型名称
    
    public UserOperationInfoDTO(UserOperation operation) {
        this.id = operation.getId();
        this.userId = operation.getUserId();
        this.type = operation.getType();
        this.operation = operation.getOperation();
        this.action = operation.getAction();
        this.info = operation.getInfo();
        this.content = operation.getContent();
        this.createTime = operation.getCreateTime();
        this.updateTime = operation.getUpdateTime(); // 新增更新时间
        
        // 设置名称
        this.operationName = getOperationName(operation.getOperation());
        this.actionName = getActionName(operation.getAction());
        this.typeName = getTypeName(operation.getType());
    }
    
    private String getOperationName(Integer operation) {
        switch (operation) {
            case 1: return "注册";
            case 2: return "修改信息";
            default: return "未知操作";
        }
    }
    
    private String getActionName(Integer action) {
        switch (action) {
            case 1: return "申请";
            case 2: return "同意";
            case 3: return "拒绝";
            default: return "未知动作";
        }
    }
    
    private String getTypeName(Integer type) {
        switch (type) {
            case 1: return "个人";
            case 2: return "企业";
            default: return "未知类型";
        }
    }
}