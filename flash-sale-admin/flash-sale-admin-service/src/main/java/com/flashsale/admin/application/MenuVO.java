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
public class MenuVO {
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
    private List<MenuVO> children;
}
