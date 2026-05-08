package com.flashsale.admin.infrastructure.sys;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("sys_menu")
public class MenuDO {
    @TableId(type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
