package com.flashsale.admin.domain.sys;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminUser {
    private Long id;
    private String username;
    private String password;
    private String salt;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<Role> roles;
    private List<String> permissions;
    private List<Menu> menus;

    public boolean isEnabled() {
        return status != null && status == 1;
    }

    public void updateLastLoginTime() {
        this.lastLoginTime = LocalDateTime.now();
    }
}
