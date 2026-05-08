package com.flashsale.admin.domain.sys;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Role {
    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private Integer status;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public boolean isEnabled() {
        return status != null && status == 1;
    }
}
