package com.flashsale.admin.domain.sys;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class Menu {
    private Long id;
    private Long parentId;
    private String menuName;
    private Integer menuType;
    private String routePath;
    private String componentPath;
    private String permCode;
    private String icon;
    private Integer sortOrder;
    private Integer visible;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @Setter
    private List<Menu> children;
}
