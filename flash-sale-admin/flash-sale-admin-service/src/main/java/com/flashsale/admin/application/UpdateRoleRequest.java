package com.flashsale.admin.application;

import lombok.Data;

@Data
public class UpdateRoleRequest {
    private String roleCode;
    private String roleName;
    private String description;
    private Integer status;
    private Integer sortOrder;
}
