package com.example.spba.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RoleDTO
{

    @NotNull(message = "参数错误", groups = RoleDTO.Update.class)
    @NotBlank(message = "参数错误", groups = RoleDTO.Update.class)
    @NotBlank(message = "请输入角色ID", groups = RoleDTO.Save.class)
    @Size(max = 10, message = "角色ID长度不能超过10个字符", groups = {RoleDTO.Save.class, RoleDTO.Update.class})
    private String roleId;

    @NotBlank(message = "请输入角色名称", groups = {RoleDTO.Save.class, RoleDTO.Update.class})
    private String name;

    /**
     * 保存时的校验分组
     */
    public interface Save{}

    /**
     * 更新时的校验分组
     */
    public interface Update{}
}
