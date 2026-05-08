package com.flashsale.admin.application;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class AdminUserVO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private List<Long> roleIds;
    private List<String> roleCodes;
}
