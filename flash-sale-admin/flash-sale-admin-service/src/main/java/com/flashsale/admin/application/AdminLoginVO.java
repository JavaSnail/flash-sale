package com.flashsale.admin.application;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginVO {
    private String token;
    private Long userId;
    private String username;
    private String realName;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
    private List<MenuVO> menus;
}
