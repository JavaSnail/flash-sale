package com.flashsale.admin.infrastructure.sys;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("sys_admin_user")
public class AdminUserDO {
    @TableId(type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
