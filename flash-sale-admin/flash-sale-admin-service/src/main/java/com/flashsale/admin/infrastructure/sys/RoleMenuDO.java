package com.flashsale.admin.infrastructure.sys;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("sys_role_menu")
public class RoleMenuDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long roleId;
    private Long menuId;
    private LocalDateTime createTime;
}
