package com.flashsale.admin.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoleRequest {
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;
    @NotBlank(message = "角色名称不能为空")
    private String roleName;
    private String description;
    private Integer sortOrder;
}
