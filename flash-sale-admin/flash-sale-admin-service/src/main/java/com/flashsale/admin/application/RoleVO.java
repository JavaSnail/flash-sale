package com.flashsale.admin.application;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class RoleVO {
    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private Integer status;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private List<Long> permissionIds;
}
