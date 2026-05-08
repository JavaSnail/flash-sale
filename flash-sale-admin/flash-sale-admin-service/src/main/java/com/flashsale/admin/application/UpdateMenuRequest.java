package com.flashsale.admin.application;

import lombok.Data;

@Data
public class UpdateMenuRequest {
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
}
