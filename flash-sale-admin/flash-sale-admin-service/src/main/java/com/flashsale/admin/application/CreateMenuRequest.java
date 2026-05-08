package com.flashsale.admin.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMenuRequest {
    private Long parentId;
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;
    @NotNull(message = "菜单类型不能为空")
    private Integer menuType;
    private String routePath;
    private String componentPath;
    private String permCode;
    private String icon;
    private Integer sortOrder;
    private Integer visible;
}
